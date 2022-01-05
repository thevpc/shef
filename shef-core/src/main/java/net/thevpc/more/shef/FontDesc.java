/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.more.shef;

/**
 *
 * @author thevpc
 */
public class FontDesc {
    String fontName;
    int fontSize;
    boolean bold;
    boolean italic;
    Boolean underline;

    public FontDesc(String fontName, int fontSize, boolean bold, boolean italic, Boolean underline) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public Boolean getUnderline() {
        return underline;
    }
    
    
}
