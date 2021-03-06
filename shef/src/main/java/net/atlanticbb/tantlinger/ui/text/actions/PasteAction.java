/*
 * Created on Jun 19, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.atlanticbb.tantlinger.ui.UIUtils;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.ShefHelper;

import org.bushe.swing.action.ActionManager;
import org.bushe.swing.action.ShouldBeEnabledDelegate;


public class PasteAction extends HTMLTextEditAction
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PasteAction()
    {
        super(i18n.str("paste"));
        putValue(MNEMONIC_KEY, new Integer(i18n.mnem("paste")));
        putValue(SMALL_ICON, UIUtils.getIcon(UIUtils.X16, "paste.png"));
        putValue(ActionManager.LARGE_ICON, UIUtils.getIcon(UIUtils.X24, "paste.png"));
		putValue(ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        addShouldBeEnabledDelegate(new ShouldBeEnabledDelegate()
        {
            public boolean shouldBeEnabled(Action a)
            {
                JEditorPane editor = getCurrentEditor();
                if (editor == null)
                    return true;

                return editor.isEditable() && editor.isEnabled();
                //return getCurrentEditor() != null &&
                //    Toolkit.getDefaultToolkit().getSystemClipboard().getContents(PasteAction.this) != null;
            }
        });

        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));
    }

    protected void updateWysiwygContextState(JEditorPane wysEditor)
    {
        this.updateEnabledState();
    }

    protected void updateSourceContextState(JEditorPane srcEditor)
    {
        this.updateEnabledState();
    }

    /* (non-Javadoc)
     * @see net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction#sourceEditPerformed(java.awt.event.ActionEvent, javax.swing.JEditorPane)
     */
    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor)
    {
        editor.paste();
    }

    /* (non-Javadoc)
     * @see net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction#wysiwygEditPerformed(java.awt.event.ActionEvent, javax.swing.JEditorPane)
     */
    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor)
    {
        ShefHelper.runPaste(editor);
    }
}
