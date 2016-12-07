package em;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;

import static em.Environment.*;

/**
 * Created by andrew on 11/22/16.
 */
public class Evaluator {
    public Lexeme eval(Lexeme pt, Lexeme env){
        if(pt == null){
            //Empty File (no statements)
            System.exit(0);
            return null;
        }
        if(pt.getType() == LexemeType.INTEGER){
            return pt;
        }
        else if(pt.getType() == LexemeType.STRING){
            return pt;
        }
        else if(pt.getType() == LexemeType.TRUE){
            return pt;
        }
        else if(pt.getType() == LexemeType.FALSE){
            return pt;
        }
        else if(pt.getType() == LexemeType.NULL){
            return pt;
        }
        else if(pt.getType() == LexemeType.ARRAY){
            return pt;
        }
        else if(pt.getType() == LexemeType.IDENTIFIER){
            return lookUp(pt, env);
        }
        else if(pt.getType() == LexemeType.UNARY){
            return evalUnary(pt, env);
        }
        else if(pt.getType() == LexemeType.STATEMENTLIST){
            return evalStatementList(pt, env);
        }
        else if(pt.getType() == LexemeType.STATEMENT){
            return evalStatement(pt, env);
        }
        else if(pt.getType() == LexemeType.PLUS){
            return evalPlus(pt, env);
        }
        else if(pt.getType() == LexemeType.MINUS){
            return evalMinus(pt, env);
        }
        else if(pt.getType() == LexemeType.MODULO){
            return evalMod(pt, env);
        }
        else if(pt.getType() == LexemeType.INCREMENT){
            return evalIncrement(pt, env);
        }
        else if(pt.getType() == LexemeType.DECREMENT){
            return evalDecrement(pt, env);
        }
        else if(pt.getType() == LexemeType.NOT){
            return evalNot(pt, env);
        }
        else if(pt.getType() == LexemeType.MULTIPLY){
            return evalMultiply(pt, env);
        }
        else if(pt.getType() == LexemeType.EQUALITY){
            return evalEquality(pt, env);
        }
        else if(pt.getType() == LexemeType.IS){
            return evalIS(pt, env);
        }
        else if(pt.getType() == LexemeType.NOT_EQUALS){
            return evalNotEquals(pt, env);
        }
        else if(pt.getType() == LexemeType.GT){
            return evalGT(pt, env);
        }
        else if(pt.getType() == LexemeType.LT){
            return evalLT(pt, env);
        }
        else if(pt.getType() == LexemeType.GTE){
            return evalGTE(pt, env);
        }
        else if(pt.getType() == LexemeType.LTE){
            return evalLTE(pt, env);
        }
        else if(pt.getType() == LexemeType.DIVIDE){
            return evalDivide(pt, env);
        }
        else if(pt.getType() == LexemeType.ARG_LIST){
            return evalArgList(pt, env);
        }
        else if(pt.getType() == LexemeType.ARRAY_LITERAL){
            return evalArrayLiteral(pt, env);
        }
        else if(pt.getType() == LexemeType.ARRAY_ACCESS){
            return evalArrayAccess(pt, env);
        }
        else if(pt.getType() == LexemeType.BLOCK){
            return evalBlock(pt, env);
        }
        else if(pt.getType() == LexemeType.IF){
            return evalIf(pt, env);
        }
        else if(pt.getType() == LexemeType.ELSE){
            return evalElse(pt, env);
        }
        else if(pt.getType() == LexemeType.VAR){
            return evalVarDef(pt, env);
        }
        else if(pt.getType() == LexemeType.ASSIGN){
            return evalAssign(pt, env);
        }
        else if(pt.getType() == LexemeType.FUNCTION){
            return evalFunctionDef(pt, env);
        }
        else if(pt.getType() == LexemeType.CLASS){
            return evalFunctionDef(pt, env);
        }
        else if(pt.getType() == LexemeType.LAMBDA){
            return evalLambdaDef(pt, env);
        }
        else if(pt.getType() == LexemeType.FUNCTION_CALL){
            return evalFunctionCall(pt, env, env);
        }
        else if(pt.getType() == LexemeType.WHILE){
            return evalWhile(pt, env);
        }
        else if(pt.getType() == LexemeType.DO){
            return evalDoWhile(pt, env);
        }
        else if(pt.getType() == LexemeType.FOR){
            return evalFor(pt, env);
        }
        else if(pt.getType() == LexemeType.AND){
            return evalAnd(pt, env);
        }
        else if(pt.getType() == LexemeType.OR){
            return evalOr(pt, env);
        }
        else if(pt.getType() == LexemeType.DOT){
            return evalDot(pt, env);
        }
        else if(pt.getType() == LexemeType.EXPRESSION0 ||
                    pt.getType() == LexemeType.EXPRESSION1 ||
                    pt.getType() == LexemeType.EXPRESSION2 ||
                    pt.getType() == LexemeType.EXPRESSION3 ||
                    pt.getType() == LexemeType.EXPRESSION4){
            return evalExpression(pt, env);
        }
        else {
            Logger.langError("No evaluator for type " + pt.getType(), pt);
            return null;
        }
    }

