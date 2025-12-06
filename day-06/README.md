# Day 6 - Trash Compactor

> Written in Rust

This is an input manipulation problem which provides a series of number sets, followed by a
mathematical operation to enact on those numbers.

* Each number set line can be expressed as `^[0-9]+( +[0-9]+)*%`
* Thw operation line can be expressed as `^[+*]( +[*+])*$`

For example, given the input:

```text
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  
```

We can rearrange the file into the following calculations:

```text
123 * 45 * 6 = 33210
328 + 64 + 98 = 490
51 * 387 * 215 = 4243455
64 + 23 + 314 = 401
```

Summing these solutions up provides the grand total of `4277556`.

## Part two

Part two instead states that each column is its own number, rather than an item within a row (and
that whitespace is _very_ important).

These numbers are read right-to-left in columns of consistent width within that column, meaning
that the prior example can instead be expressed as:

```text
356 * 24 * 1 = 8544
8 + 248 + 369 = 625
175 * 581 * 32 = 3253600
4 + 431 + 623 = 1058
```

This produces the new final total of `3263827`.

## This script

This script uses Rust and can be run with the following command:

```shell
cargo run -- -i input.txt
```

Debug logging can be enabled with `RUST_LOG=debug` as a command prefix.

A test suite has been provided with this library, and can be run with the following:

```shell
cargo test
```
