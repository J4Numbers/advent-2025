extern crate pretty_env_logger;

mod fileio;
mod objects;
mod calculator;

use std::env;
use clap::Parser;
use crate::calculator::{calculate_column_widths, generate_total, regenerate_calculations, regenerate_rtl_calculations};
use crate::objects::Calculation;

#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct ProgArgs {
    #[arg(short, long)]
    input: String,

    #[arg(long, default_value_t = false)]
    rtl: bool,
}

/// Kick off the main program
fn main() {
    if env::var("RUST_LOG").is_err() {
        env::set_var("RUST_LOG", "info");
    }

    pretty_env_logger::init();

    let args = ProgArgs::parse();
    let file_contents = fileio::read_file(&args.input);

    log::info!(
        "Found {:?} value lines in our file",
        file_contents.value_lists.len());

    let calculation_list: Vec<Calculation>;

    if args.rtl {
        let width_list = calculate_column_widths(&file_contents);
        let rtl_file = fileio::rescan_file_rtl(&args.input, &width_list);
        calculation_list = regenerate_rtl_calculations(&rtl_file);
    } else {
        calculation_list = regenerate_calculations(&file_contents);
    }

    let grand_total = generate_total(&calculation_list);

    log::info!(
        "Found grand total of {:?} after {:?} calculations",
        grand_total,calculation_list.len(),
    )
}
