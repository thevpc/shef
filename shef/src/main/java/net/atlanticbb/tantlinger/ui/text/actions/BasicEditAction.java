/*
 * Created on Nov 2, 2007
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.ShefHelper;
import net.thevpc.more.shef.UndoManagerResolver;

/**
 * Action suitable for when wysiwyg or source context does not matter.
 *
 * @author Bob Tantlinger
 *
 */
public abstract class BasicEditAction extends HTMLTextEditAction {

    static {
        ShefHelper.setManagerResolver(new UndoManagerResolver() {
            public void beginCompoundEdit(Document doc) {
                CompoundUndoManager.beginCompoundEdit(doc);
            }

            public void endCompoundEdit(Document doc) {
                CompoundUndoManager.endCompoundEdit(doc);
            }
        });
    }

    /**
     * @param name
     */
    public BasicEditAction(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction#sourceEditPerformed(java.awt.event.ActionEvent, javax.swing.JEditorPane)
     */
    protected final void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
        doEdit(e, editor);
    }

    /* (non-Javadoc)
     * @see net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction#wysiwygEditPerformed(java.awt.event.ActionEvent, javax.swing.JEditorPane)
     */
    protected final void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
        doEdit(e, editor);
    }

    protected abstract void doEdit(ActionEvent e, JEditorPane editor);

    protected void updateContextState(JEditorPane editor) {

    }

    protected final void updateWysiwygContextState(JEditorPane wysEditor) {
        updateContextState(wysEditor);
    }

    protected final void updateSourceContextState(JEditorPane srcEditor) {
        updateContextState(srcEditor);
    }
}
