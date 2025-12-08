package uk.j4numbers.adventofcode.advent_2025.day_07.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.HistoricJoin
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.LightMap
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.SplitterNode
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.WalkDetails

class MapWalkTest: FunSpec({
    test("Test empty map walk returns identical map") {
        val input = LightMap(
            startPoint = Coordinate(2, 0),
            splitters = emptyList(),
            maxHeight = 5,
            maxWidth = 5,
        );
        val walker = MapWalk();
        val walkedMap = walker.walk(input)

        walkedMap shouldBeEqual WalkDetails(
            setOf(
                HistoricJoin(Coordinate(2, 0), Coordinate(2, 1)),
                HistoricJoin(Coordinate(2, 1), Coordinate(2, 2)),
                HistoricJoin(Coordinate(2, 2), Coordinate(2, 3)),
                HistoricJoin(Coordinate(2, 3), Coordinate(2, 4)),
                HistoricJoin(Coordinate(2, 4), Coordinate(2, 5)),
            ),
            setOf(Coordinate(2, 5)),
            0);
    }

    test("Test simple split should be effective within map walk") {
        val input = LightMap(
            startPoint = Coordinate(2, 0),
            splitters = listOf(SplitterNode(Coordinate(2, 2))),
            maxHeight = 5,
            maxWidth = 5,
        );
        val walker = MapWalk();
        val walkedMap = walker.walk(input)

        walkedMap shouldBeEqual WalkDetails(
            setOf(
                HistoricJoin(Coordinate(2, 0), Coordinate(2, 1)),
                HistoricJoin(Coordinate(2, 1), Coordinate(2, 2)),
                HistoricJoin(Coordinate(2, 2), Coordinate(1, 3)),
                HistoricJoin(Coordinate(2, 2), Coordinate(3, 3)),
                HistoricJoin(Coordinate(1, 3), Coordinate(1, 4)),
                HistoricJoin(Coordinate(1, 4), Coordinate(1, 5)),
                HistoricJoin(Coordinate(3, 3), Coordinate(3, 4)),
                HistoricJoin(Coordinate(3, 4), Coordinate(3, 5)),
            ),
            setOf(
                Coordinate(1, 5),
                Coordinate(3, 5),
            ),
            1);
    }

    test("Test multi-level split should only return unique exit points") {
        val input = LightMap(
            startPoint = Coordinate(2, 0),
            splitters = listOf(
                SplitterNode(Coordinate(2, 1)),
                SplitterNode(Coordinate(1, 2)),
                SplitterNode(Coordinate(3, 2)),
            ),
            maxHeight = 5,
            maxWidth = 5,
        );
        val walker = MapWalk();
        val walkedMap = walker.walk(input)

        walkedMap shouldBeEqual WalkDetails(
            setOf(
                HistoricJoin(Coordinate(2, 0), Coordinate(2, 1)),
                HistoricJoin(Coordinate(2, 1), Coordinate(1, 2)),
                HistoricJoin(Coordinate(1, 2), Coordinate(0, 3)),
                HistoricJoin(Coordinate(1, 2), Coordinate(2, 3)),
                HistoricJoin(Coordinate(2, 1), Coordinate(3, 2)),
                HistoricJoin(Coordinate(3, 2), Coordinate(2, 3)),
                HistoricJoin(Coordinate(3, 2), Coordinate(4, 3)),
                HistoricJoin(Coordinate(0, 3), Coordinate(0, 4)),
                HistoricJoin(Coordinate(0, 4), Coordinate(0, 5)),
                HistoricJoin(Coordinate(2, 3), Coordinate(2, 4)),
                HistoricJoin(Coordinate(2, 4), Coordinate(2, 5)),
                HistoricJoin(Coordinate(4, 3), Coordinate(4, 4)),
                HistoricJoin(Coordinate(4, 4), Coordinate(4, 5)),
            ),
            setOf(
                Coordinate(0, 5),
                Coordinate(2, 5),
                Coordinate(4, 5)
            ),
            3);
    }

    test("Test calculating combinations for simple history is accurate") {
        val history = setOf(
            HistoricJoin(Coordinate(2, 0), Coordinate(2, 1)),
            HistoricJoin(Coordinate(2, 1), Coordinate(2, 2)),
            HistoricJoin(Coordinate(2, 2), Coordinate(2, 3)),
            HistoricJoin(Coordinate(2, 3), Coordinate(2, 4)),
            HistoricJoin(Coordinate(2, 4), Coordinate(2, 5)),
        );

        val walker = MapWalk();
        val weights = walker.calculateCombinations(setOf(Coordinate( 2, 5)), history);

        val exitNodes = weights.filter { weight -> weight.location.y == 0 };
        val totalCombinations = exitNodes.fold(0L) { ongoing, weight -> ongoing + weight.weight };

        totalCombinations shouldBeEqual 1L
    }

    test("Test calculating combinations for multi-step history is accurate") {
        val history = setOf(
            HistoricJoin(Coordinate(2, 0), Coordinate(2, 1)),
            HistoricJoin(Coordinate(2, 1), Coordinate(1, 2)),
            HistoricJoin(Coordinate(1, 2), Coordinate(0, 3)),
            HistoricJoin(Coordinate(1, 2), Coordinate(2, 3)),
            HistoricJoin(Coordinate(2, 1), Coordinate(3, 2)),
            HistoricJoin(Coordinate(3, 2), Coordinate(2, 3)),
            HistoricJoin(Coordinate(3, 2), Coordinate(4, 3)),
            HistoricJoin(Coordinate(0, 3), Coordinate(0, 4)),
            HistoricJoin(Coordinate(0, 4), Coordinate(0, 5)),
            HistoricJoin(Coordinate(2, 3), Coordinate(2, 4)),
            HistoricJoin(Coordinate(2, 4), Coordinate(2, 5)),
            HistoricJoin(Coordinate(4, 3), Coordinate(4, 4)),
            HistoricJoin(Coordinate(4, 4), Coordinate(4, 5)),
        );

        val walker = MapWalk();
        val weights = walker.calculateCombinations(
            setOf(
                Coordinate(0, 5),
                Coordinate(2, 5),
                Coordinate(4, 5),
            ),
            history);

        val exitNodes = weights.filter { weight -> weight.location.y == 0 };
        val totalCombinations = exitNodes.fold(0L) { ongoing, weight -> ongoing + weight.weight };

        totalCombinations shouldBeEqual 4L
    }
})
