package uk.j4numbers.adventofcode.advent_2025.day_08.utils

import java.io.File
import java.io.InputStream

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_08.pojo.Coordinate

class FileIO {
    private val logger = KotlinLogging.logger {}

    fun readFile(inputFile: String): List<Coordinate> {
        logger.debug("Reading $inputFile...");
        val inputStream: InputStream = File(inputFile).inputStream()

        val coordinates = mutableListOf<Coordinate>();
        val regex = Regex("^([0-9]+),([0-9]+),([0-9]+)$");

        inputStream.bufferedReader().forEachLine { line ->
            if (line.matches(regex)) {
                val matcher = regex.matchEntire(line)!!;
                coordinates.add(Coordinate(
                    matcher.groups[1]!!.value.toLong(),
                    matcher.groups[2]!!.value.toLong(),
                    matcher.groups[3]!!.value.toLong()));
            }
        }

        return coordinates;
    }
}
