package com.pricecomparator.backend;

import java.util.Map;

public class PriceAlertRequest {

    private String folderPath;
    private Map<String, Double> targetPrices;

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

    /**
     * Basic validation to check for null/empty fields and valid target prices.
     */
    public boolean isValid() {
        return folderPath != null && !folderPath.isBlank()
                && targetPrices != null
                && !targetPrices.isEmpty()
                && targetPrices.entrySet().stream()
                .allMatch(e -> e.getKey() != null && !e.getKey().isBlank() && e.getValue() != null && e.getValue() >= 0);
    }
}