    /* Built In Code */

    public void initBuiltIns(Lexeme environment){
        BiFunction<Lexeme, Lexeme, Lexeme> print =
                (Lexeme arglist, Lexeme env) -> {
                    Lexeme result = new Lexeme(LexemeType.FALSE);
                    while(arglist != null){
                        if(arglist.getLeft().getType() == LexemeType.ARRAY){
                            Object[] valueArray = new Object[((Lexeme[])arglist.getLeft().getValue()).length];
                            for (int i = 0; i < valueArray.length; i++) {
                                valueArray[i] = ((Lexeme[])arglist.getLeft().getValue())[i].getValue();
                            }
                            System.out.print(Arrays.toString(valueArray));
                        }
                        else{
                            System.out.print(arglist.getLeft().getValue());
                        }
                        result = arglist.getLeft();
                        arglist = arglist.getRight();
                    }
                    return result;
                };
        addBuiltIn(environment, "print", print);


        BiFunction<Lexeme, Lexeme, Lexeme> array =
                (Lexeme arglist, Lexeme env) -> {
                    Lexeme size = arglist.getLeft();
                    if(size.getType() != LexemeType.INTEGER){
                        Logger.langError("Cannot create array of size " + size.getValue(), size);
                        return null;
                    }
                    else{
                        Lexeme[] arr = new Lexeme[(Integer)size.getValue()];
                        for (int i = 0; i < arr.length; i++) {
                            arr[i] = new Lexeme(LexemeType.NULL);
                        }
                        return new Lexeme(LexemeType.ARRAY, arr);
                    }
                };
        addBuiltIn(environment, "array", array);

        BiFunction<Lexeme, Lexeme, Lexeme> size =
                (Lexeme arglist, Lexeme env) -> {
                    Lexeme arr = arglist.getLeft();
                    if(arr.getType() != LexemeType.ARRAY){
                        Logger.langError("Cannot get size of non-array type " + arr.getType(), arr);
                        return null;
                    }
                    else{
                        Lexeme[] val = (Lexeme[])arr.getValue();
                        return new Lexeme(LexemeType.INTEGER, val.length);
                    }
                };
        addBuiltIn(environment, "size", size);

        Scanner scanner = new Scanner(System.in);

        BiFunction<Lexeme, Lexeme, Lexeme> scanToken =
                (Lexeme argList, Lexeme env)->{
                    String token = scanner.next();
                    if(token.matches("^-?\\d+$")){
                        return new Lexeme(LexemeType.INTEGER, Integer.parseInt(token));
                    }
                    else{
                        return new Lexeme(LexemeType.STRING, token);
                    }
                };

        addBuiltIn(environment, "scanToken", scanToken);

        BiFunction<Lexeme, Lexeme, Lexeme> scannerHasNext =
                (Lexeme argList, Lexeme env)->{
                    boolean hasnext = scanner.hasNext();
                    if(hasnext){
                        return new Lexeme(LexemeType.TRUE);
                    }
                    else{
                        return new Lexeme(LexemeType.FALSE);
                    }
                };
        addBuiltIn(environment, "scannerHasNext", scannerHasNext);

        BiFunction<Lexeme, Lexeme, Lexeme> type =
                (Lexeme argList, Lexeme env)->{
                    if(argList.getLeft() != null){
                        return new Lexeme(LexemeType.STRING,
                                argList.getLeft().getType().toString());
                    }
                    else{
                        return new Lexeme(LexemeType.FALSE);
                    }
                };
        addBuiltIn(environment, "type", type);
    }

