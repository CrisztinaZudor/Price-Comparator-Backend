package com.pricecomparator.backend;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class CsvLoaderService {

    public List<Product> loadProducts(String filePath, String storeName, String date) throws IOException {
        List<Product> products = new CsvToBeanBuilder<Product>(new FileReader(filePath))
                .withType(Product.class)
                .withIgnoreLeadingWhiteSpace(true)
                .withSeparator(';') // VERY IMPORTANT: semicolon
                .build()
                .parse();
        for (Product p : products) {
            p.setStoreName(storeName);
            p.setDate(date);
        }
        return products;
    }
}
