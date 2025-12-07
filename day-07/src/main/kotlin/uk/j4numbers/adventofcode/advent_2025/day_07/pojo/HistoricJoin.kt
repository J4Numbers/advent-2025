package uk.j4numbers.adventofcode.advent_2025.day_07.pojo

class HistoricJoin(
    val from: Coordinate,
    val to: Coordinate,
) {
    override fun toString(): String {
        return String.format("HistoricJoin(from=$from, to=$to)");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is HistoricJoin) return false;

        return this.to == other.to && this.from == other.from;
    }

    override fun hashCode(): Int {
        return 31 * to.hashCode() + from.hashCode();
    }
}
