package uk.j4numbers.adventofcode.advent_2025.day_09

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_09.utils.FileIO
import uk.j4numbers.adventofcode.advent_2025.day_09.utils.SpaceCalculator
import java.io.File

val logger = KotlinLogging.logger {}

fun testArgs(args: Array<String>) {
    require(args.size > 1) { "Number of arguments must be at least 2." };
    require(File(args[0]).exists()) { "Please provide a file that exists." };
    require(listOf("unlimited", "limit").contains(args[1])) { "The mode must be either 'limited' or 'unlimited'." };
}

fun main(args: Array<String>) {
    testArgs(args);

    val fileParser = FileIO();

    val fileContents = fileParser.readFile(args[0]);
    logger.info { "Discovered ${fileContents.size} coordinates within input file" };

    val mode = args[1] == "limit";
    logger.info { "Running in limited mode - $mode" };
    val spaceCalculator = SpaceCalculator();
    val distanceMap = spaceCalculator.calculateAreas(fileContents, mode);

    if (distanceMap.isNotEmpty()) {
        logger.info { "Calculated largest rectangle out of ${distanceMap.size} options - ${distanceMap[0]}" };
    }
}
