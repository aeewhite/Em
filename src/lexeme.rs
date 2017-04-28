use std::io::Read;
use lexeme_type::LexemeType;

pub struct Lexeme<S> {
    source: S,
    lex_type: LexemeType,
    left_child: Option<Box<Lexeme<S>>>,
    right_child: Option<Box<Lexeme<S>>>
}

impl<S: Read+Clone> Lexeme<S>{
    fn new(src: &S, t: LexemeType ) -> Lexeme<S>{
        return Lexeme{source: src.clone(),
                        lex_type: t,
                        left_child: None,
                        right_child: None}
    }

    fn set_left(&mut self, l: Lexeme<S>){
        self.left_child = Some(Box::new(l));
    }
    
    fn get_left(&mut self)->Option<&mut Lexeme<S>>{
        match self.left_child{
            Some(ref mut child) => Some(child),
            None => None
        }
    }

    fn set_right(&mut self, r: Lexeme<S>){
        self.right_child = Some(Box::new(r));
    }
    
    fn get_right(&mut self)->Option<&mut Lexeme<S>>{
        match self.right_child{
            Some(ref mut child) => Some(child),
            None => None
        }
    }

    fn get_type(&mut self)->LexemeType{
        self.lex_type.clone()
    }

    fn get_source(&self)->S{
        self.source.clone()
    }
}


#[cfg(test)]
mod test{
    use super::*;
    use std::io::Cursor;


    #[test]
    fn test_cursor_clone(){
        let mut a = Cursor::new("test");
        let mut b = a.clone();

        let mut a_string = String::new();
        let mut b_string = String::new();

        let a_size = a.read_to_string(&mut a_string).unwrap();
        let b_size = b.read_to_string(&mut b_string).unwrap();

        assert_eq!(a_size, b_size);
        assert_eq!(a_string, b_string);
    }

    #[test]
    fn test_create(){
        let source = Cursor::new("test 🐘.");
        Lexeme::new(&source, LexemeType::STRING("letter".to_string()));
    }

    #[test]
    fn test_create_with_left_child(){
        let source = Cursor::new("test 🐘.");
        let mut root = Lexeme::new(&source, LexemeType::STRING("letter".to_string()));
        root.set_left(Lexeme::new(&source, LexemeType::STRING("left".to_string())));
    }

    #[test]
    fn test_create_with_right_child(){
        let source = Cursor::new("test 🐘.");
        let mut root = Lexeme::new(&source, LexemeType::STRING("letter".to_string()));
        root.set_right(Lexeme::new(&source, LexemeType::STRING("right".to_string())));
    }

    #[test]
    fn test_get_left_child(){
        let source = Cursor::new("test 🐘.");
        let mut root = Lexeme::new(&source, LexemeType::STRING("letter".to_string()));
        root.set_left(Lexeme::new(&source, LexemeType::STRING("left".to_string())));

        let l = root.get_left();
        match l.unwrap().get_type() {
            LexemeType::STRING(s) => assert!(s == "left".to_string()),
            _ => panic!(),
        }
    }

    #[test]
    fn test_read_from_source(){
        let mut source = Cursor::new("test 🐘.");
        
        let root = Lexeme::new(&source, LexemeType::STRING("letter".to_string()));
        let mut orig = String::new();
        let o_size = source.read_to_string(&mut orig).unwrap();

        let mut copied_source = root.get_source();
        let mut copied = String::new();
        let n_size = copied_source.read_to_string(&mut copied).unwrap();

        assert_eq!(o_size, n_size);
        assert_eq!(orig, copied);
    }

    #[test]
    fn test_lexeme_chain(){
        let source = Cursor::new("test 🐘.");
        let mut root = Lexeme::new(&source, LexemeType::FUNCTION);
        let mut right = Lexeme::new(&source, LexemeType::VAR);
        let left = Lexeme::new(&source, LexemeType::IF);

        right.set_left(left);
        root.set_right(right);

        match root.get_right(){
            Some(r) => {
                match r.get_type() {
                    LexemeType::VAR => assert!(true),
                    _ => panic!()
                }
                match r.get_left() {
                    Some(l) => {
                        match l.get_type() {
                            LexemeType::IF => assert!(true),
                            _ => panic!()
                        }
                    }
                    _ => panic!()
                }   
            },
            _ => panic!()
        }
    }

    #[test]
    fn test_get_nonexistant_child(){
        let source = Cursor::new("test 🐘.");
        let mut root = Lexeme::new(&source, LexemeType::FUNCTION);

        assert!(root.get_left().is_none());
        assert!(root.get_right().is_none());
    }
}