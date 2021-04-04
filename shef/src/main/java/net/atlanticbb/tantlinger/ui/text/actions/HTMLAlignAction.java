/*
 * Created on Feb 25, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import net.thevpc.more.shef.AlignEnum;
import net.thevpc.more.shef.ShefHelper;

import org.bushe.swing.action.ActionManager;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.HTMLUtils;

/**
 * Action which aligns HTML elements
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLAlignAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    public static final int JUSTIFY = 3;

    public static final String ALIGNMENT_NAMES[]
            = {
                i18n.str("left"),
                i18n.str("center"),
                i18n.str("right"),
                i18n.str("justify")
            };

    private static final int[] MNEMS
            = {
                i18n.mnem("left"),
                i18n.mnem("center"),
                i18n.mnem("right"),
                i18n.mnem("justify")
            };

    public static final String ALIGNMENTS[]
            = {
                "left", "center", "right", "justify"
            };

    private static final String IMGS[]
            = {
                "al_left.png", "al_center.png", "al_right.png", "al_just.png"
            };

    private int align;

    /**
     * Creates a new HTMLAlignAction
     *
     * @param al LEFT, RIGHT, CENTER, or JUSTIFY
     * @throws IllegalArgumentException
     */
    public HTMLAlignAction(int al) throws IllegalArgumentException {
        super("");
        if (al < 0 || al >= ALIGNMENTS.length) {
            throw new IllegalArgumentException("Illegal Argument");
        }

        //String pkg = getClass().getPackage().getName();
        putValue(NAME, (ALIGNMENT_NAMES[al]));
        putValue(MNEMONIC_KEY, new Integer(MNEMS[al]));

        putValue(SMALL_ICON, UIUtils.getIcon(UIUtils.X16, IMGS[al]));
        putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_RADIO);

        align = al;
    }

    protected void updateWysiwygContextState(JEditorPane ed) {
        setSelected(shouldBeSelected(ed));
    }

    private boolean shouldBeSelected(JEditorPane ed) {
        return ShefHelper.isEnabledAlign(ed, AlignEnum.values()[align]);
    }

    protected void updateSourceContextState(JEditorPane ed) {
        setSelected(false);
    }

    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
        ShefHelper.runTextAlign(editor, AlignEnum.values()[align]);
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
        String prefix = "<p align=\"" + ALIGNMENTS[align] + "\">";
        String postfix = "</p>";
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
}
