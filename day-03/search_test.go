package main

import (
  "testing"
)

type CollapseTests struct {
  numbers  []int
  expected int
}

type SequenceTests struct {
  sequence  []int
  numberLen int
  expected  int
}

type SequenceSeriesTests struct {
  sequenceSeries [][]int
  numberLen      int
  expectedTotal  int
}

func TestCollapseSliceToNumberIsAccurate(t *testing.T) {
  var computeTests []CollapseTests
  computeTests = append(computeTests,
    CollapseTests{numbers: []int{1}, expected: 1},
    CollapseTests{numbers: []int{1, 2}, expected: 12},
    CollapseTests{numbers: []int{1, 2, 3}, expected: 123},
    CollapseTests{numbers: []int{1, 2, 3, 4}, expected: 1234},
  )

  for idx, test := range computeTests {
    collapsedNumber := collapseSliceToNumber(test.numbers)
    if collapsedNumber != test.expected {
      t.Errorf("Expected values of %d does not match real value of %d for test %d (%v)",
        test.expected, collapsedNumber, idx, test.numbers)
    }
  }
}

func TestCalculateLargestNumberSequenceOverSeveralValues(t *testing.T) {
  var computeTests []SequenceTests
  computeTests = append(computeTests,
    SequenceTests{sequence: []int{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, numberLen: 2, expected: 11},
    SequenceTests{sequence: []int{9, 9, 9, 9, 9, 9, 9, 9, 9, 9}, numberLen: 2, expected: 99},
    SequenceTests{sequence: []int{9, 1, 1, 1, 1, 1, 1, 1, 1, 8}, numberLen: 2, expected: 98},
    SequenceTests{sequence: []int{2, 1, 1, 1, 1, 1, 1, 3, 1, 8}, numberLen: 2, expected: 38},
    SequenceTests{sequence: []int{8, 1, 1, 1, 1, 1, 1, 3, 9, 8}, numberLen: 2, expected: 98},
    SequenceTests{sequence: []int{5, 1, 1, 1, 1, 1, 8, 8, 9, 4}, numberLen: 2, expected: 94},
    SequenceTests{sequence: []int{5, 1, 1, 1, 1, 1, 8, 8, 8, 9}, numberLen: 2, expected: 89},
    SequenceTests{sequence: []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 9}, numberLen: 2, expected: 99},
  )

  for idx, test := range computeTests {
    largestNumber := calculateLargestNumberSequence(test.sequence, generateHighSlice(2))
    if largestNumber != test.expected {
      t.Errorf("Expected values of %d does not match real value of %d for test %d (%v)",
        test.expected, largestNumber, idx, test.sequence)
    }
  }
}

func TestCalculateSequenceTotalOverSeveralValues(t *testing.T) {
  var computeTests []SequenceSeriesTests
  computeTests = append(computeTests,
    SequenceSeriesTests{
      sequenceSeries: [][]int{
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
      },
      numberLen:     2,
      expectedTotal: 11,
    },
    SequenceSeriesTests{
      sequenceSeries: [][]int{
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
      },
      numberLen:     2,
      expectedTotal: 55,
    },
    SequenceSeriesTests{
      sequenceSeries: [][]int{
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2}, //12
        {1, 8, 1, 1, 1, 1, 1, 1, 1, 1}, //81
        {1, 1, 1, 1, 9, 1, 1, 1, 9, 1}, //99
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
      },
      numberLen:     2,
      expectedTotal: 214,
    },
    SequenceSeriesTests{
      sequenceSeries: [][]int{
        {9, 8, 7, 6, 5, 4, 3, 2, 1, 1, 1, 1, 1, 1, 1}, //98
        {8, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9}, //89
        {2, 3, 4, 2, 3, 4, 2, 3, 4, 2, 3, 4, 2, 7, 8}, //78
        {8, 1, 8, 1, 8, 1, 9, 1, 1, 1, 1, 2, 1, 1, 1}, //92
      },
      numberLen:     2,
      expectedTotal: 357,
    },
    SequenceSeriesTests{
      sequenceSeries: [][]int{
        {9, 8, 7, 6, 5, 4, 3, 2, 1, 1, 1, 1, 1, 1, 1}, //98
        {8, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9}, //89
        {2, 3, 4, 2, 3, 4, 2, 3, 4, 2, 3, 4, 2, 7, 8}, //78
        {8, 1, 8, 1, 8, 1, 9, 1, 1, 1, 1, 2, 1, 1, 1}, //92
      },
      numberLen:     12,
      expectedTotal: 3121910778619,
    },
  )

  for idx, test := range computeTests {
    totals := calculateSequenceTotals(test.sequenceSeries, test.numberLen)
    if totals != test.expectedTotal {
      t.Errorf("Expected values of %d does not match real value of %d for test %d (%v)",
        test.expectedTotal, totals, idx, test.sequenceSeries)
    }
  }
}
