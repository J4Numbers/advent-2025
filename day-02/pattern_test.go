package main

import (
  "slices"
  "testing"
)

type RangeSequenceTest struct {
  inputRange Range
  expected   []int
}

type RangeBreakdownTest struct {
  inputRange Range
  expected   []Range
}

func generateRangeSequenceTest(start int, end int, expected []int) RangeSequenceTest {
  return RangeSequenceTest{
    inputRange: Range{
      start: start,
      end:   end,
    },
    expected: expected,
  }
}

func TestDiscoverSequencesInRangesOverSeveralValuesForFixedRanges(t *testing.T) {
  var computeTests []RangeSequenceTest
  computeTests = append(computeTests,
    // Default test ranges from test input
    generateRangeSequenceTest(11, 22, []int{11, 22}),
    generateRangeSequenceTest(95, 115, []int{99}),
    generateRangeSequenceTest(998, 1012, []int{1010}),
    generateRangeSequenceTest(1188511880, 1188511890, []int{1188511885}),
    generateRangeSequenceTest(222220, 222224, []int{222222}),
    generateRangeSequenceTest(1698522, 1698528, []int{}),
    generateRangeSequenceTest(446443, 446449, []int{446446}),
    generateRangeSequenceTest(38593856, 38593862, []int{38593859}),
    generateRangeSequenceTest(565653, 565659, []int{}),
    generateRangeSequenceTest(824824821, 824824827, []int{}),
    generateRangeSequenceTest(2121212118, 2121212124, []int{}),
  )

  for idx, test := range computeTests {
    discoveredSequences := discoverSequencesInRange(test.inputRange, false)
    if !slices.Equal(discoveredSequences, test.expected) {
      t.Errorf("Expected values of %v does not match real values of %v for test %d (%d-%d)",
        test.expected, discoveredSequences, idx, test.inputRange.start, test.inputRange.end)
    }
  }
}

func TestDiscoverSequencesInRangesOverSeveralValuesForVariableRanges(t *testing.T) {
  var computeTests []RangeSequenceTest
  computeTests = append(computeTests,
    // Default test ranges from test input
    generateRangeSequenceTest(11, 22, []int{11, 22}),
    generateRangeSequenceTest(95, 115, []int{99, 111}),
    generateRangeSequenceTest(998, 1012, []int{999, 1010}),
    generateRangeSequenceTest(1188511880, 1188511890, []int{1188511885}),
    generateRangeSequenceTest(222220, 222224, []int{222222}),
    generateRangeSequenceTest(1698522, 1698528, []int{}),
    generateRangeSequenceTest(446443, 446449, []int{446446}),
    generateRangeSequenceTest(38593856, 38593862, []int{38593859}),
    generateRangeSequenceTest(565653, 565659, []int{565656}),
    generateRangeSequenceTest(824824821, 824824827, []int{824824824}),
    generateRangeSequenceTest(2121212118, 2121212124, []int{2121212121}),
  )

  for idx, test := range computeTests {
    discoveredSequences := discoverSequencesInRange(test.inputRange, true)
    if !slices.Equal(discoveredSequences, test.expected) {
      t.Errorf("Expected values of %v does not match real values of %v for test %d (%d-%d)",
        test.expected, discoveredSequences, idx, test.inputRange.start, test.inputRange.end)
    }
  }
}

func TestBreakMultiDigitRangeIntoDigitRangesIsAccurate(t *testing.T) {
  var computeTests []RangeBreakdownTest
  computeTests = append(computeTests,
    RangeBreakdownTest{inputRange: Range{start: 11, end: 22}, expected: []Range{Range{start: 11, end: 22}}},
    RangeBreakdownTest{inputRange: Range{start: 100, end: 999}, expected: []Range{Range{start: 100, end: 999}}},
    RangeBreakdownTest{inputRange: Range{start: 150, end: 1050}, expected: []Range{Range{start: 150, end: 999}, Range{start: 1000, end: 1050}}},
  )

  for idx, test := range computeTests {
    discoveredRanges := breakRangeIntoDigitRanges(test.inputRange)
    if !slices.Equal(discoveredRanges, test.expected) {
      t.Errorf("Expected ranges of %v does not match real ranges of %v for test %d (%d-%d)",
        test.expected, discoveredRanges, idx, test.inputRange.start, test.inputRange.end)
    }
  }
}
