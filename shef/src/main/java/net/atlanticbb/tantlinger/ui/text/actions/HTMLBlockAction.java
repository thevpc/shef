/*
 * Created on Feb 26, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import net.thevpc.more.shef.BlocEnum;
import net.thevpc.more.shef.ShefHelper;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.ElementWriter;
import net.thevpc.more.shef.HTMLUtils;

import org.bushe.swing.action.ActionManager;

/**
 * Action which formats HTML block level elements
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLBlockAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final int DIV = 0;
    public static final int P = 1;
    public static final int H1 = 2;
    public static final int H2 = 3;
    public static final int H3 = 4;
    public static final int H4 = 5;
    public static final int H5 = 6;
    public static final int H6 = 7;
    public static final int PRE = 8;
    public static final int BLOCKQUOTE = 9;
    public static final int OL = 10;
    public static final int UL = 11;

    private static final int KEYS[]
            = {
                KeyEvent.VK_D, KeyEvent.VK_ENTER, KeyEvent.VK_1, KeyEvent.VK_2,
                KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6,
                KeyEvent.VK_R, KeyEvent.VK_Q, KeyEvent.VK_N, KeyEvent.VK_U
            };

    public static final String[] ELEMENT_TYPES
            = {
                i18n.str("body_text"), //$NON-NLS-1$
                i18n.str("paragraph"), //$NON-NLS-1$
                i18n.str("heading") + " 1", //$NON-NLS-1$ //$NON-NLS-2$
                i18n.str("heading") + " 2", //$NON-NLS-1$ //$NON-NLS-2$
                i18n.str("heading") + " 3", //$NON-NLS-1$ //$NON-NLS-2$
                i18n.str("heading") + " 4", //$NON-NLS-1$ //$NON-NLS-2$
                i18n.str("heading") + " 5", //$NON-NLS-1$ //$NON-NLS-2$
                i18n.str("heading") + " 6", //$NON-NLS-1$ //$NON-NLS-2$
                i18n.str("preformatted"), //$NON-NLS-1$
                i18n.str("blockquote"), //$NON-NLS-1$
                i18n.str("ordered_list"), //$NON-NLS-1$
                i18n.str("unordered_list") //$NON-NLS-1$        
            };

    private int type;

    /**
     * Creates a new HTMLBlockAction
     *
     * @param type A block type - P, PRE, BLOCKQUOTE, H1, H2, etc
     *
     * @throws IllegalArgumentException
     */
    public HTMLBlockAction(int type) throws IllegalArgumentException {
        super(""); //$NON-NLS-1$
        if (type < 0 || type >= ELEMENT_TYPES.length) {
            throw new IllegalArgumentException("Illegal argument"); //$NON-NLS-1$
        }
        this.type = type;
        putValue(NAME, ELEMENT_TYPES[type]);
        putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KEYS[type], Event.ALT_MASK));
        if (type == P) {
            putValue(MNEMONIC_KEY, new Integer(i18n.mnem("paragraph"))); //$NON-NLS-1$
        } else if (type == PRE) {
            putValue(MNEMONIC_KEY, new Integer(i18n.mnem("preformatted"))); //$NON-NLS-1$
        } else if (type == BLOCKQUOTE) {
            putValue(MNEMONIC_KEY, new Integer(i18n.mnem("blockquote"))); //$NON-NLS-1$
        } else if (type == OL) {
            putValue(SMALL_ICON,
                    UIUtils.getIcon(UIUtils.X16, "listordered.png")); //$NON-NLS-1$
            putValue(MNEMONIC_KEY, new Integer(i18n.mnem("ordered_list"))); //$NON-NLS-1$
        } else if (type == UL) {
            putValue(SMALL_ICON,
                    UIUtils.getIcon(UIUtils.X16, "listunordered.png")); //$NON-NLS-1$
            putValue(MNEMONIC_KEY, new Integer(i18n.mnem("unordered_list"))); //$NON-NLS-1$
        } else {
            String s = type + ""; //$NON-NLS-1$
            putValue(Action.MNEMONIC_KEY, new Integer(s.charAt(0)));
        }
        putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
    }

    protected void updateWysiwygContextState(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();
        Element elem = document.getParagraphElement(ed.getCaretPosition());

        String elemName = elem.getName();
        if (elemName.equals("p-implied")) //$NON-NLS-1$
        {
            elemName = elem.getParentElement().getName();
        }

        if (type == DIV && (elemName.equals("div") || elemName.equals("body") || elemName.equals("td"))) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
            setSelected(true);
        } else if (type == UL) {
            Element listElem = HTMLUtils.getListParent(elem);
            setSelected(listElem != null && (listElem.getName().equals("ul")));                   //$NON-NLS-1$
        } else if (type == OL) {
            Element listElem = HTMLUtils.getListParent(elem);
            setSelected(listElem != null && (listElem.getName().equals("ol")));  //$NON-NLS-1$
        } else if (elemName.equals(ShefHelper.getTag(BlocEnum.values()[type]).toString().toLowerCase())) {
            setSelected(true);
        } else {
            setSelected(false);
        }
    }

    protected void updateSourceContextState(JEditorPane ed) {
        setSelected(false);
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
        String tag = ShefHelper.getTag(BlocEnum.values()[type]).toString();
        String prefix = "\n<" + tag + ">\n\t"; //$NON-NLS-1$ //$NON-NLS-2$
        String postfix = "\n</" + tag + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
        if (type == OL || type == UL) {
            prefix += "<li>"; //$NON-NLS-1$
            postfix = "</li>" + postfix; //$NON-NLS-1$
        }

        String sel = editor.getSelectedText();
        if (sel == null) {
            editor.replaceSelection(prefix + postfix);

            int pos = editor.getCaretPosition() - postfix.length();
            if (pos >= 0) {
                editor.setCaretPosition(pos);
            }
        } else {
            sel = prefix + sel + postfix;
            editor.replaceSelection(sel);
        }
    }

    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
        ShefHelper.runInsertBloc(editor, BlocEnum.values()[type]);
    }

}
