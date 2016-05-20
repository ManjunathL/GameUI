package com.mygubbi.game.dashboard.domain;

import java.util.List;

/**
 * Created by nitinpuri on 19-05-2016.
 */
public class FinishTypeColor {

    private String finishTypeCode;
    private List<Color> colors;

    public String getFinishTypeCode() {
        return finishTypeCode;
    }

    public void setFinishTypeCode(String finishTypeCode) {
        this.finishTypeCode = finishTypeCode;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }
}
