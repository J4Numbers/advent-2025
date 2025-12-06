use crate::objects::{Calculation, FileReport, RtlFileReport};

pub(crate) fn regenerate_calculations(file_report: &FileReport) -> Vec<Calculation> {
    let mut calculation_list = vec![];

    let mut calc_idx = 0;
    while calc_idx < file_report.op_mut_list.len() {
        calculation_list.push(Calculation {
            value_list: file_report.value_lists.iter().map(|v| v[calc_idx]).collect(),
            op_mul: file_report.op_mut_list[calc_idx]
        });
        calc_idx += 1
    }

    calculation_list
}

pub(crate) fn regenerate_rtl_calculations(file_report: &RtlFileReport) -> Vec<Calculation> {
    let mut calculation_list = vec![];

    let mut calc_idx = 0;
    while calc_idx < file_report.op_mut_list.len() {
        let calculation_set: Vec<String> = file_report.value_lists.iter().map(|v| v[calc_idx].to_string()).collect();
        let mut rtl_calculation_set: Vec<u64> = vec![];
        let mut rev_idx = calculation_set[0].len();
        while rev_idx > 0 {
            let mut working_number: u64 = 0;
            calculation_set.iter().for_each(|val| {
                let discovered_char = val.chars().nth(rev_idx-1).unwrap();
                if discovered_char != ' ' {
                    working_number = (working_number * 10) + discovered_char.to_digit(10).unwrap() as u64;
                }
            });
            rtl_calculation_set.push(working_number);
            rev_idx -= 1;
        }
        calculation_list.push(Calculation {
            value_list: rtl_calculation_set,
            op_mul: file_report.op_mut_list[calc_idx]
        });
        calc_idx += 1
    }

    calculation_list
}

pub(crate) fn calculate_column_widths(file_report: &FileReport) -> Vec<usize> {
    let mut width_list = vec![];

    if file_report.value_lists.len() > 0 {
        width_list = file_report.value_lists[0].iter().map(|v| v.to_string().len()).collect()
    }

    let mut list_idx = 1;
    while list_idx < file_report.value_lists.len() {
        let mut val_idx = 0;
        file_report.value_lists[list_idx].iter().for_each(|v| {
            let val_len = v.to_string().len();
            if val_len > width_list[val_idx] {
                width_list[val_idx] = val_len;
            }
            val_idx += 1;
        });
        list_idx += 1
    }

    width_list
}

pub(crate) fn generate_total(calculation_list: &Vec<Calculation>) -> u64 {
    let mut grand_total = 0;

    calculation_list.iter().for_each(|c| {
        log::debug!("Inspecting calculation of values {:?} where multiplication is {:?}",
            c.value_list, c.op_mul);

        let mut current = c.value_list[0];
        let mut val_idx = 1;
        while val_idx < c.value_list.len() {
            if c.op_mul {
                current *= c.value_list[val_idx];
            } else {
                current += c.value_list[val_idx];
            }
            val_idx += 1;
        }

        log::debug!("Adding new found calculation result of {:?} to ongoing total {:?} = {:?}",
            current, grand_total, grand_total + current);
        grand_total += current
    });

    grand_total
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_regenerate_calculations_is_accurate_for_simple_file_report() {
        let input = FileReport{
            value_lists: vec![
                vec![3, 2],
                vec![6, 5],
                vec![1, 10],
            ],
            op_mut_list: vec![true, false],
        };
        let calculation = regenerate_calculations(&input);
        assert_eq!(calculation, vec![
            Calculation{value_list: vec![3, 6, 1], op_mul: true},
            Calculation{value_list: vec![2, 5, 10], op_mul: false},
        ])
    }

    #[test]
    fn test_regenerate_rtl_calculations_is_accurate_for_simple_file_report() {
        let input = RtlFileReport{
            value_lists: vec![
                vec!["13".parse().unwrap(), " 2".parse().unwrap()],
                vec![" 6".parse().unwrap(), "5 ".parse().unwrap()],
                vec![" 1".parse().unwrap(), "10".parse().unwrap()],
            ],
            op_mut_list: vec![true, false],
        };
        let calculation = regenerate_rtl_calculations(&input);
        assert_eq!(calculation, vec![
            Calculation{value_list: vec![361, 1], op_mul: true},
            Calculation{value_list: vec![20, 51], op_mul: false},
        ])
    }

    #[test]
    fn test_calculate_width_list_is_accurate() {
        let input = FileReport{
            value_lists: vec![
                vec![1, 1, 123, 134, 1],
                vec![2, 12, 21, 51221, 1521],
                vec![2, 2, 2, 21, 122],
                vec![2, 2, 2, 21, 21516],
            ],
            op_mut_list: vec![false, true, false, true],
        };
        let widths = calculate_column_widths(&input);
        assert_eq!(widths, vec![1, 2, 3, 5, 5]);
    }

    #[test]
    fn test_generate_total_is_accurate_for_addition() {
        let input = vec![
            Calculation{value_list: vec![1, 2, 3], op_mul: false},
            Calculation{value_list: vec![19, 12, 9], op_mul: false},
        ];
        let total = generate_total(&input);
        assert_eq!(total, 46);
    }

    #[test]
    fn test_generate_total_is_accurate_for_multiplication() {
        let input = vec![
            Calculation{value_list: vec![1, 2, 3], op_mul: true},
            Calculation{value_list: vec![19, 12, 9], op_mul: true},
        ];
        let total = generate_total(&input);
        assert_eq!(total, 2058);
    }
}
