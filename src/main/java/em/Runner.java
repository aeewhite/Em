package em;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
            //Load the main library
            File mainlib;
            try{
                InputStream inputStream = Thread.currentThread().
                        getContextClassLoader().
                        getResourceAsStream("em/mainlib.em");
                File tempFile = File.createTempFile("mainlib", ".em");
                tempFile.deleteOnExit();
                try(FileOutputStream out = new FileOutputStream(tempFile)){
                    byte[] buffer = new byte[1024];
                    while(inputStream.read(buffer) > -1){
                        out.write(buffer);
                    }
                }
                mainlib = tempFile;

            }
            catch (Exception e){
                mainlib = null;
                e.printStackTrace();
                System.exit(1);
            }

            Lexer mainlibLexer = new Lexer(mainlib);


            //Start up the interpreter
            Logger.silly("Starting file read");

            Logger.debug("Loading Main Library");

            Recognizer mlRecognizer = new Recognizer(filename, mainlibLexer.getLexemes());
            Lexeme mlPT = mlRecognizer.parse();
            Evaluator evaluator = new Evaluator();
            Lexeme env = Environment.createEnv();
            evaluator.initBuiltIns(env);
            evaluator.eval(mlPT, env);

            Logger.debug("Reading from " + filename);

            Lexer lexer = new Lexer(filename);

            if(Logger.getLogLevel() > 0){
                for(Lexeme l : lexer.getLexemes()){
                    Logger.debug(l);
                }
            }


            Recognizer recognizer = new Recognizer(filename, lexer.getLexemes());
            Lexeme pt = recognizer.parse();
            evaluator.eval(pt, env).getValue();
        }
    }

    private static void printHelp(int exitCode){
        Logger.info("Em Interpreter v1.2.1");
        Logger.info("usage: emi filename");
        Logger.info("  options:");
        Logger.info("    -v\t\tEnable verbose logging, additional v's increase verbosity");
        Logger.info("    -h,--help\tShow this help text");
        System.exit(exitCode);
    }
}
