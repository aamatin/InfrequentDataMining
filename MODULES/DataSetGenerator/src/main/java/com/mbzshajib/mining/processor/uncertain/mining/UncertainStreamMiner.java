package com.mbzshajib.mining.processor.uncertain.mining;

import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.processor.uncertain.model.*;
import com.mbzshajib.mining.util.Constant;
import com.mbzshajib.mining.util.Utils;
import com.mbzshajib.utility.model.ProcessingError;
import com.mbzshajib.utility.model.Processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private List<InfrequentItem> inFrequentItemList;

    @Override
    public UncertainStreamMineOutput process(UncertainStreamMineInput uncertainStreamMineInput) throws ProcessingError {
        inFrequentItemList = new ArrayList<InfrequentItem>();
        WeightedTree weightedTree = uncertainStreamMineInput.getWeightedTree();
        System.out.println(weightedTree.getTraversedString());
        int maxSupport = uncertainStreamMineInput.getMaxSupport();
        try {
            startMining(weightedTree, maxSupport);
        } catch (DataNotValidException e) {
            throw new ProcessingError(e);
        }
        printOutput();
        return null;
//        printBeforeMining(weightedTree);
//        WeightedNode rootNode = weightedTree.getRootNode();
//        HeaderTable headerTable = weightedTree.getHeaderTable();
//        List<HTableItemInfo> HTableItemInfoList = headerTable.getHeaderItemInfo();
//        removeDataBelowSupport(minSupport, HTableItemInfoList);
//        sortByPrefix(HTableItemInfoList);
    }

    private void printOutput() {
        StringBuilder builder = new StringBuilder();
        int count = 1;
        builder.append("Total Frequent Items ")
                .append(Constant.TABBED_HASH)
                .append(inFrequentItemList.size())
                .append(Constant.NEW_LINE);
        for (InfrequentItem item : inFrequentItemList) {
            builder.append(count)
                    .append(item.traverse())
                    .append(Constant.NEW_LINE);
        }
        System.out.println(builder.toString());
    }

    private void startMining(WeightedTree weightedTree, int maxSupport) throws DataNotValidException {
        WeightedNode rootNode = weightedTree.getRootNode();
        List<WeightedNode> leafNodeList = new ArrayList<WeightedNode>();
        rootNode.getLeafNodeList(leafNodeList);
        HeaderTable headerTable = weightedTree.getHeaderTable();
        int windowSize = headerTable.getWindowSize();
        List<HTableItemInfo> inFrequentItemsInfo = getInfrequentItemInfoFromHeader(headerTable, maxSupport);
        //TODO:Remove data below support(Probability & Prefix Value).
        removeFrequentData(rootNode, headerTable, inFrequentItemsInfo, windowSize);
        List<HTableItemInfo> listToBeMined = headerTable.getFrequentItemInfoByPrefix(maxSupport);
//        sortByPrefix(listToBeMined);
        //TODO:LOOP
        for (int loopCounter = listToBeMined.size() - 1; loopCounter >= 0; loopCounter--) {
            WeightedNode tmpNode = rootNode.copy();
            InfrequentItem inFrequentItem = new InfrequentItem();
            inFrequentItem.addInfrequentItem(listToBeMined.get(loopCounter).getItemId());
            inFrequentItemList.add(inFrequentItem);
            mine(tmpNode, inFrequentItem, windowSize, listToBeMined.get(loopCounter).getItemId(), maxSupport, false);
        }


    }

    private void mine(WeightedNode root, InfrequentItem item, int windowSize, String id, double minSupport, boolean isFirstIteration) throws DataNotValidException {
        item.addInfrequentItem(id);
        if (isFirstIteration) {
            inFrequentItemList.add(item);
        }
        item = new InfrequentItem(item);

        constructConditionalTreeNotRemovingChild(root, id);
        removeFrequentItemNodes(root, id);
        List<WeightedNode> leafNodeList = new ArrayList<WeightedNode>();
        root.getLeafNodeList(leafNodeList);
        updateMiningProbability(leafNodeList);
        HeaderTable headerTable = generateHeaderTableForCondTree(root, windowSize);
        List<HTableItemInfo> inFrequentItemsInfo = getInfrequentItemInfoForMining(headerTable, minSupport);
        headerTable = removeFrequentData(root, headerTable, inFrequentItemsInfo, windowSize);
        updateInfrequentItem(root, item);
        List<HTableItemInfo> headerItemInfo = headerTable.getHeaderItemInfo();
        for (int i = headerItemInfo.size() - 1; i > 0; i--) {
            item = new InfrequentItem(item);
            mine(root, item, windowSize, headerItemInfo.get(i).getItemId(), minSupport, false);
        }
    }

    private List<HTableItemInfo> getInfrequentItemInfoForMining(HeaderTable headerTable, double minSupport) {
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

// problem with Mining
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

    private HeaderTable removeFrequentData(WeightedNode node, HeaderTable headerTable, List<HTableItemInfo> FrequentItemInfoByPrefix, int windowSize) throws DataNotValidException {
        for (HTableItemInfo hTableItemInfo : FrequentItemInfoByPrefix) {
            removeFrequentItemNodes(node, hTableItemInfo.getItemId());
            headerTable = generateHeaderTableForCondTree(node, windowSize);
        }
        return headerTable;
    }

    private void updateInfrequentItem(WeightedNode rootNode, InfrequentItem item) {

        for (int i = 0; i < rootNode.getChildNodeList().size(); i++) {
            WeightedNode child = rootNode.getChildNodeList().get(i);
            InfrequentItem tmpItem = new InfrequentItem(item);
            tmpItem.addInfrequentItem(child.getId());
            inFrequentItemList.add(tmpItem);
            updateInfrequentItem(child, item);
        }
    }

    private void removeFrequentItemNodes(WeightedNode rootNode, String itemId) {
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