    private void addBuiltIn(Lexeme env, String name,
                            BiFunction<Lexeme, Lexeme, Lexeme> evaluator){
        Lexeme id = new Lexeme(LexemeType.IDENTIFIER, name);
        Lexeme closure = new Lexeme(LexemeType.BUILTIN);
        closure.setValue(evaluator);
        closure.setLeft(id);

        insert(id, closure, env);
    }


    /* Evaluation Functions */

    private Lexeme evalStatementList(Lexeme pt, Lexeme env){
        Lexeme result = new Lexeme(LexemeType.FALSE);
        while(pt != null){
            result = eval(pt.getLeft(), env);
            pt = pt.getRight();
        }
        return result;
    }

    private Lexeme evalStatement(Lexeme pt, Lexeme env){
        if(pt.getRight() != null){
            if(pt.getRight().getType() == LexemeType.IDENTIFIER){
                return eval(pt.getRight(), env);
            }
            else{
                return eval(pt.getRight(), env);
            }
        }
        else{
            return eval(pt.getLeft(), env);
        }
    }

    private Lexeme evalPlus(Lexeme pt, Lexeme env){
        Lexeme addend = eval(pt.getLeft(), env);
        Lexeme augend = eval(pt.getRight(), env);
        if(addend.getType() == LexemeType.INTEGER &&
                augend.getType() == LexemeType.INTEGER){
            return new Lexeme(LexemeType.INTEGER,
                    (Integer)addend.getValue() + (Integer)augend.getValue());
        }
        else if(addend.getType() == LexemeType.STRING &&
                augend.getType() == LexemeType.STRING){
            return new Lexeme(LexemeType.STRING,
                    ((String)addend.getValue()).concat((String)augend.getValue()));
        }
        else{
            Logger.langError("Wrong types for +: " + addend.getType() +
                    " and " + augend.getType(), pt);
            return null;
        }
    }

    private Lexeme evalMinus(Lexeme pt, Lexeme env){
        Lexeme minuend = eval(pt.getLeft(), env);
        Lexeme subtrahend = eval(pt.getRight(), env);
        if(minuend.getType() == LexemeType.INTEGER &&
                subtrahend.getType() == LexemeType.INTEGER){
            return new Lexeme(LexemeType.INTEGER,
                    (Integer)minuend.getValue() - (Integer)subtrahend.getValue());
        }
        else{
            Logger.langError("Wrong types for -: "  + minuend.getType() +
                    " and " + subtrahend.getType(), pt);
            return null;
        }
    }

    private Lexeme evalMod(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        if(left.getType() == LexemeType.INTEGER &&
                right.getType() == LexemeType.INTEGER){
            return new Lexeme(LexemeType.INTEGER,
                    (Integer)left.getValue() % (Integer)right.getValue());
        }
        else{
            Logger.langError("Wrong types for %: "  + left.getType() +
                    " and " + right.getType(), pt);
            return null;
        }
    }

    private Lexeme evalMultiply(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        if(left.getType() == LexemeType.INTEGER &&
                right.getType() == LexemeType.INTEGER){
            return new Lexeme(LexemeType.INTEGER,
                    (Integer)left.getValue() * (Integer)right.getValue());
        }
        else{

            return null;
        }
    }

    private Lexeme evalDivide(Lexeme pt, Lexeme env){
        Lexeme dividend = eval(pt.getLeft(), env);
        Lexeme divisor = eval(pt.getRight(), env);
        if(dividend.getType() == LexemeType.INTEGER &&
                divisor.getType() == LexemeType.INTEGER){
            return new Lexeme(LexemeType.INTEGER,
                    (Integer)dividend.getValue() / (Integer)divisor.getValue());
        }
        else{
            Logger.langError("Wrong types for /: "  + dividend.getType() +
                    " and " + divisor.getType(), pt);
            return null;
        }
    }

