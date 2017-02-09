extern crate clap;
use clap::{Arg, App};

fn main() {
	// Setup the command line interface
	let matches = App::new("Em")
						.version("2.0.0")
						// .about("An interpreted, dynamically typed language")
						.arg(Arg::with_name("verbose")
								.short("v")
								.help("Set the verbosity level")
								.multiple(true))
						.arg(Arg::with_name("file")
								.required(true))
						.get_matches();
	
	//Determining the verbosity
	let verbosity: u64 = matches.occurrences_of("verbose");
	if matches.occurrences_of("verbose") > 0 {
		println!("verbosity level: {}", matches.occurrences_of("verbose"));
	}
	

	println!("Hello, world! lol");
}
