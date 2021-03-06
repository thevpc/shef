/*
 * Created on Nov 3, 2007
 */
package net.atlanticbb.tantlinger.ui.text;

import java.awt.Color;
import java.awt.Font;
import java.util.StringTokenizer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * @author Bob Tantlinger
 * @author Andreas Rudolph
 *
 */
@Deprecated
public class SourceCodeEditor extends RSyntaxTextArea
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    /**
     *
     */
    public SourceCodeEditor()
    {
        super();

        //SyntaxFactory.setSyntaxCatalog(getClass().getResource("/resources/syntax.catalog.xml"));
        //SyntaxFactory.loadSyntaxes();

        //setLineHighlight(true);
        //setBracketHighlight(true);
        //setAntiAlias(true);
        //setOpaque(true);
        //setCaretOnDelay(500);
        //setCaretOffDelay(500);
        //setFont(new Font("Default", Font.PLAIN, 12));

        setOpaque(true);
        setBracketMatchingEnabled( true );
        //setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        /*// Load default theme
        Properties themes = new Properties();
        InputStream in = null;
        try
        {
            in = getClass().getResource("/net/atlanticbb/tantlinger/ui/text/syntax.properties").openStream();
            themes.load(in);
            setTheme(themes, "default");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                in.close();
            }
            catch(Exception ex)
            {

            }
        }*/
    }

    /*public void setTheme(Properties props)
    {
        String prefix = "default";
        try
        {
            SyntaxDocument d = (SyntaxDocument)getDocument();
            prefix = d.getSyntax().getName();

        }
        catch(ClassCastException ex)
        {

        }

        setTheme(props, prefix);
    }*/


    /**
     * Set a color theme from a properties file
     * @param props The Properties object
     * @param prefix The prefix to use when getting the keys
     *
    public void setTheme(Properties props, String prefix) //throws Exception
    {
        SyntaxStyle[] aStyles = getSyntaxStyles();

        aStyles[Token.COMMENT1].setForeground(parseColor(props.getProperty(prefix + ".comment1.fg")));
        aStyles[Token.COMMENT1].setBackground(parseColor(props.getProperty(prefix + ".comment1.bg")));
        aStyles[Token.COMMENT1].setFontStyle(parseFont(props.getProperty(prefix + ".comment1.font")));
        aStyles[Token.COMMENT2].setForeground(parseColor(props.getProperty(prefix + ".comment2.fg")));
        aStyles[Token.COMMENT2].setBackground(parseColor(props.getProperty(prefix + ".comment2.bg")));
        aStyles[Token.COMMENT2].setFontStyle(parseFont(props.getProperty(prefix + ".comment2.font")));
        aStyles[Token.COMMENT3].setForeground(parseColor(props.getProperty(prefix + ".comment3.fg")));
        aStyles[Token.COMMENT3].setBackground(parseColor(props.getProperty(prefix + ".comment3.bg")));
        aStyles[Token.COMMENT3].setFontStyle(parseFont(props.getProperty(prefix + ".comment3.font")));
        aStyles[Token.DIGIT].setForeground(parseColor(props.getProperty(prefix + ".digit.fg")));
        aStyles[Token.DIGIT].setBackground(parseColor(props.getProperty(prefix + ".digit.bg")));
        aStyles[Token.DIGIT].setFontStyle(parseFont(props.getProperty(prefix + ".digit.font")));
        aStyles[Token.LITERAL1].setForeground(parseColor(props.getProperty(prefix + ".literal1.fg")));
        aStyles[Token.LITERAL1].setBackground(parseColor(props.getProperty(prefix + ".literal1.bg")));
        aStyles[Token.LITERAL1].setFontStyle(parseFont(props.getProperty(prefix + ".literal1.font")));
        aStyles[Token.LITERAL2].setForeground(parseColor(props.getProperty(prefix + ".literal2.fg")));
        aStyles[Token.LITERAL2].setBackground(parseColor(props.getProperty(prefix + ".literal2.bg")));
        aStyles[Token.LITERAL2].setFontStyle(parseFont(props.getProperty(prefix + ".literal2.font")));
        aStyles[Token.KEYWORD1].setForeground(parseColor(props.getProperty(prefix + ".keyword1.fg")));
        aStyles[Token.KEYWORD1].setBackground(parseColor(props.getProperty(prefix + ".keyword1.bg")));
        aStyles[Token.KEYWORD1].setFontStyle(parseFont(props.getProperty(prefix + ".keyword1.font")));
        aStyles[Token.KEYWORD2].setForeground(parseColor(props.getProperty(prefix + ".keyword2.fg")));
        aStyles[Token.KEYWORD2].setBackground(parseColor(props.getProperty(prefix + ".keyword2.bg")));
        aStyles[Token.KEYWORD2].setFontStyle(parseFont(props.getProperty(prefix + ".keyword2.font")));
        aStyles[Token.KEYWORD3].setForeground(parseColor(props.getProperty(prefix + ".keyword3.fg")));
        aStyles[Token.KEYWORD3].setBackground(parseColor(props.getProperty(prefix + ".keyword3.bg")));
        aStyles[Token.KEYWORD3].setFontStyle(parseFont(props.getProperty(prefix + ".keyword3.font")));
        aStyles[Token.KEYWORD4].setForeground(parseColor(props.getProperty(prefix + ".keyword4.fg")));
        aStyles[Token.KEYWORD4].setBackground(parseColor(props.getProperty(prefix + ".keyword4.bg")));
        aStyles[Token.KEYWORD4].setFontStyle(parseFont(props.getProperty(prefix + ".keyword4.font")));
        aStyles[Token.KEYWORD5].setForeground(parseColor(props.getProperty(prefix + ".keyword5.fg")));
        aStyles[Token.KEYWORD5].setBackground(parseColor(props.getProperty(prefix + ".keyword5.bg")));
        aStyles[Token.KEYWORD5].setFontStyle(parseFont(props.getProperty(prefix + ".keyword5.font")));
        aStyles[Token.FUNCTION].setForeground(parseColor(props.getProperty(prefix + ".function.fg")));
        aStyles[Token.FUNCTION].setBackground(parseColor(props.getProperty(prefix + ".function.bg")));
        aStyles[Token.FUNCTION].setFontStyle(parseFont(props.getProperty(prefix + ".function.font")));
        aStyles[Token.OPERATOR].setForeground(parseColor(props.getProperty(prefix + ".operator.fg")));
        aStyles[Token.OPERATOR].setBackground(parseColor(props.getProperty(prefix + ".operator.bg")));
        aStyles[Token.OPERATOR].setFontStyle(parseFont(props.getProperty(prefix + ".operator.font")));
        aStyles[Token.MARKUP].setForeground(parseColor(props.getProperty(prefix + ".markup.fg")));
        aStyles[Token.MARKUP].setBackground(parseColor(props.getProperty(prefix + ".markup.bg")));
        aStyles[Token.MARKUP].setFontStyle(parseFont(props.getProperty(prefix + ".markup.font")));
        aStyles[Token.LABEL].setForeground(parseColor(props.getProperty(prefix + ".label.fg")));
        aStyles[Token.LABEL].setBackground(parseColor(props.getProperty(prefix + ".label.bg")));
        aStyles[Token.LABEL].setFontStyle(parseFont(props.getProperty(prefix + ".label.font")));
        aStyles[Token.INVALID].setForeground(parseColor(props.getProperty(prefix + ".invalid.fg")));
        aStyles[Token.INVALID].setBackground(parseColor(props.getProperty(prefix + ".invalid.bg")));
        aStyles[Token.INVALID].setFontStyle(parseFont(props.getProperty(prefix + ".invalid.font")));
        aStyles[Token.NULL].setFontStyle(parseFont(props.getProperty(prefix + ".null.font")));
        setForeground(parseColor(props.getProperty(prefix + ".text.fg")));
        setBackground(parseColor(props.getProperty(prefix + ".text.bg")));
        setCaretColor(parseColor(props.getProperty(prefix + ".caret.fg")));
        setBracketHighlightColor(parseColor(props.getProperty(prefix + ".brackethighlight.fg")));
        //setSelectionColor(parseColor(props.getProperty(prefix + ".selectioncolor.bg")));
        setLineHighlightColor(parseColor(props.getProperty(prefix + ".linehighlight.fg")));
        //gutter.setForeground(parseColor(props.getProperty(prefix + ".gutter.fg")));
        //gutter.setBackground(parseColor(props.getProperty(prefix + ".gutter.bg")));
        //gutter.setDividerForeground(parseColor(props.getProperty(prefix + ".divider.fg")));
        //gutter.setDividerBackground(parseColor(props.getProperty(prefix + ".divider.bg")));
        //gutter.setCurrentLineForeground(parseColor(props.getProperty(prefix + ".currentline.fg")));
        //gutter.setCurrentLineBackground(parseColor(props.getProperty(prefix + ".currentline.bg")));
        //gutter.setLineIntervalForeground(parseColor(props.getProperty(prefix + ".lineinterval.fg")));
        //gutter.setLineIntervalBackground(parseColor(props.getProperty(prefix + ".lineinterval.bg")));
        //gutter.setSelectionForeground(parseColor(props.getProperty(prefix + ".gutterselection.fg")));
        //gutter.setSelectionBackground(parseColor(props.getProperty(prefix + ".gutterselection.bg")));
        //gutter.setBracketScopeForeground(parseColor(props.getProperty(prefix + ".bracketscope.fg")));
        //gutter.setBracketScopeBackground(parseColor(props.getProperty(prefix + ".bracketscope.bg")));
    }*/

   

}
