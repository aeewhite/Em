package em;

import java.io.File;
import java.io.Serializable;

/**
 * Created by andrew on 9/18/16.
 */
public class Lexeme implements Serializable{

    private LexemeType type;
    private Object value;
    private int row;
    private int col;
    private Lexeme leftChild;
    private Lexeme rightChild;
    private File file;

    public Lexeme(LexemeType type){
        this(type, null);
    }

    public Lexeme(LexemeType type, Object value){
        this(type, value, -1, -1, null);
    }

    public Lexeme(LexemeType type, Object value, int startRow, int startCol, File file){
        this.type = type;
        this.value = value;
        row=startRow;
        col=startCol;
        this.file = file;
    }

    public Lexeme(LexemeType type,  int startRow, int startCol, File file){
        this.type = type;
        this.value = null;
        row=startRow;
        col=startCol;
        this.file = file;
    }

    public LexemeType getType() {
        return type;
    }

    public Object getValue() {
        if(getType() == LexemeType.TRUE){
            return true;
        }
        else if(getType() == LexemeType.FALSE){
            return false;
        }
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Lexeme getLeft() {
        return leftChild;
    }

    public void setLeft(Lexeme leftChild) {
        this.leftChild = leftChild;
    }

    public Lexeme getRight() {
        return rightChild;
    }

    public void setRight(Lexeme rightChild) {
        this.rightChild = rightChild;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString(){
        if(value == null){
            return "<type: " + type + " @"+ row +","+ col +">";
        }
        else{
            return "<type: " + type + ", value: \"" + value.toString() + "\" @"+ row +":"+ col +">";
        }
    }
}
