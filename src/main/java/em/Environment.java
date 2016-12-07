package em;

/**
 * Created by andrew on 11/22/16.
 */
public class Environment {
    public static Lexeme createEnv() {
        return extendEnv(null, null, null);
    }

    public static Lexeme extendEnv(Lexeme og, Lexeme newParams, Lexeme newVals) {
        Lexeme env = cons(LexemeType.ENV,
                    cons(LexemeType.GLUE, newParams, newVals),
                    og);
        env.setValue("<ENV>");
        insert(new Lexeme(LexemeType.IDENTIFIER, "this"), env, env);
        return env;

    }

    public static Lexeme lookUp(Lexeme variable, Lexeme env) {
        while(env != null){
            Lexeme table = env.getLeft();
            Lexeme vars = table.getLeft();
            Lexeme vals = table.getRight();
            while(vars != null){
                if(variable.getValue().equals(vars.getLeft().getValue())){
                    return vals.getLeft();
                }
                vars = vars.getRight();
                vals = vals.getRight();
            }
            env = env.getRight();
        }
        //Variable not Found
        Logger.error(String.format("ERROR: variable %s (%d, %d) not defined",
                variable.getValue(), variable.getRow(), variable. getCol()));
        System.exit(1);
        return null;
    }

    public static Lexeme insert(Lexeme newVar, Lexeme value, Lexeme env) {
        Lexeme table = env.getLeft();
        table.setLeft( cons(LexemeType.GLUE, newVar, table.getLeft()));
        table.setRight( cons(LexemeType.GLUE, value, table.getRight()));
        return value;
    }

    public static Lexeme update(Lexeme id, Lexeme newVal, Lexeme env){
        while(env != null) {
            Lexeme table = env.getLeft();
            Lexeme vars = table.getLeft();
            Lexeme vals = table.getRight();
            while (vars != null) {
                if (id.getValue().equals(vars.getLeft().getValue())) {
                    vals.setLeft(newVal);
                    return newVal;
                }
                vars = vars.getRight();
                vals = vals.getRight();
            }
            env = env.getRight();
        }
        //Variable not Found
        Logger.error(String.format("ERROR: cannot update value of %s (%d, %d), not defined",
                id.getValue(), id.getRow(), id.getCol()));
        System.exit(1);
        return null;
    }

    private static Lexeme cons(LexemeType type, Lexeme left, Lexeme right){
        Lexeme cell = new Lexeme(type);
        cell.setLeft(left);
        cell.setRight(right);
        return cell;
    }
}
