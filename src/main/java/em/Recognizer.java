package em;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

/**
 * Recognizer and Parser
 *
 * Created by andrew on 11/7/16.
 */
public class Recognizer {
    private File file;
    private LinkedList<Lexeme> lexemes;
    private Lexeme current;

    public Recognizer(String filename, List<Lexeme> lexemes){
        this.file = new File(filename);
        this.lexemes = new LinkedList<>();
        this.lexemes.addAll(lexemes);
    }

    /**
     * Kick off the recognizer
     * @return The created parse tree
     */
    public Lexeme parse(){
        advance(); //Load the first lexeme
        return program(); //program is the overarching grammar rule
    }

    /**
     * Move to the next lexeme in the input stream
     */
    private void advance(){
        current = lexemes.poll();
    }

    /**
     * Insist that the current lexeme is of the given type
     *   - if it is, advance is called
     *   - otherwise an error is reported
     * @param type Lexeme to match against
     * @return Current lexeme
     */
    private Lexeme match(LexemeType type){
        if(current == null){
            Logger.error(String.format("ERROR in %s" +
                            "\n\tExpected %s but found EOF" +
                            ", Missing semicolon?",
                    file.getName(), type));
            System.exit(1);
        }
        Lexeme m = current;
        if(current.getType() == type){
            advance();
            return m;
        }
        else{
            //Throw fatal error
            Logger.langError(String.format("Expected %s but found %s",
                    type, current.getType()), current);
            return null;
        }
    }

    /**
     * Check whether or not the current lexeme is of the given type
     */
    private boolean check(LexemeType type){
        return current != null && current.getType() == type;
    }

    /**
     * Read a single line from a file
     * (line numbers start at 1)
     * @param linenumber
     * @return line from file as a String
     */
    private String readLineFromFile(int linenumber){
        try{
            return Files.readAllLines(file.toPath()).get(linenumber - 1);
        }
        catch (Exception e){
            return "ERROR";
        }
    }

    /**
     * Create a string with spaces to the left of a character
     * @param spaces Number of spaces to pad
     * @param start Character to place on the right
     * @return
     */
    private String leftpadcaret(int spaces, String start){
        if(spaces < 1){
            return start;
        }
        else{
            return leftpadcaret(--spaces, " "  + start);
        }
    }


    /*
    Recursive Descent Recognizer Rules
     */

    /**
     * [optStatementList]
     */
    private Lexeme program(){
        return optStatementList();
    }

    /**
     * [optStatementList] or *none*
     */
    private Lexeme optStatementList(){
        if(statementListPending()) {
            return statementList();
        }
        else{
            return null;
        }
    }

    /**
     *         [statementList]
     *         /             \
     * [statement]          [statementList]
     *                       /
     *              [statement]
     */
    private Lexeme statementList(){
        Lexeme s = new Lexeme(LexemeType.STATEMENTLIST);
        s.setLeft(statement());
        if(statementListPending()){
            s.setRight(statementList());
        }
        return s;
    }


    /**
     *                                                              [statement]
     *                                                              /
     * [vardef/block/funcDef/ifDef/forDef/whileDef/doWhileDef/expression0]
     *
     *                                                   [statement]
     *                                                    /      \
     * [vardef/block/funcDef/ifDef/forDef/whileDef/doWhileDef]  [ASSIGN]
     *                                                           /     \
     *                                               [expression0]   [expression0]
     *                                                        *in the case of assignment*
     */
    private Lexeme statement(){
        Lexeme s = new Lexeme(LexemeType.STATEMENT);
        if(varDefPending()){
            s.setRight(varDef());
            match(LexemeType.SEMICOLON);
        }
        else if(blockPending()){
            s.setLeft(block());
        }
        else if(functionDefPending()){
            s.setLeft(functionDef());
        }
        else if(classDefPending()){
            s.setLeft(classDef());
        }
        else if(ifDefPending()){
            s.setLeft(ifDef());
        }
        else if(forDefPending()){
            s.setLeft(forDef());
        }
        else if(whileDefPending()){
            s.setLeft(whileDef());
        }
        else if(doWhileDefPending()){
            s.setLeft(doWhileDef());
        }
        else if(expression0Pending()){
            Lexeme leftexp = expression0();
            Lexeme assign;
            Lexeme rightexp;
            if(check(LexemeType.ASSIGN)){
                assign = match(LexemeType.ASSIGN);
                assign.setLeft(leftexp);
                rightexp = expression0();
                assign.setRight(rightexp);
                s.setRight(assign);
            }
            else{
                s.setLeft(leftexp);
            }
            match(LexemeType.SEMICOLON);
        }
        return s;
    }

