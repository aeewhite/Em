use lexeme::Lexeme;
use lexeme_type::LexemeType;
use pushback_reader::PushbackCharReader;
use std::cell::RefCell;
use std::io::Read;
use std::io::Seek;
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
		let row = self.reader.get_line();
		let col = self.reader.get_col();
		let character = self.reader.read();
		
		let s_clone = self.source.clone();

		if let Some(c) = character{
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
					self.lex_multichar_op().or_else( || {
						self.lex_number().or_else( || {
							self.lex_keywords_and_identifiers()
						})
					})
				}
			}
		}
		else{
			None
		}
	}

	fn lex_multichar_op(&mut self) -> Option<Lexeme<S>>{
		println!("Lexing multichar op");
		//Get our character ready
		let row = self.reader.get_line();
		let col = self.reader.get_col(); 
		let c = self.reader.read().unwrap();

		let s_clone = self.source.clone();

		match c {
			'+' => {
				let test = self.reader.read();
				match test {
					Some(t) if t =='+' => Some(Lexeme::new(s_clone, LexemeType::INCREMENT, row, col)),
					Some(t) => {
						self.reader.pushback(t); 
						Some(Lexeme::new(s_clone, LexemeType::PLUS, row, col))
					},
					None => None
				}
			},
			'-' => {
				let test = self.reader.read();
				match test {
					Some(t) if t == '-' => Some(Lexeme::new(s_clone, LexemeType::DECREMENT, row, col)),
					Some(t) if t == '>' => Some(Lexeme::new(s_clone, LexemeType::ARROW, row, col)),
					Some(t) => {
						if t.is_numeric() {
							println!("Test = {:?}, c = {:?}", t, c );
							//negative numbers
							self.reader.pushback(c);
							self.reader.pushback(t);
							self.lex_number()
						}
						else {
							println!("Not numeric");
							self.reader.pushback(t);
							Some(Lexeme::new(s_clone, LexemeType::MINUS, row, col))
						}
					},
					None => Some(Lexeme::new(s_clone, LexemeType::MINUS, row, col))
				}
			},
			'/' => {
				let test = self.reader.read().unwrap();
				match test {
					'/' => {self.reader.skip_line(); self.lex()},
					_ => {
						self.reader.pushback(test); 
						Some(Lexeme::new(s_clone, LexemeType::DIVIDE, row, col))
					}
				}
			},
			'>' => {
				let test = self.reader.read().unwrap();
				match test {
					'=' => Some(Lexeme::new(s_clone, LexemeType::GTE, row, col)),
					_ => {
						self.reader.pushback(test); 
						Some(Lexeme::new(s_clone, LexemeType::GT, row, col))
					}
				}
			},
			'<' => {
				let test = self.reader.read().unwrap();
				match test {
					'=' => Some(Lexeme::new(s_clone, LexemeType::LTE, row, col)),
					_ => {
						self.reader.pushback(test); 
						Some(Lexeme::new(s_clone, LexemeType::LT, row, col))
					}
				}
			},
			'&' => {
				let test = self.reader.read().unwrap();
				match test {
					'&' => Some(Lexeme::new(s_clone, LexemeType::AND, row, col)),
					_ => {
						self.reader.pushback(test);
						self.reader.pushback(c);
						None
					}
				}
			},
			'|' => {
				let test = self.reader.read().unwrap();
				match test {
					'|' => Some(Lexeme::new(s_clone, LexemeType::OR, row, col)),
					_ => {
						self.reader.pushback(test);
						self.reader.pushback(c);
						None
					}
				}
			},
			'=' => {
				let test = self.reader.read().unwrap();
				match test {
					'=' => Some(Lexeme::new(s_clone, LexemeType::EQUALITY, row, col)),
					_ => {
						self.reader.pushback(test);
						Some(Lexeme::new(s_clone, LexemeType::ASSIGN, row, col))
					}
				}
			},
			'!' => {
				let test = self.reader.read().unwrap();
				match test {
					'=' => Some(Lexeme::new(s_clone, LexemeType::NOT_EQUALS, row, col)),
					_ => {
						self.reader.pushback(test);
						Some(Lexeme::new(s_clone, LexemeType::NOT, row, col))
					}
				}
			}
			_ => {
				self.reader.pushback(c);
				None
			}
		}
	}

	fn lex_number(&mut self) -> Option<Lexeme<S>>{
		println!("Lexing a number");
		//Get our character ready
		let row = self.reader.get_line();
		let col = self.reader.get_col(); 
		let c = self.reader.read().unwrap();
		println!("C is {:?}", c);

		let s_clone = self.source.clone();

		let mut string_buffer = String::new();

		if c != '-' && !c.is_numeric() {
			println!("Not a number");
			self.reader.pushback(c);
			return None
		}
		else{
			string_buffer.push(c);
		}
		
		loop{
			let character = self.reader.read();
			if let Some(c) = character{
				println!("loop c is {}", c );
				if !c.is_numeric() { 
					self.reader.pushback(c);
					break;
				}
				else{ string_buffer.push(c); }
			}
			else{
				break;
			}
		}
		println!("{:?}", string_buffer);
		println!("Returning Some(..) value from lex number");
		Some(Lexeme::new(s_clone, LexemeType::INTEGER(string_buffer.parse::<i32>().unwrap()), row, col))
	}

	fn lex_string(&mut self) -> Option<Lexeme<S>>{
		println!("Lexing string");
		//Get our character ready
		let row = self.reader.get_line();
		let col = self.reader.get_col(); 
		let c = self.reader.read().unwrap();

		let s_clone = self.source.clone();

		let mut string_buffer = String::new();
		// Only try to lex if we actually have a quoted string
		if c == '"' {
			loop{
				let character = self.reader.read();
				if let Some(c) = character{
					if c == '"'{
						break;
					}
					else if c == '\\'{
						string_buffer.push(self.reader.read().unwrap());
					}
					else{
						string_buffer.push(c);
					}
				}
				else{
					panic!("Unexpected end of input! Started String but never finished.");
				}
			}
			Some(Lexeme::new(s_clone, LexemeType::STRING(string_buffer), row, col))
		}
		else {
			self.reader.pushback(c);
			None
		}
	}

	fn lex_keywords_and_identifiers(&mut self) -> Option<Lexeme<S>>{
		//Get our character ready
		let row = self.reader.get_line();
		let col = self.reader.get_col(); 
		let s_clone = self.source.clone();
		
		let mut string_buffer = String::new();
		println!("In keyword lexer");

		let mut value = self.reader.read();
		while let Some(c) = value{
			println!("Read {}", c);
			if c.is_digit(36) || c.is_alphabetic() || c as u32 > 65536 || c == '-' || c == '_' {
				println!("Appending! {}", c);
				string_buffer.push(c);
				value = self.reader.read();
			}
			else{
				println!("Done building string");
				self.reader.pushback(c);
				value = None;
			}
		}

		println!("in lexer {:?}", string_buffer);

		match string_buffer.as_ref() {
			"var" => Some(Lexeme::new(s_clone, LexemeType::VAR, row, col)),
			"function" => Some(Lexeme::new(s_clone, LexemeType::FUNCTION, row, col)),
			"if" => Some(Lexeme::new(s_clone, LexemeType::IF, row, col)),
			"else" => Some(Lexeme::new(s_clone, LexemeType::ELSE, row, col)),
			"for" => Some(Lexeme::new(s_clone, LexemeType::FOR, row, col)),
			"while" => Some(Lexeme::new(s_clone, LexemeType::WHILE, row, col)),
			"do" => Some(Lexeme::new(s_clone, LexemeType::DO, row, col)),
			"true" => Some(Lexeme::new(s_clone, LexemeType::TRUE, row, col)),
			"false" => Some(Lexeme::new(s_clone, LexemeType::FALSE, row, col)),
			"null" => Some(Lexeme::new(s_clone, LexemeType::NULL, row, col)),
			"lambda" => Some(Lexeme::new(s_clone, LexemeType::LAMBDA, row, col)),
			"class" => Some(Lexeme::new(s_clone, LexemeType::CLASS, row, col)),
			"is" => Some(Lexeme::new(s_clone, LexemeType::IS, row, col)),
			_ => {
				Some(Lexeme::new(s_clone, LexemeType::IDENTIFIER(string_buffer), row, col))
			}
		}
	}
}

