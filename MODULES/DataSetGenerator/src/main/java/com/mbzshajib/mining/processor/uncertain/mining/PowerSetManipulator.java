package com.mbzshajib.mining.processor.uncertain.mining;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by CSEDU on 11/7/2015.
 */
public class PowerSetManipulator {
    public static void main(String[] args) {

        //construct the set S = {a,b,c}
        String set[] = {"a", "b", "c"};

        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        //form the power set
//        LinkedHashSet myPowerSet = powerset(list);

        //display the power set
//        System.out.println(myPowerSet.toString());

    }

    /**
     * Returns the power set from the given set by using a binary counter
     * Example: S = {a,b,c}
     * P(S) = {[], [c], [b], [b, c], [a], [a, c], [a, b], [a, b, c]}
     * @param set String[]
     * @return LinkedHashSet
     */
    public LinkedHashSet powerset(List<String> set) {

        //create the empty power set
        LinkedHashSet power = new LinkedHashSet();

        //get the number of elements in the set
        int elements = set.size();

        //the number of members of a power set is 2^n
        int powerElements = (int) Math.pow(2,elements);

        //run a binary counter for the number of power elements
        for (int i = 0; i < powerElements; i++) {

            //convert the binary number to a string containing n digits
            String binary = intToBinary(i, elements);

            //create a new set
            LinkedHashSet innerSet = new LinkedHashSet();

            //convert each digit in the current binary number to the corresponding element
            //in the given set
            for (int j = 0; j < binary.length(); j++) {
                if (binary.charAt(j) == '1')
                    innerSet.add(set.get(j));
            }

            //add the new set to the power set
            power.add(innerSet);

        }

        return power;
    }

    /**
     * Converts the given integer to a String representing a binary number
     * with the specified number of digits
     * For example when using 4 digits the binary 1 is 0001
     * @param binary int
     * @param digits int
     * @return String
     */
    private static String intToBinary(int binary, int digits) {

        String temp = Integer.toBinaryString(binary);
        int foundDigits = temp.length();
        String returner = temp;
        for (int i = foundDigits; i < digits; i++) {
            returner = "0" + returner;
        }

        return returner;
    }
}
