package com.mbzshajib.utility.dataset;

/**
 * Created by CSEDU on 11/15/2015.
 */
public class DataSetInput {
    private String dataSetName;
    private int coreSize;
    private int dataSetCount;
    private boolean isWeighted;

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getDataSetCount() {
        return dataSetCount;
    }

    public void setDataSetCount(int dataSetCount) {
        this.dataSetCount = dataSetCount;
    }

    public boolean isWeighted() {
        return isWeighted;
    }

    public void setIsWeighted(boolean isWeighted) {
        this.isWeighted = isWeighted;
    }
}
