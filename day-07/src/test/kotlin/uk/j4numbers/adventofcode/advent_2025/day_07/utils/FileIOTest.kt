package uk.j4numbers.adventofcode.advent_2025.day_07.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.Coordinate
import uk.j4numbers.adventofcode.advent_2025.day_07.pojo.SplitterNode

class FileIOTest: FunSpec ({
    test("Reading a file should return its contents") {
        val fileReader = FileIO();
        val lightMap = fileReader.readFile("src/test/resources/test-file.txt");

        lightMap.maxHeight shouldBeEqual 5;
        lightMap.maxWidth shouldBeEqual 5;
        lightMap.startPoint shouldBeEqual Coordinate(2, 0);
        lightMap.splitters shouldHaveSize 3;
        lightMap.splitters shouldContain SplitterNode(Coordinate(2, 1), 0);
        lightMap.splitters shouldContain SplitterNode(Coordinate(1, 3), 0);
        lightMap.splitters shouldContain SplitterNode(Coordinate(3, 3), 0);
    }

    test("Providing a blank file returns an empty light map") {
        val fileReader = FileIO();
        val lightMap = fileReader.readFile("src/test/resources/blank.txt");

        lightMap.maxHeight shouldBeEqual 0;
        lightMap.maxWidth shouldBeEqual 0;
        lightMap.startPoint shouldBeEqual Coordinate(-1, -1);
        lightMap.splitters shouldHaveSize 0;
    }
})