#[cfg(test)]
mod test {
	use super::*;
	use std::io::Cursor;

	#[test]
	fn single_lex_test(){
		let mut lexer = Lexer::new(Cursor::new("var"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::VAR);
	}

	#[test]
	fn emoji_identifier_test(){
		let mut lexer = Lexer::new(Cursor::new("🚀"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::IDENTIFIER("🚀".to_owned()));
	}

	#[test]
	fn lex_string_test(){
		let mut lexer = Lexer::new(Cursor::new("\"test string\""));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::STRING("test string".to_owned()));
	}

	#[test]
	fn lex_integer_test(){
		let mut lexer = Lexer::new(Cursor::new("5"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::INTEGER(5));
	}

	#[test]
	fn lex_multi_integer_test(){
		let mut lexer = Lexer::new(Cursor::new("55"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::INTEGER(55));
	}

	#[test]
	fn lex_negative_integer_test(){
		let mut lexer = Lexer::new(Cursor::new("-5"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::INTEGER(-5));
	}

	#[test]
	fn lex_boolean_test(){
		let mut lexer = Lexer::new(Cursor::new("true false"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::TRUE);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::FALSE);
	}

	#[test]
	fn lex_math_symbols_test(){
		let mut lexer = Lexer::new(Cursor::new("* / + - %"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::MULTIPLY);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::DIVIDE);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::PLUS);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::MINUS);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::MODULO);
	}

	#[test]
	fn lex_negation_test(){
		let mut lexer = Lexer::new(Cursor::new("!true"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::NOT);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::TRUE);
	}

	#[test]
	fn lex_boolean_ops_test(){
		let mut lexer = Lexer::new(Cursor::new("&& ||"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::AND);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::OR);
	}

	#[test]
	fn lex_assign_equality_test(){
		let mut lexer = Lexer::new(Cursor::new("a==b a=b"));
		lexer.lex();
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::EQUALITY);
		lexer.lex();
		lexer.lex();
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::ASSIGN);
	}

	#[test]
	fn lex_negative_multi_integer_test(){
		let mut lexer = Lexer::new(Cursor::new("-55"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::INTEGER(-55));
	}

	#[test]
	fn lex_line_col_check_test(){
		let mut lexer = Lexer::new(Cursor::new("true false"));
		if let Some(t) = lexer.lex(){
			assert_eq!(t.get_col(), 1);
			assert_eq!(t.get_line(), 1);
		}
		if let Some(f) = lexer.lex(){
			assert_eq!(f.get_col(), 6);
			assert_eq!(f.get_line(), 1);
		}
	}

	#[test]
	fn var_assignment_test(){
		let mut lexer = Lexer::new(Cursor::new("var a = b"));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::VAR);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::IDENTIFIER("a".to_owned()));
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::ASSIGN);
		assert_eq!(lexer.lex().unwrap().get_type(), LexemeType::IDENTIFIER("b".to_owned()));
	}
}
