package com.mbzshajib.mining.processor.uncertain.mining;

import com.mbzshajib.mining.collection.PowerSetGenerator;
import com.mbzshajib.mining.processor.uncertain.model.HeaderTableforIWM;
import com.mbzshajib.mining.processor.uncertain.model.ItemSet;
import com.mbzshajib.mining.processor.uncertain.model.WeightedNode;
import com.mbzshajib.mining.processor.uncertain.model.WeightedTree;

import java.util.*;

/**
 * Created by CSEDU on 11/7/2015.
 */
public class InfrequentWeightedDataMining {
    public ItemSet mineWeightedTree(WeightedTree weightedTree) {
        PowerSetManipulator powerSetManipulator = new PowerSetManipulator();
        WeightedNode rootNode = weightedTree.getRootNode();
        List<WeightedNode> distinctNodes = rootNode.getDistinctNodes();
        HeaderTableforIWM headerTable = createHeaderTable(distinctNodes, rootNode);
        Map<String, List<WeightedNode>> headerTableMap = headerTable.getMap();
        Iterator it = headerTableMap.entrySet().iterator();
        ItemSet itemSet = new ItemSet();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            List<WeightedNode> nodes = (List<WeightedNode>) pair.getValue();
            for (WeightedNode node : nodes) {
                String id = node.getId();
                itemSet.addItemSet(id);
                List<String> items = new ArrayList<>();
                WeightedNode copyNode = node;
                while (!copyNode.getParentNode().getId().equals("0")) {
                    items.add(copyNode.getParentNode().getId());
                    copyNode = copyNode.getParentNode();

                }


                int size = items.size();
                if (size > 0) {

                    LinkedHashSet powerset = powerSetManipulator.powerset(items);
                    Iterator itr = powerset.iterator();

                    while (itr.hasNext()) {
                        LinkedHashSet pattern = (LinkedHashSet) itr.next();

                        String s = "";
                        if (pattern.size() > 0) {
                            Iterator itr1 = pattern.iterator();
                            while (itr1.hasNext()) {
//                                Object next = itr1.next();
//                                System.out.println(next);
                                s += (String) itr1.next();
                            }
                        }
                        itemSet.addItemSet(s, id);
                    }
                }

//                PowerSetGenerator<String> generator = new PowerSetGenerator<String>();
//                List<List<String>> powerSet = generator.generatePowerSet(items);
//                if (powerSet.size() > 1) {
//                    for (List<String> set : powerSet) {
//                        String s = "";
//                        for (String item : set) {
//
//                            s = s+item;
//
//                            itemSet.addItemSet(item);
//                        }
//                        System.out.println(s +"\n");
//                    }
////                    String[] frequentItemSet = frequentItem.getFrequentItemSet();
////                    fRootNode.addChildesChain(frequentItemSet);
//                }

            }
            it.remove(); // avoids a ConcurrentModificationException
        }



        return itemSet;

    }

    private HeaderTableforIWM createHeaderTable(List<WeightedNode> distinctNodes, WeightedNode rootNode) {
        HeaderTableforIWM headerTableforIWM = new HeaderTableforIWM();

        for (WeightedNode weightedNode : distinctNodes) {
            headerTableforIWM.addWeightedNode(weightedNode);
        }
        return headerTableforIWM;

    }
}
