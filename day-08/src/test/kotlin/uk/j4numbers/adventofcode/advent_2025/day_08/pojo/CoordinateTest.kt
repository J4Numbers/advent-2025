package uk.j4numbers.adventofcode.advent_2025.day_08.pojo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual

private class CoordinateTupleTest(
    val first: Coordinate,
    val second: Coordinate,
    val expected: Coordinate,
)

private class DistanceTest(
    val vector: Coordinate,
    val expected: Long,
)

class CoordinateTest: FunSpec ({
    test("Subtracting Coordinates returns an accurate vector") {
        val testTuples = listOf(
            CoordinateTupleTest(Coordinate(5, 5, 5), Coordinate(5, 5, 5), Coordinate(0, 0, 0)),
            CoordinateTupleTest(Coordinate(10, 15, 20), Coordinate(5, 3, 1), Coordinate(5, 12, 19)),
            CoordinateTupleTest(Coordinate(5, 3, 1), Coordinate(10, 15, 20), Coordinate(-5, -12, -19)),
        );

        testTuples.forEach { test -> run {
            test.first.minus(test.second) shouldBeEqual test.expected;
        } }
    }
    test("Using toDistance results in an accurate distance") {
        val testTuples = listOf(
            DistanceTest(Coordinate(0, 0, 0), 0L),
            DistanceTest(Coordinate(5, 0, 0), 25L),
            DistanceTest(Coordinate(3, 4, 0), 25L),
            DistanceTest(Coordinate(0, 3, 4), 25L),
            DistanceTest(Coordinate(3, 0, 4), 25L),
            DistanceTest(Coordinate(3, 4, 12), 169L),
        );

        testTuples.forEach { test -> run {
            test.vector.toDistance() shouldBeEqual test.expected;
        } }
    }
})
