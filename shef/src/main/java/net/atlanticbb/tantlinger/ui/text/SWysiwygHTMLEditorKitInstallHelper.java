/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atlanticbb.tantlinger.ui.text;

import net.thevpc.more.shef.WysiwygHTMLEditorKitInstallHelper;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import net.atlanticbb.tantlinger.ui.text.actions.DecoratedTextAction;
import net.atlanticbb.tantlinger.ui.text.actions.EnterKeyAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction;
import net.atlanticbb.tantlinger.ui.text.actions.RemoveAction;
import net.atlanticbb.tantlinger.ui.text.actions.TabAction;

/**
 *
 * @author vpc
 */
public class SWysiwygHTMLEditorKitInstallHelper implements WysiwygHTMLEditorKitInstallHelper{
    private Map editorToActionsMap = new HashMap();
    private KeyStroke tabBackwardKS = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK);

    @Override
    public void install(JEditorPane editor) {
                if(editorToActionsMap.containsKey(editor))
            return; //already installed
        //install wysiwyg actions into the ActionMap for the editor being installed
        Map actions = new HashMap();
        InputMap inputMap = editor.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = editor.getActionMap();

        Action delegate = actionMap.get("insert-break");
        Action action = new EnterKeyAction(delegate);
        actions.put("insert-break", action);
        actionMap.put("insert-break", action);

        delegate = actionMap.get("delete-previous");
        action = new RemoveAction(RemoveAction.BACKSPACE, delegate);
        actions.put("delete-previous", action);
        actionMap.put("delete-previous", action);

        delegate = actionMap.get("delete-next");
        action = new RemoveAction(RemoveAction.DELETE, delegate);
        actions.put("delete-next", action);
        actionMap.put("delete-next", action);

        delegate = actionMap.get("insert-tab");
        action = new TabAction(TabAction.FORWARD, delegate);
        actions.put("insert-tab", action);
        actionMap.put("insert-tab", action);

        delegate = actionMap.get("paste-from-clipboard");
        HTMLTextEditAction hteAction = new net.atlanticbb.tantlinger.ui.text.actions.PasteAction();
        hteAction.putContextValue(HTMLTextEditAction.EDITOR, editor);
        actions.put("paste-from-clipboard", delegate);
        actionMap.put("paste-from-clipboard", hteAction);

        inputMap.put(tabBackwardKS, "tab-backward");//install tab backwards keystroke
        action = new TabAction(TabAction.BACKWARD, delegate);
        actions.put("tab-backward", action);
        actionMap.put("tab-backward", action);

        editorToActionsMap.put(editor, actions);
    }
    
    public void deinstall(JEditorPane ed)
    {
        if(!editorToActionsMap.containsKey(ed))
            return; //not installed installed

        //restore actions to their original state
        ActionMap actionMap = ed.getActionMap();
        Map actions = (Map)editorToActionsMap.get(ed);

        Action curAct = actionMap.get("insert-break");
        if(curAct == actions.get("insert-break"))
        {
            actionMap.put("insert-break", ((DecoratedTextAction)curAct).getDelegate());
        }

        curAct = actionMap.get("delete-previous");
        if(curAct == actions.get("delete-previous"))
        {
            actionMap.put("delete-previous", ((DecoratedTextAction)curAct).getDelegate());
        }

        curAct = actionMap.get("delete-next");
        if(curAct == actions.get("delete-next"))
        {
            actionMap.put("delete-next", ((DecoratedTextAction)curAct).getDelegate());
        }

        curAct = actionMap.get("insert-tab");
        if(curAct == actions.get("insert-tab"))
        {
            actionMap.put("insert-tab", ((DecoratedTextAction)curAct).getDelegate());
        }

        curAct = actionMap.get("paste-from-clipboard");
        if(curAct instanceof net.atlanticbb.tantlinger.ui.text.actions.PasteAction)
        {
            actionMap.put("paste-from-clipboard", (Action)actions.get("paste-from-clipboard"));
        }

        curAct = actionMap.get("tab-backward");
        if(curAct == actions.get("insert-tab"))
        {
            actionMap.remove("tab-backward");
            //inputMap.remove(tabBackwardKS);//remove backwards keystroke
        }

        editorToActionsMap.remove(ed);
    }
}
