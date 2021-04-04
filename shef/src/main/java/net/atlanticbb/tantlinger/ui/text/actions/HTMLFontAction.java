/*
 * Created on Jan 18, 2006
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.dialogs.HTMLFontDialog;
import net.thevpc.more.shef.FontDesc;
import net.thevpc.more.shef.ShefHelper;

/**
 * Action which edits an HTML font
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLFontAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HTMLFontAction() {
        super(i18n.str("font_"));         //$NON-NLS-1$
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
        HTMLFontDialog d = createDialog(editor);
        d.setLocationRelativeTo(d.getParent());
        d.setVisible(true);
        if (!d.hasUserCancelled()) {
            editor.requestFocusInWindow();
            editor.replaceSelection(d.getHTML());
        }
    }

    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
        HTMLFontDialog d = createDialog(editor);
        FontDesc font = ShefHelper.runGetFont(editor);

        d.setBold(font.isBold());
        d.setItalic(font.isItalic());
        d.setUnderline(font.isUnderline());
        if (font.getFontName() != null) {
            d.setFontName(font.getFontName());
        }
        if (font.getFontSize() != 0) {
            d.setFontSize(font.getFontSize());
        }
        d.setLocationRelativeTo(d.getParent());
        d.setVisible(true);
        if (!d.hasUserCancelled()) {
            ShefHelper.runChangeFont(editor, new FontDesc(d.getFontName(), d.getFontSize(),
                    d.isBold(), d.isItalic(), d.isUnderline()));
        }
    }

    private HTMLFontDialog createDialog(JTextComponent ed) {
        Window w = SwingUtilities.getWindowAncestor(ed);
        String t = ""; //$NON-NLS-1$
        if (ed.getSelectedText() != null) {
            t = ed.getSelectedText();
        }
        HTMLFontDialog d = null;
        if (w != null && w instanceof Frame) {
            d = new HTMLFontDialog((Frame) w, t);
        } else if (w != null && w instanceof Dialog) {
            d = new HTMLFontDialog((Dialog) w, t);
        }

        return d;
    }

}