    /**
     *          [expression0]
     *            /
     *      [expression1]
     *
     *           -OR-
     *
     *           [expression0]
     *                      \
     *                     [operator0]
     *                       /     \
     *            [expression1]  [expression0]
     *
     */
    private Lexeme expression0(){
        Lexeme e = new Lexeme(LexemeType.EXPRESSION0);
        Lexeme l = expression1();
        if(operator0Pending()){
            Lexeme op = operator0();
            op.setLeft(l);
            op.setRight(expression0());
            e.setRight(op);
        }
        else{
            e.setLeft(l);
        }
        return e;
    }

    private Lexeme operator0(){
        Lexeme op;
        if(check(LexemeType.AND)){
            op = match(LexemeType.AND);
        }
        else{
            op = match(LexemeType.OR);
        }
        return op;
    }

    /**
     *          [expression1]
     *            /
     *      [expression2]
     *
     *           -OR-
     *
     *           [expression1]
     *                      \
     *                     [operator1]
     *                       /     \
     *            [expression2]  [expression1]
     *
     */
    private Lexeme expression1(){
        Lexeme e = new Lexeme(LexemeType.EXPRESSION1);
        Lexeme l = expression2();
        if(operator1Pending()){
            Lexeme op = operator1();
            op.setLeft(l);
            op.setRight(expression1());
            e.setRight(op);
        }
        else{
            e.setLeft(l);
        }
        return e;
    }

    private Lexeme operator1(){
        Lexeme op;
        if(check(LexemeType.LT)){
            op = match(LexemeType.LT);
        }
        else if(check(LexemeType.GT)){
            op = match(LexemeType.GT);
        }
        else if(check(LexemeType.LTE)){
            op = match(LexemeType.LTE);
        }
        else if(check(LexemeType.GTE)){
            op = match(LexemeType.GTE);
        }
        else if(check(LexemeType.NOT_EQUALS)){
            op = match(LexemeType.NOT_EQUALS);
        }
        else if(check(LexemeType.EQUALITY)){
            op = match(LexemeType.EQUALITY);
        }
        else{
            op = match(LexemeType.IS);
        }
        return op;
    }

    /**
     *          [expression2]
     *            /
     *      [expression3]
     *
     *           -OR-
     *
     *           [expression2]
     *                      \
     *                     [operator2]
     *                       /     \
     *            [expression3]  [expression2]
     *
     */
    private Lexeme expression2(){
        Lexeme e = new Lexeme(LexemeType.EXPRESSION2);
        Lexeme l = expression3();
        if(operator2Pending()){
            Lexeme op = operator2();
            op.setLeft(l);
            op.setRight(expression2());
            e.setRight(op);
        }
        else{
            e.setLeft(l);
        }
        return e;
    }

    private Lexeme operator2(){
        Lexeme op;
        if(check(LexemeType.PLUS)){
            op = match(LexemeType.PLUS);
        }
        else{
            op = match(LexemeType.MINUS);
        }
        return op;
    }

    /**
     *          [expression3]
     *            /
     *      [expression4]
     *
     *           -OR-
     *
     *           [expression3]
     *                      \
     *                     [operator3]
     *                       /     \
     *            [expression4]  [expression3]
     *
     */
    private Lexeme expression3(){
        Lexeme e = new Lexeme(LexemeType.EXPRESSION3);
        Lexeme l = expression4();
        if(operator3Pending()){
            Lexeme op = operator3();
            op.setLeft(l);
            op.setRight(expression3());
            e.setRight(op);
        }
        else{
            e.setLeft(l);
        }
        return e;
    }

