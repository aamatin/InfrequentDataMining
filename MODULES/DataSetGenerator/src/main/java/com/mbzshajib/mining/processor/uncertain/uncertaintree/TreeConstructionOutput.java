package com.mbzshajib.mining.processor.uncertain.uncertaintree;

import com.mbzshajib.mining.processor.uncertain.model.WeightedTree;
import com.mbzshajib.utility.model.Output;

/**
 * *****************************************************************
 * Copyright  2015.
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email  - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/20/2015
 * @time: 11:03 PM
 * ****************************************************************
 */    

public class TreeConstructionOutput implements Output {
    private WeightedTree weightedTree;
    private long endTime;
    private long startTime;

    public WeightedTree getWeightedTree() {
        return weightedTree;
    }

    public void setWeightedTree(WeightedTree weightedTree) {
        this.weightedTree = weightedTree;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
