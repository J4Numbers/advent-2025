# Day 3 - Lobby

> Written in GoLang

This problem is a search algorithm which attempts to find the highest possible sequential two-digit
number in a series of numbers. Each input contains a series of lines containing numerals.

The following assumptions can be made about the inputs:

* Input files contain a series of lines only containing the numbers 1-9 with no spaces.

We want to find the highest sequential two-digit number for each line of numbers.

For example, the following input:

```text
987654321111111
811111111111119
234234234234278
818181911112111
```

In the above inputs, we can create the following largest numbers:

* `987654321111111` - `98` is the largest two-digit number in sequence
* `811111111111119` - `89` is the largest
* `234234234234278` - `78` is the largest
* `818181911112111` - `92` is the largest

The final answer is calculated by summing the largest number possible in each line.

In the above example, the sum of all the largest possible numbers is `357`.

## Part two

Now instead of a two-digit sequential number, we need a 12-digit sequential number with the same
rules as before.

With the example from before, we now have new largest numbers of:

* `987654321111111` - `987654321111`
* `811111111111119` - `811111111119`
* `234234234234278` - `434234234278`
* `818181911112111` - `888911112111`

The total is now `3121910778619`.

## This script

This script uses Go and can be run with the following command:

```shell
go run . -i input.txt
```

To change the length of the number, provide the new flag of `--length`. For example, to trigger
part two, run the following:

```shell
go run . -i input.txt --length 12
```

A test suite is also included and can be run with the following command:

```shell
go test
```