    private Lexeme operator3(){
        Lexeme op;
        if(check(LexemeType.MULTIPLY)){
            op = match(LexemeType.MULTIPLY);
        }
        else if(check(LexemeType.DIVIDE)){
            op = match(LexemeType.DIVIDE);
        }
        else{
            op = match(LexemeType.MODULO);
        }
        return op;
    }

    /**
     *          [expression4]
     *            /
     *      [unary]
     *
     *           -OR-
     *
     *           [expression4]
     *                      \
     *                     [operator4]
     *                       /     \
     *                  [unary]  [expression4]
     *
     */
    private Lexeme expression4(){
        Lexeme e = new Lexeme(LexemeType.EXPRESSION4);
        Lexeme l = unary();
        if(operator4Pending()){
            Lexeme op = operator4();
            op.setLeft(l);
            op.setRight(expression4());
            e.setRight(op);
        }
        else{
            e.setLeft(l);
        }
        return e;
    }

    private Lexeme operator4(){
        return match(LexemeType.DOT);
    }

    private Lexeme unaryOperator(){
        Lexeme op;
        if(check(LexemeType.NOT)){
            op = match(LexemeType.NOT);
        }
        else if(check(LexemeType.INCREMENT)){
            op = match(LexemeType.INCREMENT);
        }
        else{
            op = match(LexemeType.DECREMENT);
        }
        return op;
    }

    /**
     *       [arrayLiteral]
     *        /
     * [argList]
     */
    private Lexeme array(){
        Lexeme a = new Lexeme(LexemeType.ARRAY_LITERAL);
        match(LexemeType.OPEN_BRACKET);
        a.setLeft(optArgList());
        match(LexemeType.CLOSE_BRACKET);
        return a;
    }

    /**
     *                                 [unary]
     *                                 /
     * [INTEGER | TRUE | FALSE | ID | STRING |
     *  expression0 | array | lambda |unaryOp | varExpr]
     *
     *      [unaryOp]
     *       /
     * [unary]
     *
     */
    private Lexeme unary(){
        Lexeme u = new Lexeme(LexemeType.UNARY);
        if(check(LexemeType.INTEGER)){
            u.setLeft(match(LexemeType.INTEGER));
        }
        else if(check(LexemeType.TRUE)){
            u.setLeft(match(LexemeType.TRUE));
        }
        else if(check(LexemeType.FALSE)){
            u.setLeft(match(LexemeType.FALSE));
        }
        else if(check(LexemeType.STRING)){
            u.setLeft(match(LexemeType.STRING));
        }
        else if(check(LexemeType.NULL)){
            u.setLeft(match(LexemeType.NULL));
        }
        else if(check(LexemeType.IDENTIFIER)){
            Lexeme ID = match(LexemeType.IDENTIFIER);
            //Function calls
            if (check(LexemeType.OPEN_PAREN)) {
                Lexeme FC = new Lexeme(LexemeType.FUNCTION_CALL);
                match(LexemeType.OPEN_PAREN);
                FC.setRight(optArgList());
                match(LexemeType.CLOSE_PAREN);
                FC.setLeft(ID);
                u.setLeft(FC);
            }
            //Array access
            else if(check(LexemeType.OPEN_BRACKET)){
                Lexeme AA = new Lexeme(LexemeType.ARRAY_ACCESS);
                match(LexemeType.OPEN_BRACKET);
                AA.setRight(expression2());
                match(LexemeType.CLOSE_BRACKET);
                AA.setLeft(ID);
                u.setLeft(AA);
            }
            else if(check(LexemeType.DOT)){
                Lexeme d = match(LexemeType.DOT);
                d.setLeft(ID);
                d.setRight(unary());
                u.setLeft(d);
            }
            else{
                u.setLeft(ID);
            }
        }
        else if(lambdaPending()){
            Lexeme lam = lambda();
            if(check(LexemeType.OPEN_PAREN)) {
                Lexeme LC = new Lexeme(LexemeType.FUNCTION_CALL);
                LC.setLeft(lam);
                match(LexemeType.OPEN_PAREN);
                LC.setRight(optArgList());
                match(LexemeType.CLOSE_PAREN);
                u.setLeft(LC);
            }
            else{
                u.setLeft(lam);
            }
        }
        else if(check(LexemeType.OPEN_PAREN)){
            match(LexemeType.OPEN_PAREN);
            Lexeme expr = expression0();
            match(LexemeType.CLOSE_PAREN);
            //Function calls
            if (check(LexemeType.OPEN_PAREN)) {
                Lexeme FC = new Lexeme(LexemeType.FUNCTION_CALL);
                match(LexemeType.OPEN_PAREN);
                FC.setRight(optArgList());
                match(LexemeType.CLOSE_PAREN);
                FC.setLeft(expr);
                u.setLeft(FC);
            }
            else{
                u.setLeft(expr);
            }
        }
        else if(arrayPending()){
            u.setLeft(array());
        }
        else if(unaryOperatorPending()){
            Lexeme uOp = unaryOperator();
            uOp.setLeft(unary());
            u.setLeft(uOp);
        }
        return u;
    }

