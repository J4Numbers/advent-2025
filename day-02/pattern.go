// This file is to run through day 2 of advent of code, which is a pattern identification program.
package main

import (
  "bufio"
  "errors"
  "flag"
  "fmt"
  "os"
  "regexp"
  "slices"
  "strconv"
  "strings"
)

const FileScanner string = `[0-9]+-[0-9]+(,[0-9]+-[0-9]+)*`

// RangeIteration - The test we perform on each input string line to see if it's a valid input
const RangeIteration string = `(?P<start>[0-9]+)-(?P<end>[0-9]+)`

// debug - Choose whether to run the program in debug mode
var debug = false

// Print out a given line if debug is enabled during the runtime of this program
func debugLine(lineToDebug string) {
  if debug {
    fmt.Println(lineToDebug)
  }
}

type Range struct {
  start int
  end   int
}

// Read in a given file and return a list of ranges to search
func readFile(filename string) ([]Range, error) {
  var fileContents []Range

  file, err := os.Open(filename)
  if err != nil {
    return fileContents, err
  }
  defer file.Close()

  var fileRegex = regexp.MustCompile(FileScanner)
  var rangeRegex = regexp.MustCompile(RangeIteration)
  startIndex := rangeRegex.SubexpIndex("start")
  endIndex := rangeRegex.SubexpIndex("end")

  scanner := bufio.NewScanner(file)
  for scanner.Scan() {
    var line = scanner.Text()
    if fileRegex.MatchString(line) {
      for _, matcher := range rangeRegex.FindAllStringSubmatch(line, -1) {
        if matcher == nil {
          break
        }

        // string to int
        start, err := strconv.Atoi(matcher[startIndex])
        if err != nil {
          return fileContents, err
        }
        end, err := strconv.Atoi(matcher[endIndex])
        if err != nil {
          return fileContents, err
        }
        rngToAppend := Range{
          start: start,
          end:   end,
        }
        debugLine(
          fmt.Sprintf(
            "Range(start: %d, end: %d)",
            rngToAppend.start,
            rngToAppend.end))
        fileContents = append(fileContents, rngToAppend)
      }
    }
  }
  return fileContents, nil
}

// discoverSequencesInBoundedRange requires that the input range is a consistent number of digits
// throughout - i.e. 100-999, 1000-1500, 1-9, etc.
func discoverSequencesInBoundedRange(inputRange Range, patternLength int) ([]int, error) {
  digitLength := len(strconv.Itoa(inputRange.start))
  if digitLength != len(strconv.Itoa(inputRange.end)) {
    return nil, errors.New("inconsistent range lengths discovered")
  }
  var discoveredSequences []int

  // Only consider this range for sequences if the length of the digits is divisible by the pattern
  // length
  if digitLength/patternLength > 1 && digitLength%patternLength == 0 {
    // Spin up the initial pattern from the input range which is as long as the pattern length and
    // repeated as many times as the digit length is divisible by the pattern length
    worker := inputRange.start
    patternSwapDivisor, _ := strconv.Atoi("1" + strings.Repeat("0", patternLength))
    patternMatch := fmt.Sprintf("^(%s){%d}$",
      strconv.Itoa(worker)[0:patternLength], digitLength/patternLength)
    patternRegex := regexp.MustCompile(patternMatch)
    debugLine(fmt.Sprintf("Starting with pattern check of %s for range %d-%d",
      patternMatch, inputRange.start, inputRange.end))

    // Iterate over our search space
    for {
      // If we're at the end, then exit
      if worker > inputRange.end {
        break
      }

      // If our current iteration matches our regex, add it to our discovered list
      workerStr := strconv.Itoa(worker)
      if patternRegex.MatchString(workerStr) {
        discoveredSequences = append(discoveredSequences, worker)
      }

      // Iterate and ask if we need to generate a new pattern
      worker += 1
      if worker%patternSwapDivisor == 0 {
        proposedPattern := fmt.Sprintf("^(%s){%d}$",
          strconv.Itoa(worker)[0:patternLength], digitLength/patternLength)
        if proposedPattern != patternMatch {
          patternMatch = proposedPattern
          patternRegex = regexp.MustCompile(patternMatch)
          debugLine(fmt.Sprintf("Updating pattern check to %s for range %d-%d",
            patternMatch, inputRange.start, inputRange.end))
        }
      }
    }
  }
  return discoveredSequences, nil
}

