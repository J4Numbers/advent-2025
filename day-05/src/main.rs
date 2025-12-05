extern crate pretty_env_logger;

mod fileio;
mod objects;
mod search;

use std::env;
use clap::Parser;
use crate::search::{identify_candidates_inside_ranges, reorganise_ranges_to_be_joined, total_range_space};

#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct ProgArgs {
    #[arg(short, long)]
    input: String,
}

/// Kick off the main program
fn main() {
    if env::var("RUST_LOG").is_err() {
        env::set_var("RUST_LOG", "info");
    }

    pretty_env_logger::init();

    let args = ProgArgs::parse();
    let file_contents = fileio::read_file(args.input);

    log::info!(
        "Found {:?} base ranges and {:?} search candidates in our file",
        file_contents.ranges.len(), file_contents.candidates.len());

    let resorted_ranges = reorganise_ranges_to_be_joined(file_contents.ranges);
    let mut resorted_candidates = file_contents.candidates.clone();
    resorted_candidates.sort();
    let total_range_space = total_range_space(&resorted_ranges);

    log::info!(
        "Recalculated {:?} new ranges from the original with a total range space of {:?}",
        resorted_ranges.len(), total_range_space);

    let aligned_candidates = identify_candidates_inside_ranges(
        resorted_ranges, resorted_candidates);

    log::info!(
        "Discovered {:?} candidates within scoped bounds",
        aligned_candidates
    )
}
