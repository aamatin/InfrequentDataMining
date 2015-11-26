package com.mbzshajib.utility.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 * Md. Yasser Arafat (mdyasserarafat1@gmail.com)
 * Redistribution or Using any part of source code or binary
 * can not be done without permission of owner.
 * *****************************************************************
 * author: Md. Yasser Arafat
 * email: mdyasserarafat1@gmail.com
 * date: 11/26/2015
 * time: 10:39 PM
 * ****************************************************************
 */
public class DataSetgeneratorT10 {

    // TODO : change path for different dataset
    // Keep the file in INPUT
    // set set file name here
    // Run !!!!!!!!!!!
    // delete resultdata.dat if exist
    // See the output in resultdata.dat
    private static final String path = "INPUT/retail.dat";

    private static int index = 0;
    public static void main(String[] args) throws IOException {
        FileReaderUtil fileReaderUtil = new FileReaderUtil();
        List<List<String>> dataList = fileReaderUtil.getDataList(path);
        int count = fileReaderUtil.getDataCount(dataList);

//        DataSetInput input = getDataSetInput();
        List<Integer> randomList = RandomGenerator.generateRandom(count);

        Collections.shuffle(randomList);
        List<List<WData>> dataSet = new ArrayList<List<WData>>();
        for (int transactionNo = 0; transactionNo < dataList.size(); transactionNo++) {
            List<String> itemListForTransaction = dataList.get(transactionNo);
            List<WData> transctionRow = getWDataForTransaction(transactionNo,itemListForTransaction.size(), itemListForTransaction, randomList);
            dataSet.add(transctionRow);
        }
        for (List<WData> transaction : dataSet) {
            String string = printTransactionString(transaction);
            writeSingleLine("result", "data.dat", string);

        }
    }

    private static List<WData> getWDataForTransaction(int transactionNo, int coreSize, List<String> itemListForTransaction, List<Integer> randomList) {
        List<WData> result = new ArrayList<>();
        for (int i =0;i<itemListForTransaction.size();i++) {
            String id = itemListForTransaction.get(i);
            WData wData = new WData();
            wData.setItemId(id);
            int randomIndex = index++;
            wData.setWeight("" + randomList.get(randomIndex));
            result.add(wData);
        }
        return result;
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
//        System.out.print(builder);
        return builder.toString();
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
