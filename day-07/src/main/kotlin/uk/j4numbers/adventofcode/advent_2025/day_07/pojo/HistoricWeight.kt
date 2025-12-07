package uk.j4numbers.adventofcode.advent_2025.day_07.pojo

class HistoricWeight(
    val location: Coordinate,
    var weight: Long,
) {
    override fun toString(): String {
        return String.format("HistoricWeight($location, weight=$weight)")
    }
}
