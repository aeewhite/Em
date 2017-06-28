use std::io::Read;
use std::io::Chars;
use std::collections::vec_deque::VecDeque;

pub struct PushbackCharReader<T>{
	source: T,
	source_iter: Chars<T>,
	line: u32,
	col: u32,
	buffer: VecDeque<char>
}

impl<T: Read + Clone> PushbackCharReader<T>{
	pub fn new(src: T) -> PushbackCharReader<T>{
		let source_clone = src.clone();
		let iter = src.chars();
		let queue = VecDeque::<char>::with_capacity(10);
		return PushbackCharReader{source: source_clone,
									source_iter: iter, 
									line: 1, 
									col: 1, 
									buffer: queue};
	}

	pub fn pushback(&mut self, c: char){
		if c == '\n'{
			self.line -= 1;
		}
		else{
			self.col -= 1;
		}
		if self.buffer.len() < self.buffer.capacity(){
			self.buffer.push_back(c);
		}
		else{
			panic!("PushbackCharReader buffer is full");
		}
	}

	pub fn read(&mut self) -> Option<char>{
		println!("Col = {:?}", self.col);
		let c;
		if !self.buffer.is_empty() {
			c = self.buffer.pop_front();
		}
		else{
			if let Some(res) = self.source_iter.next(){
				c = res.ok();
			}
			else{
				c = None
			}
		}
		if c.is_some() && c.unwrap() == '\n' {
			self.line += 1;
			self.col = 1;
		}
		else{
			self.col += 1;
		}
		return c;
	}

	pub fn get_line(&self) -> u32{
		return self.line;
	}

	pub fn get_col(&self) -> u32{
		return self.col;
	}

	pub fn skip_whitespace(&mut self){
		if let Some(c) = self.read(){
			if c.is_whitespace(){
				self.skip_whitespace()
			}
			else if c == '#'{
				self.skip_line();
				self.skip_whitespace()
			}
			else{
				self.pushback(c)
			}	
		}
	}

	pub fn skip_line(&mut self){
		while let Some(c) = self.read(){
			if c == '\n' { break; }
		}
	}
}

#[cfg(test)]
mod test {
	use super::*;
	use std::io::Cursor;

	#[test]
	fn test_str_to_chars_len(){
		let test_str = "Test 🐘";
		assert!(test_str.chars().count() == 6);
	}

	#[test]
	fn test_read(){
		let source = Cursor::new("test 🐘.");
		let mut reader = PushbackCharReader::new(source);
		assert!(reader.read().unwrap() == 't');
		assert!(reader.read().unwrap() == 'e');
		assert!(reader.read().unwrap() == 's');
		assert!(reader.read().unwrap() == 't');
		assert!(reader.read().unwrap() == ' ');
		assert!(reader.read().unwrap() == '🐘');
		assert!(reader.read().unwrap() == '.');
	}

	#[test]
	fn test_col_inc(){
		let source = Cursor::new("test 🐘");
		let mut reader = PushbackCharReader::new(source);
		reader.read().unwrap();
		reader.read().unwrap();
		assert!(reader.get_col() == 3);
		reader.read().unwrap();
		assert!(reader.get_col() == 4);
	}

	#[test]
	fn test_line_inc(){
		let source = Cursor::new("test \n🐘");
		let mut reader = PushbackCharReader::new(source);
		reader.read().unwrap(); // t
		reader.read().unwrap(); // e
		reader.read().unwrap(); // s
		assert!(reader.get_line() == 1);
		reader.read().unwrap(); // t
		reader.read().unwrap(); // {space}
		reader.read().unwrap(); // {newline}
		assert!(reader.get_line() == 2);
		reader.read().unwrap(); // 🐘
		assert!(reader.get_line() == 2);
	}

	#[test]
	fn test_col_reset(){
		let source = Cursor::new("a\nd");
		let mut reader = PushbackCharReader::new(source);
		reader.read().unwrap();
		reader.read().unwrap();
		assert!(reader.get_line() == 2);
		assert!(reader.get_col() == 1);
		reader.read().unwrap();
	}

	#[test]
	fn test_pushback(){
		let source = Cursor::new("test 🐘");
		let mut reader = PushbackCharReader::new(source);
		assert!(reader.read().unwrap() == 't');
		reader.pushback('t');
		assert!(reader.read().unwrap() == 't');
		assert!(reader.read().unwrap() == 'e'); // Back to the original source
		assert!(reader.read().unwrap() == 's');
		reader.pushback('s');
		assert!(reader.read().unwrap() == 's');
		assert!(reader.read().unwrap() == 't');
		assert!(reader.read().unwrap() == ' ');
		assert!(reader.read().unwrap() == '🐘');
	}

	#[test]
	fn test_read_from_source_again(){
		let source = Cursor::new("test 🐘.");
		let mut reader = PushbackCharReader::new(source);
		assert!(reader.read().unwrap() == 't');
		assert!(reader.read().unwrap() == 'e');
		let mut string: String = String::new();
		reader.source.read_to_string(&mut string).unwrap();
		assert!(string == "test 🐘.");
	}

	#[test]
	#[should_panic(expected = "buffer is full")]
	fn test_pushback_overflow(){
		let source = Cursor::new("aaaaaaaaaaaaaaaaaaaaaaaaa");
		let mut reader = PushbackCharReader::new(source);
		for _ in 0..15 {
		    reader.read();
		}
		loop {
			reader.pushback('a');
		}
	}
}