// This file is to run through day 1 of advent of code, which is a modulo operating check.
package main

import (
  "bufio"
  "flag"
  "fmt"
  "os"
  "regexp"
  "strconv"
)

// ValidLineCheck - The test we perform on each input string line to see if it's a valid input
const ValidLineCheck string = `^(?P<direction>[LR])(?P<count>[0-9]+)$`

// debug - Choose whether to run the program in debug mode
var debug = false

// Print out a given line if debug is enabled during the runtime of this program
func debugLine(lineToDebug string) {
  if debug {
    fmt.Println(lineToDebug)
  }
}

type Instruction struct {
  pos   bool
  count int
}

// Read in a given file and return each line in a slice
func readFile(filename string) ([]Instruction, error) {
  var fileContents []Instruction

  file, err := os.Open(filename)
  if err != nil {
    return fileContents, err
  }
  defer file.Close()

  var lineRegex = regexp.MustCompile(ValidLineCheck)

  scanner := bufio.NewScanner(file)
  for scanner.Scan() {
    var line = scanner.Text()
    if lineRegex.MatchString(line) {
      var matcher = lineRegex.FindStringSubmatch(line)
      dirIndex := lineRegex.SubexpIndex("direction")
      countIndex := lineRegex.SubexpIndex("count")
      // string to int
      count, err := strconv.Atoi(matcher[countIndex])
      if err != nil {
        return fileContents, err
      }
      instrToAppend := Instruction{
        pos:   matcher[dirIndex] == "R",
        count: count,
      }
      debugLine(
        fmt.Sprintf(
          "Instruction(pos: %t, count: %d)",
          instrToAppend.pos,
          instrToAppend.count))
      fileContents = append(fileContents, instrToAppend)
    }
  }
  return fileContents, nil
}

func computeAbsolute(test int) int {
  if test < 0 {
    return test * -1
  }
  return test
}

func computeIndirectHits(prev int, current int, target int) int {
  indirectHits := computeAbsolute((prev / target) - (current / target))
  if (prev >= 0 && current < 0) || (prev < 0 && current >= 0) {
    indirectHits += 1
  }
  if current%target == 0 {
    // Do something to discount the positive edge case
    if prev >= 0 && current >= 0 {
      if prev < current {
        indirectHits -= 1
      }
    } else if prev < 0 && current < 0 {
      if current < prev {
        indirectHits -= 1
      }
    } else {
      indirectHits -= 1
    }
  }
  if prev%target == 0 {
    // Do something to discount the negative edge case
    if prev >= 0 && current >= 0 {
      if current < prev {
        indirectHits -= 1
      }
    } else if prev < 0 && current < 0 {
      if prev < current {
        indirectHits -= 1
      }
    } else {
      indirectHits -= 1
    }
  }
  debugLine(fmt.Sprintf("Found %d indirect hits between %d and %d", indirectHits, prev, current))
  return indirectHits
}

func calculateZeroesHit(instructions []Instruction, startVal int, targetVal int) (int, int) {
  var directZeroes int
  var indirectZeroes int
  var prevVal int
  var workingVal = startVal

  for _, instr := range instructions {
    prevVal = workingVal
    if instr.pos {
      workingVal += instr.count
    } else {
      workingVal -= instr.count
    }

    indirectZeroes += computeIndirectHits(prevVal, workingVal, targetVal)

    if workingVal%targetVal == 0 {
      directZeroes += 1
    }
  }

  return directZeroes, directZeroes + indirectZeroes
}

// Main function to kick the work
func main() {
  // Do some initial CLI parsing to figure out what the requested operation is.
  var filename string
  var start int
  var target int
  flag.StringVar(&filename, "i", "input.txt", "Specify input file for the program")
  flag.IntVar(&start, "s", 50, "Starting position for the pointer")
  flag.IntVar(&target, "t", 100, "Target modulo to hit to count")
  flag.BoolVar(&debug, "debug", false, "Enable debug logging")
  flag.Parse()

  // Read in the given file as a number of lines.
  instructionSet, err := readFile(filename)
  if err != nil {
    fmt.Println(err)
    os.Exit(1)
  }

  var direct, total = calculateZeroesHit(instructionSet, start, target)

  fmt.Printf("Discovered total number of times 0 was hit: %d\n", direct)
  fmt.Printf("Discovered total indirect and direct times 0 was hit: %d\n", total)
}
