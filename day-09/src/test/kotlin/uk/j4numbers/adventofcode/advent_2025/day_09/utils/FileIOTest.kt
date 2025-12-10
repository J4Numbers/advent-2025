package uk.j4numbers.adventofcode.advent_2025.day_09.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import uk.j4numbers.adventofcode.advent_2025.day_09.pojo.Coordinate

class FileIOTest: FunSpec ({
    test("Reading a file should return its contents") {
        val fileReader = FileIO();
        val coordinateList = fileReader.readFile("src/test/resources/test-file.txt");

        coordinateList shouldHaveSize 4;
        coordinateList shouldContain Coordinate(7, 1);
        coordinateList shouldContain Coordinate(11, 1);
        coordinateList shouldContain Coordinate(11, 7);
        coordinateList shouldContain Coordinate(9, 7);
    }

    test("Providing a blank file returns an empty coordinate list") {
        val fileReader = FileIO();
        val lightMap = fileReader.readFile("src/test/resources/blank.txt");

        lightMap shouldHaveSize 0;
    }
})
