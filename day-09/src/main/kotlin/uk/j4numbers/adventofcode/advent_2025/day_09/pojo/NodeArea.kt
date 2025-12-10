package uk.j4numbers.adventofcode.advent_2025.day_09.pojo

class NodeArea(
    val between: Pair<Coordinate, Coordinate>,
    val area: Long,
) {
    override fun toString(): String {
        return String.format("NodeDistance[<$between>, $area]");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is NodeArea) return false;

        return this.between == other.between && this.area == other.area;
    }

    override fun hashCode(): Int {
        return 32 * between.hashCode() * area.toInt();
    }
}
