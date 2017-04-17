use std::io::Read;
use lexeme_type::LexemeType;

//https://gist.github.com/aidanhs/5ac9088ca0f6bdd4a370

pub struct Lexeme<'a, S: 'a> {
    source: &'a S,
    lex_type: LexemeType,
    left_child: Option<Box<Lexeme<'a, S>>>,
    right_child: Option<Box<Lexeme<'a, S>>>
}

impl<'a, S: Read> Lexeme<'a, S>{
    fn new(src: &'a S, t: LexemeType ) -> Lexeme<'a, S>{
        return Lexeme{source: src,
                        lex_type: t,
                        left_child: None,
                        right_child: None}
    }
}


#[cfg(test)]
mod test{
    use super::*;
    use std::io::Cursor;

    #[test]
    fn test_create(){
        let source = Cursor::new("test 🐘.");
        let l = Lexeme::new(&source, LexemeType::STRING("letter".to_string()));
    }
}