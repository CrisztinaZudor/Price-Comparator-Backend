package com.pricecomparator.backend;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Discount {

    @CsvBindByName(column = "product_id")
    private String productId;

    @CsvBindByName(column = "product_name")
    private String productName;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "package_quantity")
    private double packageQuantity;

    @CsvBindByName(column = "package_unit")
    private String packageUnit;

    @CsvBindByName(column = "product_category")
    private String productCategory;

    @CsvBindByName(column = "from_date")
    private String fromDate;

    @CsvBindByName(column = "to_date")
    private String toDate;

    @CsvBindByName(column = "percentage_of_discount")
    private int percentageOfDiscount;

    // Optional extra field to help track which store it came from
    private String storeName;
}
