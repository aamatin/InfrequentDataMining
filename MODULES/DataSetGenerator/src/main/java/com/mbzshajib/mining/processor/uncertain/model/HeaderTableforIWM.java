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
    Map<String, HeadertableItemIWM> map;
//    Map<String, int> map;

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

    public Map<String, HeadertableItemIWM> getMap() {
        return map;
    }

    public void setMap(Map<String, HeadertableItemIWM> map) {
        this.map = map;
    }

    public void addWeightedNode(WeightedNode weightedNode) {
        String item = weightedNode.getId();
        if(!items.contains(item)){
            items.add(item);
//            List<HeadertableItemIWM> weightedNodes = new ArrayList<>();
            HeadertableItemIWM headertableItemIWM = new HeadertableItemIWM();
            headertableItemIWM.setItem(item);
            headertableItemIWM.setValue(weightedNode.getNodeWeight());
            headertableItemIWM.addNodeToList(weightedNode);
//            weightedNodes.add(headertableItemIWM);

            map.put(item, headertableItemIWM);

        } else{
            HeadertableItemIWM headertableItemIWM = map.get(item);
            headertableItemIWM.setValue(headertableItemIWM.getValue() +weightedNode.getNodeWeight());

            headertableItemIWM.addNodeToList(weightedNode);
//            weightedNodes.add(headertableItemIWM);

            map.put(item, headertableItemIWM);

        }
    }
}
