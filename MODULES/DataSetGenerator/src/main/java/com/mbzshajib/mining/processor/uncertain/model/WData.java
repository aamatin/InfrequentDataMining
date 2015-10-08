package com.mbzshajib.mining.processor.uncertain.model;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/30/2015
 * @time: 6:50 PM
 * ****************************************************************
 */

public class WData {
    private int itemWeight;
    private int maxValue;

    public WData(int itemWeight, int maxValue) {
        this.itemWeight = itemWeight;
        this.maxValue = maxValue;
    }

    public int getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(int itemWeight) {
        this.itemWeight = itemWeight;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return "WData{" +
                "itemWeight=" + itemWeight +
                ", maxValue=" + maxValue +
                '}';
    }

    public WData copy() {
        WData wData = new WData(this.itemWeight, this.maxValue);
        return wData;
    }
}
