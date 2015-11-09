package com.mbzshajib.mining.processor.uncertain.model;

import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.util.Constant;

import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/28/2015
 * @time: 8:41 PM
 * ****************************************************************
 */

public class WeightedTree {
    private WeightedNode rootNode;
    private HeaderTable headerTable;
    private int frameSize;
    private int windowSize;

    public WeightedTree(int frameSize, int windowSize) throws DataNotValidException {
        this.frameSize = frameSize;
        this.windowSize = windowSize;
        this.headerTable = new HeaderTable(windowSize);
        this.rootNode = new WeightedNode("0", windowSize);
        this.rootNode.setParentNode(null);
    }

    public String getTraversedString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UNCERTAIN TREE")
                .append(Constant.NEW_LINE);
        builder.append(rootNode.traverse());
        builder.append(Constant.NEW_LINE);
        builder.append(Constant.NEW_LINE);

//        builder.append("HEADER TABLE")
//                .append(Constant.NEW_LINE)
//                .append(Constant.NEW_LINE);
//        builder.append(headerTable.traverse());
//        builder.append(Constant.NEW_LINE);
        return builder.toString();
    }

    public WeightedNode getRootNode() {
        return rootNode;
    }

    public HeaderTable getHeaderTable() {
        return headerTable;
    }

    public void addTransactionToTree(List<WInputData> wInputData, int frameNo) throws DataNotValidException {
        WeightedNode parentNode = rootNode;
        for (WInputData inputData : wInputData) {
            parentNode = addNode(inputData, parentNode, frameNo);
        }
    }
    private WeightedNode addNode(WInputData wInputData, WeightedNode parentNode, int frameNo) throws DataNotValidException {
        int foundIndex = -1;
        for (int i = 0; i < parentNode.getChildNodeList().size(); i++) {
            if (parentNode.getChildNodeList().get(i).isSameId(wInputData.getId())) {
                foundIndex = i;
            }
        }
        if (foundIndex == -1) {
            WeightedNode node = new WeightedNode(wInputData.getId(), windowSize);
            parentNode.addChild(node);
            headerTable.updateHeaderTable(node);
            WData wData = new WData(wInputData.getItemWeight(), wInputData.getMaxValue());
            node.addUData(frameNo, wData);
            return node;
        } else {
            WeightedNode tmpNode = parentNode.getChildNodeList().get(foundIndex);
            WData wData = new WData(wInputData.getItemWeight(), wInputData.getMaxValue());
            tmpNode.addUData(frameNo, wData);
            return tmpNode;
        }
    }
    public void slideWindowAndUpdateTree() {
        removeOneFrameOldestData(rootNode);
        headerTable.slideHeaderTable();
    }

    private void removeOneFrameOldestData(WeightedNode node) {
        for (int i = 0; i < node.getChildNodeList().size(); i++) {
            WeightedNode tmpNode = node.getChildNodeList().get(i);
            tmpNode.slide();
        }
        removeEmptyChildNodes(node);
    }

    private void removeEmptyChildNodes(WeightedNode node) {
        int loopCounter = node.getChildNodeList().size();
        for (int i = 0; i < loopCounter; i++) {
            WeightedNode tmpNode = node.getChildNodeList().get(i);
            boolean isDeleted = tmpNode.removeNodeIfEmpty();
            if (isDeleted) {
                loopCounter = loopCounter - 1;
                i = -1;
            } else {
                removeEmptyChildNodes(tmpNode);

            }
        }
    }



    public void setRootNode(WeightedNode rootNode) {
        this.rootNode = rootNode;
    }

    public void setHeaderTable(HeaderTable headerTable) {
        this.headerTable = headerTable;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public WeightedTree copy() throws DataNotValidException {
        WeightedTree weightedTree = new WeightedTree(frameSize, windowSize);
        WeightedNode copiedNode = rootNode.copy();
        HeaderTable headerTable = this.headerTable.copy();
        copyHeaderTable(this.headerTable, headerTable, this.rootNode, copiedNode);
        weightedTree.setRootNode(copiedNode);
        weightedTree.setHeaderTable(headerTable);
        return weightedTree;
    }

    private HeaderTable copyHeaderTable(HeaderTable mainTable, HeaderTable clonedTable, WeightedNode mainNode, WeightedNode clonedNode) {
        for (int i = 0; i < mainNode.getChildNodeList().size(); i++) {
            WeightedNode nodeOriginal = mainNode.getChildNodeList().get(i);
            WeightedNode nodeCopied = clonedNode.getChildNodeList().get(i);

            int index = mainTable.findNode(nodeOriginal);
            if (index == -1) {
                continue;
            } else {

                clonedTable.addNode(nodeCopied, index);
            }
            copyHeaderTable(mainTable, clonedTable, nodeOriginal, nodeCopied);
        }
        return clonedTable;
    }
}
