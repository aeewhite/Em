package em;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by andrew on 9/18/16.
 */
public class Lexer {

    private FileInput input;
    private ArrayList<Lexeme> lexemes;
    private File file;

    public Lexer(String filename){
        this(new File(filename));
    }

    public Lexer(File file){
        this.file = file;
        try{
            input = new FileInput(file);
        } catch(IOException e){
            Logger.error("Error in reading file");
            e.printStackTrace();
            System.exit(1);
        }

        //Create the list for the lexemes
        lexemes = new ArrayList<>();
    }

    public ArrayList<Lexeme> getLexemes(){
        boolean go;
        do{
            go = lex();
        }while(go);
        return lexemes;
    }

    public boolean lex(){
        input.skipWhitespace();

        // Read a codepoint in
        int row = input.getRow();
        int col = input.getCol();
        int in = input.read();

        // end of file, stop lexing
        if(in <= 0 || in == 65535){
            return false;
        }

        if(in < Character.MAX_VALUE){
            switch((char) in){
                case '.':
                    lexemes.add(new Lexeme(LexemeType.DOT, row, col, this.file));
                    return true;
                case ';':
                    lexemes.add(new Lexeme(LexemeType.SEMICOLON, row, col, this.file));
                    return true;
                case ',':
                    lexemes.add(new Lexeme(LexemeType.COMMA, row, col, this.file));
                    return true;
                case '(':
                    lexemes.add(new Lexeme(LexemeType.OPEN_PAREN, row, col, this.file));
                    return true;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.CLOSE_PAREN, row, col, this.file));
                    return true;
                case '{':
                    lexemes.add(new Lexeme(LexemeType.OPEN_CURLY, row, col, this.file));
                    return true;
                case '}':
                    lexemes.add(new Lexeme(LexemeType.CLOSE_CURLY, row, col, this.file));
                    return true;
                case '[':
                    lexemes.add(new Lexeme(LexemeType.OPEN_BRACKET, row, col, this.file));
                    return true;
                case ']':
                    lexemes.add(new Lexeme(LexemeType.CLOSE_BRACKET, row, col, this.file));
                    return true;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.MULTIPLY, row, col, this.file));
                    return true;
                case '%':
                    lexemes.add(new Lexeme(LexemeType.MODULO, row, col, this.file));
                    return true;
                default:
                    input.pushback(in);
                    if(lexMultiCharacterOperators()){
                        return true;
                    }
                    if(Character.isDigit(in)){
                        return lexNumber();
                    }
                    if(in == '"'){
                        return lexString();
                    }
            }
        }
        //Check for identifiers and keywords
        if(in >= Character.MAX_VALUE){
            input.pushback(in);
        }

        return lexKeywordsAndIdentifiers();
    }

    /*Specialized lexing functions*/

    /**
     * Parse all the non-single character operators
     * @return Whether a lexeme was parsed or not
     */
    private boolean lexMultiCharacterOperators(){
        int row = input.getRow();
        int col = input.getCol();
        int in = input.read();
        if(in == '+'){
            int test = input.read();
            if(test == '+'){
                lexemes.add(new Lexeme(LexemeType.INCREMENT, row, col, this.file));
            }
            else{
                input.pushback(test);
                lexemes.add(new Lexeme(LexemeType.PLUS, row, col, this.file));
            }
            return true;
        }
        if(in == '-'){
            int test = input.read();
            if(test == '-'){
                lexemes.add(new Lexeme(LexemeType.DECREMENT, row, col, this.file));
            }
            else if(Character.isDigit(test)){
                //For handling negative numbers
                input.pushback(test);
                input.pushback(in);
                return lexNumber();
            }
            else if(test == '>'){
                lexemes.add(new Lexeme(LexemeType.ARROW, row, col, this.file));
            }
            else{
                input.pushback(test);
                lexemes.add(new Lexeme(LexemeType.MINUS, row, col, this.file));
            }
            return true;
        }
        if(in == '/'){
            int test = input.read();
            if(test == '/'){
                input.skipLine();
            }
            else{
                input.pushback(test);
                lexemes.add(new Lexeme(LexemeType.DIVIDE, row, col, this.file));
            }
            return true;
        }
        if(in == '>'){
            int test = input.read();
            if(test == '='){
                lexemes.add(new Lexeme(LexemeType.GTE, row, col, this.file));
            }
            else{
                input.pushback(test);
                lexemes.add(new Lexeme(LexemeType.GT, row, col, this.file));
            }
            return true;
        }
        if(in == '<'){
            int test = input.read();
            if(test == '='){
                lexemes.add(new Lexeme(LexemeType.LTE, row, col, this.file));
            }
            else{
                input.pushback(test);
                lexemes.add(new Lexeme(LexemeType.LT, row, col, this.file));
            }
            return true;
        }
        if(in == '&'){
            int test = input.read();
            if(test == '&'){
                lexemes.add(new Lexeme(LexemeType.AND, row, col, this.file));
                return true;
            }
            return false;
        }
        if(in == '|'){
            int test = input.read();
            if(test == '|'){
                lexemes.add(new Lexeme(LexemeType.OR, row, col, this.file));
                return true;
            }
            return false;
        }
        if(in == '='){
            int test = input.read();
            if(test == '='){
                lexemes.add(new Lexeme(LexemeType.EQUALITY, row, col, this.file));
            }
            else{
                input.pushback(test);
                lexemes.add(new Lexeme(LexemeType.ASSIGN, row, col, this.file));
            }
            return true;
        }
        if(in == '!'){
            int test = input.read();
            if(test == '='){
                lexemes.add(new Lexeme(LexemeType.NOT_EQUALS, row, col, this.file));
            }
            else{
                input.pushback(test);
                lexemes.add(new Lexeme(LexemeType.NOT, row, col, this.file));
            }
            return true;
        }
        input.pushback(in);

        return false;
    }


    /**
     * Parse lexemes for language keywords and identifiers
     * @return Where a keyword or identifier was parsed
     */
    private boolean lexKeywordsAndIdentifiers(){
        int row = input.getRow();
        int col = input.getCol();
        int in = input.read();
        StringBuilder sb = new StringBuilder();

        while(Character.isDigit(in)
                || Character.isAlphabetic(in)
                || Character.isSupplementaryCodePoint(in)
                || in == '_'
                || in == '-'){
            sb.appendCodePoint(in);
            in = input.read();
        }
        //Push back whatever broke the loop
        input.pushback(in);

        //Check all the keywords
        String inputString = sb.toString();
        if(inputString.equals("var")){
            lexemes.add(new Lexeme(LexemeType.VAR, row, col, this.file));
            return true;
        }
        else if(inputString.equals("function")){
            lexemes.add(new Lexeme(LexemeType.FUNCTION, row, col, this.file));
            return true;
        }
        else if(inputString.equals("if")){
            lexemes.add(new Lexeme(LexemeType.IF, row, col, this.file));
            return true;
        }
        else if(inputString.equals("else")){
            lexemes.add(new Lexeme(LexemeType.ELSE, row, col, this.file));
            return true;
        }
        else if(inputString.equals("for")){
            lexemes.add(new Lexeme(LexemeType.FOR, row, col, this.file));
            return true;
        }
        else if(inputString.equals("while")){
            lexemes.add(new Lexeme(LexemeType.WHILE, row, col, this.file));
            return true;
        }
        else if(inputString.equals("do")) {
            lexemes.add(new Lexeme(LexemeType.DO, row, col, this.file));
            return true;
        }
        else if(inputString.equals("true")) {
            lexemes.add(new Lexeme(LexemeType.TRUE, row, col, this.file));
            return true;
        }
        else if(inputString.equals("false")) {
            lexemes.add(new Lexeme(LexemeType.FALSE, row, col, this.file));
            return true;
        }
        else if(inputString.equals("null")) {
            lexemes.add(new Lexeme(LexemeType.NULL, row, col, this.file));
            return true;
        }
        else if(inputString.equals("lambda")) {
            lexemes.add(new Lexeme(LexemeType.LAMBDA, row, col, this.file));
            return true;
        }
        else if(inputString.equals("class")) {
            lexemes.add(new Lexeme(LexemeType.CLASS, row, col, this.file));
            return true;
        }
        else if(inputString.equals("is")) {
            lexemes.add(new Lexeme(LexemeType.IS, row, col, this.file));
            return true;
        }
        else{
            //Must be an identifier, if it didn't match any keyword
            lexemes.add(new Lexeme(LexemeType.IDENTIFIER, inputString, row, col, this.file));
            return true;
        }
    }

    /**
     * Lex a string literal
     * @return If a String lexeme was parsed
     */
    private boolean lexString(){
        int row = input.getRow();
        int col = input.getCol();
        Logger.silly("Lexing a string");
        int in = input.read();
        if(in == '"'){
            StringBuilder sb = new StringBuilder();
            in = input.read();
            do{
                if(in != '\\'){
                    sb.appendCodePoint(in);
                }
                in = input.read();
                if(in == '\\'){
                    int test = input.read();
                    sb.appendCodePoint(test);
                }
            }while(in != '"' && in != -1);
            lexemes.add(new Lexeme(LexemeType.STRING, sb.toString(), row, col, this.file));
            return true;
        }
        else{
            input.pushback(in);
            return false;
        }
    }

    /**
     * Lex a number
     * @return If a number was parsed
     */
    private boolean lexNumber(){
        int row = input.getRow();
        int col = input.getCol();
        Logger.silly("Lexing a number");
        StringBuilder sb = new StringBuilder();
        int in = input.read(); //This'll either be a "-" or a digit
        do{
            sb.appendCodePoint(in);
            in = input.read();
        }while(Character.isDigit(in));

        //Push back the non-digit character
        input.pushback(in);
        lexemes.add(new Lexeme(LexemeType.INTEGER, Integer.parseInt(sb.toString()), row, col, this.file));
        return true;
    }
}
