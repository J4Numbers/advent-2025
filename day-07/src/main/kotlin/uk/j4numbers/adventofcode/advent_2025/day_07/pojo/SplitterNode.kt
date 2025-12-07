package uk.j4numbers.adventofcode.advent_2025.day_07.pojo

class SplitterNode(
    val location: Coordinate,
    val visitedCount: Int = 0,
) {
    override fun toString(): String {
        return String.format("SplitterNode[$location, $visitedCount]");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is SplitterNode) return false;

        return this.location == other.location && this.visitedCount == other.visitedCount;
    }

    override fun hashCode(): Int {
        return 31 * location.hashCode() + visitedCount;
    }
}
