#[derive(Clone, Hash, Eq, PartialEq, Debug)]
pub struct Calculation {
    pub value_list: Vec<u64>,
    pub op_mul: bool,
}

#[derive(Clone, Hash, Eq, PartialEq, Debug)]
pub struct FileReport {
    pub value_lists: Vec<Vec<u64>>,
    pub op_mut_list: Vec<bool>,
}

#[derive(Clone, Hash, Eq, PartialEq, Debug)]
pub struct RtlFileReport {
    pub value_lists: Vec<Vec<String>>,
    pub op_mut_list: Vec<bool>,
}
