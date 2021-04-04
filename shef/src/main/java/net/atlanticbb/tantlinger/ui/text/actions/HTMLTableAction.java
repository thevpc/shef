/*
 * Created on Feb 26, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.dialogs.NewTableDialog;
import net.thevpc.more.shef.ShefHelper;

/**
 * Action which shows a dialog to insert an HTML table
 *
 * @author Bob Tantlinger
 *
 */
public class HTMLTableAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HTMLTableAction() {
        super(i18n.str("table_"));
        putValue(MNEMONIC_KEY, new Integer(i18n.mnem("table_")));

        putValue(SMALL_ICON, UIUtils.getIcon(UIUtils.X16, "table.png"));
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
    }

    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
        NewTableDialog dlg = createNewTableDialog(editor);
        if (dlg == null) {
            return;
        }
        dlg.setLocationRelativeTo(dlg.getParent());
        dlg.setVisible(true);
        if (dlg.hasUserCancelled()) {
            return;
        }

        editor.replaceSelection(dlg.getHTML());
    }

    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
        NewTableDialog dlg = createNewTableDialog(editor);
        if (dlg == null) {
            return;
        }
        dlg.setLocationRelativeTo(dlg.getParent());
        dlg.setVisible(true);
        if (dlg.hasUserCancelled()) {
            return;
        }

        String html = dlg.getHTML();
        ShefHelper.runInsertTable(editor, html);
    }

    /**
     * Creates the dialog
     *
     * @param ed
     * @return the dialog
     */
    private NewTableDialog createNewTableDialog(JTextComponent ed) {
        Window w = SwingUtilities.getWindowAncestor(ed);
        NewTableDialog d = null;
        if (w != null && w instanceof Frame) {
            d = new NewTableDialog((Frame) w);
        } else if (w != null && w instanceof Dialog) {
            d = new NewTableDialog((Dialog) w);
        }

        return d;
    }
}
