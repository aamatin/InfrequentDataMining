package com.mbzshajib.utility.dataset;

import com.mbzshajib.utility.random.GaussianRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CSEDU on 11/15/2015.
 */
public class RandomGenerator {
    private static final double MEAN = 50;
    private static final double VARIANCE = MEAN / Math.PI;

    public static List<Integer> generateRandom(int coreSize, int dataSetCount) {
        GaussianRandom gaussianRandom = new GaussianRandom(MEAN, VARIANCE);
        int totalCount = coreSize * dataSetCount;
        List<Integer> randoms = gaussianRandom.getGaussianRandoms(totalCount);
        return randoms;
    }

    public static List<Integer> generateRandom(int count) {
        GaussianRandom gaussianRandom = new GaussianRandom(MEAN, VARIANCE);
        List<Integer> randoms = gaussianRandom.getGaussianRandoms(count);
        return randoms;
    }
}
