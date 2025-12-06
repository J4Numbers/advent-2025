use std::io;
use std::fs::File;
use std::io::BufRead;
use std::path::Path;
use regex::Regex;
use crate::objects::{FileReport, RtlFileReport};

/// Read a given file in according some basic rules, splitting it into different lines
/// as long as each line matches a given regex pattern.
///
/// # Arguments
///
/// * `file_loc` - The path location to the file that we're reading in
pub(crate) fn read_file<'h, P: std::convert::AsRef<Path>>(file_loc: P) -> FileReport {
    let mut file_report = FileReport{
        value_lists: vec![],
        op_mut_list: vec![],
    };
    let val_line_re = Regex::new(r"^( *[0-9]+)+$").unwrap();
    let op_line_re = Regex::new(r"^( *[*+])+$").unwrap();

    let val_re = Regex::new(r"([0-9]+)").unwrap();
    let op_re = Regex::new(r"([*+])").unwrap();

    if let Ok(lines) = read_lines(file_loc) {
        for test_line in lines {
            if let Ok(line) = test_line {
                if val_line_re.is_match(&line) {
                    let mut val_file_line: Vec<u64> = vec![];
                    for (_, [val_item]) in val_re.captures_iter(&*line).map(|c| { c.extract() }) {
                        val_file_line.push(val_item.parse::<u64>().unwrap());
                    }
                    file_report.value_lists.push(val_file_line);
                }
                if op_line_re.is_match(&line) {
                    for (_, [op_item]) in op_re.captures_iter(&*line).map(|c| { c.extract() }) {
                        file_report.op_mut_list.push(op_item == "*");
                    }
                }
            }
        }
    }
    file_report
}

pub(crate) fn rescan_file_rtl<'h, P: std::convert::AsRef<Path>>(file_loc: P, column_widths: &Vec<usize>) -> RtlFileReport {
    let mut file_report = RtlFileReport{
        value_lists: vec![],
        op_mut_list: vec![],
    };
    let op_line_re = Regex::new(r"^( *[*+])+$").unwrap();
    let op_re = Regex::new(r"([*+])").unwrap();

    if let Ok(lines) = read_lines(file_loc) {
        for test_line in lines {
            if let Ok(line) = test_line {
                if op_line_re.is_match(&line) {
                    for (_, [op_item]) in op_re.captures_iter(&*line).map(|c| { c.extract() }) {
                        file_report.op_mut_list.push(op_item == "*");
                    }
                } else {
                    let mut rtl_line = vec![];
                    let mut read_idx = 0;
                    column_widths.iter().for_each(|width| {
                        if read_idx + width < line.len() {
                            rtl_line.push(line[read_idx..(read_idx + width)].to_string());
                        } else {
                            let final_val = line[read_idx..].to_string();
                            let final_len = final_val.len();
                            rtl_line.push(final_val + " ".repeat(width-final_len).as_str())
                        }
                        read_idx += width + 1;
                    });
                    file_report.value_lists.push(rtl_line);
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
    use super::*;

    #[test]
    fn read_file_returns_correct_expectations() {
        let discovered_lines = read_file("inputs/test-input-file.txt");
        assert_eq!(discovered_lines, FileReport{
            value_lists: vec![
                vec![5, 10, 20],
                vec![2, 2, 1]
            ],
            op_mut_list: vec![true, true, false],
        })
    }

    #[test]
    fn test_read_unaligned_file_returns_correct_expectations() {
        let discovered_lines = read_file("inputs/baseline-test.txt");
        assert_eq!(discovered_lines, FileReport{
            value_lists: vec![
                vec![123, 328, 51, 64],
                vec![45, 64, 387, 23],
                vec![6, 98, 215, 314],
            ],
            op_mut_list: vec![true, false, true, false],
        })
    }

    #[test]
    fn test_rtl_read_short_col_file_returns_correct_expectations() {
        let col_widths = vec![1, 2, 2];
        let discovered_lines = rescan_file_rtl("inputs/test-input-file.txt", &col_widths);
        assert_eq!(discovered_lines, RtlFileReport{
            value_lists: vec![
                vec!["5".parse().unwrap(), "10".parse().unwrap(), "20".parse().unwrap()],
                vec!["2".parse().unwrap(), " 2".parse().unwrap(), " 1".parse().unwrap()],
            ],
            op_mut_list: vec![true, true, false],
        })
    }

    #[test]
    fn test_rtl_read_mixed_col_file_returns_correct_expectations() {
        let col_widths = vec![3, 3, 3, 3];
        let discovered_lines = rescan_file_rtl("inputs/baseline-test.txt", &col_widths);
        assert_eq!(discovered_lines, RtlFileReport{
            value_lists: vec![
                vec!["123".parse().unwrap(), "328".parse().unwrap(), " 51".parse().unwrap(), "64 ".parse().unwrap()],
                vec![" 45".parse().unwrap(), "64 ".parse().unwrap(), "387".parse().unwrap(), "23 ".parse().unwrap()],
                vec!["  6".parse().unwrap(), "98 ".parse().unwrap(), "215".parse().unwrap(), "314".parse().unwrap()],
            ],
            op_mut_list: vec![true, false, true, false],
        })
    }
}
