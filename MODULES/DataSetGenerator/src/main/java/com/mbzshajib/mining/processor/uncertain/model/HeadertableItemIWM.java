package com.mbzshajib.mining.processor.uncertain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 * Md. Yasser Arafat (mdyasserarafat1@gmail.com)
 * Redistribution or Using any part of source code or binary
 * can not be done without permission of owner.
 * *****************************************************************
 * author: Md. Yasser Arafat
 * email: mdyasserarafat1@gmail.com
 * date: 11/22/2015
 * time: 9:48 PM
 * ****************************************************************
 */
public class HeadertableItemIWM {
    private String item;
    private int value;
    private List<WeightedNode> nodeList;

    public HeadertableItemIWM() {
        this.nodeList = new ArrayList<>();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<WeightedNode> getNodeList() {
        return nodeList;
    }

    public void addNodeToList(WeightedNode node) {
        this.nodeList.add(node);
    }
}
