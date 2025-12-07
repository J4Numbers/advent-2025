package uk.j4numbers.adventofcode.advent_2025.day_07.pojo

class Coordinate(
    val x: Int,
    val y: Int
) {
    override fun toString(): String {
        return String.format("Coordinate[$x,$y]");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is Coordinate) return false;

        return this.x == other.x && this.y == other.y;
    }

    override fun hashCode(): Int {
        return 32 * x + y
    }
}
