package com.pricecomparator.backend;

import java.util.List;

public class BasketRequest {

    private String folderPath;
    private List<String> productNames;

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }

    /**
     * Validation method to ensure required fields are populated.
     */
    public boolean isValid() {
        return folderPath != null && !folderPath.isBlank()
                && productNames != null
                && !productNames.isEmpty()
                && productNames.stream().allMatch(name -> name != null && !name.isBlank());
    }
}
