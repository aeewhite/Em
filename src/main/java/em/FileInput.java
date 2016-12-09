package em;

import java.io.*;

/**
 * Created by andrew on 9/21/16.
 */
public class FileInput {
    private PushbackReader pushBackReader;
    private int row;
    private int col;

    public FileInput(String filename) throws FileNotFoundException {
        this(new File(filename));
    }

    public FileInput(File file) throws FileNotFoundException {
        FileReader reader  = new FileReader(file);
        pushBackReader = new PushbackReader(reader, 10);
        row = 1;
        col = 1;
    }


    /**
     * Read one codepoint from the input
     * @return a single codepoint
     */
    public int read(){
        int out;

        try{
            out = pushBackReader.read();
            if(out == '\n'){
                row++;
                col = 1;
            }
            else{
                col++;
            }
            // Check if that character is part of a surrogate pair
            if(Character.isHighSurrogate((char) out)){
                // if it was, read the other member of the pair
                int secondMember = pushBackReader.read();
                // and change in to the full codepoint
                out = Character.toCodePoint((char) out, (char) secondMember);
            }
            else if(out =='\\'){
                int test = read();
                switch(test){
                    case 'n':
                        col++;
                        return '\n';
                    case 't':
                        col++;
                        return '\t';
                    case '\\':
                        col++;
                        return '\\';
                    default:
                        this.pushback(test);
                        return out;
                }
            }
        } catch (IOException e){
            System.err.println("Error in reading file");
            e.printStackTrace();
            System.exit(1);
            return -1;
        }
        return out;
    }

    /**
     * Push a single codepoint back onto the buffer to
     * be read again
     * @param codepoint codepoint to push back
     */
    public void pushback(int codepoint){
        try{
            //Regular character
            if(!Character.isSupplementaryCodePoint(codepoint)){
                pushBackReader.unread((char)codepoint);
            }
            else{
                //Split into chars
                char high = Character.highSurrogate(codepoint);
                char low = Character.lowSurrogate(codepoint);
                pushBackReader.unread(low);
                pushBackReader.unread(high);
            }
            if(codepoint == '\n'){
                row--;
            }else{
                col--;
            }
        } catch (IOException e){
            System.err.println("Error in reading file");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Skip to the end of the current line
     */
    public void skipLine(){
        int cp = this.read();
        while(cp != '\n' && cp >= 0){
            cp = this.read();
        }
    }

    /**
     * Skip whitespace characters
     */
    public void skipWhitespace(){
        int cp = this.read();
        if(Character.isWhitespace(cp)){
            this.skipWhitespace();
        }
        else if (cp == '#'){
            this.skipLine();
            this.skipWhitespace();
        }
        else{
            this.pushback(cp);
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