    /**
     *
     *    [functionCall]            [arrayAccess]           [lambdaCall]
     *       /      \                /       \               /      \
     *    [ID]     [optArglist]   [ID]  [expression2]   [lambda]   [optArglist]
     *
     */
    private Lexeme varExpr(){
        if(check(LexemeType.IDENTIFIER)) {
            Lexeme ID = match(LexemeType.IDENTIFIER);
            //Function calls
            if (check(LexemeType.OPEN_PAREN)) {
                Lexeme FC = new Lexeme(LexemeType.FUNCTION_CALL);
                match(LexemeType.OPEN_PAREN);
                FC.setRight(optArgList());
                match(LexemeType.CLOSE_PAREN);
                FC.setLeft(ID);
                return FC;
            }
            //Array access
            else {
                Lexeme AA = new Lexeme(LexemeType.ARRAY_ACCESS);
                match(LexemeType.OPEN_BRACKET);
                AA.setRight(expression2());
                match(LexemeType.CLOSE_BRACKET);
                AA.setLeft(ID);
                return AA;
            }
        }
        //Call a lambda
        else{
            Lexeme LC = new Lexeme(LexemeType.LAMBDA_CALL);
            LC.setLeft(lambda());
            match(LexemeType.OPEN_PAREN);
            LC.setRight(optArgList());
            match(LexemeType.CLOSE_PAREN);
            return LC;
        }
    }

    /**
     *
     * [paramlist] OR null
     */
    private Lexeme optParamList(){
        if(paramListPending()){
            return paramList();
        }
        else{
            return null;
        }
    }


    /**
     *     [paramlist]
     *      /      |
     *  [ID]       [paramlist]
     *               /      \
     *            [ID]    [paramlist]
     *
     */
    private Lexeme paramList(){
        Lexeme p = new Lexeme(LexemeType.PARAM_LIST);
        p.setLeft(match(LexemeType.IDENTIFIER));
        if(check(LexemeType.COMMA)){
            match(LexemeType.COMMA);
            p.setRight(paramList());
        }
        return p;
    }

    private Lexeme optArgList(){
        if(argListPending()){
            return argList();
        }
        else {
            return null;
        }
    }

    /**
     *            [ARGLIST]
     *             /    \
     * [expression0]   [ARGLIST]
     *
     */
    private Lexeme argList(){
        Lexeme a = new Lexeme(LexemeType.ARG_LIST);
        a.setLeft(expression0());
        if(check(LexemeType.COMMA)){
            match(LexemeType.COMMA);
            a.setRight(argList());
        }
        return a;
    }

