# Day 1 - Secret Entrance

> Written in GoLang

This is a modulo problem to calculate the number of times a modulo of 100 is zeroed
when following instructions to add and subtract
from a total.

The following assumptions can be made about the inputs:

* All lines contain a single instruction
* The only characters that can appear in any of the lines are `[RL][0-9]+`

The instructions state that the starting position is always 50.

Any instructions prepended with `R` will _add_ to the total. Any instructions prepended with
`L` will _subtract_ from the total.

The answer we seek is the number of times the total is divisible by 100 with no remainder.

For example...

```text
L68
L30
R48
L5
R60
L55
L1
L99
R14
L82
```

Translates to the following working total, where the first line is the starting value.

```text
  50 - 68 = -18
 -18 - 30 = -48
 -48 + 48 = 0
   0 - 5  = -5
  -5 + 60 = 55
  55 - 55 = 0
   0 - 1  = -1
  -1 - 99 = -100
-100 + 14 = -86
 -86 - 82 = -168
```

Totalling up the number of times our modulo was `0`, our final answer is `3`.

## Part two

Part two states that we're also interested in any points where the modulo boundary is passed.
This means that the above example is amended to a new total of `6` times.

```text
  50 - 68 = -18 (CROSSOVER)
 -18 - 30 = -48
 -48 + 48 = 0 (DIRECT)
   0 - 5  = -5
  -5 + 60 = 55 (CROSSOVER)
  55 - 55 = 0 (DIRECT)
   0 - 1  = -1
  -1 - 99 = -100 (DIRECT)
-100 + 14 = -86
 -86 - 82 = -168 (CROSSOVER)
```

## This script

This script uses Go and can be run with the following command:

```shell
go run . -i input.txt
```

This will return an answer for both part one and part two of the problem above.

A test suite is also included and can be run with the following command:

```shell
go test
```