    private Lexeme evalGT(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        if(left.getType() == LexemeType.INTEGER
                && right.getType() == LexemeType.INTEGER){
            if((Integer)left.getValue() > (Integer)right.getValue()){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        if(left.getType() == LexemeType.STRING
                && right.getType() == LexemeType.STRING){
            int comp = ((String)left.getValue()).compareTo(((String)right.getValue()));
            if(comp > 0){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        else{
            Logger.langError("Wrong types for >: "  + left.getType() +
                    " and " + right.getType(), pt);
            return null;
        }
    }

    private Lexeme evalLT(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        if(left.getType() == LexemeType.INTEGER
                && right.getType() == LexemeType.INTEGER){
            if((Integer)left.getValue() < (Integer)right.getValue()){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        if(left.getType() == LexemeType.STRING
                && right.getType() == LexemeType.STRING){
            int comp = ((String)left.getValue()).compareTo(((String)right.getValue()));
            if(comp < 0){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        else{
            Logger.langError("Wrong types for <: "  + left.getType() +
                    " and " + right.getType(), pt);
            return null;
        }
    }

    private Lexeme evalLTE(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        if(left.getType() == LexemeType.INTEGER
                && right.getType() == LexemeType.INTEGER){
            if((Integer)left.getValue() <= (Integer)right.getValue()){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        if(left.getType() == LexemeType.STRING
                && right.getType() == LexemeType.STRING){
            int comp = ((String)left.getValue()).compareTo(((String)right.getValue()));
            if(comp <= 0){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        else{
            Logger.langError("Wrong types for <=: "  + left.getType() +
                    " and " + right.getType(), pt);
            return null;
        }
    }

    private Lexeme evalGTE(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        if(left.getType() == LexemeType.INTEGER
                && right.getType() == LexemeType.INTEGER){
            if((Integer)left.getValue() >= (Integer)right.getValue()){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        if(left.getType() == LexemeType.STRING
                && right.getType() == LexemeType.STRING){
            int comp = ((String)left.getValue()).compareTo(((String)right.getValue()));
            if(comp >= 0){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        else{
            Logger.langError("Wrong types for >=: "  + left.getType() +
                    " and " + right.getType(), pt);
            return null;
        }
    }

    private Lexeme evalEquality(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        //Have to be the same type, but can be any type
        //TODO narrow this to actual checkable types
        if(left.getType() == right.getType()){
            if(left.getType() == LexemeType.NULL
                    && right.getType() == LexemeType.NULL){
                return new Lexeme(LexemeType.TRUE);
            }
            else if(left.getValue().equals(right.getValue())){
                return new Lexeme(LexemeType.TRUE);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
        else{
            return new Lexeme(LexemeType.FALSE);
        }
    }

    private Lexeme evalIS(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        if(left == right){
            return new Lexeme(LexemeType.TRUE);
        }
        else{
            return new Lexeme(LexemeType.FALSE);
        }
    }

    private Lexeme evalNotEquals(Lexeme pt, Lexeme env){
        if(evalEquality(pt, env).getType() == LexemeType.TRUE){
            return new Lexeme(LexemeType.FALSE);
        }
        else{
            return new Lexeme(LexemeType.TRUE);
        }
    }

    private Lexeme evalNot(Lexeme pt, Lexeme env){
        Lexeme initial = eval(pt.getLeft(), env);
        if(initial.getType() == LexemeType.TRUE){
            return new Lexeme(LexemeType.FALSE);
        }
        else if(initial.getType() == LexemeType.FALSE){
            return new Lexeme(LexemeType.TRUE);
        }
        else{
            Logger.langError("Cannot negate non-boolean type "
                    + initial.getType(), initial);
            return null;
        }
    }

    private Lexeme evalDecrement(Lexeme pt, Lexeme env){
        Lexeme initial = pt.getLeft();
        //Unwind the initial
        while(initial.getType() != LexemeType.UNARY){
            initial = initial.getLeft();
        }

        Lexeme origValue = eval(initial, env);
        if(initial.getLeft().getType() == LexemeType.IDENTIFIER &&
                origValue.getType() == LexemeType.INTEGER){
            return update(initial.getLeft(),
                    new Lexeme(LexemeType.INTEGER, (Integer)origValue.getValue() - 1),
                    env);
        }
        else if(origValue.getType() != LexemeType.INTEGER){
            Logger.langError("Cannot decrement non-integer type " +
                    initial.getLeft().getType(), initial.getLeft());
            return null;
        }
        else{
            Logger.langError("Cannot decrement value of non-IDENTIFIER type " +
                    initial.getLeft().getType(), initial.getLeft());
            return null;
        }
    }

    private Lexeme evalIncrement(Lexeme pt, Lexeme env){
        Lexeme initial = pt.getLeft();
        //Unwind the initial
        while(initial.getType() != LexemeType.UNARY){
            initial = initial.getLeft();
        }

        Lexeme origValue = eval(initial, env);
        if(initial.getLeft().getType() == LexemeType.IDENTIFIER &&
                origValue.getType() == LexemeType.INTEGER){
            return update(initial.getLeft(),
                    new Lexeme(LexemeType.INTEGER, (Integer)origValue.getValue() + 1),
                    env);
        }
        else if(origValue.getType() != LexemeType.INTEGER){
            Logger.langError("Cannot increment value of non-IDENTIFIER type " +
                    initial.getLeft().getType(), initial.getLeft());
            return null;
        }
        else{
            Logger.langError("Cannot increment value of non-IDENTIFIER type " +
                    initial.getLeft().getType(), initial.getLeft());
            return null;
        }
    }

    private Lexeme evalBlock(Lexeme pt, Lexeme env){
        //Lexeme blockScope = extendEnv(env, null, null);
        Lexeme result = new Lexeme(LexemeType.FALSE);
        while(pt != null){
            result = eval(pt.getLeft(), env);
            pt = pt.getRight();
        }
        return result;
    }

    private Lexeme evalIf(Lexeme pt, Lexeme env){
        Lexeme cond = eval(pt.getLeft(), env);
        if(cond.getType() == LexemeType.TRUE){
            return eval(pt.getRight().getLeft(), env);
        }
        else{
            //Look at else's
            if(pt.getRight().getRight() != null){
                return eval(pt.getRight().getRight(), env);
            }
            else{
                return new Lexeme(LexemeType.FALSE);
            }
        }
    }

    private Lexeme evalElse(Lexeme pt, Lexeme env){
        //else if ...
        if(pt.getLeft() != null){
            return eval(pt.getLeft(), env);
        }
        //else
        else{
            return eval(pt.getRight(), env);
        }
    }

    private Lexeme evalExpression(Lexeme pt, Lexeme env){
        if(pt.getRight() != null){
            return eval(pt.getRight(), env);
        }
        else{
            return eval(pt.getLeft(), env);
        }
    }

    private Lexeme evalVarDef(Lexeme pt, Lexeme env){
        Lexeme initial = new Lexeme(LexemeType.NULL);
        if(pt.getRight() != null){
            initial = eval(pt.getRight(), env);
        }
        insert(pt.getLeft(), initial, env);
        return initial;
    }

    private Lexeme evalUnary(Lexeme pt, Lexeme env) {
        return eval(pt.getLeft(), env);
    }

    private Lexeme evalArgList(Lexeme pt, Lexeme env){
        Lexeme newArgList = new Lexeme(LexemeType.ARG_LIST);
        Lexeme newhead = newArgList;
        while(pt != null){
            newArgList.setLeft(eval(pt.getLeft(), env));
            if(pt.getRight() != null){
                newArgList.setRight(new Lexeme(LexemeType.ARG_LIST));
            }
            pt = pt.getRight();
            newArgList = newArgList.getRight();
        }
        return newhead;
    }

    private Lexeme evalArrayLiteral(Lexeme pt, Lexeme env){
        List<Lexeme> entries = new ArrayList<>();
        if(pt.getLeft() != null) {
            Lexeme evaledArgs = eval(pt.getLeft(), env);
            while (evaledArgs != null) {
                entries.add(evaledArgs.getLeft());
                evaledArgs = evaledArgs.getRight();
            }
        }
        return new Lexeme(LexemeType.ARRAY, entries.toArray(new Lexeme[0]));
    }

    private Lexeme evalArrayAccess(Lexeme pt, Lexeme env){
        Lexeme index = eval(pt.getRight(), env);
        Lexeme array = eval(pt.getLeft(), env);
        if(array.getType() != LexemeType.ARRAY){
            Logger.langError("Attempt to index into non-array type " +
                    array.getType(), array);
            return null;
        }
        else if(index.getType() != LexemeType.INTEGER){
            Logger.langError("Attempt to access array at non-integer index" +
                index.getValue(), index);
            return null;
        }
        else {
            //We have a valid array access
            return (Lexeme)((Object[])array.getValue())[(Integer)index.getValue()];
        }
    }

    private Lexeme evalAssign(Lexeme pt, Lexeme env){
        Lexeme var = pt.getLeft();
        Lexeme val = eval(pt.getRight(), env);
        //Get to the end of the var side (so we have a unary)
        while(var.getType() != LexemeType.UNARY){
            var = var.getLeft();
        }

        if(var.getLeft().getType() == LexemeType.IDENTIFIER){
            return update(var.getLeft(), val, env);
        }
        else if(var.getLeft().getType() == LexemeType.ARRAY_ACCESS){
            //Need to change index in array
            Lexeme arr = eval(var.getLeft().getLeft(), env);
            Lexeme index = eval(var.getLeft().getRight(), env);
            if(index.getType() != LexemeType.INTEGER){
                Logger.langError("Cannot assign to non-integer index " +
                    index.getValue(), index);
                return null;
            }
            else{
                Object[] jarray = (Object[])arr.getValue();
                jarray[(Integer)index.getValue()] = val;
                arr.setValue(jarray);
                update(var.getLeft().getLeft(), arr, env);
                return val;
            }
        }
        else{
            Logger.langError("Cannot assign " + val.getValue() + " to " +
                    var.getValue() + ". " + var.getType()
                    + " is not an IDENTIFIER", var);
            return null;
        }
    }

    private Lexeme evalWhile(Lexeme pt, Lexeme env){
        Lexeme result = new Lexeme(LexemeType.FALSE);
        while(eval(pt.getLeft(), env).getType() == LexemeType.TRUE){
            result = eval(pt.getRight(), env);
        }
        return result;
    }

    private Lexeme evalFor(Lexeme pt, Lexeme env){
        Lexeme loopEnv = extendEnv(env, null, null);
        Lexeme varDeclaration = pt.getLeft().getLeft();
        Lexeme testExpression = pt.getLeft().getRight();
        Lexeme incrementExpression = pt.getRight().getLeft();
        Lexeme block = pt.getRight().getRight();

        eval(varDeclaration, loopEnv);
        Lexeme result = new Lexeme(LexemeType.FALSE);
        while(eval(testExpression, loopEnv).getType() == LexemeType.TRUE){
            result = eval(block, loopEnv);
            eval(incrementExpression, loopEnv);
        }

        return result;
    }

    private Lexeme evalDoWhile(Lexeme pt, Lexeme env){
        Lexeme result;
        do{
            result = eval(pt.getRight(), env);
        } while(eval(pt.getLeft(), env).getType() == LexemeType.TRUE);
        return result;
    }

    private Lexeme evalAnd(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        if(left.getType() != LexemeType.TRUE &&
                left.getType() != LexemeType.FALSE){
            Logger.langError("Left side of AND did not evaluate to a boolean",
                    left);
            return null;
        }
        if(left.getType() == LexemeType.FALSE){
            return new Lexeme(LexemeType.FALSE);
        }
        Lexeme right = eval(pt.getRight(), env);

        if(right.getType() != LexemeType.TRUE &&
                right.getType() != LexemeType.FALSE){
            Logger.langError("Right side of AND did not evaluate to a boolean",
                    right);
            return null;
        }
        else if(left.getType() == LexemeType.TRUE
                && eval(pt.getRight(), env).getType() == LexemeType.TRUE){
            return new Lexeme(LexemeType.TRUE);
        }
        else{
            return new Lexeme(LexemeType.FALSE);
        }
    }

    private Lexeme evalOr(Lexeme pt, Lexeme env){
        Lexeme left = eval(pt.getLeft(), env);
        Lexeme right = eval(pt.getRight(), env);
        if(left.getType() != LexemeType.TRUE &&
                left.getType() != LexemeType.FALSE){
            Logger.langError("Left side of OR did not evaluate to a boolean",
                    left);
            return null;
        }
        else if(right.getType() != LexemeType.TRUE &&
                right.getType() != LexemeType.FALSE){
            Logger.langError("Right side of OR did not evaluate to a boolean",
                    right);
            return null;

        }
        else if(eval(pt.getLeft(), env).getType() == LexemeType.TRUE
                || eval(pt.getRight(), env).getType() == LexemeType.TRUE){
            return new Lexeme(LexemeType.TRUE);
        }
        else{
            return new Lexeme(LexemeType.FALSE);
        }
    }

    private Lexeme evalDot(Lexeme pt, Lexeme env){
        Lexeme obj = eval(pt.getLeft(), env);
        if(obj.getType() != LexemeType.ENV){
            Logger.langError(String.format(
                    "Cannot access member of non-object type %s (%d, %d)",
                    obj.getType(), pt.getLeft().getRow(), pt.getCol()), pt);
            System.exit(1);
            return null;
        }
        else{
            if(pt.getRight().getLeft().getType() == LexemeType.FUNCTION_CALL){
                Lexeme right = pt.getRight().getLeft();
                return evalFunctionCall(right, obj, env);
            }
            else{
                return eval(pt.getRight(), obj);
            }
        }
    }

    private Lexeme evalFunctionDef(Lexeme pt, Lexeme env){
        Lexeme closure = new Lexeme(LexemeType.CLOSURE);
        closure.setLeft(env);
        closure.setRight(pt);
        setClosureValue(closure);
        return insert(pt.getLeft(), closure, env);
    }

    private Lexeme evalLambdaDef(Lexeme pt, Lexeme env){
        Lexeme closure = new Lexeme(LexemeType.CLOSURE);
        closure.setLeft(env);
        Lexeme func = new Lexeme(LexemeType.FUNCTION);
        func.setRight(pt);
        func.setLeft(new Lexeme(LexemeType.STRING, "anonymous"));
        closure.setRight(func);
        setClosureValue(closure);
        return closure;
    }

    private void setClosureValue(Lexeme closure){
        StringBuilder sb = new StringBuilder();
        sb.append("<function ");
        sb.append(closure.getRight().getLeft().getValue());
        sb.append("(");
        Lexeme paramlist = closure.getRight().getRight().getLeft();
        while(paramlist != null){
            sb.append(paramlist.getLeft().getValue());
            paramlist = paramlist.getRight();
            if(paramlist != null){
                sb.append(", ");
            }
        }
        sb.append(")>");
        closure.setValue(sb.toString());
    }

    @SuppressWarnings("unchecked")
    private Lexeme evalFunctionCall(Lexeme pt, Lexeme env, Lexeme callingEnv){
        Lexeme closure = eval(pt.getLeft(), env);
        Lexeme arglist = pt.getRight();
        Lexeme eargs;
        if(arglist != null){
            eargs = eval(arglist, callingEnv);
        }
        else{
            eargs = null;
        }
        if(closure.getType() == LexemeType.BUILTIN){
            BiFunction func = ((BiFunction)closure.getValue());
            return (Lexeme)func.apply(eargs, env);
        }
        else if(closure.getType() == LexemeType.CLOSURE){
            Lexeme function = closure.getRight();
            Lexeme lambda = function.getRight();
            Lexeme params = lambda.getLeft();
            Lexeme body = lambda.getRight();
            Lexeme defEnv = closure.getLeft();
            Lexeme xenv = extendEnv(defEnv, params, eargs);
            if(closure.getRight().getType() == LexemeType.FUNCTION){
                return eval(body, xenv);
            }
            else{
                //Class
                insert(new Lexeme(LexemeType.IDENTIFIER, "self"),
                        eval(new Lexeme(LexemeType.IDENTIFIER, "this"), xenv),
                        xenv);
                eval(body, xenv);
                Lexeme obj = eval(new Lexeme(LexemeType.IDENTIFIER, "this"), xenv);
                obj.setValue("<object " + function.getLeft().getValue() + ">");
                return obj;
            }
        }
        else{
            Logger.langError("Attempt to call non-function type "
                    + closure.getType(), pt.getLeft());
            return null;
        }
    }
}
