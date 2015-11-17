package com.mbzshajib.mining.processor.uncertain.mining;

import com.mbzshajib.mining.collection.PowerSetGenerator;
import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.processor.uncertain.model.*;
import com.mbzshajib.mining.util.Constant;
import com.mbzshajib.mining.util.Utils;
import com.mbzshajib.utility.file.FileUtility;
import com.mbzshajib.utility.model.ProcessingError;
import com.mbzshajib.utility.model.Processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/28/2015
 * @time: 7:46 PM
 * ****************************************************************
 */

public class UncertainStreamMiner implements Processor<UncertainStreamMineInput, UncertainStreamMineOutput> {
    public static final String TAG = UncertainStreamMiner.class.getCanonicalName();
    private List<FrequentItem> frequentItemList;
    private Map<String, WindowItem> windowItemMap;

    @Override
    public UncertainStreamMineOutput process(UncertainStreamMineInput uncertainStreamMineInput) throws ProcessingError, IOException {
        long miningStartTime = System.currentTimeMillis();
        windowItemMap = new HashMap<>();
        frequentItemList = new ArrayList<FrequentItem>();
        WeightedTree weightedTree = uncertainStreamMineInput.getWeightedTree();
//        System.out.println(weightedTree.getTraversedString());

        InfrequentWeightedDataMining itemSetMining = new InfrequentWeightedDataMining();
        ItemSet itemSet = itemSetMining.mineWeightedTree(weightedTree);
//        System.out.println(itemSet.traverse());
        List<List<WInputData>> windowTransactionList = uncertainStreamMineInput.getWindowTransactionList();
        int windowTotalWeight = getWindowTotalWeight(windowTransactionList);
        generateWindowPatternList(windowTransactionList, windowTotalWeight);
        Map<String, WindowItem> copyWindowMap = new HashMap<>(windowItemMap);
//        generateSft(windowTotalWeight);

        double support = (double) uncertainStreamMineInput.getMaxSupport() / 100;

        Map<String, WindowItem> result = getResult(itemSet, support);

        printOutput(weightedTree, itemSet, support, windowTotalWeight, copyWindowMap, result, uncertainStreamMineInput.getOutputFilePath());


        UncertainStreamMineOutput uncertainStreamMineOutput = new UncertainStreamMineOutput();
        uncertainStreamMineOutput.setItemSet(itemSet);
//        int minSupport = uncertainStreamMineInput.getMinSupport();
//        try {
//            startMining(weightedTree, minSupport);
//        } catch (DataNotValidException e) {
//            throw new ProcessingError(e);
//        }
//        printOutput();
        TimeModel timeModel = new TimeModel(miningStartTime, System.currentTimeMillis());
        uncertainStreamMineOutput.setMiningTime(timeModel);
        return uncertainStreamMineOutput;
//        printBeforeMining(weightedTree);
//        WeightedNode rootNode = weightedTree.getRootNode();
//        HeaderTable headerTable = weightedTree.getHeaderTable();
//        List<HTableItemInfo> HTableItemInfoList = headerTable.getHeaderItemInfo();
//        removeDataBelowSupport(minSupport, HTableItemInfoList);
//        sortByPrefix(HTableItemInfoList);
    }

    private void printOutput(WeightedTree weightedTree, ItemSet itemSet, double support, int windowTotalWeight, Map<String,
            WindowItem> copyWindowMap, Map<String, WindowItem> result, String outputLocation) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("########## WINDOW START ###########\n\n");
        stringBuilder.append(weightedTree.getTraversedString());
        stringBuilder.append("Total Items found in tree ")
                .append(Constant.TABBED_HASH)
                .append(itemSet.traverse())
                .append(Constant.NEW_LINE)
                .append(Constant.NEW_LINE)
                .append("Window total weight ")
                .append(Constant.TABBED_HASH)
                .append(windowTotalWeight)
                .append(Constant.NEW_LINE)
                .append(Constant.NEW_LINE)
                .append("Window Items ")
                .append(Constant.TABBED_HASH)
                .append(Constant.NEW_LINE);
        Iterator it = copyWindowMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            WindowItem windowItem = (WindowItem) pair.getValue();

