package uk.j4numbers.adventofcode.advent_2025.day_08

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_08.utils.FileIO
import uk.j4numbers.adventofcode.advent_2025.day_08.utils.NodeCalculator
import java.io.File

val logger = KotlinLogging.logger {}

fun testArgs(args: Array<String>) {
    require(args.isNotEmpty()) { "Number of arguments must be at least 2." };
    require(File(args[0]).exists()) { "Please provide a file that exists." };
    require(listOf("limit", "join").contains(args[1])) { "Mode must be set to 'limit' or 'join'."}
    if (args[1] == "limit") {
        require(args[2].toIntOrNull() != null) { "Please ensure the third option is a number." }
        require(args[2].toInt() > 0) { "Please ensure the third option is a number above 0." }
    }
}

fun main(args: Array<String>) {
    testArgs(args);

    val fileParser = FileIO();

    val fileContents = fileParser.readFile(args[0]);
    logger.info { "Discovered ${fileContents.size} coordinates within input file" };

    val nodeCalculator = NodeCalculator();
    val nodeDistances = nodeCalculator.generateDistancesBetweenNodes(fileContents);
    logger.info { "Generated ${nodeDistances.size} new distance combinations for ${fileContents.size} points" };

    when (args[1]) {
        "limit" -> {
            val maxComparisons = args[2].toInt();
            logger.info { "Generating a set of node graphs from the shortest $maxComparisons distances..." };
            val graphSets = nodeCalculator.reduceNodeDistancesToGraphs(nodeDistances, fileContents, maxComparisons);
            logger.info { "Generated ${graphSets.size} graphs from provided inputs" };

            val graphValue = nodeCalculator.reduceNodeNetworksToValue(graphSets, 3)
            logger.info { "The value of all graphs was calculated into $graphValue" };
        }
        "join" -> {
            logger.info { "Generating full graph until all points are joined..." };
            val lastJoin = nodeCalculator.joinNodeDistancesUntilOneGraph(nodeDistances, fileContents);

            logger.info { "Last join to generate a full graph was between ${lastJoin!!.between.first} and ${lastJoin.between.second}."};
            logger.info { "Last join value is ${lastJoin!!.between.first.x * lastJoin.between.second.x}"};
        }
        else -> {
            logger.warn { "Invalid mode presented. Must be 'join' or 'limit'." };
        }
    }
}
