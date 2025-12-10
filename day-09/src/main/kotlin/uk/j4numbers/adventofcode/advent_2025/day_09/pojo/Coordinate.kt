package uk.j4numbers.adventofcode.advent_2025.day_09.pojo

import kotlin.math.absoluteValue

class Coordinate(
    val x: Long,
    val y: Long,
) {
    override fun toString(): String {
        return String.format("Coordinate[$x, $y]");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is Coordinate) return false;

        return this.x == other.x && this.y == other.y;
    }

    override fun hashCode(): Int {
        return 32 * x.toInt() + y.toInt();
    }
}
