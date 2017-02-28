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
		let c;
		if !self.buffer.is_empty() {
			c = self.buffer.pop_front().unwrap();
		}
		else{
			c = self.source_iter.next().unwrap().unwrap();
		}
		if c == '\n' {
			self.line += 1;
			self.col = 1;
		}
		else{
			self.col += 1;
		}
		return c;
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
		let mut source = Cursor::new("test 🐘.");
		let mut reader = PushbackCharReader::new(&mut source);
		assert!(reader.read() == 't');
		assert!(reader.read() == 'e');
		assert!(reader.read() == 's');
		assert!(reader.read() == 't');
		assert!(reader.read() == ' ');
		assert!(reader.read() == '🐘');
		assert!(reader.read() == '.');
	}

	#[test]
	fn test_col_inc(){
		let mut source = Cursor::new("test 🐘");
		let mut reader = PushbackCharReader::new(&mut source);
		reader.read();
		reader.read();
		assert!(reader.get_col() == 3);
		reader.read();
		assert!(reader.get_col() == 4);
	}

	#[test]
	fn test_line_inc(){
		let mut source = Cursor::new("test \n🐘");
		let mut reader = PushbackCharReader::new(&mut source);
		reader.read(); // t
		reader.read(); // e
		reader.read(); // s
		assert!(reader.get_line() == 1);
		reader.read(); // t
		reader.read(); // {space}
		reader.read(); // {newline}
		assert!(reader.get_line() == 2);
		reader.read(); // 🐘
		assert!(reader.get_line() == 2);
	}

	#[test]
	fn test_col_reset(){
		let mut source = Cursor::new("a\nd");
		let mut reader = PushbackCharReader::new(&mut source);
		reader.read();
		reader.read();
		assert!(reader.get_line() == 2);
		assert!(reader.get_col() == 1);
		reader.read();
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