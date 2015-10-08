package com.mbzshajib.mining.processor.uncertain.model;

import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/20/2015
 * @time: 11:16 PM
 * ****************************************************************
 */

public class UNode {

    private String id;
    private int frameSize;
    private UNode parentNode;
    private List<UNode> childNodeList;
    private List<WData> uncertainDataList;
    private double miningProbability;

    private UNode() {

    }

    public UNode(String id, int frameSize) {
        this.id = id;
        this.frameSize = frameSize;
        childNodeList = new ArrayList<UNode>();
        uncertainDataList = new ArrayList<WData>(frameSize);
        for (int i = 0; i < frameSize; i++) {
            WData wData = new WData(0, 0);
            uncertainDataList.add(i, wData);
        }
    }

    public void addUData(int frameNo, WData dataToBeAdded) throws DataNotValidException {
        if (frameNo >= frameSize) {
            throw new DataNotValidException("Frame no " + frameNo + " must be less than frame size " + frameSize + ".");
        }
        WData wData = uncertainDataList.get(frameNo);
        wData.setMaxValue(wData.getMaxValue() + dataToBeAdded.getMaxValue());
        wData.setItemWeight(wData.getItemWeight() + dataToBeAdded.getItemWeight());
    }

    public void addChild(UNode childNode) {
        childNode.setParentNode(this);
        childNodeList.add(childNode);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(UNode parentNode) {
        this.parentNode = parentNode;
    }

    public List<UNode> getChildNodeList() {
        return childNodeList;
    }

    public void setChildNodeList(List<UNode> childNodeList) {
        this.childNodeList = childNodeList;
    }

    public String traverse() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.NEW_LINE)
                .append(Constant.HASH)
                .append("Parent ")
                .append("ID:").append(Constant.HASH).append(id);
        stringBuilder.append(Constant.TABBED_HASH)
                .append(Constant.HASH)
                .append("Child ").append(childNodeList.size())
                .append(Constant.TABBED_HASH)
                .append("[ ");
        for (UNode node : childNodeList) {
            stringBuilder
                    .append(Constant.TABBED_HASH)
                    .append(node.getId())
                    .append("[");
            for (WData wData : node.getUncertainDataList()) {
                stringBuilder.append("{")
                        .append("S-").append(wData.getItemWeight())
                        .append("}");
            }
            stringBuilder.append("]");
        }
        stringBuilder.append(" ]");
        for (UNode node : childNodeList) {
            stringBuilder.append(node.traverse());
        }
        return stringBuilder.toString();
    }

    public boolean isSameId(String id) {
        return id.equalsIgnoreCase(this.id);
    }

    public void slide() {
        this.uncertainDataList.remove(this.uncertainDataList.get(0));
        this.uncertainDataList.add(new WData(0, 0));

        for (UNode node : childNodeList) {
            node.slide();
        }
    }

    public boolean removeNodeIfEmpty() {
        boolean isEmpty = true;
        for (WData data : uncertainDataList) {
            if (data.getMaxValue() > 0) {
                isEmpty = false;
                break;
            }

        }
        if (isEmpty) {
            parentNode.getChildNodeList().remove(this);
            return true;
        } else {
            return false;
        }

    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public void setUncertainDataList(List<WData> uncertainDataList) {
        this.uncertainDataList = uncertainDataList;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public List<WData> getUncertainDataList() {
        return uncertainDataList;
    }

    public UNode copy() {
        UNode nodeToBeCopied = this;
        UNode rootNode = new UNode(new String(nodeToBeCopied.getId()), nodeToBeCopied.getFrameSize());
        rootNode.setParentNode(null);
        for (int i = 0; i < nodeToBeCopied.getChildNodeList().size(); i++) {
            UNode node = nodeToBeCopied.getChildNodeList().get(i);
            UNode copiedNode = copyNodes(node, rootNode);
            rootNode.setChildNode(copiedNode);
        }
        return rootNode;
    }

    private UNode copyNodes(UNode nodeToBeCopied, UNode parentNode) {
        UNode resultNode = new UNode(new String(nodeToBeCopied.getId()), nodeToBeCopied.getFrameSize());
        resultNode.setParentNode(parentNode);
        List<WData> wDataList = new ArrayList<WData>();
        for (WData wData : nodeToBeCopied.getUncertainDataList()) {
            wDataList.add(wData.copy());
        }
        resultNode.setUncertainDataList(wDataList);
        for (int i = 0; i < nodeToBeCopied.getChildNodeList().size(); i++) {
            UNode node = nodeToBeCopied.getChildNodeList().get(i);
            UNode copiedNode = copyNodes(node, resultNode);
            resultNode.setChildNode(copiedNode);
        }
        return resultNode;
    }

    private void setChildNode(UNode childNode) {
        this.childNodeList.add(childNode);
    }

    @Override
    public String toString() {
        return "UNode{" +
                "id='" + id + '\'' +
                '}';
    }

    public double getNodePrefixValue() {
        double result = 0;
        for (WData data : uncertainDataList) {
            result = result + data.getMaxValue();
        }
        return result;
    }

    public double getItemProbabilityValue() {
        double result = 0;
        for (WData data : uncertainDataList) {
            result = result + data.getItemWeight();
        }
        return result;
    }

    public void removeChildNode(UNode childNode) {
        this.childNodeList.remove(childNode);
    }

    public List<UNode> getDistinctNodes() {
        List<UNode> result = new ArrayList<UNode>();
        for (int index = 0; index < childNodeList.size(); index++) {
            UNode child = childNodeList.get(index);
            if (!result.contains(child)) {
                result.add(child);
            }
            result.addAll(child.getDistinctNodes());
        }
        return result;
    }

    public void removeNodeIfChildOfAnyDepth(UNode node) {
        int count = childNodeList.size();
        for (int i = 0; i < count; i++) {
            UNode child = childNodeList.get(i);
            if (child == node) {
                childNodeList.remove(node);
                i--;
                count--;
            }
            child.removeNodeIfChildOfAnyDepth(node);
        }
    }

    public void removeNodeIfChildOfAnyDepthById(String id) {
        int count = childNodeList.size();
        for (int i = 0; i < count; i++) {
            UNode child = childNodeList.get(i);
            if (child.getId().equalsIgnoreCase(id)) {
                childNodeList.remove(child);
                i--;
                count--;
            } else {
                child.removeNodeIfChildOfAnyDepthById(id);
            }
        }
    }

    public double getMiningProbability() {
        return miningProbability;
    }

    public void setMiningProbability(double miningProbability) {
        this.miningProbability = miningProbability;
    }

    public void getLeafNodeList(List<UNode> list) {
        if (childNodeList.isEmpty()) {
            list.add(this);
            return;
        } else {
            for (int i = 0; i < childNodeList.size(); i++) {
                UNode child = childNodeList.get(i);
                child.getLeafNodeList(list);
            }
        }
    }
}
