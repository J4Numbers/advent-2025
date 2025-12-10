package uk.j4numbers.adventofcode.advent_2025.day_09.utils

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.NodeArea
import kotlin.math.absoluteValue

val logger = KotlinLogging.logger {};

class SpaceCalculator {
    fun calculateAreaBetweenNodes(first: Coordinate, second: Coordinate): Long {
        val calculatedArea = ((first.x - second.x).absoluteValue + 1) * ((first.y - second.y).absoluteValue + 1);
        logger.debug { "Calculating the distance between $first and $second - $calculatedArea" };
        return calculatedArea;
    }

    fun fillNodePath(nodes: List<Coordinate>): List<Coordinate> {
        val pathProgress = mutableListOf<Coordinate>();

        if (nodes.size > 1) {
            var workingNode = nodes[0];
            val pathCircuit = nodes.toMutableList();
            pathCircuit.add(workingNode);
            for (i in 1 until pathCircuit.size) {
                val targetNode = pathCircuit[i];
                logger.debug { "New target node from working node ($workingNode) is $targetNode" };
                while (targetNode != workingNode) {
                    if (!pathProgress.contains(workingNode)) {
                        logger.debug { "Adding new node to walking path - $workingNode" };
                        pathProgress.add(workingNode);
                    }
                    if (workingNode.x != targetNode.x) {
                        val increment = if (workingNode.x > targetNode.x) -1 else 1;
                        workingNode = Coordinate(workingNode.x + increment, workingNode.y);
                    } else if (workingNode.y != targetNode.y) {
                        val increment = if (workingNode.y > targetNode.y) -1 else 1;
                        workingNode = Coordinate(workingNode.x, workingNode.y + increment);
                    }
                }
            }
        }

        return pathProgress;
    }

    fun testLineInPolygon(startPoint: Coordinate, endX: Long, pathCoordinates: List<Coordinate>, maxX: Long): Boolean {
        var inIntersect = 0;
        var outIntersect = 0;
        var intersecting = pathCoordinates.contains(startPoint);
        var focusX = startPoint.x;

        logger.debug { "Testing line $startPoint until X position of $endX and $maxX" };
        logger.debug { "Start point $startPoint was intersecting - $intersecting" };

        while (focusX <= endX) {
            val testCoord = Coordinate(focusX, startPoint.y);

            if (pathCoordinates.contains(testCoord)) {
                logger.debug { "Edge detected within shape at $testCoord" };
                intersecting = true;
            } else if (intersecting) {
                logger.debug { "Previously detected edge within shape has now passed at $testCoord" };
                inIntersect += 1;
                intersecting = false;
            }

            focusX += 1;
        }
        while (focusX <= maxX) {
            val testCoord = Coordinate(focusX, startPoint.y);

            if (pathCoordinates.contains(testCoord)) {
                logger.debug { "Edge detected outside of shape at $testCoord" };
                intersecting = true;
            } else if (intersecting) {
                logger.debug { "Previously detected edge outside of shape has now passed at $testCoord" };
                outIntersect += 1;
                intersecting = false;
            }

            focusX += 1;
        }

        if (intersecting) {
            logger.debug { "Edge of map reached while intersecting. Adding new out intersection for $focusX" };
            outIntersect += 1;
        }

        logger.debug { "$startPoint -> $endX - Found $inIntersect intersections within rect" };
        logger.debug { "$startPoint -> $maxX - Found ${inIntersect + outIntersect} intersections until the end" };

        return inIntersect % 2 == 0 && outIntersect % 2 == 1;
    }

    fun testPolygonInsidePolygon(firstCorner: Coordinate, secondCorner: Coordinate, pathCoordinates: List<Coordinate>, maxX: Long): Boolean {
        val leftCorner: Coordinate;
        val rightCorner: Coordinate;
        if  (firstCorner.x > secondCorner.x) {
            leftCorner = secondCorner;
            rightCorner = firstCorner;
        } else {
            leftCorner = firstCorner;
            rightCorner = secondCorner;
        }

        logger.debug { "Check requested for polygon $firstCorner -> $secondCorner to confirm it is within main shape" };

        val leftPoints = fillNodePath(listOf(leftCorner, Coordinate(leftCorner.x, rightCorner.y)));
        return leftPoints.all { point -> testLineInPolygon(point, rightCorner.x, pathCoordinates, maxX) };
    }

    fun calculateAreas(nodes: List<Coordinate>, limitedMode: Boolean): List<NodeArea> {
        val nodeAreas = mutableListOf<NodeArea>();

        val nodePath = fillNodePath(nodes);
        val maxX = nodes.maxBy(Coordinate::x).x;

        for (i in 0 until nodes.size) {
            for (j in i+1 until nodes.size) {
                val addPair = if (limitedMode)
                    testPolygonInsidePolygon(nodes[i], nodes[j], nodePath, maxX)
                    else true;
                if (addPair) {
                    nodeAreas.add(
                        NodeArea(
                            Pair(nodes[i], nodes[j]),
                            calculateAreaBetweenNodes(nodes[i], nodes[j]),
                        ),
                    );
                }
            }
        }

        return nodeAreas.sortedWith(compareByDescending(NodeArea::area));
    }
}
