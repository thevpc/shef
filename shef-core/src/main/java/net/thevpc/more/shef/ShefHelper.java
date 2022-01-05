/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.more.shef;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.*;

/**
 * @author thevpc
 */
public class ShefHelper {

    public static final String ALIGNMENTS[]
            = {
            "left", "center", "right", "justify"
    };
    public static UndoManagerResolver managerResolver;

    public static UndoManagerResolver getManagerResolver() {
        return managerResolver;
    }

    public static void setManagerResolver(UndoManagerResolver managerResolver) {
        ShefHelper.managerResolver = managerResolver;
    }

    public static JEditorPane installMin(JEditorPane editor) {
        editor.setContentType("text/html");
        init(editor);
        WysiwygHTMLEditorKit k = new WysiwygHTMLEditorKit();
        editor.setEditorKitForContentType("text/html", k);
        k.addInstallHelper(new MinWysiwygHTMLEditorKitInstallHelper());
        editor.setContentType("text/plain");
        editor.setContentType("text/html");
        return editor;
    }

    public static JEditorPane init(JEditorPane editor) {
        ActionMap actionMap = editor.getActionMap();
        Map<Object, Action> initial = new HashMap<>();
        for (Object k : actionMap.allKeys()) {
            Action a = actionMap.get(k);
            initial.put(k, a);
        }
        editor.putClientProperty("INITIAL_ACTION_MAP", initial);
        return editor;
    }

    public static Action getInitialActionOrNull(JEditorPane editor, Object key) {
        Map<Object, Action> actionMap = (Map<Object, Action>) editor.getClientProperty("INITIAL_ACTION_MAP");
        if (actionMap != null) {
            return actionMap.get(key);
        }
        return null;
    }

    public static Action getInitialAction(JEditorPane editor, Object key) {
        Map<Object, Action> actionMap = (Map<Object, Action>) editor.getClientProperty("INITIAL_ACTION_MAP");
        if (actionMap == null) {
            throw new IllegalArgumentException("must install init(editor)");
        }
        Action a = actionMap.get(key);
        if (a == null) {
            throw new IllegalArgumentException("initial action not found: " + key);
        }
        return a;
    }

    public static boolean isEnabledCopy(JEditorPane editor) {
        return editor != null && editor.getSelectionStart() != editor.getSelectionEnd();
    }

    public static boolean isEnabledCut(JEditorPane editor) {
        return editor != null && editor.getSelectionStart() != editor.getSelectionEnd();
    }

    public static boolean isEnabledAlign(JEditorPane editor, AlignEnum alignEnum) {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        Element elem = document.getParagraphElement(editor.getCaretPosition());
        if (HTMLUtils.isImplied(elem)) {
            elem = elem.getParentElement();
        }

        AttributeSet at = elem.getAttributes();
        return at.containsAttribute(HTML.Attribute.ALIGN, ALIGNMENTS[alignEnum.ordinal()]);
    }

    public static void runCopy(JEditorPane editor) {
        editor.copy();
    }

    public static void runCut(JEditorPane editor) {
        editor.cut();
    }

