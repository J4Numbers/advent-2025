use std::io;
use std::fs::File;
use std::io::BufRead;
use std::path::Path;
use regex::Regex;
use crate::objects::{FileReport, NodeDetails, NumberPair};

/// Read a given file in according some basic rules, splitting it into different lines
/// as long as each line matches a given regex pattern.
///
/// # Arguments
///
/// * `file_loc` - The path location to the file that we're reading in
pub(crate) fn read_file<'h, P: std::convert::AsRef<Path>>(file_loc: P) -> FileReport {
    let mut file_report = FileReport{
        grid_layout: vec![],
    };
    let re = Regex::new(r"([.@])").unwrap();

    let mut y_idx = 0;

    if let Ok(lines) = read_lines(file_loc) {
        for test_line in lines {
            if let Ok(line) = test_line {
                let mut file_line: Vec<NodeDetails> = vec![];
                let mut x_idx = 0;
                for (_, [valid_line]) in re.captures_iter(&*line).map(|c| { c.extract()}) {
                    file_line.push(NodeDetails{
                        loc: NumberPair{x: x_idx, y: y_idx },
                        is_node: valid_line == "@",
                        neighbours: 0,
                    });
                    x_idx += 1;
                }
                file_report.grid_layout.push(file_line);
                y_idx += 1;
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
        assert_eq!(discovered_lines, FileReport{ grid_layout: vec![
            vec![
                NodeDetails{ loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 1, y: 0}, is_node: true, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0}
            ],
            vec![
                NodeDetails{ loc: NumberPair{x: 0, y: 1}, is_node: true, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 1, y: 1}, is_node: false, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 2, y: 1}, is_node: true, neighbours: 0}
            ],
            vec![
                NodeDetails{ loc: NumberPair{x: 0, y: 2}, is_node: false, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 1, y: 2}, is_node: true, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 2, y: 2}, is_node: false, neighbours: 0}
            ],
        ]})
    }
}
