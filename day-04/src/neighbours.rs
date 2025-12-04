use crate::objects::{FileReport, NodeDetails};

fn calculate_surrounding_nodes(grid_layout: &Vec<Vec<NodeDetails>>, x_pos: usize, y_pos: usize, x_max: usize, y_max: usize) -> i32 {
    let mut discovered_surrounding = 0;
    if x_pos > 0 {
        // Top left
        if y_pos > 0 && grid_layout[y_pos-1][x_pos-1].is_node {
            discovered_surrounding += 1;
        }
        // Middle left
        if grid_layout[y_pos][x_pos-1].is_node {
            discovered_surrounding += 1;
        }
        // Bottom left
        if y_pos < (y_max - 1) && grid_layout[y_pos+1][x_pos-1].is_node {
            discovered_surrounding += 1
        }
    }

    // Top middle
    if y_pos > 0 && grid_layout[y_pos-1][x_pos].is_node {
        discovered_surrounding += 1;
    }
    // Bottom middle
    if y_pos < (y_max - 1) && grid_layout[y_pos+1][x_pos].is_node {
        discovered_surrounding += 1
    }

    if x_pos < (x_max - 1) {
        // Top right
        if y_pos > 0 && grid_layout[y_pos-1][x_pos+1].is_node {
            discovered_surrounding += 1;
        }
        // Middle right
        if grid_layout[y_pos][x_pos+1].is_node {
            discovered_surrounding += 1;
        }
        // Bottom right
        if y_pos < (y_max - 1) && grid_layout[y_pos+1][x_pos+1].is_node {
            discovered_surrounding += 1
        }
    }

    discovered_surrounding
}

pub(crate) fn calculate_neighbours(file_report: &mut FileReport) {
    let y_max = file_report.grid_layout.len();
    let x_max = file_report.grid_layout[0].len();

    let mut y_idx = 0;
    let mut x_idx = 0;

    while y_idx < y_max {
        while x_idx < x_max {
            let new_neighbours = calculate_surrounding_nodes(&file_report.grid_layout, x_idx, y_idx, x_max, y_max);
            file_report.grid_layout[y_idx][x_idx].neighbours = new_neighbours;
            log::debug!("Discovered {new_neighbours:?} against X:{x_idx:?} Y:{y_idx:?}");
            x_idx += 1;
        }
        x_idx = 0;
        y_idx += 1;
    }
}

pub(crate) fn remove_nodes_with_max_neighbours(file_report: &mut FileReport, max_neighbours: i32) -> i32 {
    let mut total = 0;
    file_report.grid_layout.iter_mut().for_each(|row| row.iter_mut().for_each(|node| {
        if node.is_node && node.neighbours <= max_neighbours {
            total += 1;
            node.is_node = false
        }
    }));

    total
}

#[cfg(test)]
mod tests {
    use crate::objects::{NodeDetails, NumberPair};
    use super::*;

