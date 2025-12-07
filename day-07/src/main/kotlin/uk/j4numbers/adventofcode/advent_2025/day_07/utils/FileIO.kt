package uk.j4numbers.adventofcode.advent_2025.day_07.utils

import java.io.File
import java.io.InputStream

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.LightMap
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.SplitterNode

class FileIO {
    private val logger = KotlinLogging.logger {}

    fun readFile(inputFile: String): LightMap {
        logger.debug("Reading $inputFile...");
        val inputStream: InputStream = File(inputFile).inputStream()

        var startCoordinate = Coordinate(-1, -1);
        val splitterList = mutableListOf<SplitterNode>()
        var yIdx = 0;
        var xIdx = 0;

        inputStream.bufferedReader().forEachLine { line ->
            if (line.matches(Regex("^[.^S]+$"))) {
                xIdx = 0;
                line.chunked(1).forEach { char ->
                    when (char) {
                        "^" -> splitterList.add(SplitterNode(Coordinate(xIdx, yIdx), 0))
                        "S" -> startCoordinate = Coordinate(xIdx, yIdx);
                    }
                    xIdx += 1
                }
                yIdx += 1
            }
        }

        return LightMap(startCoordinate, splitterList, xIdx, yIdx);
    }
}
