package com.mbzshajib.mining.processor.uncertain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CSEDU on 10/10/2015.
 */
public class InfrequentItem {
    private List<String> inFrequentSet;


        public InfrequentItem() {
            inFrequentSet =  new ArrayList<String>();
        }

        public InfrequentItem(InfrequentItem infrequentItem) {
            this.inFrequentSet = new ArrayList<String>();
            for (String string : infrequentItem.getInfrequentSet()) {
                this.inFrequentSet.add(new String(string));
            }
        }

        public void addInfrequentItem(String itemId) {
            boolean found = false;
            for (String id : inFrequentSet) {
                if (id.equals(itemId)) {
                    found = true;
                    break;
                }


            }
            if (!found) inFrequentSet.add(new String(itemId));
        }



        public String traverse() {
            StringBuilder builder = new StringBuilder();
            builder.append("Item: ");
            for (String string : inFrequentSet) {
                builder.append("(")
                        .append(string)
                        .append(")");
            }
            return builder.toString();
        }

    public List<String> getInfrequentSet() {
        return inFrequentSet;
    }
}


