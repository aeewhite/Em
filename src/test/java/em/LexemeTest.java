package em;

import org.junit.Test;

import static org.junit.Assert.*;

public class LexemeTest{

    @Test
    public void valueConstructorTest(){
        Lexeme testLexeme= new Lexeme<>(LexemeType.IDENTIFIER, "foo");
        assertNotEquals(testLexeme, null);
        assertEquals(testLexeme.getType(), LexemeType.IDENTIFIER);

        assertTrue(testLexeme.getValue() instanceof String);

        assertEquals(testLexeme.getValue(), "foo");
    }

    @Test
    public void noValueConstructorTest(){
        Lexeme testLexeme= new Lexeme<>(LexemeType.IDENTIFIER);
        assertNotEquals(testLexeme, null);
        assertEquals(testLexeme.getType(), LexemeType.IDENTIFIER);

        assertEquals(testLexeme.getValue(), null);
    }

    @Test
    public void toStringWithValueTest(){
        Lexeme testLexeme= new Lexeme<>(LexemeType.IDENTIFIER, "foo");
        String result = testLexeme.toString();

        assertEquals(result, "<type: IDENTIFIER, value: \"foo\">");
    }

    @Test
    public void toStringWithoutValueTest(){
        Lexeme testLexeme= new Lexeme<>(LexemeType.SEMICOLON);
        String result = testLexeme.toString();

        assertEquals(result, "<type: SEMICOLON>");
    }

}
