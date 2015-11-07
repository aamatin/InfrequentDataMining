package com.mbzshajib.mining.processor.uncertain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CSEDU on 11/7/2015.
 */
public class ItemSet {
    private List<String> itemSet;

    public ItemSet() {
        this.itemSet = new ArrayList<>();
    }

    public void addItemSet(String itemIdString, String leaf) {
        String itemId = itemIdString+leaf;
        boolean found = false;
        for (String id : itemSet) {
            if (id.equals(itemId)) {
                found = true;
                break;
            }


        }
        if (!found) {
            itemSet.add(new String(itemId));
        }
    }

    public void addItemSet(String itemId) {
        boolean found = false;
        for (String id : itemSet) {
            if (id.equals(itemId)) {
                found = true;
                break;
            }


        }
        if (!found) {
            itemSet.add(new String(itemId));
        }
    }

    public List<String> getItemSet() {
        return itemSet;
    }

    public String traverse() {
        StringBuilder builder = new StringBuilder();
        builder.append("Item: ");
        for (String string : itemSet) {
            builder.append("(")
                    .append(string)
                    .append(")");
        }
        return builder.toString();
    }
}
