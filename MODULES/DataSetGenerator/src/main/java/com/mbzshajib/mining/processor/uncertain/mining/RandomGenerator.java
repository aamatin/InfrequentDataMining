package com.mbzshajib.mining.processor.uncertain.mining;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * *****************************************************************
 * Copyright  2015.
 * Md. Yasser Arafat (mdyasserarafat1@gmail.com)
 * Redistribution or Using any part of source code or binary
 * can not be done without permission of owner.
 * *****************************************************************
 * author: Md. Yasser Arafat
 * email: mdyasserarafat1@gmail.com
 * date: 11/28/2015
 * time: 9:04 PM
 * ****************************************************************
 */
public class RandomGenerator {
    public static List<Double> infrequentRandomList;
    public static List<Double> timeRandomList;

    public RandomGenerator() {
        infrequentRandomList = new ArrayList<Double>();
        infrequentRandomList.add(18.0);
        infrequentRandomList.add(19.0);
        infrequentRandomList.add(20.0);
        infrequentRandomList.add(21.0);
        infrequentRandomList.add(22.0);
        infrequentRandomList.add(23.0);
        infrequentRandomList.add(24.0);
        infrequentRandomList.add(25.0);
        infrequentRandomList.add(26.0);
        infrequentRandomList.add(27.0);

        timeRandomList = new ArrayList<Double>();
        timeRandomList.add(350.0);
        timeRandomList.add(382.0);
        timeRandomList.add(384.0);
        timeRandomList.add(386.0);
        timeRandomList.add(388.0);
        timeRandomList.add(390.0);
        timeRandomList.add(400.0);
        timeRandomList.add(395.0);
        timeRandomList.add(375.0);
        timeRandomList.add(405.0);
    }

    public static void main(String[] args) {
        RandomGenerator obj = new RandomGenerator();
        for(int i = 0; i < 10; i++){
            System.out.println(obj.getRandomList(infrequentRandomList));
        }

    }

    public double getRandomList(List<Double> list) {

        //0-4
        int index = ThreadLocalRandom.current().nextInt(list.size());
        System.out.println("\nIndex :" + index );
        return list.get(index);

    }
}
