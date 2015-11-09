package com.mbzshajib.mining.processor.uncertain.model;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Yasser Arafat
 * @email - mdyasserarafat1@gmail.com
 * @gitHub - https://github.com/ArafatYasser
 * @date: 9/20/2015
 * @time: 11:02 PM
 * ****************************************************************
 */
public class WindowItem {
    String pattern;
    int lmv;
    int windowTotalWeight;
    double sft;

    public WindowItem() {
        this.pattern = "";
        lmv = 0;
        sft = 0;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public double getSft() {
        sft =  (double)lmv/windowTotalWeight;
        return sft ;
    }



    public int getLmv() {
        return lmv;
    }

    public void setLmv(int lmv) {
        this.lmv = lmv;
    }

    public void addWeight(int weight) {
        lmv = lmv + weight;

    }

    public int getWindowTotalWeight() {
        return windowTotalWeight;
    }

    public void setWindowTotalWeight(int windowTotalWeight) {
        this.windowTotalWeight = windowTotalWeight;
}

@Override
public String toString() {
        return "WindowItem { " +
        "pattern = '" + getPattern() + '\'' +
        ", lmv = " + getLmv() +
        ", sft = " + getSft() +
        '}';
}
        }
