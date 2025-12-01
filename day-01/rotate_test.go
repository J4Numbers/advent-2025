package main

import (
  "testing"
)

type IndirectZeroTest struct {
  prev     int
  current  int
  expected int
}

func generateIndirectZeroTest(prev int, curr int, expected int) IndirectZeroTest {
  return IndirectZeroTest{
    prev:     prev,
    current:  curr,
    expected: expected,
  }
}

func TestComputeIndirectZeroesWithTestSeries(t *testing.T) {
  var computeTests []IndirectZeroTest
  computeTests = append(computeTests,
    // Zero click tests within boundaries - POSITIVE
    generateIndirectZeroTest(100, 50, 0),
    generateIndirectZeroTest(40, 50, 0),
    generateIndirectZeroTest(0, 50, 0),
    generateIndirectZeroTest(0, 100, 0),
    // Zero click tests within boundaries - NEGATIVE
    generateIndirectZeroTest(-1, -100, 0),
    generateIndirectZeroTest(-10, -20, 0),
    generateIndirectZeroTest(-100, -150, 0),
    generateIndirectZeroTest(-100, -200, 0),
    // Zero click tests across boundaries
    generateIndirectZeroTest(0, -5, 0),
    generateIndirectZeroTest(-5, -0, 0),

    // Single click tests within boundaries - POSITIVE
    generateIndirectZeroTest(0, 150, 1),
    generateIndirectZeroTest(50, 200, 1),
    // Single click tests within boundaries - NEGATIVE
    generateIndirectZeroTest(-100, -250, 1),
    generateIndirectZeroTest(-50, -200, 1),
    // Single click tests across boundaries
    generateIndirectZeroTest(-50, 50, 1),
    generateIndirectZeroTest(50, -50, 1),
    generateIndirectZeroTest(0, -150, 1),
    generateIndirectZeroTest(-150, 0, 1),

    // Multi-click tests within boundaries - POSITIVE
    generateIndirectZeroTest(0, 500, 4),
    // Multi-click tests within boundaries - NEGATIVE
    generateIndirectZeroTest(-100, -500, 3),
    // Multi-click tests across boundaries
    generateIndirectZeroTest(150, -200, 3))

  for idx, test := range computeTests {
    indirectHits := computeIndirectHits(test.prev, test.current, 100)
    if indirectHits != test.expected {
      t.Errorf("Expected value of %d does not match real value of %d for test %d - %d::%d",
        test.expected, indirectHits, idx, test.prev, test.current)
    }
  }
}

func TestCalculateZeroesWorksForStandardPositiveRotation(t *testing.T) {
  var instructions []Instruction
  var directExpected = 1
  var totalExpected = 1
  instructions = append(instructions, Instruction{
    pos:   true,
    count: 50,
  })

  direct, total := calculateZeroesHit(instructions, 50, 100)
  if direct != directExpected {
    t.Errorf("Expected value of %d does not match direct hits of %d",
      directExpected, direct)
  }
  if total != totalExpected {
    t.Errorf("Expected value of %d does not match total hits of %d",
      totalExpected, total)
  }
}

func TestCalculateZeroesWorksForStandardNegativeRotation(t *testing.T) {
  var instructions []Instruction
  var directExpected = 1
  var totalExpected = 1
  instructions = append(instructions, Instruction{
    pos:   false,
    count: 50,
  })

  direct, total := calculateZeroesHit(instructions, 50, 100)
  if direct != directExpected {
    t.Errorf("Expected value of %d does not match direct hits of %d",
      directExpected, direct)
  }
  if total != totalExpected {
    t.Errorf("Expected value of %d does not match total hits of %d",
      totalExpected, total)
  }
}

func TestCalculateZeroesWorksForLargePositiveRotation(t *testing.T) {
  var instructions []Instruction
  var directExpected = 0
  var totalExpected = 5
  instructions = append(instructions, Instruction{
    pos:   true,
    count: 500,
  })

  direct, total := calculateZeroesHit(instructions, 50, 100)
  if direct != directExpected {
    t.Errorf("Expected value of %d does not match direct hits of %d",
      directExpected, direct)
  }
  if total != totalExpected {
    t.Errorf("Expected value of %d does not match total hits of %d",
      totalExpected, total)
  }
}

func TestCalculateZeroesWorksForLargeNegativeRotation(t *testing.T) {
  var instructions []Instruction
  var directExpected = 0
  var totalExpected = 5
  instructions = append(instructions, Instruction{
    pos:   false,
    count: 500,
  })

  direct, total := calculateZeroesHit(instructions, 50, 100)
  if direct != directExpected {
    t.Errorf("Expected value of %d does not match direct hits of %d",
      directExpected, direct)
  }
  if total != totalExpected {
    t.Errorf("Expected value of %d does not match total hits of %d",
      totalExpected, total)
  }
}
