/*
 * Created on Mar 3, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.thevpc.more.shef.ShefHelper;



/**
 * Action which inserts a horizontal rule
 * 
 * @author Bob Tantlinger
 *
 */
public class HTMLHorizontalRuleAction extends HTMLTextEditAction
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public HTMLHorizontalRuleAction()
    {
        super(i18n.str("horizontal_rule"));
        putValue(MNEMONIC_KEY, new Integer(i18n.mnem("horizontal_rule")));
        putValue(SMALL_ICON, UIUtils.getIcon(UIUtils.X16, "hrule.png")); 
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor)
    {
        editor.replaceSelection("<hr>");
    }
    
    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor)
    {
        ShefHelper.runInsertHorizontalRule(editor);
    }
}
