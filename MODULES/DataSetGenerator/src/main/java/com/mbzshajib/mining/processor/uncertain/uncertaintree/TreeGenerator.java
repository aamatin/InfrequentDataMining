package com.mbzshajib.mining.processor.uncertain.uncertaintree;

import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.processor.uncertain.mining.UncertainStreamMineOutput;
import com.mbzshajib.mining.processor.uncertain.model.WInputData;
import com.mbzshajib.mining.processor.uncertain.model.WeightedTree;
import com.mbzshajib.utility.model.ProcessingError;
import com.mbzshajib.utility.model.Processor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/20/2015
 * @time: 11:03 PM
 * ****************************************************************
 */

public class TreeGenerator implements Processor<TreeConstructionInput, TreeConstructionOutput> {
    private static final String TAG = TreeGenerator.class.getCanonicalName();
    private BufferedReader bufferedReader;
    private TreeConstructionInput treeConstructionInput;
    private long endTime;
    private long globalStartTime;
    private long startTime;
    private List<List<WInputData>> windowTransactionList;


    public TreeGenerator() {
        windowTransactionList = new ArrayList<>();
    }

    @Override
    public TreeConstructionOutput process(TreeConstructionInput treeConstructionInput) throws ProcessingError {
        int windowSize = treeConstructionInput.getWindowSize() * treeConstructionInput.getFrameSize();
        this.treeConstructionInput = treeConstructionInput;
        WeightedTree weightedTree = null;
        int windowStartTransaction = 1;
        int windowEndTransaction;
        try {
            initialize();
            weightedTree = new WeightedTree(treeConstructionInput.getFrameSize(), treeConstructionInput.getWindowSize());
            windowEndTransaction = windowSize;
            for (int frameNo = 0; frameNo < treeConstructionInput.getWindowSize(); frameNo++) {
                for (int i = 0; i < treeConstructionInput.getFrameSize(); i++) {
                    List<WInputData> nodes = getTransaction();
                    windowTransactionList.add(nodes);
                    weightedTree.addTransactionToTree(nodes, frameNo);
                }
            }
            UncertainStreamMineOutput uncertainStreamMineOutput = treeConstructionInput.getWindowCompletionCallback().sendUpdate(createUpdate(weightedTree));
            for (int i = 0; i < treeConstructionInput.getFrameSize(); i++) {
                windowTransactionList.remove(0);
            }

            weightedTree.slideWindowAndUpdateTree();
            List<WInputData> nodes = null;
            int frameCounter = 0;
            while (!(nodes = getTransaction()).isEmpty()) {
                if (!(frameCounter < treeConstructionInput.getFrameSize())) {
                    frameCounter = 0;
                    treeConstructionInput.getWindowCompletionCallback().sendUpdate(createUpdate(weightedTree));
                    for (int i = 0; i < treeConstructionInput.getFrameSize(); i++) {
                        windowTransactionList.remove(0);
                    }
                    weightedTree.slideWindowAndUpdateTree();
                }
                windowTransactionList.add(nodes);
                weightedTree.addTransactionToTree(nodes, treeConstructionInput.getWindowSize() - 1);
                frameCounter++;

            }
            finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DataNotValidException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createUpdate(weightedTree);
    }


    private TreeConstructionOutput createUpdate(WeightedTree weightedTree) {
        TreeConstructionOutput treeConstructionOutput = new TreeConstructionOutput();
        treeConstructionOutput.setStartTime(startTime);
        startTime = System.currentTimeMillis();
        treeConstructionOutput.setEndTime(System.currentTimeMillis());
        try {
            treeConstructionOutput.setWeightedTree(weightedTree.copy());
        } catch (DataNotValidException e) {
            e.printStackTrace();
        }
        treeConstructionOutput.setWindowTransactionList(windowTransactionList);
        return treeConstructionOutput;
    }

    private void finish() {
        endTime = System.currentTimeMillis();
    }


    private void initialize() throws ProcessingError, FileNotFoundException, DataNotValidException {
        globalStartTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        bufferedReader = new BufferedReader(new FileReader(new File(treeConstructionInput.getInputFilePath())));
    }


    private List<WInputData> getTransaction() throws IOException, DataNotValidException {
        String line = bufferedReader.readLine();
        if (line == null) {
            bufferedReader.close();
            return Collections.emptyList();
        }

        List<WInputData> uiInputList = new ArrayList<WInputData>();

        String[] transactionItems = line.split(" ");
        for (int i = 0; i < transactionItems.length; i++) {
            String[] tmp = transactionItems[i].split("-");
            String id = tmp[0];
            int weight = Integer.parseInt(tmp[1]);
            WInputData inputData = new WInputData(id, weight);
            uiInputList.add(inputData);
        }
        int max = findMax(uiInputList);
        updateMax(uiInputList, max);
        removeEmpty(uiInputList);
        return uiInputList;
    }

    private void removeEmpty(List<WInputData> uiInputList) {
        for (int i = 0; i < uiInputList.size(); i++) {
            WInputData data = uiInputList.get(i);
            if (data.getItemWeight() == 0) {
                uiInputList.remove(data);
            }
        }
    }

    private void updateMax(List<WInputData> uiInputList, int max) {
        for (WInputData data : uiInputList) {
            data.setMaxValue(max);
        }
    }

    private int findMax(List<WInputData> uiInputList) {
        int result = 0;
        for (WInputData data : uiInputList) {
//            if (data.getItemWeight() > result) {
//                result = data.getItemWeight();
//            }
            result += data.getItemWeight();
        }
        return result;
    }
}
