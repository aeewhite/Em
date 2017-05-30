use lexeme::Lexeme;
use lexeme_type::LexemeType;
use pushback_reader::PushbackCharReader;
use std::cell::RefCell;
use std::io::Read;
use std::io::Seek;
use std::io::SeekFrom;
use std::rc::Rc;

pub struct Lexer<S>{
	source: Rc<RefCell<S>>,
	reader: PushbackCharReader<S>
}


impl <S: Read+Seek+Clone> Lexer<S>{
	pub fn new(src: S) -> Lexer<S>{
		return Lexer{
			reader: PushbackCharReader::new(src.clone()),
			source: Rc::new(RefCell::new(src))
		}
	}

	pub fn lex(&mut self) -> Option<Lexeme<S>>{
		self.reader.skip_whitespace();

		//Get our character ready
		let row = self.reader.get_line(); //TODO: check if these should be
		let col = self.reader.get_col();  //before or after read
		let c = self.reader.read();
		

		let s_clone = self.source.clone();

		match c {
			'.' => Some(Lexeme::new(s_clone, LexemeType::DOT, row, col)),
			';' => Some(Lexeme::new(s_clone, LexemeType::SEMICOLON, row, col)),
			',' => Some(Lexeme::new(s_clone, LexemeType::COMMA, row, col)),
			'(' => Some(Lexeme::new(s_clone, LexemeType::OPEN_PAREN, row, col)),
			')' => Some(Lexeme::new(s_clone, LexemeType::CLOSE_PAREN, row, col)),
			'{' => Some(Lexeme::new(s_clone, LexemeType::OPEN_CURLY, row, col)),
			'}' => Some(Lexeme::new(s_clone, LexemeType::CLOSE_CURLY, row, col)),
			'[' => Some(Lexeme::new(s_clone, LexemeType::OPEN_BRACKET, row, col)),
			']' => Some(Lexeme::new(s_clone, LexemeType::CLOSE_BRACKET, row, col)),
			'*' => Some(Lexeme::new(s_clone, LexemeType::MULTIPLY, row, col)),
			'%' => Some(Lexeme::new(s_clone, LexemeType::MODULO, row, col)),
			'"' => {self.reader.pushback(c); self.lex_string()},
			_ => {
				self.reader.pushback(c);
				self.lex_multichar_op().or(
					self.lex_number().or(
						self.lex_keywords_and_identifiers()))
			}
		}
	}

	fn lex_multichar_op(&mut self) -> Option<Lexeme<S>>{
		//Get our character ready
		let row = self.reader.get_line();
		let col = self.reader.get_col(); 
		let c = self.reader.read();

		let s_clone = self.source.clone();

		match c {
			'+' => {
				char test = self.reader.read()
				match test {
					'+' => Some(Lexeme::new(s_clone, LexemeType::INCREMENT, row, col)),
					_ => {
						self.reader.pushback(test); 
						Some(Lexeme::new(s_clone, LexemeType::INCREMENT, row, col))
					}
				}
			},
			'-' => {
				char test = self.reader.read()
				match test {
					'-' => Some(Lexeme::new(s_clone, LexemeType::DECREMENT, row, col)),
					'>' => Some(Lexeme::new(s_clone, LexemeType::ARROW, row, col)),
					_ => {
						if test.is_numeric() { //TODO: I don't think this is necessary
							//negative numbers
							self.reader.pushback(test);
							self.reader.pushback(c);
							self.lex_number()
						}
						else {
							self.reader.pushback(test);
							Some(Lexeme::new(s_clone, LexemeType::MINUS, row, col)),
						}
					}
				}
			},
			'/' => {
				let test = self.reader.read();
				match test {
					'/' => {self.reader.skip_line(); self.lex()},
					_ => {
						self.reader.pushback(test); 
						Some(Lexeme::new(s_clone, LexemeType::DIVIDE, row, col))
					}
				}
			},
			'>' => {
				let test = self.reader.read();
				match test {
					'=' => Some(Lexeme::new(s_clone, LexemeType::GTE, row, col)),
					_ => {
						self.reader.pushback(test); 
						Some(Lexeme::new(s_clone, LexemeType::GT, row, col))
					}
				}
			},
			'<' => {
				let test = self.reader.read();
				match test {
					'=' => Some(Lexeme::new(s_clone, LexemeType::LTE, row, col)),
					_ => {
						self.reader.pushback(test); 
						Some(Lexeme::new(s_clone, LexemeType::LT, row, col))
					}
				}
			},
			'&' => {
				
			},
			'|' => {
				
			},
			'=' => {
				
			},
			'!' => {
				
			}
		}
	}

	fn lex_number(&mut self) -> Option<Lexeme<S>>{
		//Get our character ready
		let row = self.reader.get_line();
		let col = self.reader.get_col(); 
		let c = self.reader.read();

		None
	}

	fn lex_string(&mut self) -> Option<Lexeme<S>>{
		//Get our character ready
		let row = self.reader.get_line();
		let col = self.reader.get_col(); 
		let c = self.reader.read();

		None
	}

	fn lex_keywords_and_identifiers(&mut self) -> Option<Lexeme<S>>{
		//Get our character ready
		let row = self.reader.get_line();
		let col = self.reader.get_col(); 
		let c = self.reader.read();

		None
	}
}