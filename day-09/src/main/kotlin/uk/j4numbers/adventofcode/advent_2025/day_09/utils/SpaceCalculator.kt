package uk.j4numbers.adventofcode.advent_2025.day_09.utils

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.CoordinateJoin
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.NodeArea
import kotlin.math.absoluteValue

val logger = KotlinLogging.logger {};

class SpaceCalculator {
    fun calculateAreaBetweenNodes(first: Coordinate, second: Coordinate): Long {
        val calculatedArea = ((first.x - second.x).absoluteValue + 1) * ((first.y - second.y).absoluteValue + 1);
        logger.debug { "Calculating the distance between $first and $second - $calculatedArea" };
        return calculatedArea;
    }

    fun fillNodePath(nodes: List<Coordinate>): Set<CoordinateJoin> {
        val pathProgress = mutableSetOf<CoordinateJoin>();

        if (nodes.size > 1) {
            var workingNode = nodes[0];
            for (i in 1 until nodes.size) {
                pathProgress.add(CoordinateJoin(workingNode, nodes[i]));
                workingNode = nodes[i];
            }
            pathProgress.add(CoordinateJoin(workingNode, nodes[0]));
        }

        return pathProgress;
    }

    fun testNodeOnCircuit(node: Coordinate, nodeCircuit: Set<CoordinateJoin>): Boolean {
        // Quick filter
        val deepCandidates = nodeCircuit.filter { test -> test.first.x == node.x || test.first.y == node.y }
        // Deep filter
        val testNodeOnCoordinateJoin: (CoordinateJoin) -> Boolean = {
            var returnVal: Boolean;
            if (it.first.x == node.x) {
                logger.debug { "Inspecting candidate $node Y val between ${it.first.y} and ${it.second.y}" };
                returnVal = (node.y in it.first.y..it.second.y) || (node.y in it.second.y..it.first.y);
            } else {
                logger.debug { "Inspecting candidate $node X val between ${it.first.x} and ${it.second.x}" };
                returnVal = (node.x in it.first.x..it.second.x) || (node.x in it.second.x..it.first.x);
            }
            returnVal
        }

        return deepCandidates.any(testNodeOnCoordinateJoin);
    }

    fun expandPath(nodes: List<Coordinate>, nodeCircuit: Set<CoordinateJoin>, expansionCount: Long, maxPoint: Coordinate): List<Coordinate> {
        val expandedPath = mutableListOf<Coordinate>();

        for (node in nodes) {
            val leftAdjacent = testNodeOnCircuit(Coordinate(node.x - 1, node.y), nodeCircuit);
            val upAdjacent = testNodeOnCircuit(Coordinate(node.x, node.y - 1), nodeCircuit);

            // Interrogate horizontal intersections
            var lIntersectCount = countIntersectsAgainstPolygon(
                CoordinateJoin(Coordinate(-1, node.y), node),
                nodeCircuit);
            var rIntersectCount = countIntersectsAgainstPolygon(
                CoordinateJoin(node, Coordinate(maxPoint.x, node.y)),
                nodeCircuit);

            val rightFacingNode = if (lIntersectCount == rIntersectCount) {
                leftAdjacent;
            } else {
                rIntersectCount % 2 == 1;
            }
            logger.debug { "Inspecting $node horizontally shows it is facing to the right: $rightFacingNode ($lIntersectCount/$rIntersectCount)" }

            // Ask if node is vertically towards the bottom
            lIntersectCount = countIntersectsAgainstPolygon(
                CoordinateJoin(Coordinate(node.x, -1), node),
                nodeCircuit);
            rIntersectCount = countIntersectsAgainstPolygon(
                CoordinateJoin(node, Coordinate(node.x, maxPoint.y)),
                nodeCircuit);

            val downFacingNode: Boolean = if (lIntersectCount == rIntersectCount) {
                upAdjacent;
            } else {
                rIntersectCount % 2 == 1;
            }
            logger.debug { "Inspecting $node vertically shows it is facing downwards: $downFacingNode ($lIntersectCount/$rIntersectCount)" }

            val horizontalModifier = if (rightFacingNode) expansionCount else expansionCount * -1;
            val verticalModifier = if (downFacingNode) expansionCount else expansionCount * -1;

            expandedPath.add(Coordinate(node.x + horizontalModifier, node.y + verticalModifier));
        }

        return expandedPath
    }

    fun testLineInPolygon(lineToTest: CoordinateJoin, pathCoordinates: Set<CoordinateJoin>): Boolean {
        return pathCoordinates.none {
            logger.debug { "Testing $it against $lineToTest - intersects: ${it.intersects(lineToTest)}"};
            it.intersects(lineToTest)
        };
    }

    fun countIntersectsAgainstPolygon(lineToTest: CoordinateJoin, pathCoordinates: Set<CoordinateJoin>): Int {
        val intersectingList = pathCoordinates.filter { it.intersects(lineToTest) };
        val joinSets = mutableListOf<MutableSet<Coordinate>>();

        intersectingList.forEach { intersection -> run {
            val added = false;
            val existingJoins = joinSets.filter { joinSeries -> joinSeries.contains(intersection.first) || joinSeries.contains(intersection.second) };
            if (existingJoins.isEmpty()) {
                joinSets.add(mutableSetOf(intersection.first, intersection.second));
            } else if (existingJoins.size == 1) {
                existingJoins[0].add(intersection.first);
                existingJoins[0].add(intersection.second);
            } else if (existingJoins.size == 2) {
                existingJoins[0].addAll(existingJoins[1]);
                joinSets.remove(existingJoins[1]);
            }
        } }

        logger.debug { "Reduced ${intersectingList.size} total intersections to ${joinSets.size} collapsed intersections for $lineToTest" };

        return joinSets.size;
    }

    fun testPolygonInsidePolygon(firstCorner: Coordinate, secondCorner: Coordinate, pathCoordinates: Set<CoordinateJoin>): Boolean {
        logger.debug { "Check requested for polygon $firstCorner -> $secondCorner to confirm it is within main shape" };

        val edges = setOf(
            CoordinateJoin(firstCorner, Coordinate(firstCorner.x, secondCorner.y)),
            CoordinateJoin(Coordinate(firstCorner.x, secondCorner.y), secondCorner),
            CoordinateJoin(secondCorner, Coordinate(secondCorner.x, firstCorner.y)),
            CoordinateJoin(Coordinate(secondCorner.x, firstCorner.y), firstCorner),
        );
        return edges.all { edge -> testLineInPolygon(edge, pathCoordinates) };
    }

    fun calculateAreas(nodes: List<Coordinate>, limitedMode: Boolean): List<NodeArea> {
        val nodeAreas = mutableListOf<NodeArea>();

        val nodePath = fillNodePath(nodes);
        val maxX = nodes.maxBy(Coordinate::x).x + 1;
        val maxY = nodes.maxBy(Coordinate::y).y + 1;
        val boundaryPath = fillNodePath(expandPath(nodes, nodePath, 1, Coordinate(maxX, maxY)));

        for (i in 0 until nodes.size) {
            for (j in i+1 until nodes.size) {
                val addPair = if (limitedMode)
                    testPolygonInsidePolygon(nodes[i], nodes[j], boundaryPath)
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
