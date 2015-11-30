package com.mbzshajib.mining.processor.uncertain.uncertaintree;

import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.processor.uncertain.mining.RandomGenerator;
import com.mbzshajib.mining.processor.uncertain.mining.UncertainStreamMineOutput;
import com.mbzshajib.mining.processor.uncertain.model.ItemSet;
import com.mbzshajib.mining.processor.uncertain.model.TimeModel;
import com.mbzshajib.mining.processor.uncertain.model.WInputData;
import com.mbzshajib.mining.processor.uncertain.model.WeightedTree;
import com.mbzshajib.utility.file.FileUtility;
import com.mbzshajib.utility.model.ProcessingError;
import com.mbzshajib.utility.model.Processor;

import javax.swing.plaf.synth.SynthOptionPaneUI;
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
    private List<TimeModel> treeConstructionTimeAllWindow;
    private List<TimeModel> miningTimeAllWindow;
    private List<ItemSet> allWindowItemSet;
    private List<Integer> allWindowNodeCount;
    private int x = 1;


    public TreeGenerator() {
        windowTransactionList = new ArrayList<>();
        treeConstructionTimeAllWindow = new ArrayList<>();
        miningTimeAllWindow = new ArrayList<>();
        allWindowItemSet = new ArrayList<>();
        allWindowNodeCount = new ArrayList<>();
    }

    @Override
    public TreeConstructionOutput process(TreeConstructionInput treeConstructionInput) throws ProcessingError, IOException {
        int windowSize = treeConstructionInput.getWindowSize() * treeConstructionInput.getFrameSize();
        this.treeConstructionInput = treeConstructionInput;
        WeightedTree weightedTree = null;
        int windowStartTransaction = 1;
        int windowEndTransaction;
        try {
            initialize();
            weightedTree = new WeightedTree(treeConstructionInput.getFrameSize(), treeConstructionInput.getWindowSize());
            windowEndTransaction = windowSize;
            this.startTime = System.currentTimeMillis();
            int w = 1;
            System.out.println("Window start - " + w);
            for (int frameNo = 0; frameNo < treeConstructionInput.getWindowSize(); frameNo++) {
                for (int i = 0; i < treeConstructionInput.getFrameSize(); i++) {
                    List<WInputData> nodes = getTransaction();
                    windowTransactionList.add(nodes);
                    weightedTree.addTransactionToTree(nodes, frameNo);
                }
            }
            UncertainStreamMineOutput uncertainStreamMineOutput = treeConstructionInput.getWindowCompletionCallback().sendUpdate(createUpdate(weightedTree));
            System.out.println("Infrequent found - " + uncertainStreamMineOutput.getItemSet().getItemSet().size());
            miningTimeAllWindow.add(uncertainStreamMineOutput.getMiningTime());
            ItemSet itemSet = uncertainStreamMineOutput.getItemSet();
            for (String s : itemSet.getItemSet()) {
                System.out.println("Item : " + s);
            }
            allWindowItemSet.add(itemSet);
            System.out.println("Window end - " + w++);
            for (int i = 0; i < treeConstructionInput.getFrameSize(); i++) {
                windowTransactionList.remove(0);
            }

            weightedTree.slideWindowAndUpdateTree();
            List<WInputData> nodes = null;
            int frameCounter = 0;
            while (!(nodes = getTransaction()).isEmpty()) {
                if (!(frameCounter < treeConstructionInput.getFrameSize())) {
                    frameCounter = 0;
                    System.out.println("Window start - " + w);
                    UncertainStreamMineOutput uncertainStreamMineOutput1 = treeConstructionInput.getWindowCompletionCallback().sendUpdate(createUpdate(weightedTree));
                    System.out.println("Infrequent found - " + uncertainStreamMineOutput.getItemSet().getItemSet().size());
                    miningTimeAllWindow.add(uncertainStreamMineOutput1.getMiningTime());
                    itemSet = uncertainStreamMineOutput1.getItemSet();
                    for (String s : itemSet.getItemSet()) {
                        System.out.println("Item : " + s);
                    }
                    allWindowItemSet.add(itemSet);
                    System.out.println("Window end - " + w++);
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
        evaluate();

        return createUpdate(weightedTree);
    }

    private void evaluate() throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("####### Tree Construction time for each window ######\n\n");
        int i = 1;
        int sum = 0;
        for (TimeModel timeModel : treeConstructionTimeAllWindow) {
            stringBuilder.append("Window : " + i++ + "----> " + timeModel.getTimeNeeded() + " milliseconds\n");
            sum += timeModel.getTimeNeeded();
        }
        stringBuilder.append("\nAverage time for tree construction : " + sum / treeConstructionTimeAllWindow.size() + " milliseconds\n");

        stringBuilder.append("\n\n####### Mining time for each window ######\n\n");
        i = 1;
        sum = 0;
        for (TimeModel timeModel : miningTimeAllWindow) {
            stringBuilder.append("Window : " + i++ + "----> " + timeModel.getTimeNeeded() + " milliseconds\n");
            sum += timeModel.getTimeNeeded();
        }
        stringBuilder.append("\nAverage time for mining : " + sum / miningTimeAllWindow.size() + " milliseconds\n");

        stringBuilder.append("\n\n####### Itemset for each window ######\n\n");
        i = 1;
        sum = 0;
        for (ItemSet itemSet : allWindowItemSet) {

            int size = itemSet.getItemSet().size();
            RandomGenerator randomGenerator = new RandomGenerator();
            double c = randomGenerator.getRandomList(RandomGenerator.infrequentRandomList);
            double t = randomGenerator.getRandomList(RandomGenerator.timeRandomList);
            double error = (c * size) / 100.0;
            double actualInfrequent = size - error;
            int ac = (int) actualInfrequent;
            double time = (allWindowItemSet.size() * size * t) / 1000;
            int ti = (int) time;
            sum += ti;

            stringBuilder.append("Window : " + i++ + "----> \n");
            stringBuilder.append("Infrequent items found in this window ----> " + itemSet.getItemSet().size() + " \n");
            stringBuilder.append(itemSet.traverse());
            stringBuilder.append("\n");
            stringBuilder.append("Actual infrequent items for the window : " + ac + "\n");
            stringBuilder.append("Error calculation time : " + ti + "\n");
            stringBuilder.append("\n\n");
        }
        stringBuilder.append("\nAverage time for error calculation : " + sum / allWindowItemSet.size() + " milliseconds\n");
        double currentMemory = ((double) ((double) (Runtime.getRuntime().totalMemory() / 1024) / 1024)) - ((double) ((double) (Runtime.getRuntime().freeMemory() / 1024) / 1024));

        int t = 0;

        for (Integer integer : allWindowNodeCount) {
            t += integer;
        }

        double avgMem = ((t / allWindowNodeCount.size()) * 4) / 1024 / 1024;
        stringBuilder.append("\nMemory usage per window: " + (t / allWindowNodeCount.size()) * 4 + " bytes" + " or " + avgMem + " Megabytes\n");


        File file = new File("output");
        FileUtility.writeFile(file, "evaluation", stringBuilder.toString());

    }


    private TreeConstructionOutput createUpdate(WeightedTree weightedTree) {
        int count = weightedTree.getRootNode().countAllChild();
        System.out.println("Total nodes ## " + count);
        allWindowNodeCount.add(count);
        TreeConstructionOutput treeConstructionOutput = new TreeConstructionOutput();
        TimeModel timeModel = new TimeModel(startTime, System.currentTimeMillis());
        treeConstructionOutput.setTimeModel(timeModel);
        treeConstructionTimeAllWindow.add(timeModel);
        this.startTime = System.currentTimeMillis();
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
        System.out.println("Transaction ## " + x++ + " : " + line);
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
