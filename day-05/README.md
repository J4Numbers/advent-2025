# Day 4 - Cafeteria

> Written in Rust

This is a search space problem similar to day 3, where a series of ranges are provided as a set
of inputs, and the second half of the input is a series of numbers.

This program must identify which provided numbers fall inside of those valid ranges.

The puzzle input can therefore be split into two parts:
* Part one will contain a set of number ranges, one on each line
* These ranges are in the format of `^[0-9]+-[0-9]+$`
* Part two will contain a set of numbers, one on each line
* These numbers are simply `[0-9]+`
* Part one and part two of the input are separated by a blank line

For example:

```text
3-5
10-14
16-20
12-18

1
5
8
11
17
32
```

This puzzle input has four ranges. `5`, `11`, and `17` all fall within at least one of these
ranges.

This means `3` numbers fall within at least one range.

## Part two

Part two just asks what the total search space of the ranges was.

In the above example, the search space could be re-written into:

```text
3-5   = 3
10-20 = 11
```

Meaning the total search space was `14`.

## This script

This script uses Rust and can be run with the following command to generate the answer for part
one and for part two:

```shell
cargo run -- -i input.txt
```

Debug logging can be enabled with `RUST_LOG=debug` as a command prefix.

A test suite has been provided with this library, and can be run with the following:

```shell
cargo test
```
