package com.pricecomparator.backend;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvLoaderService {

    public List<Product> loadProducts(String filePath, String storeName, String date) throws IOException {
        List<Product> products = new CsvToBeanBuilder<Product>(new FileReader(filePath))
                .withType(Product.class)
                .withIgnoreLeadingWhiteSpace(true)
                .withSeparator(';')
                .build()
                .parse();
        for (Product p : products) {
            p.setStoreName(storeName);
            p.setDate(date);
        }
        return products;
    }

    public List<Product> loadAllProductsFromFolder(String folderPath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv") && !name.contains("discount"));

        List<Product> allProducts = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                String filename = file.getName().replace(".csv", "");
                String[] parts = filename.split("_");
                String store = parts[0];
                String date = parts[1];

                List<Product> products = loadProducts(file.getAbsolutePath(), store, date);
                allProducts.addAll(products);
            }
        }

        return allProducts;
    }

    public List<Discount> loadAllDiscountsFromFolder(String folderPath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv") && name.contains("discount"));

        List<Discount> allDiscounts = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                String filename = file.getName().replace(".csv", "");
                String store = filename.split("_")[0];

                List<Discount> discounts = new CsvToBeanBuilder<Discount>(new FileReader(file.getAbsolutePath()))
                        .withType(Discount.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .withSeparator(';')  // <- Important!
                        .build()
                        .parse();

                for (Discount d : discounts) {
                    d.setStoreName(store);
                }

                allDiscounts.addAll(discounts);
            }
        }
        return allDiscounts;
    }

}