    /**
     *        [functionDef]
     *         /         \
     * [ID(Name)]      [lambda]
     *                  /    \
     *          [paramlist]  [block]
     *
     */
    private Lexeme functionDef(){
        Lexeme fd = match(LexemeType.FUNCTION);
        fd.setLeft(match(LexemeType.IDENTIFIER));
        Lexeme lam = new Lexeme(LexemeType.LAMBDA);
        match(LexemeType.OPEN_PAREN);
        lam.setLeft(optParamList());
        match(LexemeType.CLOSE_PAREN);
        lam.setRight(block());
        fd.setRight(lam);
        return fd;
    }

    /**
     *          [classDef]
     *         /         \
     * [ID(Name)]      [lambda]
     *                  /    \
     *          [paramlist]  [block]
     *
     */
    private Lexeme classDef(){
        Lexeme fd = match(LexemeType.CLASS);
        fd.setLeft(match(LexemeType.IDENTIFIER));
        Lexeme lam = new Lexeme(LexemeType.LAMBDA);
        match(LexemeType.OPEN_PAREN);
        lam.setLeft(optParamList());
        match(LexemeType.CLOSE_PAREN);
        lam.setRight(block());
        fd.setRight(lam);
        return fd;
    }

    /**
     *     [VARDEF]
     *     /     \
     * [ID]     [expression0] (if assignment)
     *
     */
    private Lexeme varDef(){
        Lexeme v = match(LexemeType.VAR);
        v.setLeft(match(LexemeType.IDENTIFIER));
        if(check(LexemeType.ASSIGN)){
            match(LexemeType.ASSIGN);
            v.setRight(expression0());
        }
        return v;
    }

    /**
     *             [BLOCK]
     *             /
     * [statementList]
     */
    private Lexeme block(){
        Lexeme b = new Lexeme(LexemeType.BLOCK);
        match(LexemeType.OPEN_CURLY);
        b.setLeft(optStatementList());
        match(LexemeType.CLOSE_CURLY);
        return b;
    }

    /**
     *           [IF]
     *          /   \
     * [condition] [GLUE]
     *             /    \
     *       [BLOCK]   [ElSE]
     *
     */
    private Lexeme ifDef(){
        Lexeme tag = match(LexemeType.IF);
        match(LexemeType.OPEN_PAREN);
        //Condition
        tag.setLeft(expression0());
        match(LexemeType.CLOSE_PAREN);
        Lexeme g = new Lexeme(LexemeType.GLUE);
        //If body
        g.setLeft(block());
        //Else clauses
        g.setRight(optElseIf());
        tag.setRight(g);
        return tag;
    }

    /**
     *    [ELSE]
     *    /   \
     * [IF] [BLOCK]
     *
     * Left path is for else if, right is just else
     * There should only be one
     */
    private Lexeme optElseIf(){
        Lexeme e = null;
        if(check(LexemeType.ELSE)){
            e = match(LexemeType.ELSE);
            if(ifDefPending()){
                e.setLeft(ifDef());
            }
            else{
                e.setRight(block());
            }
        }
        return e;
    }

    /**
     *
     *                     [FOR]
     *                 /           \
     *       [GLUE]                     [GLUE]
     *       /   \                      /    \
     * [VARDEF] [expression0] [expression0] [block]
     *               ^              ^
     *             test        incrementor
     */
    private Lexeme forDef(){
        Lexeme f = match(LexemeType.FOR);
        Lexeme left = new Lexeme(LexemeType.GLUE);
        match(LexemeType.OPEN_PAREN);
        //variable creation
        left.setLeft(varDef());
        //test condition
        match(LexemeType.SEMICOLON);
        left.setRight(expression0());
        match(LexemeType.SEMICOLON);
        Lexeme right = new Lexeme(LexemeType.GLUE);
        //incrementer
        right.setLeft(expression0());
        match(LexemeType.CLOSE_PAREN);
        right.setRight(block());

        f.setRight(right);
        f.setLeft(left);
        return f;
    }

    /**
     *          [WHILE]
     *          /     \
     * [expression0] [BLOCK]
     */
    private Lexeme whileDef(){
        Lexeme w = match(LexemeType.WHILE);
        match(LexemeType.OPEN_PAREN);
        w.setLeft(expression0());
        match(LexemeType.CLOSE_PAREN);
        w.setRight(block());
        return w;
    }

