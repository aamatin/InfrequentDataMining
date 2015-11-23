package com.mbzshajib.mining.processor.uncertain.mining;

import com.mbzshajib.mining.collection.PowerSetGenerator;
import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.processor.uncertain.model.*;

import java.util.*;

/**
 * Created by CSEDU on 11/7/2015.
 */
public class InfrequentWeightedDataMining {
    List<String> itemFrequent;

    public ItemSet mineWeightedTree(WeightedTree weightedTree, double threshold) throws DataNotValidException {
//        System.out.println("Window start");
        itemFrequent = new ArrayList<>();
        PowerSetManipulator powerSetManipulator = new PowerSetManipulator();
        WeightedNode rootNode = weightedTree.getRootNode();
        List<WeightedNode> distinctNodes = rootNode.getDistinctNodes();
        HeaderTableforIWM headerTable = createHeaderTable(distinctNodes, rootNode);
        Map<String, HeadertableItemIWM> map = headerTable.getMap();

        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String s = (String) entry.getKey();
//            System.out.println(s);

            WeightedTree tree = weightedTree.copy();
            WeightedNode root = tree.getRootNode();
            WeightedNode weightedNode = root.copy();
            WeightedNode conditionalTreeNotRemovingChild = constructConditionalTreeNotRemovingChild(weightedNode, s);

            List<WeightedNode> leafNodeList = new ArrayList<WeightedNode>();
            weightedNode.getLeafNodeList(leafNodeList);
            updateMiningProbabilityForCondTree(leafNodeList);
//            for (WeightedNode leaf : leafNodeList) {
//                if (leaf.getParentNode() != null) {
//                    WeightedNode parent = leaf.getParentNode();
//                    parent.getChildNodeList().remove(leaf);
//                    leaf.setParentNode(null);
//                }
//            }
            if (leafNodeList.size() == 0) {

            }
//            if (leafNodeList.size() == 1) {
//                updateFrequentItemForOneBranch(weightedNode, new FrequentItem(s));
//            } else {
            mine(weightedNode, threshold, s);
//            }

        }



        ItemSet itemSet = new ItemSet();
        itemSet.addItems(itemFrequent);
//        Map<String, List<WeightedNode>> headerTableMap/* = headerTable.getMap()*/ = null;
//        Iterator it = headerTableMap.entrySet().iterator();
//        ItemSet itemSet = new ItemSet();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
////            System.out.println(pair.getKey() + " = " + pair.getValue());
//            List<WeightedNode> nodes = (List<WeightedNode>) pair.getValue();
//            for (WeightedNode node : nodes) {
//                String id = node.getId();
//                itemSet.addItemSet(id);
//                List<String> items = new ArrayList<>();
//                WeightedNode copyNode = node;
//                while (!copyNode.getParentNode().getId().equals("0")) {
//                    items.add(copyNode.getParentNode().getId());
//                    copyNode = copyNode.getParentNode();
//
//                }
//
//
//                int size = items.size();
//                if (size > 0) {
//
//                    LinkedHashSet powerset = powerSetManipulator.powerset(items);
//                    Iterator itr = powerset.iterator();
//
//                    while (itr.hasNext()) {
//                        LinkedHashSet pattern = (LinkedHashSet) itr.next();
//
//                        String s = "";
//                        if (pattern.size() > 0) {
//                            Iterator itr1 = pattern.iterator();
//                            while (itr1.hasNext()) {
////                                Object next = itr1.next();
////                                System.out.println(next);
//                                s += (String) itr1.next();
//                            }
//                        }
//                        itemSet.addItemSet(s, id);
//                    }
//                }
//
////                PowerSetGenerator<String> generator = new PowerSetGenerator<String>();
////                List<List<String>> powerSet = generator.generatePowerSet(items);
////                if (powerSet.size() > 1) {
////                    for (List<String> set : powerSet) {
////                        String s = "";
////                        for (String item : set) {
////
////                            s = s+item;
////
////                            itemSet.addItemSet(item);
////                        }
////                        System.out.println(s +"\n");
////                    }
//////                    String[] frequentItemSet = frequentItem.getFrequentItemSet();
//////                    fRootNode.addChildesChain(frequentItemSet);
////                }
//
//            }
//            it.remove(); // avoids a ConcurrentModificationException
//        }
//
//
//
//        return itemSet;


        return itemSet;

    }

    public void mine(WeightedNode rootNode, double threshold, String item) {
//        System.out.println(mine++);

        List<String> frequent = new ArrayList<>();
        List<WeightedNode> leafNodeList = new ArrayList<WeightedNode>();
        rootNode.getLeafNodeList(leafNodeList);
        for (WeightedNode weightedNode : leafNodeList) {
//            System.out.println("Nodec weight : " + + weightedNode.getNodeWeight());
            if (weightedNode.getNodeWeight() > threshold) {
                weightedNode.setParentNode(null);
            } else {
                String pattern = weightedNode.getId();
                if (!frequent.contains(pattern)) {
                    frequent.add(pattern);
                }


                while (weightedNode.getParentNode() != null) {
                    if(!weightedNode.getParentNode().getId().equals("0")){
                        pattern = weightedNode.getParentNode().getId() + pattern;
                        if (!frequent.contains(pattern)) {
                            frequent.add(pattern);
                        }
                        weightedNode = weightedNode.getParentNode();
                    } else{
                        break;
                    }
                }
            }
        }
        for (String s : frequent) {
            if(!itemFrequent.contains(s)){
                itemFrequent.add(s);
            }
        }


//        if (frequent.size() > 0) {
//            if (!itemFrequent.contains(item)) {
//                itemFrequent.add(item);
//            }
//        }
//

//        for (String s : frequent) {
//            String pattern = s + item;
//            if (!itemFrequent.contains(pattern)) {
//                itemFrequent.add(pattern);
//            }
//        }


//        WeightedNode rootNode = weightedTree.getRootNode();
//        List<WeightedNode> distinctNodes = rootNode.getDistinctNodes();
//        HeaderTableforIWM headerTable = createHeaderTable(distinctNodes, rootNode);
////        UHeader header = new UHeader();
//////        List<UNode> distinctNodes = rootNode.getDistinctNodes();
////        for (WeightedNode distinct : distinctNodes) {
////            header.addNodeToHeader(distinct);
////        }
////        headerTable.sortByMiningValueDsc();
//        List<String> itemList = headerTable.getItems();
//        int count = itemList.size();
//        for (String item : itemList) {
//            HeadertableItemIWM headertableItemIWM = headerTable.getMap().get(item);
//            if (item.getTotalMiningVal() < minSup) {
//                header.removeItemFromListWithNodes(item);
//                i--;
//                count--;
//            } else {
//                break;
//            }

    }
