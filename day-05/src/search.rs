use crate::objects::Range;

pub(crate) fn reorganise_ranges_to_be_joined(ranges: Vec<Range>) -> Vec<Range> {
    let mut new_ranges: Vec<Range> = vec![];

    // Sort ranges by their low values
    let mut sorted_ranges = ranges.clone();
    sorted_ranges.sort_by(|a, b| a.low.cmp(&b.low));

    let mut working_low = 0;
    let mut working_high = 0;

    // Iterate over all ranges
    sorted_ranges.iter().for_each(|r| {
        // If the next range up is not overlapping or sequential...
        if r.low > working_high + 1 {
            // And we're not on the first loop
            if working_low >= 1 {
                // Add newly computed ranges to our final result
                new_ranges.push(Range{low: working_low, high: working_high});
            }
            // We're starting from afresh, so this is a new low
            working_low = r.low;
        }
        // If we have a new high, then overwrite the high
        if r.high > working_high {
            working_high = r.high
        }
    });

    // Add the final discovered range to the range set
    new_ranges.push(Range{low: working_low, high: working_high});

    new_ranges
}

pub(crate) fn total_range_space(ranges: &Vec<Range>) -> u64 {
    let mut total_space = 0;

    ranges.iter().for_each(|r| {
       total_space += r.high - r.low + 1;
    });

    total_space
}

pub(crate) fn identify_candidates_inside_ranges(ranges: Vec<Range>, candidates: Vec<u64>) -> u64 {
    let mut included_candidates = 0;

    let mut range_idx: usize = 0;

    candidates.iter().for_each(|c| {
        loop {
            if range_idx >= ranges.len() {
                log::debug!("Candidate {:?} exceeds our highest range of {:?}",
                    c, ranges[range_idx-1].high);
                break
            }
            if c >= &ranges[range_idx].low {
                if c > &ranges[range_idx].high {
                    log::debug!("Comparing candidate {:?} against range <{:?}:{:?}> - UNCONTAINED (above)",
                        c, ranges[range_idx].low, ranges[range_idx].high);
                    range_idx += 1
                } else {
                    // Within range. Do nothing.
                    log::debug!("Comparing candidate {:?} against range <{:?}:{:?}> - CONTAINED",
                        c, ranges[range_idx].low, ranges[range_idx].high);
                    included_candidates += 1;
                    break
                }
            } else {
                log::debug!("Comparing candidate {:?} against range <{:?}:{:?}> - UNCONTAINED (below)",
                    c, ranges[range_idx].low, ranges[range_idx].high);
                break
            }
        }
    });

    // Assume ranges and vectors are sorted appropriately
    // Iterate over the candidates by comparing them with the currently selected range
    // until that range max is exceeded
    // Once the range max is exceeded, select the next range and continue

    included_candidates
}

#[cfg(test)]
mod tests {
    use crate::objects::{Range};
    use super::*;

    #[test]
    fn test_reorganise_ranges_does_not_reorganise_non_overlapping_ranges() {
        let input = vec![Range{low: 3, high: 5}, Range{low: 7, high: 10}];

        let new_ranges = reorganise_ranges_to_be_joined(input);
        assert_eq!(new_ranges, vec![Range{low: 3, high: 5}, Range{low: 7, high: 10}]);
    }

    #[test]
    fn test_reorganise_ranges_reorganises_directly_overlapping_ranges() {
        let input = vec![Range{low: 3, high: 5}, Range{low: 3, high: 10}];

        let new_ranges = reorganise_ranges_to_be_joined(input);
        assert_eq!(new_ranges, vec![Range{low: 3, high: 10}]);
    }

    #[test]
    fn test_reorganise_ranges_reorganises_sequential_ranges_ranges() {
        let input = vec![Range{low: 3, high: 5}, Range{low: 6, high: 10}];

        let new_ranges = reorganise_ranges_to_be_joined(input);
        assert_eq!(new_ranges, vec![Range{low: 3, high: 10}]);
    }

    #[test]
    fn test_reorganise_ranges_reduces_wholly_overlapping_ranges() {
        let input = vec![Range{low: 3, high: 6}, Range{low: 4, high: 5}];

        let new_ranges = reorganise_ranges_to_be_joined(input);
        assert_eq!(new_ranges, vec![Range{low: 3, high: 6}]);
    }

    #[test]
    fn test_reorganise_ranges_chains_intersecting_ranges() {
        let input = vec![
            Range{low: 3, high: 5},
            Range{low: 5, high: 15},
            Range{low: 14, high: 20},
            Range{low: 10, high: 21},
            Range{low: 15, high: 20},
            Range{low: 20, high: 25},
        ];

        let new_ranges = reorganise_ranges_to_be_joined(input);
        assert_eq!(new_ranges, vec![Range{low: 3, high: 25}]);
    }

    #[test]
    fn test_total_range_space_is_accurate_on_single_ranges() {
        let input = vec![Range{low: 3, high: 5}];

        let range_space = total_range_space(&input);
        assert_eq!(range_space, 3);
    }

    #[test]
    fn test_total_range_space_is_accurate_on_flat_ranges() {
        let input = vec![Range{low: 3, high: 3}];

        let range_space = total_range_space(&input);
        assert_eq!(range_space, 1);
    }

    #[test]
    fn test_total_range_space_is_accurate_on_multiple_ranges() {
        let input = vec![Range{low: 3, high: 5}, Range{low: 100, high: 105}];

        let range_space = total_range_space(&input);
        assert_eq!(range_space, 9);
    }

    #[test]
    fn test_identify_candidates_inside_ranges_is_accurate_on_outside_boundary() {
        let range_input = vec![Range{low: 3, high: 5}];
        let candidate_input = vec![2, 6];

        let outside_range = identify_candidates_inside_ranges(range_input, candidate_input);
        assert_eq!(outside_range, 0);
    }

    #[test]
    fn test_identify_candidates_inside_ranges_is_accurate_on_inside_boundary() {
        let range_input = vec![Range{low: 3, high: 5}];
        let candidate_input = vec![3, 5];

        let outside_range = identify_candidates_inside_ranges(range_input, candidate_input);
        assert_eq!(outside_range, 2);
    }

    #[test]
    fn test_identify_candidates_inside_ranges_is_accurate_on_varied_ranges() {
        let range_input = vec![Range{low: 50, high: 100}, Range{low: 102, high: 105}];
        let candidate_input = vec![10, 20, 30, 49, 50, 51, 75, 99, 100, 101, 102, 104, 105, 106, 1000];

        let outside_range = identify_candidates_inside_ranges(range_input, candidate_input);
        assert_eq!(outside_range, 8);
    }

    #[test]
    fn test_identify_candidates_large_number_test() {
        let range_input = vec![Range{low: 545451540103685, high: 551641667068045}];
        let candidate_input = vec![551602948015558];

        let outside_range = identify_candidates_inside_ranges(range_input, candidate_input);
        assert_eq!(outside_range, 1);
    }
}
