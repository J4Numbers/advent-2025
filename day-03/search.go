// This file is to run through day 3 of advent of code, which is a sequential search.
package main

import (
  "bufio"
  "flag"
  "fmt"
  "math"
  "os"
  "regexp"
  "strconv"
  "strings"
)

// SequenceSearch - The test we perform on each input string line to see if it's a valid input
const SequenceSearch string = `^[1-9]+$`

// debug - Choose whether to run the program in debug mode
var debug = false

// Print out a given line if debug is enabled during the runtime of this program
func debugLine(lineToDebug string) {
  if debug {
    fmt.Println(lineToDebug)
  }
}

// Read in a given file and return a list of ranges to search
func readFile(filename string) ([][]int, error) {
  var fileContents [][]int

  file, err := os.Open(filename)
  if err != nil {
    return fileContents, err
  }
  defer file.Close()

  var lineRegex = regexp.MustCompile(SequenceSearch)

  scanner := bufio.NewScanner(file)
  for scanner.Scan() {
    var line = scanner.Text()
    if lineRegex.MatchString(line) {
      var intLine []int
      for _, chr := range strings.Split(line, "") {
        n, _ := strconv.Atoi(chr)
        intLine = append(intLine, n)
      }
      fileContents = append(fileContents, intLine)
    }
  }
  return fileContents, nil
}

// collapseSliceToNumber is a helper function to transform a slice into a base10 number
func collapseSliceToNumber(sliceToCollapse []int) int {
  var total int
  for idx, digit := range sliceToCollapse {
    total += digit * int(math.Pow10(len(sliceToCollapse)-(idx+1)))
  }
  return total
}

// generateHighSlice generate a new slice which is completely zeroed
func generateHighSlice(sliceSize int) []int {
  highSlice := make([]int, sliceSize)
  for idx, _ := range highSlice {
    highSlice[idx] = 0
  }
  return highSlice
}

// testHighsFilled returns true if all items in a slice have been filled in and are no-longer
// zero.
func testHighsFilled(sliceToTest []int) bool {
  highsFilled := true
  for _, digit := range sliceToTest {
    highsFilled = highsFilled && digit > 0
  }
  return highsFilled
}

// calculateLargestNumberSequence will return the largest sequential numbers within a sequence.
// This is a recursive function with some simplifications to reduce the search space.
func calculateLargestNumberSequence(sequence []int, highNumbers []int) int {
  largest := 0
  debugLine(fmt.Sprintf("Sequence %v with current high numbers %v",
    sequence, highNumbers))

  // Iterate over each digit within the sequence
  for idx, digit := range sequence {
    // Iterate over the discovered high numbers so far
    for highIdx, high := range highNumbers {
      // If the current digit is larger than the discovered high number...
      if digit > high {
        // Generate a new high number slice which contains all of our discovered high numbers
        // up to this point with our newly discovered high number, and zero everything else so
        // we can fill them in again.
        newHighs := generateHighSlice(len(highNumbers))
        copy(newHighs, highNumbers[0:highIdx])
        newHighs[highIdx] = digit

        // Recurse with our new high number set and our reduced sequence and ask if that is our new
        // highest number. If it is, return right away.
        testVal := calculateLargestNumberSequence(sequence[idx+1:], newHighs)
        if testVal > largest {
          return testVal
        }
      }
      // If this was the first 0 in our high number array, we don't want to leave a gap, so we
      // can break out of the high number iteration now.
      if high == 0 {
        break
      }
    }
  }
  // If all of our high numbers have been filled, we can generate a new base case for our largest
  // number which we will return.
  if testHighsFilled(highNumbers) {
    largest = collapseSliceToNumber(highNumbers)
  }
  return largest
}

// calculateSequenceTotals sums up the largest possible numbers made up of many sequences
func calculateSequenceTotals(sequences [][]int, numberLength int) int {
  var ongoingTotal int
  for _, seq := range sequences {
    ongoingTotal += calculateLargestNumberSequence(seq, generateHighSlice(numberLength))
  }
  return ongoingTotal
}

// Main function to kick the work
func main() {
  // Do some initial CLI parsing to figure out what the requested operation is.
  var filename string
  var numberLength int
  flag.StringVar(&filename, "i", "input.txt", "Specify input file for the program")
  flag.IntVar(&numberLength, "length", 2, "The length of the number to build")
  flag.BoolVar(&debug, "debug", false, "Enable debug logging")
  flag.Parse()

  // Read in the given file as a number of lines.
  sequenceCollection, err := readFile(filename)
  if err != nil {
    fmt.Println(err)
    os.Exit(1)
  }

  var totalLargest = calculateSequenceTotals(sequenceCollection, numberLength)

  fmt.Printf("Discovered sum largest numbers: %d\n", totalLargest)
}