    public static void runInsertEnter(JEditorPane editor) {
        HTMLDocument document = null;
        try {
            if (!editor.isEditable() || !editor.isEnabled()) {
                return;
            }
            document = (HTMLDocument) editor.getDocument();
        } catch (ClassCastException ex) {
            // don't know what to do with this type
            // so pass off the event to the delegate
            Action insertBreak0 = getInitialAction(editor, "insert-break");
            insertBreak0.actionPerformed(null);
            return;
        }

        document = (HTMLDocument) editor.getDocument();
        Element elem = document.getParagraphElement(editor.getCaretPosition());
        Element parentElem = elem.getParentElement();
        HTML.Tag tag = HTML.getTag(elem.getName());
        HTML.Tag parentTag = HTML.getTag(parentElem.getName());
        int caret = editor.getCaretPosition();

        beginCompoundEdit(document);
        try {
            if (HTMLUtils.isImplied(elem)) {
                //are we inside a list item?
                if (parentTag.equals(HTML.Tag.LI)) {
                    //does the list item have any contents
                    if (parentElem.getEndOffset() - parentElem.getStartOffset() > 1) {
                        String txt = "";
                        //caret at start of listitem
                        if (caret == parentElem.getStartOffset()) {
                            document.insertBeforeStart(parentElem, toListItem(txt));
                        }//caret in the middle of list item content
                        else if (caret < parentElem.getEndOffset() - 1 && caret > parentElem.getStartOffset()) {
                            int len = parentElem.getEndOffset() - caret;
                            txt = document.getText(caret, len);
                            caret--;// hmmm
                            document.insertAfterEnd(parentElem, toListItem(txt));
                            document.remove(caret, len);
                        } else//caret at end of list item
                        {
                            document.insertAfterEnd(parentElem, toListItem(txt));
                        }

                        editor.setCaretPosition(caret + 1);
                    } else// empty list item
                    {
                        Element listParentElem = HTMLUtils.getListParent(parentElem).getParentElement();
                        //System.out.println(listParentElem.getName());

                        if (isListItem(HTML.getTag(listParentElem.getName())))//nested list
                        {
                            //System.out.println("nested list============");

                            //document.insertAfterEnd(parentElem, (toListItem("")));
                            //editor.setCaretPosition(elem.getEndOffset());
                            HTML.Tag listParentTag = HTML.getTag(HTMLUtils.getListParent(listParentElem).toString());
                            /*HTMLEditorKit.InsertHTMLTextAction a =
                                new HTMLEditorKit.InsertHTMLTextAction("insert",
                                "", listParentTag, HTML.Tag.LI);
                            a.actionPerformed(e);*/
                            int start = parentElem.getStartOffset();

                            Element nextElem = HTMLUtils.getNextElement(document, parentElem);

                            int len = nextElem.getEndOffset() - start;

                            String ml = HTMLUtils.getElementHTML(listParentElem, true);
                            //System.out.println(ml);
                            //System.out.println("------------------");

                            ml = ml.replaceFirst("\\<li\\>\\s*\\<\\/li\\>\\s*\\<\\/ul\\>", "</ul>");
                            ml = ml.replaceFirst("\\<ul\\>\\s*\\<\\/ul\\>", "");
                            //System.out.println(ml);

                            document.setOuterHTML(listParentElem, ml);
                            //document.remove(start, len);
                            //HTMLUtils.removeElement(elem);

                        }//are we directly under a table cell?
                        else if (listParentElem.getName().equals("td")) {
                            //reset the table cell contents nested in a <div>
                            //we do this because otherwise the next table cell would
                            //get deleted!! Perhaps this is a bug in swing's html implemenation?
                            encloseInDIV(listParentElem, document);
                            editor.setCaretPosition(caret + 1);
                        } else //end the list
                        {
                            if (isInList(listParentElem)) {
                                //System.out.println("======nested list============");
                                HTML.Tag listParentTag = HTML.getTag(HTMLUtils.getListParent(listParentElem).toString());
                                HTMLEditorKit.InsertHTMLTextAction a
                                        = new HTMLEditorKit.InsertHTMLTextAction("insert",
                                        "<li></li>", listParentTag, HTML.Tag.LI);
                                a.actionPerformed(null);
                            } else {
                                HTML.Tag root = HTML.Tag.BODY;
                                if (HTMLUtils.getParent(elem, HTML.Tag.TD) != null) {
                                    root = HTML.Tag.TD;
                                }

                                HTMLEditorKit.InsertHTMLTextAction a
                                        = new HTMLEditorKit.InsertHTMLTextAction("insert",
                                        "<p></p>", root, HTML.Tag.P);
                                a.actionPerformed(null);
                            }

                            HTMLUtils.removeElement(parentElem);
                        }
                    }
                } else //not a list
                {
                    //System.out.println("IMPLIED DEFAULT");
                    //System.out.println("elem: " + elem.getName());
                    //System.out.println("pelem: " + parentElem.getName());

                    if (parentTag.isPreformatted()) {
                        insertImpliedBR();
                    } else if (parentTag.equals(HTML.Tag.TD)) {
                        encloseInDIV(parentElem, document);
                        editor.setCaretPosition(caret + 1);
                    } else if (parentTag.equals(HTML.Tag.BODY) || isInList(elem)) {
                        //System.out.println("insertParagraphAfter elem");
                        insertParagraphAfter(elem, editor);
                    } else {
                        //System.out.println("***insertParagraphAfter parentElem");
                        insertParagraphAfter(parentElem, editor);
                    }
                }
            } else //not implied
            {
                //we need to check for this here in case any straggling li's
                //or dd's exist
                if (isListItem(tag)) {
                    if ((elem.getEndOffset() - editor.getCaretPosition()) == 1) {
                        //System.out.println("inserting \\n ");
                        //caret at end of para
                        editor.replaceSelection("\n ");
                        editor.setCaretPosition(editor.getCaretPosition() - 1);
                    } else {
                        //System.out.println("NOT implied delegate");
                        Action insertBreak0 = getInitialAction(editor, "insert-break");
                        insertBreak0.actionPerformed(null);
                    }
                } else {
                    //System.out.println("not implied insertparaafter1 " + elem.getName());
                    insertParagraphAfter(elem, editor);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
    }

    public static void runClearStyles(JEditorPane editor) {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();

        //Element el = document.getCharacterElement(editor.getCaretPosition());
        MutableAttributeSet attrs = new SimpleAttributeSet();
        attrs.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);

        //int cpos = editor.getCaretPosition();
        int selStart = editor.getSelectionStart();
        int selEnd = editor.getSelectionEnd();

        if (selEnd > selStart) {
            document.setCharacterAttributes(selStart, selEnd - selStart, attrs, true);
        }

        kit.getInputAttributes().removeAttributes(kit.getInputAttributes());
        kit.getInputAttributes().addAttributes(attrs);

        /*//boolean shouldClearSel = false;
        if(editor.getSelectedText() == null)
        {
            editor.replaceSelection("  ");
            editor.setSelectionStart(editor.getCaretPosition() - 1);
            editor.setSelectionEnd(editor.getSelectionStart() + 1); 
            document.setCharacterAttributes(editor.getSelectionStart(), 
                editor.getSelectionEnd() - editor.getSelectionStart(), attrs, true);
            editor.setSelectionStart(editor.getCaretPosition());
            editor.setSelectionEnd(editor.getCaretPosition());
        }
        else
        {
            document.setCharacterAttributes(editor.getSelectionStart(), 
                editor.getSelectionEnd() - editor.getSelectionStart(), attrs, true);
        }*/
    }

    private static boolean isListItem(HTML.Tag t) {
        return (t.equals(HTML.Tag.LI)
                || t.equals(HTML.Tag.DT) || t.equals(HTML.Tag.DD));
    }

    private static String toListItem(String txt) {
        return "<li>" + txt + "</li>";
    }

    private static boolean isInList(Element el) {
        return HTMLUtils.getListParent(el) != null;
    }

    private static void insertImpliedBR() {
        HTMLEditorKit.InsertHTMLTextAction hta
                = new HTMLEditorKit.InsertHTMLTextAction("insertBR",
                "<br>", HTML.Tag.IMPLIED, HTML.Tag.BR);
        hta.actionPerformed(null);
    }

    private static void encloseInDIV(Element elem, HTMLDocument document)
            throws Exception {
        //System.out.println("enclosing in div: " + elem.getName());
        HTML.Tag tag = HTML.getTag(elem.getName());
        String html = HTMLUtils.getElementHTML(elem, false);
        html = HTMLUtils.createTag(tag,
                elem.getAttributes(), "<div>" + html + "</div><div></div>");

        document.setOuterHTML(elem, html);
    }

    /**
     * Inserts a paragraph after the current paragraph of the same type
     *
     * @param elem
     * @param editor
     * @throws BadLocationException
     * @throws java.io.IOException
     */
    private static void insertParagraphAfter(Element elem, JEditorPane editor)
            throws BadLocationException, java.io.IOException {
        int cr = editor.getCaretPosition();
        HTMLDocument document = (HTMLDocument) elem.getDocument();
        HTML.Tag t = HTML.getTag(elem.getName());
        int endOffs = elem.getEndOffset();
        int startOffs = elem.getStartOffset();

        //if this is an implied para, make the new para a div
        if (t == null || elem.getName().equals("p-implied")) {
            t = HTML.Tag.DIV;
        }

        String html;
        //got to test for this here, otherwise <hr> and <br>
        //get duplicated
        if (cr == startOffs) {
            html = createBlock(t, elem, "");
        } else //split the current para at the cursor position
        {
            StringWriter out = new StringWriter();
            ElementWriter w = new ElementWriter(out, elem, startOffs, cr);
            w.write();
            html = createBlock(t, elem, out.toString());
        }

        if (cr == endOffs - 1) {
            html += createBlock(t, elem, "");
        } else {
            StringWriter out = new StringWriter();
            ElementWriter w = new ElementWriter(out, elem, cr, endOffs);
            w.write();
            html += createBlock(t, elem, out.toString());
        }

        //copy the current para's character attributes
        AttributeSet chAttribs;
        if (endOffs > startOffs && cr == endOffs - 1) {
            chAttribs = new SimpleAttributeSet(document.getCharacterElement(cr - 1).getAttributes());
        } else {
            chAttribs = new SimpleAttributeSet(document.getCharacterElement(cr).getAttributes());
        }

        document.setOuterHTML(elem, html);

        cr++;
        Element p = document.getParagraphElement(cr);
        if (cr == endOffs) {
            //update the character attributes for the added paragraph
            //FIXME If the added paragraph is at the start/end
            //of the document, the char attrs dont get set
            setCharAttribs(p, chAttribs);
        }

        editor.setCaretPosition(p.getStartOffset());
    }

    private static String createBlock(HTML.Tag t, Element elem, String html) {
        AttributeSet attribs = elem.getAttributes();
        return HTMLUtils.createTag(t, attribs,
                HTMLUtils.removeEnclosingTags(elem, html));
    }

    private static void setCharAttribs(Element p, AttributeSet chAttribs) {
        HTMLDocument document = (HTMLDocument) p.getDocument();
        int start = p.getStartOffset();
        int end = p.getEndOffset();

        SimpleAttributeSet sas = new SimpleAttributeSet(chAttribs);
        sas.removeAttribute(HTML.Attribute.SRC);
        //if the charattribs contains a br, hr, or img attribute, it'll erase
        //any content in the paragraph
        boolean skipAttribs = false;
        for (Enumeration ee = sas.getAttributeNames(); ee.hasMoreElements(); ) {
            Object n = ee.nextElement();
            String val = chAttribs.getAttribute(n).toString();
            ////System.out.println(n + " " + val);
            skipAttribs = val.equals("br") || val.equals("hr") || val.equals("img");
        }

        if (!skipAttribs) {
            document.setCharacterAttributes(start, end - start, sas, true);
        }
    }

    public static void runTextAlign(JEditorPane editor, AlignEnum alignEnum) {
        HTMLDocument doc = (HTMLDocument) editor.getDocument();
        Element curE = doc.getParagraphElement(editor.getSelectionStart());
        Element endE = doc.getParagraphElement(editor.getSelectionEnd());
        //System.err.println("ALIGN " + curE.getName());

        beginCompoundEdit(doc);
        while (true) {
            alignElement(curE, alignEnum);
            if (curE.getEndOffset() >= endE.getEndOffset()
                    || curE.getEndOffset() >= doc.getLength()) {
                break;
            }
            curE = doc.getParagraphElement(curE.getEndOffset() + 1);
        }
        endCompoundEdit(doc);
    }

    private static void alignElement(Element elem, AlignEnum alignEnum) {
        HTMLDocument doc = (HTMLDocument) elem.getDocument();

        if (HTMLUtils.isImplied(elem)) {
            HTML.Tag tag = HTML.getTag(elem.getParentElement().getName());
            //System.out.println(tag);
            //pre tag doesn't support an align attribute
            //http://www.w3.org/TR/REC-html32#pre
            if (tag != null && (!tag.equals(HTML.Tag.BODY))
                    && (!tag.isPreformatted() && !tag.equals(HTML.Tag.DD))) {
                SimpleAttributeSet as = new SimpleAttributeSet(elem.getAttributes());
                as.removeAttribute("align");
                as.addAttribute("align", ALIGNMENTS[alignEnum.ordinal()]);

                Element parent = elem.getParentElement();
                String html = HTMLUtils.getElementHTML(elem, false);
                html = HTMLUtils.createTag(tag, as, html);
                String snipet = "";
                for (int i = 0; i < parent.getElementCount(); i++) {
                    Element el = parent.getElement(i);
                    if (el == elem) {
                        snipet += html;
                    } else {
                        snipet += HTMLUtils.getElementHTML(el, true);
                    }
                }

                try {
                    doc.setOuterHTML(parent, snipet);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            //Set the HTML attribute on the paragraph...
            MutableAttributeSet set = new SimpleAttributeSet(elem.getAttributes());
            set.removeAttribute(HTML.Attribute.ALIGN);
            set.addAttribute(HTML.Attribute.ALIGN, ALIGNMENTS[alignEnum.ordinal()]);
            //Set the paragraph attributes...
            int start = elem.getStartOffset();
            int length = elem.getEndOffset() - elem.getStartOffset();
            doc.setParagraphAttributes(start, length - 1, set, true);
        }
    }

    public static void runInsertBloc(JEditorPane editor, BlocEnum type) {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        int caret = editor.getCaretPosition();
        beginCompoundEdit(document);
        try {
            if (type == BlocEnum.OL || type == BlocEnum.UL) {
                insertList(editor, type);
            } else {
                changeBlockType(editor, type);
            }
            editor.setCaretPosition(caret);
        } catch (Exception awwCrap) {
            awwCrap.printStackTrace();
        }

        endCompoundEdit(document);
    }

    private static void insertList(JEditorPane editor, BlocEnum type)
            throws BadLocationException {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        int caretPos = editor.getCaretPosition();
        Element elem = document.getParagraphElement(caretPos);
        HTML.Tag parentTag = HTML.getTag(elem.getParentElement().getName());

        //check if we need to change the list from one type to another
        Element listParent = elem.getParentElement().getParentElement();
        HTML.Tag listTag = HTML.getTag(listParent.getName());
        if (listTag.equals(HTML.Tag.UL) || listTag.equals(HTML.Tag.OL)) {
            HTML.Tag t = HTML.getTag(listParent.getName());
            if (type == BlocEnum.OL && t.equals(HTML.Tag.UL)) {
                changeListType(listParent, HTML.Tag.OL, document);
                return;
            } else if (type == BlocEnum.UL && listTag.equals(HTML.Tag.OL)) {
                changeListType(listParent, HTML.Tag.UL, document);
                return;
            }
        }

        if (!parentTag.equals(HTML.Tag.LI))//don't allow nested lists
        {
            //System.err.println("INSERT LIST");
            changeBlockType(editor, type);
        } else//is already a list, so turn off list
        {
            HTML.Tag root = getRootTag(elem);
            String txt = HTMLUtils.getElementHTML(elem, false);
            editor.setCaretPosition(elem.getEndOffset());
            insertHTML("<p>" + txt + "</p>", HTML.Tag.P, root); //$NON-NLS-1$ //$NON-NLS-2$
            HTMLUtils.removeElement(elem);
        }

    }

    private static void changeBlockType(JEditorPane editor, BlocEnum type)
            throws BadLocationException {
        HTMLDocument doc = (HTMLDocument) editor.getDocument();
        Element curE = doc.getParagraphElement(editor.getSelectionStart());
        Element endE = doc.getParagraphElement(editor.getSelectionEnd());

        Element curTD = HTMLUtils.getParent(curE, HTML.Tag.TD);
        HTML.Tag tag = getTag(type);
        HTML.Tag rootTag = getRootTag(curE);
        String html = ""; //$NON-NLS-1$

        if (isListType(type)) {
            html = "<" + getTag(type) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
            tag = HTML.Tag.LI;
        }

        //a list to hold the elements we want to change
        List elToRemove = new ArrayList();
        elToRemove.add(curE);

        while (true) {
            html += HTMLUtils.createTag(tag,
                    curE.getAttributes(), HTMLUtils.getElementHTML(curE, false));
            if (curE.getEndOffset() >= endE.getEndOffset()
                    || curE.getEndOffset() >= doc.getLength()) {
                break;
            }
            curE = doc.getParagraphElement(curE.getEndOffset() + 1);
            elToRemove.add(curE);

            //did we enter a (different) table cell?
            Element ckTD = HTMLUtils.getParent(curE, HTML.Tag.TD);
            if (ckTD != null && !ckTD.equals(curTD)) {
                break;//stop here so we don't mess up the table
            }
        }

        if (isListType(type)) {
            html += "</" + getTag(type) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        //set the caret to the start of the last selected block element
        editor.setCaretPosition(curE.getStartOffset());

        //insert our changed block
        //we insert first and then remove, because of a bug in jdk 6.0
        insertHTML(html, getTag(type), rootTag);

        //now, remove the elements that were changed.
        for (Iterator it = elToRemove.iterator(); it.hasNext(); ) {
            Element c = (Element) it.next();
            HTMLUtils.removeElement(c);
        }
    }

    private static boolean isListType(BlocEnum type) {
        return type == BlocEnum.OL || type == BlocEnum.UL;
    }

    /**
     * Gets the tag
     *
     * @param  type type
     * @return tag
     */
    public static HTML.Tag getTag(BlocEnum type) {
        HTML.Tag tag = HTML.Tag.DIV;

        switch (type) {
            case P:
                tag = HTML.Tag.P;
                break;
            case H1:
                tag = HTML.Tag.H1;
                break;
            case H2:
                tag = HTML.Tag.H2;
                break;
            case H3:
                tag = HTML.Tag.H3;
                break;
            case H4:
                tag = HTML.Tag.H4;
                break;
            case H5:
                tag = HTML.Tag.H5;
                break;
            case H6:
                tag = HTML.Tag.H6;
                break;
            case PRE:
                tag = HTML.Tag.PRE;
                break;
            case UL:
                tag = HTML.Tag.UL;
                break;
            case OL:
                tag = HTML.Tag.OL;
                break;
            case BLOCKQUOTE:
                tag = HTML.Tag.BLOCKQUOTE;
                break;
            case DIV:
                tag = HTML.Tag.DIV;
                break;
        }

        return tag;
    }

    private static HTML.Tag getRootTag(Element elem) {
        HTML.Tag root = HTML.Tag.BODY;
        if (HTMLUtils.getParent(elem, HTML.Tag.TD) != null) {
            root = HTML.Tag.TD;
        }
        return root;
    }

    /*private String cutOutElement(Element el) throws BadLocationException
    {
        String txt = HTMLUtils.getElementHTML(el, false);       
        HTMLUtils.removeElement(el);        
        return txt;
    }*/
    private static void insertHTML(String html, HTML.Tag tag, HTML.Tag root) {
        HTMLEditorKit.InsertHTMLTextAction a
                = new HTMLEditorKit.InsertHTMLTextAction("insertHTML", html, root, tag);             //$NON-NLS-1$
        a.actionPerformed(null);
    }

    private static void changeListType(Element listParent, HTML.Tag replaceTag, HTMLDocument document) {
        StringWriter out = new StringWriter();
        ElementWriter w = new ElementWriter(out, listParent);
        try {
            w.write();
            String html = out.toString();
            html = html.substring(html.indexOf('>') + 1, html.length());
            html = html.substring(0, html.lastIndexOf('<'));
            html = '<' + replaceTag.toString() + '>' + html + "</" + replaceTag.toString() + '>'; //$NON-NLS-1$
            document.setOuterHTML(listParent, html);
        } catch (Exception idiotic) {
        }
    }

    public static FontDesc runGetFont(JEditorPane editor) {
        HTMLDocument doc = (HTMLDocument) editor.getDocument();
        Element chElem = doc.getCharacterElement(editor.getCaretPosition());
        AttributeSet sas = chElem.getAttributes();
        Object o = sas.getAttribute(StyleConstants.FontFamily);
        String fontName = null;
        if (o != null) {
            fontName = (o.toString());
        }
        o = sas.getAttribute(StyleConstants.FontSize);
        int fontSize = 12;
        if (o != null) {
            try {
                fontSize = (Integer.parseInt(o.toString()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        boolean bold = (sas.containsAttribute(StyleConstants.Bold, Boolean.TRUE));
        boolean italic = (sas.containsAttribute(StyleConstants.Italic, Boolean.TRUE));
        boolean underlined = (sas.containsAttribute(StyleConstants.Underline, Boolean.TRUE));
        return new FontDesc(fontName, fontSize, bold, italic, underlined);
    }

    public static void runChangeFont(JEditorPane editor, FontDesc d) {
        HTMLDocument doc = (HTMLDocument) editor.getDocument();
        Element chElem = doc.getCharacterElement(editor.getCaretPosition());
        AttributeSet sas = chElem.getAttributes();

        MutableAttributeSet tagAttrs = new SimpleAttributeSet();
        tagAttrs.addAttribute(StyleConstants.FontFamily, d.getFontName());
        tagAttrs.addAttribute(StyleConstants.FontSize, new Integer(d.getFontSize()));
        tagAttrs.addAttribute(StyleConstants.Bold, new Boolean(d.isBold()));
        tagAttrs.addAttribute(StyleConstants.Italic, new Boolean(d.isItalic()));
        if (d.getUnderline() != null) {
            tagAttrs.addAttribute(StyleConstants.Underline, d.getUnderline());
        }

        beginCompoundEdit(editor.getDocument());
        HTMLUtils.setCharacterAttributes(editor, tagAttrs);
        endCompoundEdit(editor.getDocument());
    }

    public static void runSetColor(JEditorPane editor, Color color) {
        if (color != null) {
            Action a = new StyledEditorKit.ForegroundAction("Color", color);
            a.actionPerformed(null);
        }
    }

    public static void runSetFontSize(JEditorPane editor, FontSizeEnum size) {
        Action a = new StyledEditorKit.FontSizeAction(size.getPreferredName(), size.getPreferredSize());
        a.actionPerformed(null);
    }

    public static void runInsertHorizontalRule(JEditorPane editor) {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        int caret = editor.getCaretPosition();
        Element elem = document.getParagraphElement(caret);

        HTML.Tag tag = HTML.getTag(elem.getName());
        if (elem.getName().equals("p-implied")) {
            tag = HTML.Tag.IMPLIED;
        }

        HTMLEditorKit.InsertHTMLTextAction a
                = new HTMLEditorKit.InsertHTMLTextAction("", "<hr>", tag, HTML.Tag.HR);
        a.actionPerformed(null);
    }

    public static void runInsertImage(JEditorPane editor, String imageHtml) {
        String tagText = imageHtml;
        if (editor.getCaretPosition() == editor.getDocument().getLength()) {
            tagText += "&nbsp;"; //$NON-NLS-1$
        }
        editor.replaceSelection(""); //$NON-NLS-1$
        HTML.Tag tag = HTML.Tag.IMG;
        if (tagText.startsWith("<a")) //$NON-NLS-1$
        {
            tag = HTML.Tag.A;
        }

        HTMLUtils.insertHTML(tagText, tag, editor);
    }

    public static void runToggleInlineStyle(JEditorPane editor, InlineStyleEnum type) {
        beginCompoundEdit(editor.getDocument());

        MutableAttributeSet attr = new SimpleAttributeSet();
        attr.addAttributes(HTMLUtils.getCharacterAttributes(editor));
        boolean enable = !isDefined(attr, type);
        HTML.Tag tag = getTag(type);

        if (enable) {
            //System.err.println("adding style");
            attr = new SimpleAttributeSet();
            attr.addAttribute(tag, new SimpleAttributeSet());
            //doesn't replace any attribs, just adds the new one
            HTMLUtils.setCharacterAttributes(editor, attr);
        } else {
            //System.err.println("clearing style");
            //Kind of a ham-fisted way to do this, but sometimes there are
            //CSS attributes, someties there are HTML.Tag attributes, and sometimes
            //there are both. So, we have to remove 'em all to make sure this type
            //gets completely disabled

            //remove the CSS style
            //STRONG, EM, CITE, CODE have no CSS analogs  
            if (type == InlineStyleEnum.BOLD) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.FONT_WEIGHT, "bold");
            } else if (type == InlineStyleEnum.ITALIC) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.FONT_STYLE, "italic");
            } else if (type == InlineStyleEnum.UNDERLINE) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.TEXT_DECORATION, "underline");
            } else if (type == InlineStyleEnum.STRIKE) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.TEXT_DECORATION, "line-through");
            } else if (type == InlineStyleEnum.SUP) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.VERTICAL_ALIGN, "sup");
            } else if (type == InlineStyleEnum.SUB) {
                HTMLUtils.removeCharacterAttribute(editor, CSS.Attribute.VERTICAL_ALIGN, "sub");
            }

            HTMLUtils.removeCharacterAttribute(editor, tag); //make certain the tag is also removed
        }

