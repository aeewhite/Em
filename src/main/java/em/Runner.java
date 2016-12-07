package em;

/**
 * Created by andrew on 9/21/16.
 */
public class Runner {
    public static void main(String[] args) {
        boolean help = false;

        String filename = null;
        int verbosity = 0;
        for(String arg : args){
            if(arg.equals("-h") ||arg.equals("--help")){
                help = true;
            }
            else if(arg.startsWith("-v")){
                //loop through arg and count v's
                for(char c : arg.toCharArray()){
                    if(c == 'v'){
                        verbosity++;
                    }
                }
            }
            else{
                filename = arg;
            }
        }

        Logger.setLogLevel(verbosity);

        Logger.debug("Log level: " + verbosity);

        if(help){
            printHelp(0);
        }
        else if(filename == null){
            Logger.error("ERROR: Please supply a filename");
            printHelp(1);
        }
        else{
            //Start up the interpreter
            Logger.silly("Starting file read");
            Logger.debug("Reading from " + filename);

            Lexer lexer = new Lexer(filename);

            for(Lexeme l : lexer.getLexemes()){
                Logger.debug(l);
            }

            Recognizer recognizer = new Recognizer(filename, lexer.getLexemes());
            Lexeme pt = recognizer.parse();

            Evaluator evaluator = new Evaluator();
            Lexeme env = Environment.createEnv();
            evaluator.initBuiltIns(env);

            evaluator.eval(pt, env).getValue();
        }
    }

    private static void printHelp(int exitCode){
        Logger.info("usage: emi filename");
        Logger.info("  options:");
        Logger.info("    -v\t\tEnable verbose logging, additional v's increase verbosity");
        Logger.info("    -h,--help\tShow this help text");
        System.exit(exitCode);
    }
}
