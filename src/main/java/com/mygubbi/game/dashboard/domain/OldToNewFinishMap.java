package com.mygubbi.game.dashboard.domain;

/**
 * Created by Shruthi on 9/14/2017.
 */
public class OldToNewFinishMap
{
    public static final String OLD_CODE="oldCode";
    public static final String NEW_CODE="newCode";
    public static final String FLAG="flag";

    private String oldCode;
    private String newCode;
    private String flag;

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "OldToNewFinishMap{" +
                "oldCode='" + oldCode + '\'' +
                ", newCode='" + newCode + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }
}
