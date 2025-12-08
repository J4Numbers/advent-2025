package uk.j4numbers.adventofcode.advent_2025.day_08.utils

import mu.KotlinLogging
import uk.j4numbers.adventofcode.advent_2025.day_08.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_08.pojo.NodeDistance

class NodeCalculator {
    val logger = KotlinLogging.logger {};

    fun generateDistancesBetweenNodes(nodeList: List<Coordinate>): List<NodeDistance> {
        val nodeDistances = mutableListOf<NodeDistance>();

        for (i in 0 until nodeList.size) {
            for (j in i+1 until nodeList.size) {
                val newNode = NodeDistance(
                    Pair(nodeList[i], nodeList[j]),
                    nodeList[i].minus(nodeList[j]).toDistance(),
                );
                logger.debug { "Registering distance between ${newNode.between} for distance ${newNode.distance}" };
                nodeDistances.add(newNode);
            }
        }

        return nodeDistances.sortedWith(compareBy(NodeDistance::distance));
    }

    fun joinNodeNetworks(ongoingNodeNetwork: MutableList<List<Coordinate>>, unseenNodes: MutableList<Coordinate>, nodeJoin: NodeDistance) {
        logger.debug { "Including connection ${nodeJoin.between} (${nodeJoin.distance}) into the ongoing graphs..." };

        val foundNetworkLeft: List<Coordinate>;
        if (unseenNodes.contains(nodeJoin.between.first)) {
            logger.debug { "Previously unseen node of ${nodeJoin.between.first}. Starting new graph." };
            foundNetworkLeft = mutableListOf(nodeJoin.between.first);
            unseenNodes.remove(nodeJoin.between.first);
        } else {
            logger.debug { "Node of ${nodeJoin.between.first} already exists in a graph. Retrieving..." };
            foundNetworkLeft = ongoingNodeNetwork.filter { network -> network.contains(nodeJoin.between.first) }[0].toMutableList();
            ongoingNodeNetwork.remove(foundNetworkLeft);
        }

        val foundNetworkRight: List<Coordinate>;
        if (unseenNodes.contains(nodeJoin.between.second)) {
            logger.debug { "Previously unseen node of ${nodeJoin.between.second}. Starting new graph." };
            foundNetworkRight = mutableListOf(nodeJoin.between.second);
            unseenNodes.remove(nodeJoin.between.second);
        } else if (foundNetworkLeft.contains(nodeJoin.between.second)) {
            logger.debug { "Node of ${nodeJoin.between.second} already exists alongside ${nodeJoin.between.first}. Ignoring..." };
            foundNetworkRight = mutableListOf();
        } else {
            logger.debug { "Node of ${nodeJoin.between.second} already exists in a graph. Retrieving..." };
            foundNetworkRight = ongoingNodeNetwork.filter { network -> network.contains(nodeJoin.between.second) }[0].toMutableList();
            ongoingNodeNetwork.remove(foundNetworkRight);
        }

        foundNetworkRight.addAll(foundNetworkLeft);
        logger.debug { "Network containing ${nodeJoin.between} is now of size ${foundNetworkRight.size}." };
        ongoingNodeNetwork.add(foundNetworkRight);
    }

    fun reduceNodeDistancesToGraphs(distanceList: List<NodeDistance>, allNodes: List<Coordinate>, maxComparisons: Int): List<List<Coordinate>> {
        val networks = mutableListOf<List<Coordinate>>();
        val unseenNodes = allNodes.toMutableList();

        distanceList.subList(0, maxComparisons).forEach { nodeToJoin -> run {
            joinNodeNetworks(networks, unseenNodes, nodeToJoin)
        } }

        logger.debug { "All other nodes have not seen any connections. ${unseenNodes.size} nodes were unseen." };
        unseenNodes.forEach { node -> run { networks.add(listOf(node)) }};

        return networks;
    }

    fun joinNodeDistancesUntilOneGraph(distanceList: List<NodeDistance>, allNodes: List<Coordinate>): NodeDistance? {
        val networks = mutableListOf<List<Coordinate>>();
        val unseenNodes = allNodes.toMutableList();

        var finalNodeDistance: NodeDistance? = null;

        for (nodeToJoin in distanceList) {
            joinNodeNetworks(networks, unseenNodes, nodeToJoin);
            if (unseenNodes.isEmpty() && networks.size == 1) {
                finalNodeDistance = nodeToJoin;
                break;
            }
        }

        return finalNodeDistance;
    }

    fun reduceNodeNetworksToValue(nodeNetwork: List<List<Coordinate>>, topCount: Int): Long {
        val topNetworks = nodeNetwork.sortedByDescending { it.size }
            .subList(0, topCount);

        topNetworks.forEach { network -> run {
            logger.debug { "Top network identified containing ${network.size} nodes" };
        } }

        return topNetworks.fold(1L) { acc, network -> acc * network.size };
    }
}
