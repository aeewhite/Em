use std::io::Read;
use std::io::Seek;
use std::io::SeekFrom;
use std::rc::Rc;
use std::cell::RefCell;
use lexeme_type::LexemeType;

#[derive(Debug)]
pub struct Lexeme<S> {
    source: Rc<RefCell<S>>,
    lex_type: LexemeType,
    left_child: Option<Box<Lexeme<S>>>,
    right_child: Option<Box<Lexeme<S>>>,
    line: u32,
    col: u32
}

impl<S: Read+Seek> Lexeme<S>{
    pub fn new(src: Rc<RefCell<S>>, t: LexemeType, l: u32, c: u32) -> Lexeme<S>{
        return Lexeme{source: src.clone(),
                        lex_type: t,
                        left_child: None,
                        right_child: None,
                        line: l,
                        col: c}
    }
    fn new_without_position(src: Rc<RefCell<S>>, t: LexemeType ) -> Lexeme<S>{
        return Lexeme{source: src.clone(),
                        lex_type: t,
                        left_child: None,
                        right_child: None,
                        line: 0,
                        col: 0}
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

    pub fn get_type(&mut self)->LexemeType{
        self.lex_type.clone()
    }

    fn get_source(&self)->Rc<RefCell<S>>{
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
        let source = Rc::new(RefCell::new(Cursor::new("test 🐘.")));
        Lexeme::new_without_position(source, LexemeType::STRING("letter".to_string()));
    }

    #[test]
    fn test_create_with_left_child(){
        let source = Rc::new(RefCell::new(Cursor::new("test 🐘.")));
        let mut root = Lexeme::new_without_position(source.clone(), LexemeType::STRING("letter".to_string()));
        root.set_left(Lexeme::new_without_position(source, LexemeType::STRING("left".to_string())));
    }

    #[test]
    fn test_create_with_right_child(){
        let source = Rc::new(RefCell::new(Cursor::new("test 🐘.")));
        let mut root = Lexeme::new_without_position(source.clone(), LexemeType::STRING("letter".to_string()));
        root.set_right(Lexeme::new_without_position(source, LexemeType::STRING("right".to_string())));
    }

    #[test]
    fn test_get_left_child(){
        let source = Rc::new(RefCell::new(Cursor::new("test 🐘.")));
        let mut root = Lexeme::new_without_position(source.clone(), LexemeType::STRING("letter".to_string()));
        root.set_left(Lexeme::new_without_position(source.clone(), LexemeType::STRING("left".to_string())));

        let l = root.get_left();
        match l.unwrap().get_type() {
            LexemeType::STRING(s) => assert!(s == "left".to_string()),
            _ => panic!(),
        }
    }

    #[test]
    fn source_clone_test(){
        // Create a source and clone it
        let source = Rc::new(RefCell::new(Cursor::new("test 🐘.")));
        let copy = source.clone();

        // Read into strings
        let mut original_str = String::new();
        let mut copied_str = String::new();
        let original_length = source.borrow_mut().read_to_string(&mut original_str).unwrap();
        let new_position = copy.borrow_mut().seek(SeekFrom::Start(0)).unwrap(); // This is very important
        assert_eq!(new_position, 0);
        let copied_length = copy.borrow_mut().read_to_string(&mut copied_str).unwrap();

        // Test their equality
        assert_eq!(original_length, copied_length);
        assert_eq!(original_str, copied_str);
    }

    #[test]
    fn test_read_from_source(){
        // Create a source
        let source = Rc::new(RefCell::new(Cursor::new("test 🐘.")));
        
        // Create a lexeme from that source
        let root = Lexeme::new_without_position(source.clone(), LexemeType::STRING("letter".to_string()));
        let mut orig = String::new();
        let o_size = source.borrow_mut().read_to_string(&mut orig).unwrap();

        // Get the source back from the lexeme
        let copied_source = root.get_source();
        let new_position = copied_source.borrow_mut().seek(SeekFrom::Start(0)).unwrap();
        assert_eq!(new_position, 0);
        let mut copied = String::new();
        let n_size = copied_source.borrow_mut().read_to_string(&mut copied).unwrap();

        assert_eq!(o_size, n_size);
        assert_eq!(orig, copied);
    }

    #[test]
    fn test_lexeme_chain(){
        let source = Rc::new(RefCell::new(Cursor::new("test 🐘.")));
        let mut root = Lexeme::new_without_position(source.clone(), LexemeType::FUNCTION);
        let mut right = Lexeme::new_without_position(source.clone(), LexemeType::VAR);
        let left = Lexeme::new_without_position(source.clone(), LexemeType::IF);

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
        let source = Rc::new(RefCell::new(Cursor::new("test 🐘.")));
        let mut root = Lexeme::new_without_position(source.clone(), LexemeType::FUNCTION);

        assert!(root.get_left().is_none());
        assert!(root.get_right().is_none());
    }
}