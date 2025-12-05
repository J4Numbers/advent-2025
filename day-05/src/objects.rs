#[derive(Copy, Clone, Hash, Eq, PartialEq, Debug)]
pub struct Range {
    pub low: u64,
    pub high: u64,
}

#[derive(Clone, Hash, Eq, PartialEq, Debug)]
pub struct FileReport {
    pub ranges: Vec<Range>,
    pub candidates: Vec<u64>
}
