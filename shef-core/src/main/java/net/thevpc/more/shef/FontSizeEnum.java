/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.more.shef;

/**
 *
 * @author thevpc
 */
public enum FontSizeEnum {

    XXSMALL("xx-small", 8),
    XSMALL("x-small", 10),
    SMALL("small", 12),
    MEDIUM("medium",14),
    LARGE("large", 18),
    XLARGE("x-large", 24),
    XXLARGE("xx-large", 36);
    public String preferredName;
    public int preferredSize;

    private FontSizeEnum(String preferredName, int preferredSize) {
        this.preferredName = preferredName;
        this.preferredSize = preferredSize;
    }

    public int getPreferredSize() {
        return preferredSize;
    }

    public String getPreferredName() {
        return preferredName;
    }

}
