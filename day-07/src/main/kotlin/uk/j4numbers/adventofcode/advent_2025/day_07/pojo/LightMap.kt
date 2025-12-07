package uk.j4numbers.adventofcode.advent_2025.day_07.pojo

class LightMap(
    val startPoint: Coordinate,
    val splitters: List<SplitterNode> = emptyList(),
    val maxWidth: Int = 0,
    val maxHeight: Int = 0,
) {
    override fun toString(): String {
        return String.format("LightMap[($maxWidth, $maxHeight), $startPoint, $splitters]");
    }
}
