/*
 * Created on Jun 12, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.HTMLUtils;
import net.thevpc.more.shef.ShefHelper;

/**
 * Remove Action for Wysiwyg HTML editing
 *
 * @author Bob Tantlinger
 *
 */
public class RemoveAction extends DecoratedTextAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final int BACKSPACE = 0;
    public static final int DELETE = 1;

    private int type = BACKSPACE;
    //private Action delegate = null;

    public RemoveAction(int type, Action defaultAction) {
        super("RemoveAction", defaultAction);
        //delegate = defaultAction;
        this.type = type;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        JEditorPane editor;

        try {
            editor = (JEditorPane) getTextComponent(e);
            if (!editor.isEditable() || !editor.isEnabled()) {
                return;
            }
        } catch (ClassCastException ex) {
            delegate.actionPerformed(e);
            return;
        }

        if (type == DELETE) {
            ShefHelper.runDeleteNext(editor);
        }

        if (type == BACKSPACE) {
            ShefHelper.runDeletePrevious(editor);
        }

        delegate.actionPerformed(e);
    }

}
