package com.mbzshajib.utility.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by CSEDU on 11/15/2015.
 */
public class DataSetGenerator {
    public static void main(String[] args) throws IOException {
               DataSetInput input = getDataSetInput();
        List<Integer> randomList = RandomGenerator.generateRandom(input.getCoreSize(), input.getDataSetCount());

        Collections.shuffle(randomList);
        List<List<WData>> dataSet = new ArrayList<List<WData>>();
        for (int transactionNo = 0; transactionNo < input.getDataSetCount(); transactionNo++) {
            List<String> itemListForTransaction = getTransactionData(input.getCoreSize());
            List<WData> transctionRow = getWDataForTransaction(transactionNo, input.getCoreSize(), itemListForTransaction, randomList);
            dataSet.add(transctionRow);
        }
        for (List<WData> transaction : dataSet) {
            String string = printTransactionString(transaction);
            writeSingleLine("result", "data.dat", string);

        }
    }

    private static String printTransactionString(List<WData> transaction) {
        StringBuilder builder = new StringBuilder();
        for (WData data : transaction) {
            builder.append(data.getItemId())
                    .append("-")
                    .append(data.getWeight())
                    .append(" ");
        }
        builder.append("\n");
        System.out.print(builder);
        return builder.toString();
    }

    private static List<WData> getWDataForTransaction(int transactionNo, int coreSize, List<String> itemListForTransaction, List<Integer> randomList) {
        List<WData> result = new ArrayList<>();
        for (int i =0;i<itemListForTransaction.size();i++) {
            String id = itemListForTransaction.get(i);
            WData wData = new WData();
            wData.setItemId(id);
            int randomIndex = transactionNo*coreSize+i;
            wData.setWeight("" + randomList.get(randomIndex));
            result.add(wData);
        }
        return result;
    }

    private static List<String> getTransactionData(int coreSize) {
        List<String> result = new ArrayList<String>();
        for (int id = 0; id < coreSize; id++) {
            result.add((id + 1) + "");
        }
        return result;
    }

    public static DataSetInput getDataSetInput() {
        DataSetInput dataSetInput = new DataSetInput();
        dataSetInput.setDataSetName("First Test Dataset");
        dataSetInput.setCoreSize(16);
        dataSetInput.setDataSetCount(10);
        dataSetInput.setIsWeighted(true);
        return dataSetInput;
    }
    public static void writeSingleLine(String resultDir, String resultFileName, String message) throws IOException {
        createPath(resultDir);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resultDir + resultFileName, true));
        bufferedWriter.write(message);
        bufferedWriter.close();


    }
    private static void createPath(String path) {
        File dir = new File(path);
        if (!(dir.exists())) {
            dir.mkdirs();
        }
    }


}
