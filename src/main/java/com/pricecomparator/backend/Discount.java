package com.pricecomparator.backend;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Data;

import java.time.LocalDate;

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

    @CsvCustomBindByName(column = "from_date", converter = LocalDateConverter.class)
    private LocalDate fromDate;

    @CsvCustomBindByName(column = "to_date", converter = LocalDateConverter.class)
    private LocalDate toDate;

    @CsvBindByName(column = "percentage_of_discount")
    private int percentageOfDiscount;

    // Optional field to track store
    private String storeName;

    public boolean isValid() {
        return productId != null && !productId.isBlank()
                && productName != null && !productName.isBlank()
                && packageQuantity >= 0
                && percentageOfDiscount >= 0 && percentageOfDiscount <= 100
                && fromDate != null && toDate != null
                && !fromDate.isAfter(toDate);
    }
}
