package em;

import java.io.File;
import java.nio.file.Files;

/**
 * Log handler with custom log levels
 *
 * Log Level 0 = only important messages and errors
 * Log level 1 = debug messages
 * Log level 2 = silly
 *
 *
 * Created by andrew on 9/24/16.
        */
public class Logger {

    private static int logLevel;

    public static void setLogLevel(int level){
        logLevel = level;
    }

    private static void log(int level, String logMessage){
        if(logLevel < 0){
            System.err.println(logMessage);
        }
        else if(level <= logLevel){
            System.out.println(logMessage);
        }
    }

    public static void info(Object message){
        log(0, message.toString());
    }

    public static void debug(Object message){
        log(1, message.toString());
    }

    public static void silly(Object message){
        log(2, message.toString());
    }

    public static void error(Object message){
        log(-1, message.toString());
    }

    public static void langError(String message, Lexeme lexeme){
        //Top line
        String errorLine;
        if(lexeme.getFile() != null){
            errorLine = String.format("ERROR in %s at (%d,%d)",
                    lexeme.getFile().getName(),
                    lexeme.getRow(),
                    lexeme.getCol());
            String fileline = readLineFromFile(lexeme.getFile(), lexeme.getRow());
            int tabCount = fileline.length() - fileline.replace("\t", "").length();
            String caretline = leftpadcaret(tabCount, lexeme.getCol() - tabCount -1, "^");
            Logger.error(String.format("%s\n\t%s\n\t%s\n\t%s",
                    errorLine,
                    fileline,
                    caretline,
                    message));
        }
        else{
            Logger.error("ERROR: " + message);
        }
        //Throw fatal error
        System.exit(1);
    }

    /**
     * Read a single line from a file
     * (line numbers start at 1)
     * @param file
     * @param linenumber
     * @return line from file as a String
     */
    private static String readLineFromFile(File file, int linenumber){
        try{
            return Files.readAllLines(file.toPath()).get(linenumber - 1);
        }
        catch (Exception e){
            return "ERROR";
        }
    }

    /**
     * Create a string with spaces to the left of a character
     * @param tabs Number of tabs to pad
     * @param spaces Number of spaces to pad
     * @param start Character to place on the right
     * @return
     */
    private static String leftpadcaret(int tabs, int spaces, String start){
        if(tabs <= 0 && spaces <= 0){
            return start;
        }
        else if(spaces > 0 ){
            return leftpadcaret(tabs, --spaces, " "  + start);
        }

        else{
            // (tabs > 0)
            return leftpadcaret(--tabs, spaces, "\t"  + start);
        }
    }
}
