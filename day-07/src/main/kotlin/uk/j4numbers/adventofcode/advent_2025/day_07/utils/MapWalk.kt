package uk.j4numbers.adventofcode.advent_2025.day_07.utils

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.HistoricJoin
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.HistoricWeight
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.LightMap
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.WalkDetails

class MapWalk {
    private val logger = KotlinLogging.logger {}

    fun walk(map: LightMap) : WalkDetails {
        val completeHistory = mutableSetOf<HistoricJoin>()
        val exitNodes = mutableSetOf<Coordinate>();
        val visitedNodes = mutableSetOf<Coordinate>();
        val walkingNodes = mutableSetOf<Coordinate>(map.startPoint);
        val splitterCoords = map.splitters.map { splitterNode -> splitterNode.location }

        var uniqueSplits = 0;

        while (!walkingNodes.isEmpty()) {
            val inspect: Coordinate = walkingNodes.elementAt(0);

            logger.debug { "$inspect under inspection..." }

            if (inspect.y >= map.maxHeight) {
                // Escaped the map. Add to our exit count.
                exitNodes.add(inspect);
                logger.debug { "$inspect escaped the map! New exit count of ${exitNodes.size}..." }
            } else {
                if (splitterCoords.contains(inspect)) {
                    logger.debug { "Splitter discovered at $inspect! Attempting split..." }
                    uniqueSplits += 1;

                    // Do split left
                    if (inspect.x > 0) {
                        val leftNode = Coordinate(inspect.x - 1, inspect.y + 1);
                        completeHistory.add(HistoricJoin(inspect, leftNode))
                        if (!visitedNodes.contains(leftNode) && !walkingNodes.contains(leftNode)) {
                            logger.debug { "(Left split from $inspect) - Discovered new node to inspect at $leftNode." }
                            walkingNodes.add(leftNode);
                        } else {
                            logger.debug { "Already visited $leftNode. Skipping." }
                        }
                    }
                    // Do split right
                    if (inspect.x < map.maxWidth - 1) {
                        val rightNode = Coordinate(inspect.x + 1, inspect.y + 1);
                        completeHistory.add(HistoricJoin(inspect, rightNode));
                        if (!visitedNodes.contains(rightNode) && !walkingNodes.contains(rightNode)) {
                            logger.debug { "(Right split from $inspect) - Discovered new node to inspect at $rightNode." }
                            walkingNodes.add(rightNode);
                        } else {
                            logger.debug { "Already visited $rightNode. Skipping." }
                        }
                    }
                } else {
                    // Walk down
                    val nextNode = Coordinate(inspect.x, inspect.y + 1);
                    completeHistory.add(HistoricJoin(inspect, nextNode));
                    if (!visitedNodes.contains(nextNode) && !walkingNodes.contains(nextNode)) {
                        logger.debug { "(Walk down from $inspect) - Discovered new node to inspect at $nextNode." }
                        walkingNodes.add(nextNode);
                    }
                }
            }
            visitedNodes.add(inspect);
            walkingNodes.remove(inspect);
        }

        return WalkDetails(completeHistory, exitNodes, uniqueSplits);
    }

    fun calculateCombinations(endNodes: Set<Coordinate>, historicJourney: Set<HistoricJoin>): Set<HistoricWeight> {
        val weights = mutableMapOf<Coordinate, HistoricWeight>();
        val followNodes = endNodes.map { node -> HistoricWeight(node, 1L) }.toMutableSet();
        var focusIdx = endNodes.elementAt(0).y;

        while (focusIdx >= 0) {
            val inspect = followNodes.filter { node -> node.location.y == focusIdx }.elementAt(0);

            // Ensure our current node is tracked and/or updated
            if (weights.containsKey(inspect.location)) {
                logger.debug { "Updating existing backtrack point ${inspect.location} to new weight ${weights[inspect.location]?.weight?.plus(inspect.weight)}" };
                weights[inspect.location]?.weight += inspect.weight;
            } else {
                logger.debug { "Found new backtrack point ${inspect.location} with initial weight of ${inspect.weight}" };
                weights[inspect.location] = inspect
            }

            followNodes.remove(inspect);

            if (followNodes.none { node -> node.location.y == focusIdx }) {
                weights.keys.filter { coord -> coord.y == focusIdx }.forEach { k -> run {
                    // Find joins which we have not already seen
                    historicJourney.filter { join -> join.to == k }.forEach { join ->
                        run {
                            logger.debug { "Planning new check of ${join.from} (from ${join.to}) with initial weight ${weights[k]?.weight}" };
                            followNodes.add(HistoricWeight(join.from, weights[k]!!.weight));
                        }
                    }
                } }
                focusIdx -= 1;
            }
        }

        return weights.values.toSet();
    }
}
