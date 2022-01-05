/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.more.shef;

import javax.swing.text.Document;

/**
 *
 * @author thevpc
 */
public interface UndoManagerResolver {

    public void beginCompoundEdit(Document doc);

    public void endCompoundEdit(Document doc);
}
