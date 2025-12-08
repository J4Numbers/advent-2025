package uk.j4numbers.adventofcode.advent_2025.day_08.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import uk.j4numbers.adventofcode.advent_2025.day_08.pojo.Coordinate

class FileIOTest: FunSpec ({
    test("Reading a file should return its contents") {
        val fileReader = FileIO();
        val coordinateList = fileReader.readFile("src/test/resources/test-file.txt");

        coordinateList shouldHaveSize 3;
        coordinateList shouldContain Coordinate(123, 456, 789);
        coordinateList shouldContain Coordinate(111, 222, 333);
        coordinateList shouldContain Coordinate(824, 178, 192);
    }

    test("Providing a blank file returns an empty coordinate list") {
        val fileReader = FileIO();
        val lightMap = fileReader.readFile("src/test/resources/blank.txt");

        lightMap shouldHaveSize 0;
    }
})
