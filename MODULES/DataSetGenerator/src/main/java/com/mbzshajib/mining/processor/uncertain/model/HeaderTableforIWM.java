package com.mbzshajib.mining.processor.uncertain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CSEDU on 11/7/2015.
 */
public class HeaderTableforIWM {
    List<String> items;
    Map<String, List<WeightedNode>> map;

    public HeaderTableforIWM() {
        this.items = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Map<String, List<WeightedNode>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<WeightedNode>> map) {
        this.map = map;
    }

    public void addWeightedNode(WeightedNode weightedNode) {
        String item = weightedNode.getId();
        if(!items.contains(item)){
            items.add(item);
            List<WeightedNode> weightedNodes = new ArrayList<>();
            weightedNodes.add(weightedNode);

            map.put(item, weightedNodes);

        } else{
            List<WeightedNode> weightedNodes = map.get(item);
            weightedNodes.add(weightedNode);
            map.put(item, weightedNodes);
        }
    }
}
