/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.more.shef;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;

/**
 *
 * @author vpc
 */
public class MinWysiwygHTMLEditorKitInstallHelper implements WysiwygHTMLEditorKitInstallHelper {

    private Map editorToActionsMap = new HashMap();
    private KeyStroke tabBackwardKS = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK);

    @Override
    public void install(JEditorPane editor) {
        if (editorToActionsMap.containsKey(editor)) {
            return; //already installed
        }        //install wysiwyg actions into the ActionMap for the editor being installed
        Map actions = new HashMap();
        InputMap inputMap = editor.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = editor.getActionMap();

        Action delegate = actionMap.get("insert-break");
        Action action = new AbstractAction() {
            {
                putValue(NAME, "insert-break");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ShefHelper.runInsertEnter(editor);
            }

        };
        actions.put("insert-break", action);
        actionMap.put("insert-break", action);

        delegate = actionMap.get("delete-previous");
        action = new AbstractAction() {
            {
                putValue(NAME, "delete-previous");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ShefHelper.runDeletePrevious(editor);
            }

        };
        actions.put("delete-previous", action);
        actionMap.put("delete-previous", action);

        delegate = actionMap.get("delete-next");
        action = new AbstractAction() {
            {
                putValue(NAME, "delete-previous");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ShefHelper.runDeleteNext(editor);
            }

        };
        actions.put("delete-next", action);
        actionMap.put("delete-next", action);

        delegate = actionMap.get("insert-tab");
        action = new AbstractAction() {
            {
                putValue(NAME, "delete-previous");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ShefHelper.runInsertTabBackward(editor);
            }

        };
        actions.put("insert-tab", action);
        actionMap.put("insert-tab", action);

        delegate = actionMap.get("paste-from-clipboard");
        Action hteAction = new AbstractAction() {
            {
                putValue(NAME, "delete-previous");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ShefHelper.runPaste(editor);
            }

        };
        actions.put("paste-from-clipboard", delegate);
        actionMap.put("paste-from-clipboard", hteAction);

        inputMap.put(tabBackwardKS, "tab-backward");//install tab backwards keystroke
        action = new AbstractAction() {
            {
                putValue(NAME, "delete-previous");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ShefHelper.runInsertTabBackward(editor);
            }

        };
        actions.put("tab-backward", action);
        actionMap.put("tab-backward", action);

        editorToActionsMap.put(editor, actions);
    }

    public void deinstall(JEditorPane ed) {
        if (!editorToActionsMap.containsKey(ed)) {
            return; //not installed installed
        }
        //restore actions to their original state
        ActionMap actionMap = ed.getActionMap();
        Map actions = (Map) editorToActionsMap.get(ed);

        actionMap.put("insert-break", ShefHelper.getInitialAction(ed, "insert-break"));

        actionMap.put("delete-previous", ShefHelper.getInitialAction(ed, "delete-previous"));

        actionMap.put("delete-next", ShefHelper.getInitialAction(ed, "delete-next"));

        actionMap.put("insert-tab", ShefHelper.getInitialAction(ed, "insert-tab"));

        actionMap.put("paste-from-clipboard", ShefHelper.getInitialAction(ed, "paste-from-clipboard"));

        Action curAct = actionMap.get("tab-backward");
        if (curAct == actions.get("insert-tab")) {
            actionMap.remove("tab-backward");
            //inputMap.remove(tabBackwardKS);//remove backwards keystroke
        }

        editorToActionsMap.remove(ed);
    }
}
