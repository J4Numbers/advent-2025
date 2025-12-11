package uk.j4numbers.adventofcode.advent_2025.day_09.pojo

import kotlin.math.max
import kotlin.math.min

class CoordinateJoin(
    val first: Coordinate,
    val second: Coordinate,
) {
    override fun toString(): String {
        return String.format("CoordinateJoin($first, $second)");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other !is CoordinateJoin) return false;

        return if (first == other.first) {
            second == other.second
        } else {
            first == other.second && second == other.first;
        }
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        return result
    }

    fun intersects(other: CoordinateJoin): Boolean {
        fun onSegment(p: Coordinate, q: Coordinate, r: Coordinate): Boolean {
            return q.x <= max(p.x, r.x) &&
                    q.x >= min(p.x, r.x) &&
                    q.y <= max(p.y, r.y) &&
                    q.y >= min(p.y, r.y);
        }

        fun orient(p: Coordinate, q: Coordinate, r: Coordinate): Int {
            val basic: Long = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
            if (basic == 0L) return 0;
            return if (basic > 0) 1 else 2;
        }

        val o1 = orient(first, second, other.first);
        val o2 = orient(first, second, other.second);
        val o3 = orient(other.first, other.second, first);
        val o4 = orient(other.first, other.second, second);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        if (o1 == 0 && onSegment(first, other.first, second)) return true;
        if (o2 == 0 && onSegment(first, other.second, second)) return true;
        if (o3 == 0 && onSegment(other.first, first, other.second)) return true;
        if (o4 == 0 && onSegment(other.first, second, other.second)) return true;

        return false;
    }
}
