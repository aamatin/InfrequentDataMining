package com.mbzshajib.mining.processor.uncertain.mining;

import com.mbzshajib.mining.processor.uncertain.model.WInputData;
import com.mbzshajib.mining.processor.uncertain.model.WeightedTree;
import com.mbzshajib.utility.model.Input;

import java.util.List;

/**
 * *****************************************************************
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @copyright 2015.
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 10/3/2015
 * @time: 9:30 PM
 * ****************************************************************
 */

public class UncertainStreamMineInput implements Input {

    private int maxSupport;
    private WeightedTree weightedTree;
    private List<List<WInputData>> windowTransactionList;
    private String outputFilePath;


    public int getMaxSupport() {
        return maxSupport;
    }

    public void setMaxSupport(int maSupport) {
        this.maxSupport = maSupport;
    }

    public int getMinSupport() {
        return maxSupport;
    }

    public void setMinSupport(int maxSupport) {
        this.maxSupport = maxSupport;

    }

    public WeightedTree getWeightedTree() {
        return weightedTree;
    }

    public void setWeightedTree(WeightedTree weightedTree) {
        this.weightedTree = weightedTree;
    }

    public List<List<WInputData>> getWindowTransactionList() {
        return windowTransactionList;
    }

    public void setWindowTransactionList(List<List<WInputData>> windowTransactionList) {
        this.windowTransactionList = windowTransactionList;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }
}
