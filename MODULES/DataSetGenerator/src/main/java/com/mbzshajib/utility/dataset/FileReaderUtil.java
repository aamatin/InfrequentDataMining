package com.mbzshajib.utility.dataset;

import com.mbzshajib.mining.processor.uncertain.model.WInputData;

import java.io.*;
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
 * time: 10:00 PM
 * ****************************************************************
 */
public class FileReaderUtil {
    public static void main(String[] args) {
        List<List<String>> dataList = new FileReaderUtil().getDataList("INPUT/T40I10D100K.dat");
        System.out.println(dataList.size());

        for (List<String> strings : dataList) {
            for (String string : strings) {
                System.out.print(string + " ");
            }
            System.out.println("\n");
        }
    }

    public List<List<String>> getDataList(String path){
        BufferedReader bufferedReader = null;
        List<List<String>>  dataL = new ArrayList<>();
        try {
            List<String> dataList = null;
            bufferedReader = new BufferedReader(new FileReader(new File(path)));

            String line = bufferedReader.readLine();
            while(line!=null){
                dataList = new ArrayList<String>();

                String[] dataListStr = line.split(" ");
                for (int i = 0; i < dataListStr.length; i++) {
                    dataList.add(dataListStr[i]);
                }

                dataL.add(dataList);
                line = bufferedReader.readLine();


            }

            if (line == null) {
                bufferedReader.close();
//            return Collections.emptyList();
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataL;
    }

    public int getDataCount(List<List<String>> dataList) {
        int count = 0;
        for (List<String> strings : dataList) {
            for (String string : strings) {
                count++;
            }

        }
        return count;
    }
}
