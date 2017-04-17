#[allow(non_camel_case_types)]
#[allow(dead_code)]
pub enum LexemeType{
    VAR,
    FUNCTION,
    WHILE,
    DO,
    IF,
    ELSE,
    FOR,
    IDENTIFIER(String),
    STRING(String),
    INTEGER(i32),
    TRUE,
    FALSE,
    NULL,
    SEMICOLON,
    DOT,
    OPEN_PAREN,
    CLOSE_PAREN,
    OPEN_CURLY,
    CLOSE_CURLY,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    COMMA,
    PLUS,
    INCREMENT,
    DECREMENT,
    MINUS,
    MULTIPLY,
    MODULO,
    ARROW,
    DIVIDE,
    ASSIGN,
    EQUALITY,
    NOT_EQUALS,
    GT,
    LT,
    GTE,
    LTE,
    AND,
    OR,
    NOT,
    STATEMENT,
    STATEMENTLIST,
    GLUE,
    EXPRESSION0,
    EXPRESSION1,
    EXPRESSION2,
    EXPRESSION3,
    EXPRESSION4,
    ARRAY,
    UNARY,
    FUNCTION_CALL,
    ARRAY_ACCESS,
    LAMBDA_CALL,
    PARAM_LIST,
    ARG_LIST,
    ARRAY_LITERAL,
    LAMBDA,
    BLOCK,
    ENV,
    CLOSURE,
    BUILTIN,
    CLASS,
    IS
}

#[cfg(test)]
mod test{
    use super::*;

    #[test]
    fn test_enum_tuple(){
        let s = LexemeType::IDENTIFIER("Test".to_string());

        if let LexemeType::IDENTIFIER(val) = s{
            assert!(val == "Test");
        }
    }

    #[test]
    fn test_integer_math(){
        let a = LexemeType::INTEGER(5);
        let b = LexemeType::INTEGER(10);

        if let LexemeType::INTEGER(val1) = a{
            if let LexemeType::INTEGER(val2) = b{
                assert!(val1 + val2 == 15);
            }
        }
    }
}