    /**
     *             [DO]
     *             /  \
     * [expression0]  [block]
     *
     */
    private Lexeme doWhileDef(){
        Lexeme d = match(LexemeType.DO);
        d.setRight(block());
        match(LexemeType.WHILE);
        match(LexemeType.OPEN_PAREN);
        //test condition
        d.setLeft(expression0());
        match(LexemeType.CLOSE_PAREN);
        match(LexemeType.SEMICOLON);
        return d;
    }

    /**
     *         [LAMBDA]
     *          /   \
     * [PARAMLIST] [BLOCK]
     */
    private Lexeme lambda(){
        Lexeme l = match(LexemeType.LAMBDA);
        match(LexemeType.OPEN_PAREN);
        l.setLeft(optParamList());
        match(LexemeType.CLOSE_PAREN);
        match(LexemeType.ARROW);
        l.setRight(block());
        return l;
    }

    /*
    Pending Functions
     */

    private boolean statementListPending(){ return statementPending();}

    private boolean statementPending(){
        boolean r = expression0Pending() || varDefPending() || blockPending() ||
                functionDefPending() || classDefPending() || ifDefPending() ||
                forDefPending() || whileDefPending() || doWhileDefPending();
        return r;
    }

    private boolean expression0Pending(){ return expression1Pending();}

    private boolean operator0Pending(){
        return check(LexemeType.AND) || check(LexemeType.OR);
    }

    private boolean expression1Pending(){ return expression2Pending();}

    private boolean operator1Pending(){
        return check(LexemeType.LT) || check(LexemeType.GT) ||
                check(LexemeType.LTE) || check(LexemeType.GTE) ||
                check(LexemeType.NOT_EQUALS) || check(LexemeType.EQUALITY) ||
                check(LexemeType.IS);
    }

    private boolean expression2Pending(){ return expression3Pending();}

    private boolean operator2Pending(){
        return check(LexemeType.PLUS) || check(LexemeType.MINUS);
    }

    private boolean expression3Pending(){ return expression4Pending();}

    private boolean operator3Pending(){
        return check(LexemeType.MULTIPLY) || check(LexemeType.DIVIDE) ||
                check(LexemeType.MODULO);
    }

    private boolean expression4Pending(){ return unaryPending();}

    private boolean operator4Pending(){ return check(LexemeType.DOT); }

    private boolean unaryOperatorPending(){
        return check(LexemeType.NOT) || check(LexemeType.INCREMENT) ||
                check(LexemeType.DECREMENT);
    }

    private boolean arrayPending(){ return check(LexemeType.OPEN_BRACKET);}

    private boolean unaryPending(){
        return check(LexemeType.INTEGER) || check(LexemeType.TRUE) ||
                check(LexemeType.FALSE) || check(LexemeType.STRING) ||
                check(LexemeType.IDENTIFIER) || check(LexemeType.OPEN_PAREN) ||
                lambdaPending() || arrayPending() || unaryOperatorPending() ||
                varExprPending();
    }

    private boolean varExprPending(){
        return check(LexemeType.IDENTIFIER) || lambdaPending();
    }

    private boolean paramListPending(){ return check(LexemeType.IDENTIFIER);}

    private boolean argListPending(){ return unaryPending();}

    private boolean functionDefPending(){ return check(LexemeType.FUNCTION);}

    private boolean classDefPending(){ return check(LexemeType.CLASS);}

    private boolean varDefPending(){ return check(LexemeType.VAR);}

    private boolean blockPending(){ return check(LexemeType.OPEN_CURLY);}

    private boolean ifDefPending(){ return check(LexemeType.IF);}

    private boolean forDefPending(){ return check(LexemeType.FOR);}

    private boolean whileDefPending(){ return check(LexemeType.WHILE);}

    private boolean doWhileDefPending(){ return check(LexemeType.DO);}

    private boolean lambdaPending(){ return check(LexemeType.LAMBDA);}
}
