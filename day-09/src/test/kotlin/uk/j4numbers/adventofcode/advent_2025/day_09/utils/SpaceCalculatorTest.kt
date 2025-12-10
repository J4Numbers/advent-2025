package uk.j4numbers.adventofcode.advent_2025.day_09.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.NodeArea

class NodeDistanceTest(
    val first: Coordinate,
    val second: Coordinate,
    val expected: Long,
)

class LinePathInclusionTest(
    val startPoint: Coordinate,
    val endX: Long,
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

        points shouldHaveSize 3;
        points shouldContain Coordinate(0, 0);
        points shouldContain Coordinate(0, 1);
        points shouldContain Coordinate(0, 2);
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

        points shouldHaveSize 8;
        points shouldContain Coordinate(0, 0);
        points shouldContain Coordinate(0, 1);
        points shouldContain Coordinate(0, 2);
        points shouldContain Coordinate(1, 2);
        points shouldContain Coordinate(2, 2);
        points shouldContain Coordinate(2, 1);
        points shouldContain Coordinate(2, 0);
        points shouldContain Coordinate(1, 0);
    }

    test("Test checking line on polygon within a square works for across empty air") {
        val pathPoints = listOf(
            Coordinate(0, 0),
            Coordinate(0, 1),
            Coordinate(0, 2),
            Coordinate(1, 2),
            Coordinate(2, 2),
            Coordinate(2, 1),
            Coordinate(2, 0),
            Coordinate(1, 0),
        );
        val spaceCalculator = SpaceCalculator();
        val testSpecs = listOf(
            LinePathInclusionTest(Coordinate(0, 0), 2, true),
            LinePathInclusionTest(Coordinate(0, 1), 2, true),
            LinePathInclusionTest(Coordinate(0, 2), 2, true)
        )

        for (spec in testSpecs) {
            val allInPolygon = spaceCalculator.testLineInPolygon(spec.startPoint, spec.endX, pathPoints, 2);
            allInPolygon shouldBe spec.expected;
        }
    }

    test("Test checking line on polygon within an irregular polygon returns appropriately") {
        // .......
        // .#####.
        // .#...#.
        // .#####.
        // .##.##.
        // .##.##.
        // .......
        val pathPoints = listOf(
            Coordinate(0, 0), Coordinate(0, 1), Coordinate(0, 2),
            Coordinate(0, 3), Coordinate(0, 4), Coordinate(1, 4),
            Coordinate(1, 3), Coordinate(1, 2), Coordinate(2, 2),
            Coordinate(3, 2), Coordinate(3, 3), Coordinate(3, 4),
            Coordinate(4, 4), Coordinate(4, 3), Coordinate(4, 2),
            Coordinate(4, 1), Coordinate(4, 0), Coordinate(3, 0),
            Coordinate(2, 0), Coordinate(1, 0),
        );
        val spaceCalculator = SpaceCalculator();
        val testSpecs = listOf(
            LinePathInclusionTest(Coordinate(0, 0), 4, true),
            LinePathInclusionTest(Coordinate(0, 1), 4, true),
            LinePathInclusionTest(Coordinate(0, 2), 4, true),
            LinePathInclusionTest(Coordinate(0, 3), 4, false),
            LinePathInclusionTest(Coordinate(0, 4), 4, false),
        )

        for (spec in testSpecs) {
            val allInPolygon = spaceCalculator.testLineInPolygon(spec.startPoint, spec.endX, pathPoints, 2);
            allInPolygon shouldBe spec.expected;
        }
    }

    test("Test checking line on polygon in an inverse shape should return false") {
        // .......
        // .#####.
        // .#...#.
        // .###.#.
        // ...#.#.
        // ...###.
        // .......
        val pathPoints = listOf(
            Coordinate(0, 0), Coordinate(0, 1), Coordinate(0, 2),
            Coordinate(1, 2), Coordinate(2, 2), Coordinate(2, 3),
            Coordinate(2, 4), Coordinate(3, 4), Coordinate(4, 4),
            Coordinate(4, 3), Coordinate(4, 2), Coordinate(4, 1),
            Coordinate(4, 0), Coordinate(3, 0), Coordinate(2, 0),
            Coordinate(1, 0),
        );
        val spaceCalculator = SpaceCalculator();
        val testSpecs = listOf(
            LinePathInclusionTest(Coordinate(0, 2), 2, true),
            LinePathInclusionTest(Coordinate(0, 3), 2, false),
            LinePathInclusionTest(Coordinate(0, 4), 2, false),

            LinePathInclusionTest(Coordinate(0, 2), 4, true),
            LinePathInclusionTest(Coordinate(0, 3), 4, false),
            LinePathInclusionTest(Coordinate(0, 4), 4, false),
        )

        for (spec in testSpecs) {
            val allInPolygon = spaceCalculator.testLineInPolygon(spec.startPoint, spec.endX, pathPoints, 4);
            allInPolygon shouldBe spec.expected;
        }
    }

    test("Test generating space area from no inputs returns an empty list in unlimited mode") {
        val inputs = emptyList<Coordinate>();
        val spaceCalculator = SpaceCalculator();

        val areas = spaceCalculator.calculateAreas(inputs, false);

        areas shouldHaveSize 0;
    }

    test("Test generating space area from no inputs returns an empty list in limited mode") {
        val inputs = emptyList<Coordinate>();
        val spaceCalculator = SpaceCalculator();

        val areas = spaceCalculator.calculateAreas(inputs, true);

        areas shouldHaveSize 0;
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

        areas shouldHaveSize 11;
        areas[0] shouldBeEqual NodeArea(Pair(Coordinate(3, 10), Coordinate(10, 1)), 63);
    }
})
