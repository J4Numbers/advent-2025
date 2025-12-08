# Day 8 - Playground

> Written in Kotlin

This is a mapping algorithm within 3-dimensional space. Given a series of inputs which describe the
3-dimensional co-ordinates of several points, we need to connect the closest two nodes in order.
for a given number of pairs and then calculate a function of how big these created graphs are.

Regarding the input:

* All contain three numbers in the format of `X, Y, Z`
* Each number in every row is positive

For example, given the following junction definitions:

```text
162,817,812
57,618,57
906,360,560
592,479,940
352,342,300
466,668,158
542,29,236
431,825,988
739,650,466
52,470,668
216,146,977
819,987,18
117,168,530
805,96,715
346,949,466
970,615,88
941,993,340
862,61,35
984,92,344
425,690,689
```

The closest two points within this list are `162,817,812` and `425,690,689`, which are joined
into a single graph. Repeating this exercise connects `162,817,812` and `431,825,988`, expanding
the existing graph of two into three.

Repeating this again results in joining `906,360,560` and `805,96,715` into a new graph of two
alongside the existing graph of three. The next join, however, would have been between `431,825,988`
and `425,690,689`, which are already joined within the same graph. Therefore, we don't count this
as a new join.

After making the ten shortest connections, there are 11 graphs in total:
* one graph which contains 5 nodes
* one graph which contains 4 nodes
* two graphs which contain 2 nodes each
* and seven nodes which are isolated.

Multiplying together the sizes of the three largest circuits (`5`, `4`, and one of the circuits of
size `2`) produces `40` as our final answer.

## Part two

Part two instead asks for the product of the last two nodes which were joined together to create
a complete graph. In the example before, the last two nodes which would have been joined to
create a complete graph were `216,146,977`, and `117,168,530`.

We calculate their product using only the X value for each node, in this case, `216` and `117`.

This results in a product of `25272` for our example.

## This script

This script uses Kotlin and requires compilation before it can be run:

```shell
mvn clean package
java -jar <program> input.txt <mode> [connection_count]
```

* Part one is run with a mode of `limit` and ensuring that the `connection_count` is provided.
* Part two is run with a mode of `join`. `connection_count` is not required.

To enable debug logging, please provide the additional option of `-Dlog.level=debug`, i.e.:

```shell
java -Dlog.level=debug -jar <program> input.txt <mode> [connection_count]
```

A test package has been provided which can be run with:

```shell
mvn clean test
```
