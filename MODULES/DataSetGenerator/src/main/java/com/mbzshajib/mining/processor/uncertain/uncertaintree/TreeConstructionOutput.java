package com.mbzshajib.mining.processor.uncertain.uncertaintree;

import com.mbzshajib.mining.processor.uncertain.model.TimeModel;
import com.mbzshajib.mining.processor.uncertain.model.WInputData;
import com.mbzshajib.mining.processor.uncertain.model.WeightedTree;
import com.mbzshajib.utility.model.Output;

import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/20/2015
 * @time: 11:03 PM
 * ****************************************************************
 */

public class TreeConstructionOutput implements Output {
    private WeightedTree weightedTree;
    private TimeModel timeModel;
    private List<List<WInputData>> windowTransactionList;

    public WeightedTree getWeightedTree() {
        return weightedTree;
    }

    public void setWeightedTree(WeightedTree weightedTree) {
        this.weightedTree = weightedTree;
    }

    public TimeModel getTimeModel() {
        return timeModel;
    }

    public void setTimeModel(TimeModel timeModel) {
        this.timeModel = timeModel;
    }

    public List<List<WInputData>> getWindowTransactionList() {
        return windowTransactionList;
    }

    public void setWindowTransactionList(List<List<WInputData>> windowTransactionList) {
        this.windowTransactionList = windowTransactionList;
    }
}
