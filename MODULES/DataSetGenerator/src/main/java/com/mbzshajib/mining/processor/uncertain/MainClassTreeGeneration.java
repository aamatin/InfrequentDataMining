package com.mbzshajib.mining.processor.uncertain;

import com.mbzshajib.mining.Initializer;
import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.processor.uncertain.mining.UncertainStreamMineInput;
import com.mbzshajib.mining.processor.uncertain.mining.UncertainStreamMineOutput;
import com.mbzshajib.mining.processor.uncertain.mining.UncertainStreamMiner;
import com.mbzshajib.mining.processor.uncertain.model.WeightedTree;
import com.mbzshajib.mining.processor.uncertain.uncertaintree.TreeConstructionInput;
import com.mbzshajib.mining.processor.uncertain.uncertaintree.TreeConstructionOutput;
import com.mbzshajib.mining.processor.uncertain.uncertaintree.TreeGenerator;
import com.mbzshajib.mining.processor.uncertain.uncertaintree.WindowCompletionCallback;
import com.mbzshajib.utility.model.ProcessingError;

import java.io.IOException;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/20/2015
 * @time: 11:02 PM
 * ****************************************************************
 */

public class MainClassTreeGeneration {
    public static final String TAG = MainClassTreeGeneration.class.getCanonicalName();

    private static final int MIN_SUP = -500;
    private static int windowNumber = 1;

    public static void main(String[] args) throws ProcessingError, IOException, DataNotValidException {
        Initializer.initialize();
        TreeConstructionInput treeConstructionInput = getTreeInput();
        TreeGenerator processor = new TreeGenerator();
        TreeConstructionOutput treeConstructionOutput = processor.process(treeConstructionInput);
        WeightedTree tree = treeConstructionOutput.getWeightedTree();


    }

    private static UncertainStreamMineInput getMiningInput(TreeConstructionOutput treeConstructionOutput) {
        UncertainStreamMineInput result = new UncertainStreamMineInput();
        result.setMaxSupport(MIN_SUP);
        result.setWeightedTree(treeConstructionOutput.getWeightedTree());
        return result;
    }

    private static TreeConstructionInput getTreeInput() {
        final TreeConstructionInput treeConstructionInput = new TreeConstructionInput();
        treeConstructionInput.setInputFilePath("INPUT/resultdata.dat");
        treeConstructionInput.setFrameSize(300);
        treeConstructionInput.setWindowSize(2);
        treeConstructionInput.setWindowCompletionCallback(new WindowCompletionCallback() {
            @Override
            public UncertainStreamMineOutput sendUpdate(TreeConstructionOutput treeConstructionOutput) throws ProcessingError {
                windowNumber++;
                UncertainStreamMineInput uncertainStreamMineInput = getMiningInput(treeConstructionOutput);
                UncertainStreamMiner uncertainStreamMiner = new UncertainStreamMiner();
                UncertainStreamMineOutput mineOutput = uncertainStreamMiner.process(uncertainStreamMineInput);
                mineOutput.setMaxSupport(uncertainStreamMineInput.getMaxSupport());
                return mineOutput;
            }
        });
        return treeConstructionInput;
    }


}
