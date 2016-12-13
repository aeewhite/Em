package em;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by andrew on 11/24/16.
 */
public class EmException extends RuntimeException {
    public EmException(String message, Lexeme lexeme){
        //Top line
        String errorLine;
        if(lexeme.getFile() != null){
            String filename = lexeme.getFile().getName();
            if(filename.matches("^mainlib.*\\.em$")){
                filename = "mainlib.em";
            }
            errorLine = String.format("ERROR in %s at (%d,%d)",
                    filename,
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
        if(Logger.getLogLevel() > 0){
            Logger.debug("--------------------------------------------");
            Logger.debug("STACKTRACE");

            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for(int i = 2; i < stack.length; i++){
                Logger.debug("\t at " + stack[i]);
            }
        }
        System.exit(1);
    }

    /**
     * Read a single line from a file
     * (line numbers start at 1)
     * @param file
     * @param linenumber
     * @return line from file as a String
     */
    private String readLineFromFile(File file, int linenumber){
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
    private String leftpadcaret(int tabs, int spaces, String start){
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
