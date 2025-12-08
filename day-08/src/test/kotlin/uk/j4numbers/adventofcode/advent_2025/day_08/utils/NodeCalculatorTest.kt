package uk.j4numbers.adventofcode.advent_2025.day_08.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import uk.j4numbers.adventofcode.advent_2025.day_08.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_08.pojo.NodeDistance

class NodeCalculatorTest: FunSpec ({
    test("Calculating the distances between nodes should return nothing when one node is provided") {
        val nodeCalculator = NodeCalculator();
        val distanceLists = nodeCalculator.generateDistancesBetweenNodes(listOf(Coordinate(0, 0, 0)));

        distanceLists shouldHaveSize 0;
    }

    test("Calculating the distance between two coordinates should return single item") {
        val nodeCalculator = NodeCalculator();
        val distanceLists = nodeCalculator.generateDistancesBetweenNodes(listOf(
            Coordinate(0, 0, 0),
            Coordinate(5, 5, 5),
        ));

        distanceLists shouldHaveSize 1;
        distanceLists[0] shouldBeEqual NodeDistance(
            Pair(Coordinate(0, 0, 0), Coordinate(5, 5, 5)),
            75
        );
    }

    test("Generating distances between several nodes should return a sorted list by the closest distances first") {
        val nodeCalculator = NodeCalculator();
        val distanceLists = nodeCalculator.generateDistancesBetweenNodes(listOf(
            Coordinate(1, 1, 1),
            Coordinate(0, 0, 0),
            Coordinate(0, 4, 10),
            Coordinate(0, 0, 1),
            Coordinate(2, 2, 2),
        ));

        distanceLists shouldHaveSize 10;
        distanceLists[0] shouldBeEqual NodeDistance(
            Pair(Coordinate(0, 0, 0), Coordinate(0, 0, 1)),
            1,
        );
    }

    test("Generating graphs from NodeDistance lists should return a limited number of connections based on shortest paths") {
        val nodeCalculator = NodeCalculator();
        val inputs = listOf(
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(0, 0, 1)), 1),
            NodeDistance(Pair(Coordinate(0, 0, 1), Coordinate(5, 5, 5)), 64),
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(5, 5, 5)), 75),
        );

        val distanceGraphs = nodeCalculator.reduceNodeDistancesToGraphs(inputs, listOf(
            Coordinate(0, 0, 0),
            Coordinate(5, 5, 5),
            Coordinate(0, 0, 1),
        ), 1);

        distanceGraphs shouldHaveSize 2;
        val longGraph = distanceGraphs.firstOrNull { graph -> graph.size == 2 };
        longGraph?.shouldContain(inputs[0].between.first);
        longGraph?.shouldContain(inputs[0].between.second);
        distanceGraphs shouldContain listOf(Coordinate(5, 5, 5));
    }

    test("Generating graphs from NodeDistance lists should join ongoing graphs") {
        val nodeCalculator = NodeCalculator();
        val inputs = listOf(
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(0, 0, 1)), 1),
            NodeDistance(Pair(Coordinate(0, 0, 1), Coordinate(10, 9, 9)), 245),
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(10, 9, 9)), 262),
        );

        val distanceGraphs = nodeCalculator.reduceNodeDistancesToGraphs(inputs, listOf(
            Coordinate(0, 0, 0),
            Coordinate(0, 0, 1),
            Coordinate(10, 9, 9),
        ), 2);

        distanceGraphs shouldHaveSize 1;
        distanceGraphs[0] shouldHaveSize 3;
        distanceGraphs[0] shouldContain Coordinate(0, 0, 0);
        distanceGraphs[0] shouldContain Coordinate(0, 0, 1);
        distanceGraphs[0] shouldContain Coordinate(10, 9, 9);
    }

    test("Duplicated node inclusion in existing circuits should not be re-added to existing graphs") {
        val nodeCalculator = NodeCalculator();
        val inputs = listOf(
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(0, 0, 1)), 1),
            NodeDistance(Pair(Coordinate(0, 0, 1), Coordinate(10, 9, 9)), 245),
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(10, 9, 9)), 262),
        );

        val distanceGraphs = nodeCalculator.reduceNodeDistancesToGraphs(inputs, listOf(
            Coordinate(0, 0, 0),
            Coordinate(0, 0, 1),
            Coordinate(10, 9, 9),
        ), 3);

        distanceGraphs shouldHaveSize 1;
        distanceGraphs[0] shouldHaveSize 3;
        distanceGraphs[0] shouldContain Coordinate(0, 0, 0);
        distanceGraphs[0] shouldContain Coordinate(0, 0, 1);
        distanceGraphs[0] shouldContain Coordinate(10, 9, 9);
    }

    test("Generating graphs from NodeDistance lists should join partial graphs") {
        val nodeCalculator = NodeCalculator();
        val inputs = listOf(
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(0, 0, 1)), 1),
            NodeDistance(Pair(Coordinate(10, 10, 10), Coordinate(10, 9, 9)), 2),
            NodeDistance(Pair(Coordinate(0, 0, 1), Coordinate(10, 9, 9)), 245),
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(10, 9, 9)), 262),
            NodeDistance(Pair(Coordinate(0, 0, 1), Coordinate(10, 10, 10)), 281),
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(10, 10, 10)), 300),
        );

        val distanceGraphs = nodeCalculator.reduceNodeDistancesToGraphs(inputs, listOf(
            Coordinate(0, 0, 0),
            Coordinate(0, 0, 1),
            Coordinate(10, 9, 9),
            Coordinate(10, 10, 10),
        ), 3);

        distanceGraphs shouldHaveSize 1;
        distanceGraphs[0] shouldHaveSize 4;
        distanceGraphs[0] shouldContain Coordinate(0, 0, 0);
        distanceGraphs[0] shouldContain Coordinate(0, 0, 1);
        distanceGraphs[0] shouldContain Coordinate(10, 9, 9);
        distanceGraphs[0] shouldContain Coordinate(10, 10, 10);
    }

    test("Generating joined graph from NodeDistance lists should return last join point") {
        val nodeCalculator = NodeCalculator();
        val inputs = listOf(
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(0, 0, 1)), 1),
            NodeDistance(Pair(Coordinate(10, 10, 10), Coordinate(10, 9, 9)), 2),
            NodeDistance(Pair(Coordinate(0, 0, 1), Coordinate(10, 9, 9)), 245),
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(10, 9, 9)), 262),
            NodeDistance(Pair(Coordinate(0, 0, 1), Coordinate(10, 10, 10)), 281),
            NodeDistance(Pair(Coordinate(0, 0, 0), Coordinate(10, 10, 10)), 300),
        );

        val distanceGraphs = nodeCalculator.joinNodeDistancesUntilOneGraph(inputs, listOf(
            Coordinate(0, 0, 0),
            Coordinate(0, 0, 1),
            Coordinate(10, 9, 9),
            Coordinate(10, 10, 10),
        ));

        distanceGraphs.shouldNotBeNull();
        distanceGraphs shouldBeEqual NodeDistance(Pair(Coordinate(0, 0, 1), Coordinate(10, 9, 9)), 245);
    }

    test("Reducing network graphs down to a value is accurate for disconnected graphs") {
        val nodeCalculator = NodeCalculator();
        val inputs = listOf(
            listOf(Coordinate(0, 0, 0)),
            listOf(Coordinate(0, 0, 1)),
            listOf(Coordinate(0, 0, 2)),
        );

        val graphValue = nodeCalculator.reduceNodeNetworksToValue(inputs, 3);

        graphValue shouldBeEqual 1L;
    }

    test("Reducing network graphs down to a value should take the largest graph first") {
        val nodeCalculator = NodeCalculator();
        val inputs = listOf(
            listOf(Coordinate(0, 0, 0), Coordinate(1, 0, 0)),
            listOf(Coordinate(0, 0, 1), Coordinate(1, 0, 1), Coordinate(2, 0, 1)),
            listOf(Coordinate(0, 0, 2)),
        );

        val graphValue = nodeCalculator.reduceNodeNetworksToValue(inputs, 1);

        graphValue shouldBeEqual 3L;
    }

    test("Reducing network graphs down to a value is accurate for larger graphs") {
        val nodeCalculator = NodeCalculator();
        val inputs = listOf(
            listOf(Coordinate(0, 0, 0), Coordinate(1, 0, 0)),
            listOf(Coordinate(0, 0, 1), Coordinate(2, 0, 0)),
            listOf(Coordinate(0, 0, 2)),
        );

        val graphValue = nodeCalculator.reduceNodeNetworksToValue(inputs, 3);

        graphValue shouldBeEqual 4L;
    }
})
