package com.mbzshajib.mining.processor.uncertain.model;
 /**
 * *****************************************************************
 * Copyright  2015.
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email  - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/30/2015
 * @time: 7:17 PM
 * ****************************************************************
 */    

public class WInputData {
  private String id ;
     private int itemWeight;
     private int maxValue;

  public WInputData(String id, int itemWeight) {
   this.id = id;
   this.itemWeight = itemWeight;
  }

  public String getId() {
   return id;
  }

  public void setId(String id) {
   this.id = id;
  }

  public int getMaxValue() {
   return maxValue;
  }

  public void setMaxValue(int maxValue) {
   this.maxValue = maxValue;
  }

  public int getItemWeight() {
   return itemWeight;
  }

  public void setItemWeight(int itemWeight) {
   this.itemWeight = itemWeight;
  }
 }