        endCompoundEdit(editor.getDocument());
    }

    public static boolean isDefined(AttributeSet attr, InlineStyleEnum type) {
        boolean hasSC = false;
        if (type == InlineStyleEnum.SUP) {
            hasSC = StyleConstants.isSuperscript(attr);
        } else if (type == InlineStyleEnum.SUB) {
            hasSC = StyleConstants.isSubscript(attr);
        } else if (type == InlineStyleEnum.BOLD) {
            hasSC = StyleConstants.isBold(attr);
        } else if (type == InlineStyleEnum.ITALIC) {
            hasSC = StyleConstants.isItalic(attr);
        } else if (type == InlineStyleEnum.UNDERLINE) {
            hasSC = StyleConstants.isUnderline(attr);
        } else if (type == InlineStyleEnum.STRIKE) {
            hasSC = StyleConstants.isStrikeThrough(attr);
        }

        return hasSC || (attr.getAttribute(getTag(type)) != null);
    }

    public static HTML.Tag getTag(InlineStyleEnum type) {
        HTML.Tag tag = null;

        switch (type) {
            case EM:
                tag = HTML.Tag.EM;
                break;
            case STRONG:
                tag = HTML.Tag.STRONG;
                break;
            case CODE:
                tag = HTML.Tag.CODE;
                break;
            case SUP:
                tag = HTML.Tag.SUP;
                break;
            case SUB:
                tag = HTML.Tag.SUB;
                break;
            case CITE:
                tag = HTML.Tag.CITE;
                break;
            case BOLD:
                tag = HTML.Tag.B;
                break;
            case ITALIC:
                tag = HTML.Tag.I;
                break;
            case UNDERLINE:
                tag = HTML.Tag.U;
                break;
            case STRIKE:
                tag = HTML.Tag.STRIKE;
                break;
        }
        return tag;
    }

    public static void runInsertLineBreak(JEditorPane editor) {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        int pos = editor.getCaretPosition();
        String elName
                = document
                .getParagraphElement(pos)
                .getName();
        /*
         * if ((elName.toUpperCase().equals("PRE")) ||
         * (elName.toUpperCase().equals("P-IMPLIED"))) {
         * editor.replaceSelection("\r"); return;
         */
        HTML.Tag tag = HTML.getTag(elName);
        if (elName.toUpperCase().equals("P-IMPLIED")) {
            tag = HTML.Tag.IMPLIED;
        }

        HTMLEditorKit.InsertHTMLTextAction hta
                = new HTMLEditorKit.InsertHTMLTextAction(
                "insertBR",
                "<br>",
                tag,
                HTML.Tag.BR);
        hta.actionPerformed(null);
    }

    public static void runInsertLink(JEditorPane editor, String linkHtml) {
        String tagText = linkHtml;
        //if(editor.getCaretPosition() == document.getLength())
        if (editor.getSelectedText() == null) {
            tagText += "&nbsp;";
        }

        editor.replaceSelection("");
        HTMLUtils.insertHTML(tagText, HTML.Tag.A, editor);
    }

    public static void runInsertTable(JEditorPane editor, String tableHtml) {
        HTMLDocument document = (HTMLDocument) editor.getDocument();
        String html = tableHtml;

        Element elem = document.getParagraphElement(editor.getCaretPosition());
        beginCompoundEdit(document);
        try {
            if (HTMLUtils.isElementEmpty(elem)) {
                document.setOuterHTML(elem, html);
            } else if (elem.getName().equals("p-implied")) {
                document.insertAfterEnd(elem, html);
            } else {
                HTMLUtils.insertHTML(html, HTML.Tag.TABLE, editor);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
    }

    public static void runIndent(JEditorPane editor) {
        int cp = editor.getCaretPosition();
        beginCompoundEdit(editor.getDocument());
        List elems = getParagraphElements(editor);
        if (elems.size() > 0) {
            List listElems = getLeadingTralingListElems(elems);
            elems.removeAll(listElems);
            HTMLDocument doc = (HTMLDocument) editor.getDocument();
            blockquoteElements(elems, doc);
            adjustListElemsIndent(listElems, doc, true);
        }
        endCompoundEdit(editor.getDocument());
        editor.setCaretPosition(cp);
    }

    public static void runUnIndent(JEditorPane editor) {
        int cp = editor.getCaretPosition();
        beginCompoundEdit(editor.getDocument());
        List elems = getParagraphElements(editor);
        if (elems.size() > 0) {
            List listElems = getLeadingTralingListElems(elems);
            elems.removeAll(listElems);
            Set elsToIndent = new HashSet();
            Set elsToOutdent = new HashSet();
            Element lastBqParent = null;
            for (int i = 0; i < elems.size(); i++) {
                Element el = (Element) elems.get(i);
                Element bqParent = HTMLUtils.getParent(el, HTML.Tag.BLOCKQUOTE);
                if (bqParent == null) {
                    continue;
                }

                if (lastBqParent == null || bqParent.getStartOffset() >= lastBqParent.getEndOffset()) {
                    elsToOutdent.add(bqParent);
                    lastBqParent = bqParent;
                }

                if (i == 0 || i == elems.size() - 1) {
                    int c = bqParent.getElementCount();
                    for (int j = 0; j < c; j++) {
                        Element bqChild = bqParent.getElement(j);
                        int start = bqChild.getStartOffset();
                        int end = bqChild.getEndOffset();
                        if (end < editor.getSelectionStart() || start > editor.getSelectionEnd()) {
                            elsToIndent.add(bqChild);
                        }
                    }
                }
            }

            HTMLDocument doc = (HTMLDocument) editor.getDocument();
            adjustListElemsIndent(listElems, doc, false);
            blockquoteElements(new ArrayList(elsToIndent), doc);
            unblockquoteElements(new ArrayList(elsToOutdent), doc);
        }
        endCompoundEdit(editor.getDocument());
        editor.setCaretPosition(cp);
    }

    public static List getParagraphElements(JEditorPane editor) {
        List elems = new ArrayList();
        try {
            HTMLDocument doc = (HTMLDocument) editor.getDocument();
            Element curE = getParaElement(doc, editor.getSelectionStart());
            Element endE = getParaElement(doc, editor.getSelectionEnd());

            while (curE.getEndOffset() <= endE.getEndOffset()) {
                elems.add(curE);
                curE = getParaElement(doc, curE.getEndOffset() + 1);
                if (curE.getEndOffset() >= doc.getLength()) {
                    break;
                }
            }
        } catch (ClassCastException cce) {
        }

        return elems;
    }

    private static Element getParaElement(HTMLDocument doc, int pos) {
        Element curE = doc.getParagraphElement(pos);
        /*while(HTMLUtils.isImplied(curE))
        {
            curE = curE.getParentElement();
        }*/

 /*Element lp = HTMLUtils.getListParent(curE);
        if(lp != null)
            curE = lp;*/
        return curE;
    }

    private static List getLeadingTralingListElems(List elems) {
        Set listElems = new HashSet();
        for (int i = 0; i < elems.size(); i++) {
            Element el = (Element) elems.get(i);
            if (HTMLUtils.getListParent(el) != null) {
                listElems.add(el);
            } else {
                break;
            }
        }

        for (int i = elems.size() - 1; i >= 0; i--) {
            Element el = (Element) elems.get(i);
            if (HTMLUtils.getListParent(el) != null) {
                listElems.add(el);
            } else {
                break;
            }
        }

        return new ArrayList(listElems);
    }

    private static void adjustListElemsIndent(List elems, HTMLDocument doc, boolean isIndent) {
        Set rootLists = new HashSet();
        Set liElems = new HashSet();
        for (int i = 0; i < elems.size(); i++) {
            Element liEl = HTMLUtils.getParent((Element) elems.get(i), HTML.Tag.LI);
            if (liEl == null) {
                continue;
            }
            liElems.add(liEl);
            Element rootList = HTMLUtils.getListParent(liEl);
            if (rootList != null) {
                while (HTMLUtils.getListParent(rootList.getParentElement()) != null) {
                    rootList = HTMLUtils.getListParent(rootList.getParentElement());
                }
                rootLists.add(rootList);
            }
        }

        for (Iterator it = rootLists.iterator(); it.hasNext(); ) {
            Element rl = (Element) it.next();
            String newHtml = buildListHTML(rl, new ArrayList(liElems), isIndent);
            System.err.println(newHtml);
            try {
                doc.setInnerHTML(rl, newHtml);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String buildListHTML(Element list, List liItems, boolean isIndent) {
        List items = getItems(list, liItems, 0, isIndent);
        ListItem lastItem = null;
        StringBuffer html = new StringBuffer();
        for (int i = 0; i < items.size(); i++) {
            ListItem item = (ListItem) items.get(i);
            if (lastItem != null && (lastItem.level != item.level || !lastItem.listTag.equals(item.listTag))) {
                if (lastItem.level > item.level) {
                    html.append(openOrCloseList(lastItem.listTag, -1 * (lastItem.level - item.level)));
                    html.append(item.html);
                } else if (item.level > lastItem.level) {
                    html.append(openOrCloseList(item.listTag, (item.level - lastItem.level)));
                    html.append(item.html);
                } else {
                    //html.append("</" + lastItem.listTag + ">");
                    //html.append("<" + item.listTag + ">");
                    html.append(item.html);
                }
            } else {
                if (lastItem == null) {
                    html.append(openOrCloseList(item.listTag, item.level));
                }
                html.append(item.html);
            }

            lastItem = item;
        }

        if (lastItem != null) {
            html.append(openOrCloseList(lastItem.listTag, -1 * lastItem.level));
        }

        return html.toString();
    }

    private static String openOrCloseList(HTML.Tag ltag, int level) {
        String tag;
        if (level < 0) {
            tag = "</" + ltag + ">\n";
        } else {
            tag = "<" + ltag + ">\n";
        }
        int c = Math.abs(level);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < c; i++) {
            sb.append(tag);
        }
        return sb.toString();
    }

    private static List getItems(Element list, List selLiElems, int level, boolean isIndent) {
        int c = list.getElementCount();
        List items = new ArrayList();
        for (int i = 0; i < c; i++) {
            Element e = list.getElement(i);
            if (e.getName().equals("li")) {
                ListItem item = new ListItem();
                item.listTag = HTML.getTag(list.getName());
                item.level = level;
                if (selLiElems.contains(e)) {
                    if (isIndent) {
                        item.level++;
                    } else {
                        if (item.level > 0) {
                            item.level--;
                        }
                    }
                }
                item.html = HTMLUtils.getElementHTML(e, true);
                items.add(item);
            } else if (HTMLUtils.getListParent(e) == e) {
                items.addAll(getItems(e, selLiElems, level + 1, isIndent));
            }
        }
        return items;
    }

    private static void blockquoteElements(List elems, HTMLDocument doc) {
        for (Iterator it = elems.iterator(); it.hasNext(); ) {
            Element curE = (Element) it.next();
            String eleHtml = HTMLUtils.getElementHTML(curE, true);
            StringBuffer sb = new StringBuffer();
            sb.append("<blockquote>\n");
            sb.append(eleHtml);
            sb.append("</blockquote>\n");
            try {
                doc.setOuterHTML(curE, sb.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void unblockquoteElements(List elems, HTMLDocument doc) {
        for (Iterator it = elems.iterator(); it.hasNext(); ) {
            Element curE = (Element) it.next();
            if (!curE.getName().equals("blockquote")) {
                continue;
            }

            String eleHtml = HTMLUtils.getElementHTML(curE, false);
            HTML.Tag t = HTMLUtils.getStartTag(eleHtml);
            if (t == null || !t.breaksFlow()) {
                eleHtml = "<p>\n" + eleHtml + "</p>\n";
            }

            try {
                doc.setOuterHTML(curE, eleHtml);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static int getIndentationLevel(Element el) {
        int level = 0;
        while ((!el.getName().equals("body")) && (!el.getName().equals("td"))) {
            if (el.getName().equals("blockquote")) {
                level++;
            }
            el = el.getParentElement();
        }

        return level;
    }

    public static void runInsertHTML(String html, HTML.Tag tag, HTML.Tag root) {
        HTMLEditorKit.InsertHTMLTextAction a
                = new HTMLEditorKit.InsertHTMLTextAction("insertHTML", html, root, tag);//$NON-NLS-1$
        a.actionPerformed(null);
    }

    public static void runInsertHTML(JEditorPane editor, String htmlFragment) {
        if (htmlFragment != null) {
            beginCompoundEdit(editor.getDocument());
            //HTMLUtils.insertHTML("<div>" + htmlFragment + "</div>", HTML.Tag.DIV, editor);
            HTMLUtils.insertArbitraryHTML(htmlFragment, editor);
            endCompoundEdit(editor.getDocument());
        }
    }

    public static void runDeleteNext(JEditorPane editor) {
        HTMLDocument document;

        try {
            if (!editor.isEditable() || !editor.isEnabled()) {
                return;
            }
            document = (HTMLDocument) editor.getDocument();
        } catch (ClassCastException ex) {
            Action old = getInitialAction(editor, "delete-next");
            old.actionPerformed(null);
            return;
        }

        Element elem = document.getParagraphElement(editor.getCaretPosition());
        int caretPos = editor.getCaretPosition();
        int start = elem.getStartOffset();
        int end = elem.getEndOffset();
        boolean noSelection = editor.getSelectedText() == null;

        if ((end - 1) == caretPos && caretPos != document.getLength() && noSelection) {
            Element nextElem = document.getParagraphElement(caretPos + 1);

            //Do not delete table cells
            Element tdElem = HTMLUtils.getParent(elem, HTML.Tag.TD);
            if (tdElem != null && caretPos >= (tdElem.getEndOffset() - 1)) {
                return;
            }

            Element nextTDElem = HTMLUtils.getParent(nextElem, HTML.Tag.TD);
            if (tdElem == null && nextTDElem != null) {
                return;
            }

            String curPara = HTMLUtils.getElementHTML(elem, false);
            String html = HTMLUtils.getElementHTML(nextElem, false);
            html = curPara + html;

            beginCompoundEdit(document);
            try {
                document.setInnerHTML(elem, html);
                HTMLUtils.removeElement(nextElem);

                editor.setCaretPosition(caretPos);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            endCompoundEdit(document);

            return;
        }
        getInitialAction(editor, "delete-next").actionPerformed(null);
    }

    public static void runDeletePrevious(JEditorPane editor) {
        HTMLDocument document;

        try {
            if (!editor.isEditable() || !editor.isEnabled()) {
                return;
            }
            document = (HTMLDocument) editor.getDocument();
        } catch (ClassCastException ex) {
            Action old = getInitialAction(editor, "delete-previous");
            old.actionPerformed(null);
            return;
        }

        Element elem = document.getParagraphElement(editor.getCaretPosition());
        int caretPos = editor.getCaretPosition();
        int start = elem.getStartOffset();
        int end = elem.getEndOffset();
        boolean noSelection = editor.getSelectedText() == null;

        if (start == caretPos && caretPos > 1 && noSelection) {
            Element prevElem = document.getParagraphElement(start - 1);

            //do not delete table cells
            Element tdElem = HTMLUtils.getParent(elem, HTML.Tag.TD);
            if (tdElem != null && caretPos < tdElem.getStartOffset() + 1) {
                return;
            }

            Element prevTDElem = HTMLUtils.getParent(prevElem, HTML.Tag.TD);
            if (tdElem == null && prevTDElem != null) {
                return;
            }

            int newPos = prevElem.getEndOffset();
            String html = HTMLUtils.getElementHTML(prevElem, false);
            String curPara = HTMLUtils.getElementHTML(elem, false);
            html = html + curPara;

            beginCompoundEdit(document);
            try {
                document.setInnerHTML(prevElem, html);
                HTMLUtils.removeElement(elem);

                editor.setCaretPosition(newPos - 1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            endCompoundEdit(document);
            return;
        }
        getInitialAction(editor, "delete-previous").actionPerformed(null);
    }

    public static void runPaste(JEditorPane editor) {
        editor.paste();
//        HTMLEditorKit ekit = (HTMLEditorKit) editor.getEditorKit();
//        HTMLDocument document = (HTMLDocument) editor.getDocument();
//        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
//        try {
//            beginCompoundEdit(document);
//            Transferable content = clip.getContents(editor);
//            String txt = content.getTransferData(
//                    new DataFlavor(String.class, "String")).toString();
//            document.replace(editor.getSelectionStart(),
//                    editor.getSelectionEnd() - editor.getSelectionStart(),
//                    txt, ekit.getInputAttributes());
//
//        } catch (Exception ex) {
//            //ex.printStackTrace();
//        } finally {
//            endCompoundEdit(document);
//        }
    }

    public static void runInsertTabForward(JEditorPane editor) {
        HTMLDocument document;
        document = (HTMLDocument) editor.getDocument();
        Element elem = document.getParagraphElement(editor.getCaretPosition());
        Element tdElem = HTMLUtils.getParent(elem, HTML.Tag.TD);
        if (tdElem != null) {
            try {
                editor.setCaretPosition(tdElem.getEndOffset());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        } else {
            Action a = getInitialActionOrNull(editor, "insert-tab");
            if (a != null) {
                a.actionPerformed(null);
            }
        }
    }

    public static void runInsertTabBackward(JEditorPane editor) {
        HTMLDocument document;
        document = (HTMLDocument) editor.getDocument();
        Element elem = document.getParagraphElement(editor.getCaretPosition());
        Element tdElem = HTMLUtils.getParent(elem, HTML.Tag.TD);
        if (tdElem != null) {
            try {
                int position = tdElem.getStartOffset() - 1;
                if (position >= 0) {
                    editor.setCaretPosition(position);
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        } else {
            Action a = getInitialActionOrNull(editor, "tab-backward");
            if (a != null) {
                a.actionPerformed(null);
            }
        }
    }

    public static void runInsertTableCell(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();

        Element curElem = document.getParagraphElement(ed.getCaretPosition());
        Element td = HTMLUtils.getParent(curElem, HTML.Tag.TD);
        Element tr = HTMLUtils.getParent(curElem, HTML.Tag.TR);
        //HTMLDocument document = getDocument();
        if (td == null || tr == null || document == null) {
            return;
        }

        beginCompoundEdit(document);
        try {
            document.insertAfterEnd(td, "<td></td>");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
    }

    public static void runInsertTableRow(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();

        Element curElem = document.getParagraphElement(ed.getCaretPosition());
        Element td = HTMLUtils.getParent(curElem, HTML.Tag.TD);
        Element tr = HTMLUtils.getParent(curElem, HTML.Tag.TR);
        //HTMLDocument document = getDocument();
        if (td == null || tr == null || document == null) {
            return;
        }

        beginCompoundEdit(document);
        try {
            insertRowAfter(tr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
    }

    public static void runInsertTableCol(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();

        Element curElem = document.getParagraphElement(ed.getCaretPosition());
        Element td = HTMLUtils.getParent(curElem, HTML.Tag.TD);
        Element tr = HTMLUtils.getParent(curElem, HTML.Tag.TR);
        //HTMLDocument document = getDocument();
        if (td == null || tr == null || document == null) {
            return;
        }

        beginCompoundEdit(document);
        try {
            insertColumnAfter(td);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
    }

    public static void runDeleteTableCell(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();

        Element curElem = document.getParagraphElement(ed.getCaretPosition());
        Element td = HTMLUtils.getParent(curElem, HTML.Tag.TD);
        Element tr = HTMLUtils.getParent(curElem, HTML.Tag.TR);
        //HTMLDocument document = getDocument();
        if (td == null || tr == null || document == null) {
            return;
        }

        beginCompoundEdit(document);
        try {
            removeCell(td);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
    }

    public static void runDeleteTableRow(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();

        Element curElem = document.getParagraphElement(ed.getCaretPosition());
        Element td = HTMLUtils.getParent(curElem, HTML.Tag.TD);
        Element tr = HTMLUtils.getParent(curElem, HTML.Tag.TR);
        //HTMLDocument document = getDocument();
        if (td == null || tr == null || document == null) {
            return;
        }

        beginCompoundEdit(document);
        try {
            removeRow(tr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
    }

    /**
     * by: vpc
     *
     * @param ed editor
     */
    public static void runDeleteTable(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();

        Element curElem = document.getParagraphElement(ed.getCaretPosition());
        Element tab = HTMLUtils.getParent(curElem, HTML.Tag.TABLE);
        if (tab == null) {
            return;
        }

        beginCompoundEdit(document);
        String tbefore = ed.getText();
        try {
            removeTable(tab);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
        String tafter = ed.getText();
        System.out.println(tafter);
    }

    public static void runDeleteTableCol(JEditorPane ed) {
        HTMLDocument document = (HTMLDocument) ed.getDocument();

        Element curElem = document.getParagraphElement(ed.getCaretPosition());
        Element td = HTMLUtils.getParent(curElem, HTML.Tag.TD);
        Element tr = HTMLUtils.getParent(curElem, HTML.Tag.TR);
        //HTMLDocument document = getDocument();
        if (td == null || tr == null || document == null) {
            return;
        }

        beginCompoundEdit(document);
        try {
            removeColumn(td);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        endCompoundEdit(document);
    }

    private static void removeCell(Element td) throws Exception {
        Element tr = HTMLUtils.getParent(td, HTML.Tag.TR);
        if (tr != null && td.getName().equals("td")) {
            if (td.getEndOffset() != tr.getEndOffset()) {
                remove(td);
            } else if (getRowCellCount(tr) <= 1) {
                remove(tr);
            } else {
                StringWriter out = new StringWriter();
                ElementWriter w = new ElementWriter(out, tr, tr.getStartOffset(), td.getStartOffset());
                w.write();

                HTMLDocument doc = (HTMLDocument) tr.getDocument();
                doc.setOuterHTML(tr, out.toString());
            }
        }
    }

    private static void insertRowAfter(Element tr) throws Exception {
        Element table = HTMLUtils.getParent(tr, HTML.Tag.TABLE);

        if (table != null && tr.getName().equals("tr")) {
            HTMLDocument doc = (HTMLDocument) tr.getDocument();
            if (tr.getEndOffset() != table.getEndOffset()) {
                doc.insertAfterEnd(tr, getRowHTML(tr));
            } else {
                AttributeSet atr = table.getAttributes();
                String tbl = HTMLUtils.getElementHTML(table, false);
                tbl += getRowHTML(tr);

                tbl = HTMLUtils.createTag(HTML.Tag.TABLE, atr, tbl);
                doc.setOuterHTML(table, tbl);
            }
        }
    }

    private static void removeTable(Element table) throws Exception {
        if (table != null && table.getName().equals("table")) {
            remove(table);
//            Element pe = table.getParentElement();
//            if(pe!=null){
//                if(pe instanceof AbstractDocument.BranchElement){
//                    AbstractDocument.BranchElement be=(AbstractDocument.BranchElement)pe;
//                    List<Element> ch=new ArrayList<>();
//                    for (int i = 0; i < be.getElementCount(); i++) {
//                        Element ee = be.getElement(i);
//                        if(ee!=table){
//                            ch.add(ee);
//                        }
//                    }
//                    be.replace(
//                            pe.getStartOffset(),
//                            pe.getEndOffset()-pe.getStartOffset(),
//                            ch.toArray(new Element[0])
//                    );
//                }
//            }
        }
    }

    private static void removeRow(Element tr) throws Exception {
        Element table = HTMLUtils.getParent(tr, HTML.Tag.TABLE);
        if (table != null && tr.getName().equals("tr")) {
            if (tr.getEndOffset() != table.getEndOffset()) {
                remove(tr);
            } else if (getTableRowCount(table) <= 1) {
                remove(table);
            } else {
                StringWriter out = new StringWriter();
                ElementWriter w = new ElementWriter(out, table, table.getStartOffset(), tr.getStartOffset());
                w.write();

                HTMLDocument doc = (HTMLDocument) tr.getDocument();
                doc.setOuterHTML(table, out.toString());
            }
        }
    }

    private static int getTableRowCount(Element table) {
        int count = 0;
        for (int i = 0; i < table.getElementCount(); i++) {
            Element e = table.getElement(i);
            if (e.getName().equals("tr")) {
                count++;
            }
        }

        return count;
    }

    private static int getRowCellCount(Element tr) {
        int count = 0;
        for (int i = 0; i < tr.getElementCount(); i++) {
            Element e = tr.getElement(i);
            if (e.getName().equals("td")) {
                count++;
            }
        }

        return count;
    }

    private static void remove(Element el) throws BadLocationException {
        int start = el.getStartOffset();
        int len = el.getEndOffset() - start;
        HTMLDocument doc = (HTMLDocument) el.getDocument();
//        if(start<=0 && len>=doc.getLength()){
        //BUG Workaround!
//            doc.remove(0, doc.getLength());
//            doc.insertString(0, "empty", null);
        if (el instanceof AbstractDocument.BranchElement) {
            AbstractDocument.BranchElement b = (AbstractDocument.BranchElement) el;
            Object n = b.getAttribute(StyleConstants.NameAttribute);
            TreeNode p = b.getParent();
            int qq = p.getIndex(b);
            if (HTML.Tag.TABLE.equals(n)) {
//                    try {
//                        doc.insertBeforeStart(b,"<p></p>");
//                    } catch (IOException e) {
//                        throw new IllegalArgumentException(e);
//                    }
                int offset = b.getStartOffset();
                if (qq >= 0 && p instanceof AbstractDocument.BranchElement) {
                    AbstractDocument.BranchElement bb = (AbstractDocument.BranchElement) p;
                    if(bb.getElementCount()==1) {
//                        try {
//                            doc.insertBeforeEnd(bb, "<p></p>");
//                        } catch (IOException e) {
//                            throw new IllegalArgumentException(e);
//                        }
//                        bb.replace(qq, 1, new Element[0]);
                        return;
                    }
                }
//                    int q = b.getChildCount();
//                    for (int i = 0; i < q; i++) {
//                        doc.removeElement((Element) b.getChildAt(0));
//                    }
            }
        }
//        }
        doc.removeElement(el);

//        if (el.getEndOffset() > doc.getLength()) {
//            len = doc.getLength() - start;
//        }
//        doc.remove(start, len);
//        if(start==0 && len==0){
//            for (Element rootElement : doc.getRootElements()) {
//                while(true){
//                    int c0 = rootElement.getElementCount();
//                    if(c0 >0) {
//                        doc.removeElement(rootElement.getElement(0));
//                        int c1 = rootElement.getElementCount();
//                        if(c1>=c0){
//                            break;
//                        }
//                    }else{
//                        break;
//                    }
//                }
//            }
//        }
    }

    private static int getCellIndex(Element tr, Element td) {
        int tdIndex = -1;
        for (int i = 0; i < tr.getElementCount(); i++) {
            Element e = tr.getElement(i);
            if (e.getStartOffset() == td.getStartOffset()) {
                tdIndex = i;
                break;
            }
        }

        return tdIndex;
    }

    private static void removeColumn(Element td) throws Exception {
        Element tr = HTMLUtils.getParent(td, HTML.Tag.TR);

        int tdIndex = getCellIndex(tr, td);
        if (tdIndex == -1) {
            return;
        }

        Element table = HTMLUtils.getParent(tr, HTML.Tag.TABLE);
        for (int i = 0; i < table.getElementCount(); i++) {
            Element row = table.getElement(i);
            if (row.getName().equals("tr")) {
                Element e = row.getElement(tdIndex);
                if (e != null && e.getName().equals("td")) {
                    removeCell(e);
                }
            }
        }
    }

    private static void insertColumnAfter(Element td) throws Exception {
        Element tr = HTMLUtils.getParent(td, HTML.Tag.TR);
        HTMLDocument doc = (HTMLDocument) tr.getDocument();

        int tdIndex = getCellIndex(tr, td);
        if (tdIndex == -1) {
            return;
        }

        Element table = HTMLUtils.getParent(tr, HTML.Tag.TABLE);
        for (int i = 0; i < table.getElementCount(); i++) {
            Element row = table.getElement(i);
            if (row.getName().equals("tr")) {
                AttributeSet attr = row.getAttributes();
                int cellCount = row.getElementCount();

                String rowHTML = "";
                String cell = "<td></td>";
                for (int j = 0; j < cellCount; j++) {
                    Element e = row.getElement(j);
                    rowHTML += HTMLUtils.getElementHTML(e, true);
                    if (j == tdIndex) {
                        rowHTML += cell;
                    }
                }

                int tds = row.getElementCount() - 1;
                if (tds < tdIndex) {
                    for (; tds <= tdIndex; tds++) {
                        rowHTML += cell;
                    }
                }

                rowHTML = HTMLUtils.createTag(HTML.Tag.TR, attr, rowHTML);
                doc.setOuterHTML(row, rowHTML);
            }
        }
    }

    private static String getRowHTML(Element tr) {
        String trTag = "<tr>";
        if (tr.getName().equals("tr")) {
            for (int i = 0; i < tr.getElementCount(); i++) {
                if (tr.getElement(i).getName().equals("td")) {
                    trTag += "<td></td>";
                }
            }
        }
        trTag += "</tr>";
        return trTag;
    }

    public static boolean isInTD(JEditorPane tc) {
        Element td = null;
        if (tc != null) {
            HTMLDocument doc = (HTMLDocument) tc.getDocument();
            try {
                Element curElem = doc.getParagraphElement(tc.getCaretPosition());
                td = HTMLUtils.getParent(curElem, HTML.Tag.TD);
            } catch (Exception ex) {
            }
        }

        return td != null;
    }

    public static void beginCompoundEdit(Document doc) {
        if (managerResolver != null) {
            managerResolver.beginCompoundEdit(doc);
        }
    }

    public static void endCompoundEdit(Document doc) {
        if (managerResolver != null) {
            managerResolver.endCompoundEdit(doc);
        }
    }

    private static class ListItem {

        String html;
        int level;
        HTML.Tag listTag;
    }
}
