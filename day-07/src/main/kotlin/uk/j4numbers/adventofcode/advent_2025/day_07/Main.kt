package uk.j4numbers.adventofcode.advent_2025.day_07

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_07.utils.FileIO
import uk.j4numbers.adventofcode.advent_2025.day_07.utils.MapWalk
import java.io.File

val logger = KotlinLogging.logger {}

fun testArgs(args: Array<String>) {
    require(args.isNotEmpty()) { "Number of arguments must be at least 1." };
    require(File(args[0]).exists()) { "Please provide a file that exists." };
}

fun main(args: Array<String>) {
    testArgs(args);

    val fileParser = FileIO();

    val fileContents = fileParser.readFile(args[0]);
    logger.info { "Discovered map of height ${fileContents.maxHeight} with ${fileContents.splitters.size} splitters"};

    val mapWalker = MapWalk();
    val walkDetails = mapWalker.walk(fileContents);
    logger.info { "Discovered ${walkDetails.exitPoints.size} exit points from map with ${walkDetails.uniqueSplits} unique splits" };

    val combinationTrain = mapWalker.calculateCombinations(walkDetails.exitPoints, walkDetails.history);
    val topNode = combinationTrain.filter { weight -> weight.location.y == 0 }[0];
    logger.info { "Discovered ${topNode.weight} possible combinations within this map" };
}
