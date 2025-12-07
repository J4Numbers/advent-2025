package uk.j4numbers.adventofcode.advent_2025.day_07.pojo

class WalkDetails(
    val history: Set<HistoricJoin>,
    val exitPoints: Set<Coordinate>,
    val uniqueSplits: Int,
) {
    override fun toString(): String {
        return String.format("WalkDetails(history=$history,exitPoints=$exitPoints, uniqueSplits=$uniqueSplits)");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is WalkDetails) return false;

        return this.history.containsAll(other.history) &&
                this.exitPoints.containsAll(other.exitPoints) &&
                this.uniqueSplits == other.uniqueSplits;
    }

    override fun hashCode(): Int {
        return 31 * exitPoints.hashCode() + uniqueSplits;
    }
}
