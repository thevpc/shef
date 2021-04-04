/*
 * Created on Feb 25, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.HTMLUtils;
import net.thevpc.more.shef.ShefHelper;
import net.thevpc.more.shef.InlineStyleEnum;

import org.bushe.swing.action.ActionManager;



/**
 * Action which toggles inline HTML elements
 * 
 * @author Bob Tantlinger
 *
 */
public class HTMLInlineAction extends HTMLTextEditAction
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static final int EM = 0;
    public static final int STRONG = 1;
    public static final int CODE = 2;
    public static final int CITE = 3;
    public static final int SUP = 4;
    public static final int SUB = 5;    
    public static final int BOLD = 6;    
    public static final int ITALIC = 7;    
    public static final int UNDERLINE = 8;    
    public static final int STRIKE = 9;

    public static final String[] INLINE_TYPES = 
    {        
        i18n.str("emphasis"),
        i18n.str("strong"),
        i18n.str("code"),
        i18n.str("cite"),
        i18n.str("superscript"),
        i18n.str("subscript"),        
        i18n.str("bold"),
        i18n.str("italic"),
        i18n.str("underline"),
        i18n.str("strikethrough")        
    };
    
    private static final int[] MNEMS =
    {
        i18n.mnem("emphasis"),
        i18n.mnem("strong"),
        i18n.mnem("code"),
        i18n.mnem("cite"),
        i18n.mnem("superscript"),
        i18n.mnem("subscript"),        
        i18n.mnem("bold"),
        i18n.mnem("italic"),
        i18n.mnem("underline"),
        i18n.mnem("strikethrough")
    };
    
    private int type;

    /**
     * Creates a new HTMLInlineAction
     * 
     * @param itype an inline element type (BOLD, ITALIC, STRIKE, etc)
     * @throws IllegalArgumentException
     */
    public HTMLInlineAction(int itype) throws IllegalArgumentException
    {
        super("");
        type = itype;
        if(type < 0 || type >= INLINE_TYPES.length)
            throw new IllegalArgumentException("Illegal Argument");
        putValue(NAME, (INLINE_TYPES[type]));
        putValue(MNEMONIC_KEY, new Integer(MNEMS[type]));
        
        Icon ico = null;
        KeyStroke ks = null;
        if(type == BOLD)
        {
            ico = UIUtils.getIcon(UIUtils.X16, "bold.png");
            ks = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        }
        else if(type == ITALIC)
        {
            ico = UIUtils.getIcon(UIUtils.X16, "italic.png");
            ks = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        }
        else if(type == UNDERLINE)
        {
            ico = UIUtils.getIcon(UIUtils.X16, "underline.png");
            ks = KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK);
        }
        putValue(SMALL_ICON, ico);
        putValue(ACCELERATOR_KEY, ks);
        putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_CHECKBOX);
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
    }
    
    protected void updateWysiwygContextState(JEditorPane ed)
    {        
        setSelected(ShefHelper.isDefined(HTMLUtils.getCharacterAttributes(ed),InlineStyleEnum.values()[type]));
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor)
    {
        HTML.Tag tag = ShefHelper.getTag(InlineStyleEnum.values()[type]);
        String prefix = "<" + tag.toString() + ">";
        String postfix = "</" + tag.toString() + ">";
        String sel = editor.getSelectedText();
        if(sel == null)
        {
            editor.replaceSelection(prefix + postfix);
            
            int pos = editor.getCaretPosition() - postfix.length();
            if(pos >= 0)
            	editor.setCaretPosition(pos);                    		  
        }
        else
        {
            sel = prefix + sel + postfix;
            editor.replaceSelection(sel);                
        }
    }

    
    
    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor)
    {        
        ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.values()[type]);
        //HTMLUtils.printAttribs(HTMLUtils.getCharacterAttributes(editor));        
    }
    
    
    
        
    protected void updateSourceContextState(JEditorPane ed)
    {
        setSelected(false);
    }
}