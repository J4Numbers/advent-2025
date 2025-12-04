#[derive(Copy, Clone, Hash, Eq, PartialEq, Debug)]
pub struct NumberPair {
    pub x: i32,
    pub y: i32,
}

#[derive(Copy, Clone, Hash, Eq, PartialEq, Debug)]
pub struct NodeDetails {
    pub loc: NumberPair,
    pub is_node: bool,
    pub neighbours: i32,
}

#[derive(Clone, Hash, Eq, PartialEq, Debug)]
pub struct FileReport {
    pub grid_layout: Vec<Vec<NodeDetails>>,
}