//        updateFrequentItem(header, frequentItem);
//        for (UHItem item : header.getItemList()) {
//            UNode copy = rootNode.copy();
//            UHeader copyHeader = new UHeader();
//            List<UNode> copyDistinctNodes = copy.getDistinctNodes();
//            for (UNode distinct : copyDistinctNodes) {
//                copyHeader.addNodeToHeader(distinct);
//            }
//            copyHeader.removeItemByIdWithNode(item.getItemId());
//
//            List<UNode> leafList = new ArrayList<UNode>();
//            copy.getLeafNodeList(leafList);
//            updateMiningProbability(leafList);
//
//            FrequentItem copyFrequent = new FrequentItem(frequentItem);
//            copyFrequent.addFrequentItem(item.getItemId());
//            if (leafList.size() == 1) {
//                updateFrequentItemForOneBranch(copy, frequentItem);
//            } else {
//                mine(copy, minSup, copyFrequent);
//            }
//
//        }
    //TODO: Create Header With Mining Probability Only
    //TODO: Remove Item Less Than min_sup by mining Value
    //TODO: ADD PATTERNS TO ROOT_F_PATTERNS
    //TODO: LOOP(EACH ITEM IN HEADER)
    //  TODO: COPY ROOT
    //  TODO: REMOVE ITEM FROM COPY TREE HEADER ACTUALLY
    //  TODO: ADD ITEM TO EXISTING frequentItem
    //  TODO: MINE(COPY, minsup, created frequentItem).
//    }


    private void updateFrequentItemForOneBranch(WeightedNode root, FrequentItem frequentItem) {
        List<WeightedNode> nodeList = new ArrayList<WeightedNode>();
        WeightedNode child;
        WeightedNode parent = root;
        while (parent.getChildNodeList().size() == 1) {
            child = parent.getChildNodeList().get(0);
            nodeList.add(child);
            parent = child;
        }
        PowerSetGenerator<WeightedNode> generator = new PowerSetGenerator<WeightedNode>();
        List<List<WeightedNode>> powerSet = generator.generatePowerSet(nodeList);
        if (powerSet.size() > 1) {
            for (List<WeightedNode> set : powerSet) {
                for (WeightedNode item : set) {
                    frequentItem.addFrequentItem(item.getId());
                }
            }
//            List<String>frequentItemSet = frequentItem.getFrequentSet();
//            fRootNode.addChildesChain(frequentItemSet);
        }

    }

    private void updateMiningProbabilityForCondTree(List<WeightedNode> leafNodeList) {
        for (WeightedNode leafNode : leafNodeList) {
            leafNode.setMiningProbability(leafNode.getNodeWeight());
            updateParentMiningData(leafNode);
        }

    }

    private void updateParentMiningData(WeightedNode leafNode) {
        WeightedNode parentNode = leafNode.getParentNode();
        if (leafNode.getId().equals("0") || parentNode.getId().equals("0")) {
            return;
        } else {
            parentNode.setMiningProbability(parentNode.getMiningProbability() + leafNode.getNodeWeight());
            updateParentMiningData(parentNode);
        }
    }

    private void prepareConditionalTree(String item, WeightedTree weightedTree) throws DataNotValidException {

    }

    private WeightedNode constructConditionalTreeNotRemovingChild(WeightedNode node, String id) {
        if (node.getId().equalsIgnoreCase(id)) {
            node.setChildNodeList(new ArrayList<WeightedNode>());
//            node.getParentNode().setMiningProbability(node.getNodePrefixValue());
            return node;
        } else {
            if (node.getChildNodeList() == null || node.getChildNodeList().isEmpty()) {
                return null;
            } else {
                WeightedNode returnNode = null;
                int count = node.getChildNodeList().size();
                for (int index = 0; index < count; index++) {
                    WeightedNode child = node.getChildNodeList().get(index);
                    WeightedNode foundNode = constructConditionalTreeNotRemovingChild(child, id);
                    if (foundNode == null) {
                        node.removeChildNode(child);
                        index--;
                        count--;
                    } else {
                        returnNode = foundNode;
                    }
                }
                return returnNode;
            }
        }
    }

    private HeaderTableforIWM createHeaderTable(List<WeightedNode> distinctNodes, WeightedNode rootNode) {
        HeaderTableforIWM headerTableforIWM = new HeaderTableforIWM();

        for (WeightedNode weightedNode : distinctNodes) {
            headerTableforIWM.addWeightedNode(weightedNode);
        }
        return headerTableforIWM;

    }
}
