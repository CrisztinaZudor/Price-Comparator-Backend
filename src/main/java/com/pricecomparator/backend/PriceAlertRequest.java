package com.pricecomparator.backend;

import java.util.Map;

public class PriceAlertRequest {
    private String folderPath;
    private Map<String, Double> targetPrices; // productName -> target price

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Map<String, Double> getTargetPrices() {
        return targetPrices;
    }

    public void setTargetPrices(Map<String, Double> targetPrices) {
        this.targetPrices = targetPrices;
    }
}