    #[test]
    fn test_calculate_surrounding_nodes_is_accurate_in_nil_case() {
        let input = FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: false, neighbours: 0},
            ],
        ]};

        let discovered_nodes = calculate_surrounding_nodes(&input.grid_layout, 1, 1, 3, 3);
        assert_eq!(discovered_nodes, 0);
    }

    #[test]
    fn test_calculate_surrounding_nodes_is_accurate_on_borders() {
        let input = FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 1},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: false, neighbours: 1},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: false, neighbours: 1},
            ],
        ]};

        let top_left = calculate_surrounding_nodes(&input.grid_layout, 0, 0, 3, 3);
        let top_middle = calculate_surrounding_nodes(&input.grid_layout, 1, 0, 3, 3);
        let top_right = calculate_surrounding_nodes(&input.grid_layout, 2, 0, 3, 3);
        let middle_left = calculate_surrounding_nodes(&input.grid_layout, 0, 1, 3, 3);
        let middle = calculate_surrounding_nodes(&input.grid_layout, 1, 1, 3, 3);
        let middle_right = calculate_surrounding_nodes(&input.grid_layout, 2, 1, 3, 3);
        let bottom_left = calculate_surrounding_nodes(&input.grid_layout, 0, 2, 3, 3);
        let bottom_middle = calculate_surrounding_nodes(&input.grid_layout, 1, 2, 3, 3);
        let bottom_right = calculate_surrounding_nodes(&input.grid_layout, 2, 2, 3, 3);
        assert_eq!(top_left, 1);
        assert_eq!(top_middle, 1);
        assert_eq!(top_right, 1);
        assert_eq!(middle_left, 1);
        assert_eq!(middle, 0);
        assert_eq!(middle_right, 1);
        assert_eq!(bottom_left, 1);
        assert_eq!(bottom_middle, 1);
        assert_eq!(bottom_right, 1);
    }

    #[test]
    fn test_calculate_surrounding_nodes_is_accurate_on_for_multiple_nodes() {
        let input = FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: false, neighbours: 2},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: false, neighbours: 3},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: false, neighbours: 2},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: true, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: true, neighbours: 2},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: true, neighbours: 1},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 3}, is_node: false, neighbours: 2},
                NodeDetails{loc: NumberPair{x: 1, y: 3}, is_node: false, neighbours: 3},
                NodeDetails{loc: NumberPair{x: 2, y: 3}, is_node: false, neighbours: 3},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 4}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 4}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 4}, is_node: false, neighbours: 0},
            ],
        ]};

        let high_left = calculate_surrounding_nodes(&input.grid_layout, 0, 0, 3, 5);
        let high_middle = calculate_surrounding_nodes(&input.grid_layout, 1, 0, 3, 5);
        let high_right = calculate_surrounding_nodes(&input.grid_layout, 2, 0, 3, 5);
        let top_left = calculate_surrounding_nodes(&input.grid_layout, 0, 1, 3, 5);
        let top_middle = calculate_surrounding_nodes(&input.grid_layout, 1, 1, 3, 5);
        let top_right = calculate_surrounding_nodes(&input.grid_layout, 2, 1, 3, 5);
        let middle_left = calculate_surrounding_nodes(&input.grid_layout, 0, 2, 3, 5);
        let middle = calculate_surrounding_nodes(&input.grid_layout, 1, 2, 3, 5);
        let middle_right = calculate_surrounding_nodes(&input.grid_layout, 2, 2, 3, 5);
        let bottom_left = calculate_surrounding_nodes(&input.grid_layout, 0, 3, 3, 5);
        let bottom_middle = calculate_surrounding_nodes(&input.grid_layout, 1, 3, 3, 5);
        let bottom_right = calculate_surrounding_nodes(&input.grid_layout, 2, 3, 3, 5);
        let low_left = calculate_surrounding_nodes(&input.grid_layout, 0, 4, 3, 5);
        let low_middle = calculate_surrounding_nodes(&input.grid_layout, 1, 4, 3, 5);
        let low_right = calculate_surrounding_nodes(&input.grid_layout, 2, 4, 3, 5);
        assert_eq!(high_left, 0);
        assert_eq!(high_middle, 0);
        assert_eq!(high_right, 0);
        assert_eq!(top_left, 2);
        assert_eq!(top_middle, 3);
        assert_eq!(top_right, 2);
        assert_eq!(middle_left, 1);
        assert_eq!(middle, 2);
        assert_eq!(middle_right, 1);
        assert_eq!(bottom_left, 2);
        assert_eq!(bottom_middle, 3);
        assert_eq!(bottom_right, 2);
        assert_eq!(low_left, 0);
        assert_eq!(low_middle, 0);
        assert_eq!(low_right, 0);
    }

    #[test]
    fn test_calculate_neighbours_does_not_update_neighbours_in_nil_case() {
        let mut input = FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0},
            ]
        ]};

        calculate_neighbours(&mut input);

        assert_eq!(input, FileReport{ grid_layout: vec![
            vec![
                NodeDetails{ loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0}
            ],
        ]});
    }

    #[test]
    fn test_calculate_neighbours_updates_neighbours_in_simple_case() {
        let mut input = FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0},
            ]
        ]};

        calculate_neighbours(&mut input);

        assert_eq!(input, FileReport{ grid_layout: vec![
            vec![
                NodeDetails{ loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 1},
                NodeDetails{ loc: NumberPair{x: 1, y: 0}, is_node: true, neighbours: 0},
                NodeDetails{ loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 1}
            ],
        ]});
    }

    #[test]
    fn test_calculate_neighbours_updates_two_dimensional_neighbours_in_simple_case() {
        let mut input = FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: false, neighbours: 0},
            ],
        ]};

        calculate_neighbours(&mut input);

        assert_eq!(input, FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 1},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: false, neighbours: 1},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: false, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: false, neighbours: 1},
            ],
        ]});
    }

    #[test]
    fn test_calculate_surrounded_node_updates_two_dimensional_neighbours_accurately() {
        let mut input = FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: true, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: true, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: true, neighbours: 0},
            ],
        ]};

        calculate_neighbours(&mut input);

        assert_eq!(input, FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: true, neighbours: 3},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: true, neighbours: 5},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: true, neighbours: 3},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: true, neighbours: 5},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: true, neighbours: 8},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: true, neighbours: 5},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: true, neighbours: 3},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: true, neighbours: 5},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: true, neighbours: 3},
            ],
        ]});
    }

    #[test]
    fn test_calculate_multiple_neighbours_updates_directly_adjacent_nodes() {
        let mut input = FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: true, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: true, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 3}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 3}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 3}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 4}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 4}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 4}, is_node: false, neighbours: 0},
            ],
        ]};

        calculate_neighbours(&mut input);

        assert_eq!(input, FileReport{grid_layout: vec![
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 0}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 0}, is_node: false, neighbours: 0},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 1}, is_node: false, neighbours: 2},
                NodeDetails{loc: NumberPair{x: 1, y: 1}, is_node: false, neighbours: 3},
                NodeDetails{loc: NumberPair{x: 2, y: 1}, is_node: false, neighbours: 2},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 2}, is_node: true, neighbours: 1},
                NodeDetails{loc: NumberPair{x: 1, y: 2}, is_node: true, neighbours: 2},
                NodeDetails{loc: NumberPair{x: 2, y: 2}, is_node: true, neighbours: 1},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 3}, is_node: false, neighbours: 2},
                NodeDetails{loc: NumberPair{x: 1, y: 3}, is_node: false, neighbours: 3},
                NodeDetails{loc: NumberPair{x: 2, y: 3}, is_node: false, neighbours: 2},
            ],
            vec![
                NodeDetails{loc: NumberPair{x: 0, y: 4}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 1, y: 4}, is_node: false, neighbours: 0},
                NodeDetails{loc: NumberPair{x: 2, y: 4}, is_node: false, neighbours: 0},
            ],
        ]});
    }

    #[test]
    fn test_count_total_nodes_with_max_neighbours_includes_simple_node_case() {
        let mut input = FileReport {
            grid_layout: vec![
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 0 }, is_node: false, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 1, y: 0 }, is_node: false, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 2, y: 0 }, is_node: false, neighbours: 1 },
                ],
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 1 }, is_node: false, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 1, y: 1 }, is_node: true, neighbours: 0 },
                    NodeDetails { loc: NumberPair { x: 2, y: 1 }, is_node: false, neighbours: 1 },
                ],
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 4 }, is_node: false, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 1, y: 4 }, is_node: false, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 2, y: 4 }, is_node: false, neighbours: 1 },
                ],
            ]
        };

        let total_found = remove_nodes_with_max_neighbours(&mut input, 3);
        assert_eq!(total_found, 1)
    }

    #[test]
    fn test_count_total_nodes_with_max_neighbours_includes_cross_node_case() {
        let mut input = FileReport {
            grid_layout: vec![
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 0 }, is_node: true, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 1, y: 0 }, is_node: false, neighbours: 3 },
                    NodeDetails { loc: NumberPair { x: 2, y: 0 }, is_node: true, neighbours: 1 },
                ],
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 1 }, is_node: false, neighbours: 3 },
                    NodeDetails { loc: NumberPair { x: 1, y: 1 }, is_node: true, neighbours: 4 },
                    NodeDetails { loc: NumberPair { x: 2, y: 1 }, is_node: false, neighbours: 3 },
                ],
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 4 }, is_node: true, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 1, y: 4 }, is_node: false, neighbours: 3 },
                    NodeDetails { loc: NumberPair { x: 2, y: 4 }, is_node: true, neighbours: 1 },
                ],
            ]
        };

        let total_found = remove_nodes_with_max_neighbours(&mut input, 3);
        assert_eq!(total_found, 4);
        assert_eq!(input, FileReport {
            grid_layout: vec![
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 0 }, is_node: false, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 1, y: 0 }, is_node: false, neighbours: 3 },
                    NodeDetails { loc: NumberPair { x: 2, y: 0 }, is_node: false, neighbours: 1 },
                ],
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 1 }, is_node: false, neighbours: 3 },
                    NodeDetails { loc: NumberPair { x: 1, y: 1 }, is_node: true, neighbours: 4 },
                    NodeDetails { loc: NumberPair { x: 2, y: 1 }, is_node: false, neighbours: 3 },
                ],
                vec![
                    NodeDetails { loc: NumberPair { x: 0, y: 4 }, is_node: false, neighbours: 1 },
                    NodeDetails { loc: NumberPair { x: 1, y: 4 }, is_node: false, neighbours: 3 },
                    NodeDetails { loc: NumberPair { x: 2, y: 4 }, is_node: false, neighbours: 1 },
                ],
            ]
        });
    }
}
