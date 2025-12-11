package uk.j4numbers.adventofcode.advent_2025.day_09.pojo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CoordinateJoinSpec(
    val segmentA: CoordinateJoin,
    val segmentB: CoordinateJoin,
    val intersects: Boolean,
)

class CoordinateJoinTest: FunSpec({
    test("Coordinate joins should report intersections accurately") {
        val testSpecs = listOf(
            CoordinateJoinSpec(
                CoordinateJoin(Coordinate(1, 1), Coordinate(10, 1)),
                CoordinateJoin(Coordinate(1, 2), Coordinate(10, 2)),
                false),
            CoordinateJoinSpec(
                CoordinateJoin(Coordinate(1, 5), Coordinate(10, 5)),
                CoordinateJoin(Coordinate(5, 1), Coordinate(5, 10)),
                true),
            CoordinateJoinSpec(
                CoordinateJoin(Coordinate(1, 10), Coordinate(10, 10)),
                CoordinateJoin(Coordinate(1, 10), Coordinate(1, 1)),
                true),
        );

        testSpecs.forEach { spec -> run {
            spec.segmentA.intersects(spec.segmentB) shouldBe spec.intersects;
        } }
    }
})
