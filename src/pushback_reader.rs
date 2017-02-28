use std::io::Read;
use std::io::Chars;
use std::collections::vec_deque::VecDeque;

pub struct PushbackCharReader<T>{
	source_iter: Chars<T>,
	line: u32,
	col: u32,
	buffer: VecDeque<char>
}

impl<T: Read> PushbackCharReader<T>{
	fn new(src: T) -> PushbackCharReader<T>{
		let mut iter = src.chars();
		let mut buf = Vec::<char>::new();
		let mut queue = VecDeque::<char>::with_capacity(10);
		return PushbackCharReader{source_iter: iter, 
									line: 1, 
									col: 1, 
									buffer: queue};
	}

	fn pushback(&mut self, c: char){
		if self.buffer.len() < self.buffer.capacity(){
			self.buffer.push_back(c);
		}
		else{
			panic!("PushbackCharReader buffer is full");
		}
	}

	fn read(&mut self) -> char{
		if !self.buffer.is_empty() {
			return self.buffer.pop_front().unwrap();
		}
		else{
			let c = self.source_iter.next().unwrap().unwrap();
			return c;
		}
	}

	fn get_line(&self) -> u32{
		return self.line;
	}

	fn get_col(&self) -> u32{
		return self.col;
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
		let mut source = Cursor::new("test 🐘");
		let mut reader = PushbackCharReader::new(&mut source);
		assert!(reader.read() == 't');
		assert!(reader.read() == 'e');
		assert!(reader.read() == 's');
		assert!(reader.read() == 't');
		assert!(reader.read() == ' ');
		assert!(reader.read() == '🐘');
	}

	#[test]
	fn test_pushback(){
		let mut source = Cursor::new("test 🐘");
		let mut reader = PushbackCharReader::new(&mut source);
		assert!(reader.read() == 't');
		reader.pushback('t');
		assert!(reader.read() == 't');
	}

	#[test]
	#[should_panic(expected = "buffer is full")]
	fn test_pushback_overflow(){
		let mut source = Cursor::new("test 🐘");
		let mut reader = PushbackCharReader::new(&mut source);
		loop {
			reader.pushback('a');
		}
	}
}