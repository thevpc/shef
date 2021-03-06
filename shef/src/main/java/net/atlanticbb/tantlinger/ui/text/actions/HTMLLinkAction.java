/*
 * Created on Feb 26, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.thevpc.more.shef.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.dialogs.HyperlinkDialog;
import net.thevpc.more.shef.ShefHelper;

/**
 * Action which displays a dialog to insert a hyperlink
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLLinkAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Set hiddenAttributes;

    public HTMLLinkAction() {
        this(null);
    }

    public HTMLLinkAction(Set hiddenAttributes) {
        super(i18n.str("hyperlink_"));
        putValue(MNEMONIC_KEY, new Integer(i18n.mnem("hyperlink_")));

        putValue(SMALL_ICON, UIUtils.getIcon(UIUtils.X16, "link.png"));
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
        this.hiddenAttributes = hiddenAttributes;
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
        HyperlinkDialog dlg = createDialog(editor);
        if (dlg == null) {
            return;
        }

        dlg.setLocationRelativeTo(dlg.getParent());
        //dlg.setName(editor.getSelectedText());
        dlg.setLinkText(editor.getSelectedText());
        dlg.setVisible(true);
        if (dlg.hasUserCancelled()) {
            return;
        }

        editor.requestFocusInWindow();
        editor.replaceSelection(dlg.getHTML());
    }

    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
        HyperlinkDialog dlg = createDialog(editor);
        if (dlg == null) {
            return;
        }

        if (editor.getSelectedText() != null) {
            dlg.setLinkText(editor.getSelectedText());
        }
        dlg.setLocationRelativeTo(dlg.getParent());
        dlg.setVisible(true);
        if (dlg.hasUserCancelled()) {
            return;
        }

        ShefHelper.runInsertLink(editor, dlg.getHTML());
        dlg = null;
    }

    protected HyperlinkDialog createDialog(JTextComponent ed) {
        Window w = SwingUtilities.getWindowAncestor(ed);
        HyperlinkDialog d = null;
        if (w != null && w instanceof Frame) {
            d = new HyperlinkDialog((Frame) w);
        } else if (w != null && w instanceof Dialog) {
            d = new HyperlinkDialog((Dialog) w);
        }

        if (d != null && hiddenAttributes != null) {
            d.setHiddenAttributes(hiddenAttributes);
        }

        return d;
    }
}
