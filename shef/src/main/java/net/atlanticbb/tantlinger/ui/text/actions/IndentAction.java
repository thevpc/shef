/*
 * Created on Nov 19, 2007
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.JEditorPane;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.ShefHelper;

/**
 * @author Bob Tantlinger
 *
 */
public class IndentAction extends HTMLTextEditAction {

    private static final long serialVersionUID = 1L;

    public static final int INDENT = 0;
    public static final int OUTDENT = 1;

    protected int direction;

    /**
     * @param name
     */
    public IndentAction(int direction) throws IllegalArgumentException {
        super("");
        if (direction == INDENT) {
            putValue(NAME, "Indent");
        } else if (direction == OUTDENT) {
            putValue(NAME, "Outdent");
        } else {
            throw new IllegalArgumentException("Invalid indentation direction");
        }
        this.direction = direction;

    }

    /* (non-Javadoc)
     * @see net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction#sourceEditPerformed(java.awt.event.ActionEvent, javax.swing.JEditorPane)
     */
    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {
        // TODO Auto-generated method stub

    }

   
 

    /* (non-Javadoc)
     * @see net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction#wysiwygEditPerformed(java.awt.event.ActionEvent, javax.swing.JEditorPane)
     */
    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane editor) {
        int cp = editor.getCaretPosition();
        CompoundUndoManager.beginCompoundEdit(editor.getDocument());
        if (direction == INDENT) {
            ShefHelper.runIndent(editor);
        } else {
            ShefHelper.runUnIndent(editor);
        }
        CompoundUndoManager.endCompoundEdit(editor.getDocument());
        editor.setCaretPosition(cp);
    }

}