            stringBuilder.append(windowItem.toString())
                    .append(Constant.NEW_LINE);
            it.remove();
        }
        stringBuilder.append(Constant.NEW_LINE)
                .append("Support ")
                .append(Constant.TABBED_HASH)
                .append(support)
                .append(Constant.NEW_LINE)
                .append(Constant.NEW_LINE)
                .append("Infrequent Items ")
                .append(Constant.TABBED_HASH)
                .append(Constant.NEW_LINE);

        Iterator it1 = result.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry) it1.next();
            WindowItem windowItem = (WindowItem) pair.getValue();

            stringBuilder.append(windowItem.toString())
                    .append(Constant.NEW_LINE);
            it1.remove();
        }
        stringBuilder.append("\n\n########## WINDOW END ###########\n");
        System.out.println(stringBuilder.toString());

        File file = new File("output");
        FileUtility.writeFile(file, "data", stringBuilder.toString());


    }

    private Map<String, WindowItem> getResult(ItemSet itemSet, double support) {
        Map<String, WindowItem> windowItemMapOutput = new HashMap<>();
        Iterator it = windowItemMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            WindowItem windowItem = (WindowItem) pair.getValue();

            if (windowItem.getSft() < support && itemSet.getItemSet().contains(windowItem.getPattern())) {

                windowItemMapOutput.put(windowItem.getPattern(), windowItem);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }

//        System.out.println("### RESULT ###");
//        Iterator it1 = windowItemMapOutput.entrySet().iterator();
//        while (it1.hasNext()) {
//            Map.Entry pair = (Map.Entry) it1.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            it1.remove(); // avoids a ConcurrentModificationException
//        }
//        System.out.println("### RESULT ###");
        return windowItemMapOutput;

    }

    private int getWindowTotalWeight(List<List<WInputData>> windowTransactionList) {
        int weight = 0;
        for (List<WInputData> wInputDatas : windowTransactionList) {
            for (WInputData wInputData : wInputDatas) {
                weight += wInputData.getItemWeight();
            }

        }
        return weight;

    }

    private void generateWindowPatternList(List<List<WInputData>> windowTransactionList, int windowTotalWeight) {
        PowerSetGenerator powerSetGenerator = new PowerSetGenerator();
        for (List<WInputData> wInputDatas : windowTransactionList) {
            List<ArrayList<WInputData>> list = powerSetGenerator.generatePowerSet(wInputDatas);
            for (List<WInputData> arrayList : list) {
                if (arrayList.size() > 0) {
                    WindowItem windowItem = new WindowItem();
                    windowItem.setWindowTotalWeight(windowTotalWeight);
                    for (WInputData wInputData : arrayList) {
                        windowItem.setPattern(windowItem.getPattern() + wInputData.getId());
                        windowItem.setLmv(windowItem.getLmv() + wInputData.getItemWeight());
                    }
                    addToWindowItemList(windowItem);
                }
            }
        }
    }

    private void addToWindowItemList(WindowItem windowItem) {
//        System.out.println(windowItem.getPattern());
        WindowItem item = windowItemMap.get(windowItem.getPattern());
        if (item == null) {
            windowItemMap.put(windowItem.getPattern(), windowItem);
        } else {
            windowItemMap.remove(item);
            item.addWeight(windowItem.getLmv());
            windowItemMap.put(item.getPattern(), item);
        }

        //To change body of created methods use File | Settings | File Templates.
    }

