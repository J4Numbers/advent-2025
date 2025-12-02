# Day 2 - Gift Shop

> Written in GoLang

This is a problem which focuses on pattern recognition within numbers. We are given an input with
a series of inclusive areas and must identify all instances in those inclusive areas which are
duplicated sequences of even length.

The following assumptions can be made about the inputs:

* A single line is provided with all ranges to search
* Each range is made up of two numbers in the format `[0-9]+-[0-9]+` with no leading zeroes
* All ranges are separated by a comma (`,`)

For example, the following input:

```text
11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124
```

Describes the following 11 ranges:

```text
11-22
95-115
998-1012
1188511880-1188511890
222220-222224
1698522-1698528
446443-446449
38593856-38593862
565653-565659
824824821-824824827
2121212118-2121212124
```

The following duplicated sequences can be identified from that list:

```text
11-22                 - 11, 22
95-115                - 99
998-1012              - 1010
1188511880-1188511890 - 1188511885
222220-222224         - 222222
1698522-1698528       - None
446443-446449         - 446446
38593856-38593862     - 38593859
565653-565659         - None
824824821-824824827   - None
2121212118-2121212124 - None
```

To generate the final answer, we add up all the invalid IDs to a total of `1227775554`.

## Part two

Part two now states that the repeating sequence can be of any length within the pattern. We can
still reduce this search space by only considering patterns lengths which are neatly divisible
within our input ranges (i.e. a repeating pattern of `123` cannot be applied to `1231231234`)

In the worst case, this would then apply as a pattern of 1 character repeated (i.e. `999`).

With this in mind, the invalid IDs from the example above now become:

```text
11-22                 - 11, 22
95-115                - 99, 111
998-1012              - 999, 1010
1188511880-1188511890 - 1188511885
222220-222224         - 222222
1698522-1698528       - None
446443-446449         - 446446
38593856-38593862     - 38593859
565653-565659         - 565656
824824821-824824827   - 824824824
2121212118-2121212124 - 2121212121
```

To a new total of `4174379265`

## This script

This script uses Go and can be run with the following command:

```shell
go run . -i input.txt
```

This will return an answer for part one. To return the answer for part two, please enable the
`--variable` flag. i.e.

```shell
go run . -i input.txt --variable
```

A test suite is also included and can be run with the following command:

```shell
go test
```
