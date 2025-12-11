package uk.j4numbers.adventofcode.advent_2025.day_09.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.CoordinateJoin
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.NodeArea

class NodeDistanceTest(
    val first: Coordinate,
    val second: Coordinate,
    val expected: Long,
)

class CoordinateJoinInclusionTest(
    val join: CoordinateJoin,
    val test: Coordinate,
    val expected: Boolean,
)

class LinePathInclusionTest(
    val line: CoordinateJoin,
    val expected: Boolean,
)

class SpaceCalculatorTest: FunSpec({
    test("Calculating area between two nodes is accurate") {
        val testSpecs = listOf(
            NodeDistanceTest(Coordinate(0, 0), Coordinate(5, 5), 36),
            NodeDistanceTest(Coordinate(5, 5), Coordinate(0, 0), 36),
            NodeDistanceTest(Coordinate(0, 0), Coordinate(0, 5), 6),
            NodeDistanceTest(Coordinate(0, 0), Coordinate(0, 0), 1),
        );
        val spaceCalculator = SpaceCalculator();

        testSpecs.forEach { spec -> run {
            spaceCalculator.calculateAreaBetweenNodes(spec.first, spec.second) shouldBeEqual spec.expected;
        } }
    }

    test("Fill node path should return a list of points between two simple points") {
        val inputs = listOf(
            Coordinate(0, 0),
            Coordinate(0, 2),
        );
        val spaceCalculator = SpaceCalculator();

        val points = spaceCalculator.fillNodePath(inputs);

        points shouldHaveSize 2;
        points shouldContain CoordinateJoin(Coordinate(0, 0), Coordinate(0, 2));
        points shouldContain CoordinateJoin(Coordinate(0, 2), Coordinate(0, 0));
    }

    test("Fill node path should return a list of points on a square") {
        val inputs = listOf(
            Coordinate(0, 0),
            Coordinate(0, 2),
            Coordinate(2, 2),
            Coordinate(2, 0),
        );
        val spaceCalculator = SpaceCalculator();

        val points = spaceCalculator.fillNodePath(inputs);

        points shouldHaveSize 4;
        points shouldContain CoordinateJoin(Coordinate(0, 0), Coordinate(0, 2));
        points shouldContain CoordinateJoin(Coordinate(0, 2), Coordinate(2, 2));
        points shouldContain CoordinateJoin(Coordinate(2, 2), Coordinate(2, 0));
        points shouldContain CoordinateJoin(Coordinate(2, 0), Coordinate(0, 0));
    }

    test("Testing value on circuit line returns returns correctly for a range of values") {
        val testSpecs = listOf(
            CoordinateJoinInclusionTest(
                CoordinateJoin(Coordinate(1, 1), Coordinate(10, 1)),
                Coordinate(5, 5), false),
            CoordinateJoinInclusionTest(
                CoordinateJoin(Coordinate(1, 1), Coordinate(10, 1)),
                Coordinate(1, 5), false),
            CoordinateJoinInclusionTest(
                CoordinateJoin(Coordinate(1, 1), Coordinate(10, 1)),
                Coordinate(50, 1), false),
            CoordinateJoinInclusionTest(
                CoordinateJoin(Coordinate(1, 1), Coordinate(10, 1)),
                Coordinate(5, 1), true),
            CoordinateJoinInclusionTest(
                CoordinateJoin(Coordinate(1, 1), Coordinate(10, 1)),
                Coordinate(1, 1), true),
        );
        val spaceCalculator = SpaceCalculator();

        testSpecs.forEach { spec -> run {
            val onCircuit = spaceCalculator.testNodeOnCircuit(spec.test, setOf(spec.join));
            onCircuit shouldBe spec.expected;
        } }
    }

    test("Expand graph should expand simple graph nodes correctly") {
        val nodes = listOf(
            Coordinate(1, 1),
            Coordinate(1, 2),
            Coordinate(2, 2),
            Coordinate(2, 1),
        )
        val circuit = setOf(
            CoordinateJoin(Coordinate(1, 1), Coordinate(1, 2)),
            CoordinateJoin(Coordinate(1, 2), Coordinate(2, 2)),
            CoordinateJoin(Coordinate(2, 2), Coordinate(2, 1)),
            CoordinateJoin(Coordinate(2, 1), Coordinate(1, 1)),
        )
        val spaceCalculator = SpaceCalculator();

        val newNodes = spaceCalculator.expandPath(nodes, circuit, 1, Coordinate(3, 3));

        newNodes shouldHaveSize 4;
        newNodes shouldContain Coordinate(0, 0);
        newNodes shouldContain Coordinate(0, 3);
        newNodes shouldContain Coordinate(3, 3);
        newNodes shouldContain Coordinate(3, 0);
    }

    test("Expand graph should expand concave graph nodes correctly") {
        val nodes = listOf(
            Coordinate(1, 1),
            Coordinate(1, 5),
            Coordinate(3, 5),
            Coordinate(3, 3),
            Coordinate(5, 3),
            Coordinate(5, 1),
        )
        val path = setOf(
            CoordinateJoin(Coordinate(1, 1), Coordinate(1, 5)),
            CoordinateJoin(Coordinate(1, 5), Coordinate(3, 5)),
            CoordinateJoin(Coordinate(3, 5), Coordinate(3, 3)),
            CoordinateJoin(Coordinate(3, 3), Coordinate(5, 3)),
            CoordinateJoin(Coordinate(5, 3), Coordinate(5, 1)),
            CoordinateJoin(Coordinate(5, 1), Coordinate(1, 1)),
        )
        val spaceCalculator = SpaceCalculator();

        val newNodes = spaceCalculator.expandPath(nodes, path, 1, Coordinate(6, 6));

        newNodes shouldHaveSize 6;
        newNodes shouldContain Coordinate(0, 0);
        newNodes shouldContain Coordinate(0, 6);
        newNodes shouldContain Coordinate(4, 6);
        newNodes shouldContain Coordinate(4, 4);
        newNodes shouldContain Coordinate(6, 4);
        newNodes shouldContain Coordinate(6, 0);
    }

    test("Test counting intercepts with a polygon returns simple intersects") {
        val pathPoints = setOf(
            CoordinateJoin(Coordinate(5, 1), Coordinate(5, 10)),
        );
        val lineToTest = CoordinateJoin(Coordinate(1, 5), Coordinate(10, 5));
        val spaceCalculator = SpaceCalculator();

        val intersectCount = spaceCalculator.countIntersectsAgainstPolygon(lineToTest, pathPoints);

        intersectCount shouldBeEqual 1;
    }

    test("Test counting intercepts with a polygon returns one for parallel intersects") {
        val pathPoints = setOf(
            CoordinateJoin(Coordinate(5, 5), Coordinate(10, 5)),
        );
        val lineToTest = CoordinateJoin(Coordinate(1, 5), Coordinate(10, 5));
        val spaceCalculator = SpaceCalculator();

        val intersectCount = spaceCalculator.countIntersectsAgainstPolygon(lineToTest, pathPoints);

        intersectCount shouldBeEqual 1;
    }

    test("Test counting intercepts with a polygon returns one for corner intercepts") {
        val pathPoints = setOf(
            CoordinateJoin(Coordinate(5, 1), Coordinate(5, 5)),
            CoordinateJoin(Coordinate(5, 5), Coordinate(10, 5)),
            CoordinateJoin(Coordinate(10, 5), Coordinate(10, 10)),
        );
        val lineToTest = CoordinateJoin(Coordinate(1, 5), Coordinate(10, 5));
        val spaceCalculator = SpaceCalculator();

        val intersectCount = spaceCalculator.countIntersectsAgainstPolygon(lineToTest, pathPoints);

        intersectCount shouldBeEqual 1;
    }

    test("Test checking line on polygon within a square works for across empty air") {
        val pathPoints = setOf(
            CoordinateJoin(Coordinate(0, 0), Coordinate(0, 4)),
            CoordinateJoin(Coordinate(0, 4), Coordinate(4, 4)),
            CoordinateJoin(Coordinate(4, 4), Coordinate(4, 0)),
            CoordinateJoin(Coordinate(4, 0), Coordinate(0, 0)),
        );
        val spaceCalculator = SpaceCalculator();
        val testSpecs = listOf(
            LinePathInclusionTest(CoordinateJoin(Coordinate(1, 1), Coordinate(3, 1)), true),
            LinePathInclusionTest(CoordinateJoin(Coordinate(1, 2), Coordinate(3, 2)), true),
            LinePathInclusionTest(CoordinateJoin(Coordinate(1, 3), Coordinate(3, 3)), true),
        )

        for (spec in testSpecs) {
            val allInPolygon = spaceCalculator.testLineInPolygon(spec.line, pathPoints);
            allInPolygon shouldBe spec.expected;
        }
    }

    test("Test checking line on polygon within an irregular polygon returns appropriately") {
        // .........
        // .#######.
        // .#.....#.
        // .#.....#.
        // .#..#..#.
        // .#..#..#.
        // .#######.
        // .........
        val pathPoints = setOf(
            CoordinateJoin(Coordinate(1, 1), Coordinate(1, 6)),
            CoordinateJoin(Coordinate(1, 6), Coordinate(4, 6)),
            CoordinateJoin(Coordinate(4, 6), Coordinate(4, 4)),
            CoordinateJoin(Coordinate(4, 6), Coordinate(7, 6)),
            CoordinateJoin(Coordinate(7, 6), Coordinate(7, 1)),
            CoordinateJoin(Coordinate(7, 1), Coordinate(1, 1)),
        );
        val spaceCalculator = SpaceCalculator();
        val testSpecs = listOf(
            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 2), Coordinate(6, 2)), true),
            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 3), Coordinate(6, 3)), true),
            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 4), Coordinate(6, 4)), false),
            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 5), Coordinate(6, 5)), false),
        )

        for (spec in testSpecs) {
            val allInPolygon = spaceCalculator.testLineInPolygon(spec.line, pathPoints);
            allInPolygon shouldBe spec.expected;
        }
    }

    test("Test checking line on polygon in an inverse shape should return false") {
        // .........
        // .#######.
        // .#.....#.
        // .#.....#.
        // .#.....#.
        // .###...#.
        // ...#...#.
        // ...#####.
        // .........
        val pathPoints = setOf(
            CoordinateJoin(Coordinate(1, 1), Coordinate(1, 5)),
            CoordinateJoin(Coordinate(1, 5), Coordinate(3, 5)),
            CoordinateJoin(Coordinate(3, 5), Coordinate(3, 7)),
            CoordinateJoin(Coordinate(3, 7), Coordinate(7, 7)),
            CoordinateJoin(Coordinate(7, 7), Coordinate(7, 1)),
            CoordinateJoin(Coordinate(7, 1), Coordinate(1, 1)),
        );
        val spaceCalculator = SpaceCalculator();
        val testSpecs = listOf(
            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 2), Coordinate(6, 2)), true),
            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 3), Coordinate(6, 3)), true),
            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 4), Coordinate(6, 4)), true),

            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 5), Coordinate(6, 5)), false),
            LinePathInclusionTest(CoordinateJoin(Coordinate(2, 6), Coordinate(6, 6)), false),
        )

        for (spec in testSpecs) {
            val allInPolygon = spaceCalculator.testLineInPolygon(spec.line, pathPoints);
            allInPolygon shouldBe spec.expected;
        }
    }

    test("Test generating space area from two inputs should return a single size in unlimited mode") {
        val inputs = listOf(
            Coordinate(1, 1),
            Coordinate(2, 2)
        );
        val spaceCalculator = SpaceCalculator();

        val areas = spaceCalculator.calculateAreas(inputs, false);

        areas shouldHaveSize 1;
        areas shouldContain NodeArea(Pair(Coordinate(1, 1), Coordinate(2, 2)), 4);
    }

    test("Test generating space area from two inputs should return a single size in limited mode") {
        val inputs = listOf(
            Coordinate(1, 1),
            Coordinate(1, 2)
        );
        val spaceCalculator = SpaceCalculator();

        val areas = spaceCalculator.calculateAreas(inputs, true);

        areas shouldHaveSize 1;
        areas shouldContain NodeArea(Pair(Coordinate(1, 1), Coordinate(1, 2)), 2);
    }

    test("Test generating space areas from multiple inputs should return all combinations of sizes with the largest returned first for unlimited mode") {
        val inputs = listOf(
            Coordinate(1, 1),
            Coordinate(2, 2),
            Coordinate(3, 3),
            Coordinate(4, 4),
            Coordinate(5, 5),
        );
        val spaceCalculator = SpaceCalculator();

        val areas = spaceCalculator.calculateAreas(inputs, false);

        areas shouldHaveSize 10;
        areas[0] shouldBeEqual NodeArea(Pair(Coordinate(1, 1), Coordinate(5, 5)), 25);
    }

    test("Test generating space areas from multiple inputs should return a subset of combinations for limited mode") {
        val inputs = listOf(
            Coordinate(1, 1),
            Coordinate(1, 5),
            Coordinate(3, 5),
            Coordinate(3, 10),
            Coordinate(10, 10),
            Coordinate(10, 1),
        );
        val spaceCalculator = SpaceCalculator();

        val areas = spaceCalculator.calculateAreas(inputs, false);

        areas shouldHaveSize 15;
        areas[0] shouldBeEqual NodeArea(Pair(Coordinate(1, 1), Coordinate(10, 10)), 100);
    }
})
