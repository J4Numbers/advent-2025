package uk.j4numbers.adventofcode.advent_2025.day_08.pojo

class NodeDistance(
    val between: Pair<Coordinate, Coordinate>,
    val distance: Long,
) {
    override fun toString(): String {
        return String.format("NodeDistance[<$between>, $distance]");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is NodeDistance) return false;

        return this.between == other.between && this.distance == other.distance;
    }

    override fun hashCode(): Int {
        return 32 * between.hashCode() * distance.toInt();
    }
}
