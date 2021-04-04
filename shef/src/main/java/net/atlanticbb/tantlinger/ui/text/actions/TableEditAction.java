/*
 * Created on Jun 16, 2005
 *
 */
package net.atlanticbb.tantlinger.ui.text.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.bushe.swing.action.ShouldBeEnabledDelegate;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.thevpc.more.shef.HTMLUtils;
import net.thevpc.more.shef.ShefHelper;

/**
 *
 * Action for adding and removing table elements
 *
 * @author Bob Tantlinger
 *
 */
public class TableEditAction extends HTMLTextEditAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final int INSERT_CELL = 0;
    public static final int DELETE_CELL = 1;
    public static final int INSERT_ROW = 2;
    public static final int DELETE_ROW = 3;
    public static final int INSERT_COL = 4;
    public static final int DELETE_COL = 5;

    private static final String NAMES[]
            = {
                i18n.str("insert_cell"),
                i18n.str("delete_cell"),
                i18n.str("insert_row"),
                i18n.str("delete_row"),
                i18n.str("insert_column"),
                i18n.str("delete_column")
            };

    private int type;

    public TableEditAction(int type) throws IllegalArgumentException {
        super("");
        if (type < 0 || type >= NAMES.length) {
            throw new IllegalArgumentException("Invalid type");
        }
        this.type = type;
        putValue(NAME, NAMES[type]);
        addShouldBeEnabledDelegate(new ShouldBeEnabledDelegate() {
            public boolean shouldBeEnabled(Action a) {
                return (getEditMode() != SOURCE) && ShefHelper.isInTD(getCurrentEditor());
            }
        });
    }

    protected void wysiwygEditPerformed(ActionEvent e, JEditorPane ed) {
        if (type == INSERT_CELL) {
            ShefHelper.runInsertTableCell(ed);
        } else if (type == DELETE_CELL) {
            ShefHelper.runDeleteTableCell(ed);
        } else if (type == INSERT_ROW) {
            ShefHelper.runInsertTableRow(ed);
        } else if (type == DELETE_ROW) {
            ShefHelper.runDeleteTableRow(ed);
        } else if (type == INSERT_COL) {
            ShefHelper.runInsertTableCol(ed);
        } else if (type == DELETE_COL) {
            ShefHelper.runDeleteTableCol(ed);
        }
    }

    protected void updateWysiwygContextState(JEditorPane wysEditor) {
        boolean isInTd = ShefHelper.isInTD(wysEditor);
        if ((isInTd && !isEnabled()) || (isEnabled() && !isInTd)) {
            updateEnabled();
        }
    }


    /* (non-Javadoc)
     * @see net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction#sourceEditPerformed(java.awt.event.ActionEvent, javax.swing.JEditorPane)
     */
    protected void sourceEditPerformed(ActionEvent e, JEditorPane editor) {

    }
}