// breakRangeIntoDigitRanges takes an initial range which may cover multiple digit boundaries (i.e.
// 99-9999 covers 99-99, 100-999, and 1000-9999) and break it into individual digit ranges.
func breakRangeIntoDigitRanges(inputRange Range) []Range {
  // Our initial values are the digit lengths of our start and end points in the range
  startDigitLength := len(strconv.Itoa(inputRange.start))
  endDigitLength := len(strconv.Itoa(inputRange.end))

  var rangeList []Range
  workingStart := inputRange.start
  for {
    // If our start and end digit lengths meet, then make sure that they join up together before
    // exiting out.
    if startDigitLength == endDigitLength {
      rangeList = append(rangeList, Range{start: workingStart, end: inputRange.end})
      break
    }

    // If we have a digit gap between our start and end, compute a working end which is the largest
    // possible range within a given number of digits (i.e. 99, 999, 9999, etc.) and match it with
    // the working start.
    workingEnd, _ := strconv.Atoi(strings.Repeat("9", startDigitLength))
    rangeList = append(rangeList, Range{start: workingStart, end: workingEnd})

    // Update the working start to be the lowest possible number in the next digit range (i.e.
    // 10, 100, 1000, etc.), and increment our start digit length for the next loop.
    workingStart, _ = strconv.Atoi("1" + strings.Repeat("0", startDigitLength))
    startDigitLength += 1
  }

  return rangeList
}

func discoverSequencesInRange(inputRange Range, variableRange bool) []int {
  var discoveredSequences []int

  // For each single digit range...
  for _, singleRange := range breakRangeIntoDigitRanges(inputRange) {
    rangeDigitLen := len(strconv.Itoa(singleRange.start))
    if variableRange {
      // If we have a variable pattern range, request sequences for all possible ranges and
      // append the discovered sequences in each of those ranges to our total list
      for i := 1; i <= rangeDigitLen; i++ {
        sequences, err := discoverSequencesInBoundedRange(singleRange, i)
        if err != nil {
          panic(err)
        }
        discoveredSequences = append(discoveredSequences, sequences...)
      }
    } else {
      // If we have a fixed pattern range, only consider this sequence if its digit length is
      // divisible by 2. If it is, then request sequences for half its length and append those
      // discovered sequences to our total list
      if rangeDigitLen%2 == 0 {
        sequences, err := discoverSequencesInBoundedRange(singleRange, rangeDigitLen/2)
        if err != nil {
          panic(err)
        }
        discoveredSequences = append(discoveredSequences, sequences...)
      }
    }
  }

  // Sort and unique-ify the discovered sequences to remove duplicates
  slices.Sort(discoveredSequences)
  return slices.Compact(discoveredSequences)
}

func iterateOverRanges(rangeInputs []Range, variableRange bool) int {
  identifiedInvalidTotal := 0

  for rangeIdx, inputRange := range rangeInputs {
    for _, invalidId := range discoverSequencesInRange(inputRange, variableRange) {
      identifiedInvalidTotal += invalidId
    }
    debugLine(fmt.Sprintf("Found new total of %d invalid IDs after %d ranges", identifiedInvalidTotal, rangeIdx))
  }

  return identifiedInvalidTotal
}

// Main function to kick the work
func main() {
  // Do some initial CLI parsing to figure out what the requested operation is.
  var filename string
  var variableRange bool
  flag.StringVar(&filename, "i", "input.txt", "Specify input file for the program")
  flag.BoolVar(&variableRange, "variable", false, "Enable variable pattern ranges")
  flag.BoolVar(&debug, "debug", false, "Enable debug logging")
  flag.Parse()

  // Read in the given file as a number of lines.
  rangeSet, err := readFile(filename)
  if err != nil {
    fmt.Println(err)
    os.Exit(1)
  }

  var invalidIdTotal = iterateOverRanges(rangeSet, variableRange)

  fmt.Printf("Discovered sum of invalid IDs: %d\n", invalidIdTotal)
}
