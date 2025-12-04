extern crate pretty_env_logger;

mod fileio;
mod objects;
mod neighbours;

use std::env;
use clap::Parser;
use crate::neighbours::{calculate_neighbours, remove_nodes_with_max_neighbours};

#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct ProgArgs {
    #[arg(short, long)]
    input: String,

    #[arg(long, default_value_t = false)]
    enable_loop: bool,
}

/// Kick off the main program
fn main() {
    if env::var("RUST_LOG").is_err() {
        env::set_var("RUST_LOG", "info");
    }

    pretty_env_logger::init();

    let args = ProgArgs::parse();
    let mut file_contents = fileio::read_file(args.input);

    log::info!(
        "Found {:?} lines in our file",
        file_contents.grid_layout.len());

    let mut total_removed_nodes = 0;
    let mut iteration = 0;

    loop {
        iteration += 1;
        calculate_neighbours(&mut file_contents);
        let nodes_removed = remove_nodes_with_max_neighbours(&mut file_contents, 3);
        total_removed_nodes += nodes_removed;
        log::info!(
            "Removed {:?} ({:?}) nodes with fewer than 4 neighbours on iteration {:?}",
            nodes_removed, total_removed_nodes, iteration,
        );

        if !args.enable_loop || nodes_removed == 0 {
            break;
        }
    }

    log::info!(
        "Removed {:?} total nodes after {:?} iterations",
        total_removed_nodes, iteration
    )
}
