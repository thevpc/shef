package net.atlanticbb.tantlinger.ui.text.actions;

/*
 * Created on Dec 10, 2005
 *
 */
import java.awt.event.ActionEvent;
import java.io.StringWriter;
import java.util.Enumeration;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import net.thevpc.more.shef.ShefHelper;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.ElementWriter;
import net.thevpc.more.shef.HTMLUtils;

/**
 * Action which properly inserts breaks for an HTMLDocument
 *
 * @author Bob Tantlinger
 *
 */
public class EnterKeyAction extends DecoratedTextAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //private Action delegate = null;

    /**
     * Creates a new EnterKeyAction.
     *
     * @param defaultEnterAction Should be the default action
     */
    public EnterKeyAction(Action defaultEnterAction) {
        super("EnterAction", defaultEnterAction);
        //delegate = defaultEnterAction;
    }

    public void actionPerformed(ActionEvent e) {
        JEditorPane editor;
        HTMLDocument document;

        try {
            editor = (JEditorPane) getTextComponent(e);
            ShefHelper.runInsertEnter(editor);
        } catch (ClassCastException ex) {
            // don't know what to do with this type
            // so pass off the event to the delegate
            delegate.actionPerformed(e);
            return;
        }
        ;
    }

   
}
