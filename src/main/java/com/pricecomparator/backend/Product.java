
package com.pricecomparator.backend;

import com.opencsv.bean.CsvBindByName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Product {

    @CsvBindByName(column = "product_id")
    private String productId;

    @CsvBindByName(column = "product_name")
    private String productName;

    @CsvBindByName(column = "product_category")
    private String productCategory;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "package_quantity")
    private double packageQuantity;

    @CsvBindByName(column = "package_unit")
    private String packageUnit;

    @CsvBindByName(column = "price")
    private double price;

    @CsvBindByName(column = "currency")
    private String currency;

    // Optional extra fields
    private String storeName;
    private String date;

    @JsonProperty("valuePerUnit")
    public double getValuePerUnit() {
        return packageQuantity > 0 ? price / packageQuantity : Double.MAX_VALUE;
    }

    public void validate() {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID is missing.");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name is missing.");
        }
        if (packageQuantity <= 0) {
            throw new IllegalArgumentException("Invalid package quantity: " + packageQuantity);
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + price);
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is missing.");
        }
    }
}
