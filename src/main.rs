#![feature(io)]
extern crate clap;
use clap::{Arg, App};
mod lexeme_type;
mod lexeme;
mod lexer;
mod pushback_reader;

fn main() {
	// Setup the command line interface
	let matches = App::new("Em")
						.version("2.0.0")
						.about("An interpreted, dynamically typed language")
						.arg(Arg::with_name("verbose")
								.short("v")
								.help("Set the verbosity level")
								.multiple(true))
						.arg(Arg::with_name("file")
								.required(true))
						.get_matches();
	
	//Determining the verbosity
	let verbosity: u64 = matches.occurrences_of("verbose");
	if verbosity > 0 {
		println!("verbosity level: {}", verbosity);
	}

	println!("Hello, world! lol");
}


#[cfg(test)]
mod test {
	#[test]
	fn test_assert() {
		assert!(true);
	}
}