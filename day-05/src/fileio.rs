use std::io;
use std::fs::File;
use std::io::BufRead;
use std::path::Path;
use regex::Regex;
use crate::objects::{FileReport, Range};

/// Read a given file in according some basic rules, splitting it into different lines
/// as long as each line matches a given regex pattern.
///
/// # Arguments
///
/// * `file_loc` - The path location to the file that we're reading in
pub(crate) fn read_file<'h, P: std::convert::AsRef<Path>>(file_loc: P) -> FileReport {
    let mut file_report = FileReport{
        ranges: vec![],
        candidates: vec![],
    };
    let range_regex = Regex::new(r"^([0-9]+)-([0-9]+)$").unwrap();
    let candidate_regex = Regex::new(r"^([0-9]+)$").unwrap();

    let mut range_mode = true;

    if let Ok(lines) = read_lines(file_loc) {
        for test_line in lines {
            if let Ok(line) = test_line {
                if line == "" {
                    range_mode = false
                } else {
                    if range_mode {
                        for (low_val, high_val) in range_regex.captures_iter(&*line).map(|c| {
                            (
                                c.get(1).map_or("0", |m| m.as_str()).parse::<u64>().unwrap(),
                                c.get(2).map_or("0", |m| m.as_str()).parse::<u64>().unwrap(),
                            )
                        }) {
                            file_report.ranges.push(Range{low: low_val, high: high_val})
                        }
                    } else {
                        for candidate_val in candidate_regex.captures_iter(&*line).map(|c| {
                            c.get(1).map_or("0", |m| m.as_str()).parse::<u64>().unwrap()
                        }) {
                            file_report.candidates.push(candidate_val);
                        }
                    }
                }
            }
        }
    }
    file_report
}

/// Scan in all the lines of a given file, splitting them apart on newline and returning the
/// whole file, even if not all of the lines are valid.
///
/// # Arguments
///
/// * `file_loc` - The path location to the file we're reading in
fn read_lines<P>(file_loc: P) -> io::Result<io::Lines<io::BufReader<File>>> where P: AsRef<Path>, {
    let file = File::open(file_loc)?;
    Ok(io::BufReader::new(file).lines())
}

#[cfg(test)]
mod tests {
    use crate::objects::Range;
    use super::*;

    #[test]
    fn test_read_file_returns_correct_expectations() {
        let discovered_lines = read_file("inputs/test-input-file.txt");
        assert_eq!(discovered_lines, FileReport{
            ranges: vec![Range{low: 3, high: 10}, Range{low: 5, high: 15}],
            candidates: vec![1, 5, 20],
        })
    }

    #[test]
    fn test_read_file_can_deal_with_large_numbers() {
        let discovered_lines = read_file("inputs/test-big-number-input-file.txt");
        assert_eq!(discovered_lines, FileReport{
            ranges: vec![
                Range{low: 318557298670257, high: 320203701512914},
                Range{low: 257988476988123, high: 258475833446953},
                Range{low: 239506567904360, high: 239506567904360}
            ],
            candidates: vec![278008535048920, 552156651060434, 364818531904503],
        })
    }
}
