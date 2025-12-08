package uk.j4numbers.adventofcode.advent_2025.day_08.pojo

class Coordinate(
    val x: Long,
    val y: Long,
    val z: Long,
) {
    override fun toString(): String {
        return String.format("Coordinate[$x, $y, $z]");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is Coordinate) return false;

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    override fun hashCode(): Int {
        return 32 * x.toInt() + y.toInt() * z.toInt();
    }

    fun minus(other: Coordinate): Coordinate {
        return Coordinate(
            x - other.x,
            y - other.y,
            z - other.z,
        );
    }

    fun toDistance(): Long {
        return (x * x) + (y * y) + (z * z);
    }
}
