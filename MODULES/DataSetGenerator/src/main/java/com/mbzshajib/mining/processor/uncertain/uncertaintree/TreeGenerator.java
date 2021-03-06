package com.mbzshajib.mining.processor.uncertain.uncertaintree;

import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.processor.uncertain.model.WInputData;
import com.mbzshajib.mining.processor.uncertain.model.UncertainTree;
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

    @Override
    public TreeConstructionOutput process(TreeConstructionInput treeConstructionInput) throws ProcessingError {
        this.treeConstructionInput = treeConstructionInput;
        UncertainTree uncertainTree = null;
        try {
            initialize();
            uncertainTree = new UncertainTree(treeConstructionInput.getFrameSize(), treeConstructionInput.getWindowSize());
            for (int frameNo = 0; frameNo < treeConstructionInput.getWindowSize(); frameNo++) {
                for (int i = 0; i < treeConstructionInput.getFrameSize(); i++) {
                    List<WInputData> nodes = getTransaction();
                    uncertainTree.addTransactionToTree(nodes, frameNo);
                }
            }
            treeConstructionInput.getWindowCompletionCallback().sendUpdate(createUpdate(uncertainTree));
            uncertainTree.slideWindowAndUpdateTree();
            List<WInputData> nodes = null;
            int frameCounter = 0;
            while (!(nodes = getTransaction()).isEmpty()) {
                if (!(frameCounter < treeConstructionInput.getWindowSize())) {
                    frameCounter = 0;
                    treeConstructionInput.getWindowCompletionCallback().sendUpdate(createUpdate(uncertainTree));
                    uncertainTree.slideWindowAndUpdateTree();
                }
                uncertainTree.addTransactionToTree(nodes, treeConstructionInput.getWindowSize() - 1);
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

        return createUpdate(uncertainTree);
    }

    private TreeConstructionOutput createUpdate(UncertainTree uncertainTree) {
        TreeConstructionOutput treeConstructionOutput = new TreeConstructionOutput();
        treeConstructionOutput.setStartTime(startTime);
        startTime = System.currentTimeMillis();
        treeConstructionOutput.setEndTime(System.currentTimeMillis());
        try {
            treeConstructionOutput.setUncertainTree(uncertainTree.copy());
        } catch (DataNotValidException e) {
            e.printStackTrace();
        }
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
            if (data.getItemWeight() > result) {
                result = data.getItemWeight();
            }
        }
        return result;
    }
}