//    private void printOutput() {
//        StringBuilder builder = new StringBuilder();
//        int count = 1;
//        builder.append("Total Frequent Items ")
//                .append(Constant.TABBED_HASH)
//                .append(frequentItemList.size())
//                .append(Constant.NEW_LINE);
//        for (FrequentItem item : frequentItemList) {
//            builder.append(count)
//                    .append(item.traverse())
//                    .append(Constant.NEW_LINE);
//        }
//        System.out.println(builder.toString());
//    }

    private void startMining(WeightedTree weightedTree, int minSupport) throws DataNotValidException {
        WeightedNode rootNode = weightedTree.getRootNode();
        List<WeightedNode> leafNodeList = new ArrayList<WeightedNode>();
        rootNode.getLeafNodeList(leafNodeList);
        for (int i = 0; i < leafNodeList.size(); i++) {
            WeightedNode node = leafNodeList.get(i);
            //removeFrequentItem(node, minSupport);
            FrequentItem frequentItem = new FrequentItem();
            List<WData> uncertainDataList = node.getUncertainDataList();
//            int total =0;
//            for (WData data:uncertainDataList){
//                total+=data.getItemWeight();
//            }
//            if(total>minSupport){
//                WeightedNode parentNode = node.getParentNode();
//                parentNode.getChildNodeList().remove(node);
//            }
//            for (WData data:uncertainDataList){
//
//
//            }
            mine(rootNode, frequentItem, weightedTree.getWindowSize(), node.getId(), minSupport, false);
        }
        //TODOTODO: generate


//        HeaderTable headerTable = generateHeaderTableForCondTree(rootNode, weightedTree.getWindowSize());
//        List<HTableItemInfo> frequentItemInfoByPrefix = headerTable.getFrequentItemInfoByPrefix(minSupport);
//        headerTable = removeInfrequentData(rootNode, headerTable, frequentItemInfoByPrefix, weightedTree.getWindowSize());
//        for (WeightedNode node : leafNodeList) {
//            WeightedNode nodeToBeMinned = rootNode.copy();
//            FrequentItem frequentItem = new FrequentItem();
//            frequentItem.addFrequentItem(node.getId());
//            frequentItemList.add(frequentItem);
//
//            mine(nodeToBeMinned, frequentItem, weightedTree.getWindowSize(), nodeToBeMinned.getId(), minSupport, false);
//
//        }
//        HeaderTable headerTable = weightedTree.getHeaderTable();
//        int windowSize = headerTable.getWindowSize();
//        List<HTableItemInfo> inFrequentItemsInfo = getInfrequentItemInfoFromHeader(headerTable, minSupport);
//        //TODOTODO:Remove data below support(Probability & Prefix Value).
//        removeInfrequentData(rootNode, headerTable, inFrequentItemsInfo, windowSize);
//        List<HTableItemInfo> listToBeMined = headerTable.getFrequentItemInfoByPrefix(minSupport);
////        sortByPrefix(listToBeMined);
//        //TODOTODO:LOOP
//        for (int loopCounter = listToBeMined.size() - 1; loopCounter >= 0; loopCounter--) {
//            WeightedNode tmpNode = rootNode.copy();
//            FrequentItem frequentItem = new FrequentItem();
//            frequentItem.addFrequentItem(listToBeMined.get(loopCounter).getItemId());
//            frequentItemList.add(frequentItem);
//            mine(tmpNode, frequentItem, windowSize, listToBeMined.get(loopCounter).getItemId(), minSupport, false);
//        }


    }

    private void removeFrequentItem(WeightedNode node, int min_support) {

        while (!node.getId().equals('0')) {
            if (node.getItemProbabilityValue() > min_support) {
                WeightedNode tempNode = node.getParentNode();
                tempNode.getChildNodeList().remove(node);
//              tempNode_value = getMiningProbability()
                removeFrequentItem(node, min_support);
            }
        }
    }


    private void miningProcess(WeightedNode root, FrequentItem item, int windowSize, String id, int minSupport, boolean isFirstIteration) throws DataNotValidException {
        item.addFrequentItem(id);
        if (isFirstIteration) {
            frequentItemList.add(item);
        }
        item = new FrequentItem(item);
        constructConditionalTree(root);

    }

    private WeightedNode constructConditionalTree(WeightedNode root) {

        WeightedNode miningTree;
        List<WeightedNode> leafNodeList = new ArrayList<WeightedNode>();
        root.getLeafNodeList(leafNodeList);

        return null;
    }


    private void mine(WeightedNode root, FrequentItem item, int windowSize, String id, int minSupport, boolean isFirstIteration) throws DataNotValidException {
        item.addFrequentItem(id);
        if (isFirstIteration) {
            frequentItemList.add(item);
        }
        item = new FrequentItem(item);

        constructConditionalTreeNotRemovingChild(root, id);
//        removeInfrequentItemNodes(root, id);
        List<WeightedNode> leafNodeList = new ArrayList<WeightedNode>();
        root.getLeafNodeList(leafNodeList);
        updateMiningProbability(leafNodeList);
        HeaderTable headerTable = generateHeaderTableForCondTree(root, windowSize);
        List<HTableItemInfo> inFrequentItemsInfo = getInfrequentItemInfoForMining(headerTable, minSupport);
//        headerTable = removeInfrequentData(root, headerTable, inFrequentItemsInfo, windowSize);
//        updateFrequentItem(root, item);
        List<HTableItemInfo> headerItemInfo = headerTable.getHeaderItemInfo();
        for (int i = headerItemInfo.size() - 1; i > 0; i--) {
            item = new FrequentItem(item);
            mine(root, item, windowSize, headerItemInfo.get(i).getItemId(), minSupport, false);
        }
    }

    private List<HTableItemInfo> getInfrequentItemInfoForMining(HeaderTable headerTable, int minSupport) {
        List<HTableItemInfo> inFrequentItemInfoByMiningValue = headerTable.getInFrequentItemInfoByMiningValue(minSupport);
        return inFrequentItemInfoByMiningValue;
    }

    private List<HTableItemInfo> getInfrequentItemInfoFromHeader(HeaderTable headerTable, double minSupport) {
        List<HTableItemInfo> inFrequentItemInfoByPrefix = headerTable.getInFrequentItemInfoByPrefix(minSupport);
        List<HTableItemInfo> inFrequentItemInfoBySupport = headerTable.getInFrequentItemInfoBySupport(minSupport);
        List<HTableItemInfo> result = new ArrayList<HTableItemInfo>();
        result.addAll(inFrequentItemInfoByPrefix);
        result.addAll(inFrequentItemInfoBySupport);
        return result;
    }


    private void updateMiningProbability(List<WeightedNode> leafNodeList) {
        for (WeightedNode leafNode : leafNodeList) {
            updateParentMiningData(leafNode);
        }

    }

    private void updateParentMiningData(WeightedNode leafNode) {
        WeightedNode parentNode = leafNode.getParentNode();
        if (leafNode.getId().equals("0") || parentNode.getId().equals("0")) {
            return;
        } else {
            parentNode.setMiningProbability(parentNode.getMiningProbability() + leafNode.getMiningProbability());
            updateParentMiningData(parentNode);
        }
    }

    private HeaderTable removeInfrequentData(WeightedNode node, HeaderTable headerTable, List<HTableItemInfo> inFrequentItemInfoByPrefix, int windowSize) throws DataNotValidException {
        for (HTableItemInfo hTableItemInfo : inFrequentItemInfoByPrefix) {
            removeInfrequentItemNodes(node, hTableItemInfo.getItemId());
            headerTable = generateHeaderTableForCondTree(node, windowSize);
        }
        return headerTable;
    }

    private void updateFrequentItem(WeightedNode rootNode, FrequentItem item) {

        for (int i = 0; i < rootNode.getChildNodeList().size(); i++) {
            WeightedNode child = rootNode.getChildNodeList().get(i);
            FrequentItem tmpItem = new FrequentItem(item);
            tmpItem.addFrequentItem(child.getId());
            frequentItemList.add(tmpItem);
            updateFrequentItem(child, item);
        }
    }

    private void removeInfrequentItemNodes(WeightedNode rootNode, String itemId) {
        rootNode.removeNodeIfChildOfAnyDepthById(itemId);
    }

    private HeaderTable generateHeaderTableForCondTree(WeightedNode rootNode, int windowSize) throws DataNotValidException {
        List<WeightedNode> distinctList = rootNode.getDistinctNodes();

        HeaderTable headerTable = new HeaderTable(windowSize);
        headerTable.updateHeaderTableFromDistinctList(distinctList);
        return headerTable;
    }

    @Deprecated
    private WeightedNode constructConditionalTreeRemovingChild(WeightedNode node, String id) {
        if (node.getId().equalsIgnoreCase(id)) {
            WeightedNode parentNode = node.getParentNode();
            parentNode.removeChildNode(node);
            return parentNode;
        } else {
            if (node.getChildNodeList() == null || node.getChildNodeList().isEmpty()) {
                return null;
            } else {
                WeightedNode returnNode = null;
                int count = node.getChildNodeList().size();
                for (int index = 0; index < count; index++) {
                    WeightedNode child = node.getChildNodeList().get(index);
                    WeightedNode foundNode = constructConditionalTreeRemovingChild(child, id);
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

    private void sortByID(List<HTableItemInfo> sortedFilteredById) {
        Collections.sort(sortedFilteredById, new Comparator<HTableItemInfo>() {
            @Override
            public int compare(HTableItemInfo one, HTableItemInfo two) {
                int idOne = Integer.parseInt(one.getItemId());
                int idTwo = Integer.parseInt(two.getItemId());
                int diff = idTwo - idOne;
                return diff;
            }
        });

    }

    private void sortByPrefix(List<HTableItemInfo> sortedFilteredById) {
        Collections.sort(sortedFilteredById, new Comparator<HTableItemInfo>() {
            @Override
            public int compare(HTableItemInfo one, HTableItemInfo two) {
                double diff = two.getItemPrefixValue() - one.getItemPrefixValue();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

    }

    private void sortByProbability(List<HTableItemInfo> sortedFilteredById) {
        Collections.sort(sortedFilteredById, new Comparator<HTableItemInfo>() {
            @Override
            public int compare(HTableItemInfo one, HTableItemInfo two) {
                double diff = two.getItemProbabilityValue() - one.getItemProbabilityValue();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

    }

    @Deprecated
    private void removeDataBelowSupport(double minSupport, List<HTableItemInfo> HTableItemInfoList) {
        removePrefixDataBelowSupport(HTableItemInfoList, minSupport);
        removeProbabilityDataBelowSupport(HTableItemInfoList, minSupport);
    }

    @Deprecated
    private void removePrefixDataBelowSupport(List<HTableItemInfo> HTableItemInfoList, double minSupport) {
        for (int index = 0; index < HTableItemInfoList.size(); index++) {
            HTableItemInfo HTableItemInfo = HTableItemInfoList.get(index);
            if (HTableItemInfo.getItemPrefixValue() < minSupport) {
                HTableItemInfoList.remove(HTableItemInfo);
            }
        }
    }

    @Deprecated
    private void removeProbabilityDataBelowSupport(List<HTableItemInfo> HTableItemInfoList, double minSupport) {
        for (int index = 0; index < HTableItemInfoList.size(); index++) {
            HTableItemInfo HTableItemInfo = HTableItemInfoList.get(index);
            if (HTableItemInfo.getItemProbabilityValue() < minSupport) {
                HTableItemInfoList.remove(HTableItemInfo);
            }
        }
    }

    @Deprecated
    private WeightedNode constructConditionalTreeOld(WeightedNode root, String id) {
        int count = root.getChildNodeList().size();
        for (int index = 0; index < count; index++) {
            WeightedNode child = root.getChildNodeList().get(index);
            WeightedNode foundNode = constructConditionalTreeNotRemovingChild(child, id);
            if (foundNode == null) {
                root.removeChildNode(child);
                index--;
                count--;
            }
        }
        return root;
    }


    private WeightedNode constructConditionalTreeNotRemovingChild(WeightedNode node, String id) {
        if (node.getId().equalsIgnoreCase(id)) {
            node.setChildNodeList(new ArrayList<WeightedNode>());
            node.getParentNode().setMiningProbability(node.getNodePrefixValue());
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

    private void printBeforeMining(WeightedTree weightedTree) {

        Utils.log(Constant.MULTI_STAR);
        Utils.log(TAG, "Tree For Mining");
        Utils.log(Constant.MULTI_STAR);
        Utils.log(TAG, weightedTree.getTraversedString());
        Utils.log(Constant.MULTI_STAR);
        Utils.log(TAG, "Mining Started");
        Utils.log(Constant.MULTI_STAR);
    }
}
