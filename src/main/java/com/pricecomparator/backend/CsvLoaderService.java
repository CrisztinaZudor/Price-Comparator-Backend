package com.pricecomparator.backend;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvLoaderService {

    public List<Product> loadProducts(String filePath, String storeName, String date) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            throw new FileNotFoundException("File not found or not readable: " + filePath);
        }

        List<Product> products = new CsvToBeanBuilder<Product>(new FileReader(file))
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
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("Invalid folder path: " + folderPath);
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv") && !name.contains("discount"));
        if (files == null || files.length == 0) {
            throw new IOException("No product CSV files found in folder: " + folderPath);
        }

        List<Product> allProducts = new ArrayList<>();
        for (File file : files) {
            String filename = file.getName().replace(".csv", "");
            String[] parts = filename.split("_");
            if (parts.length < 2) continue;

            String store = parts[0];
            String date = parts[1];

            allProducts.addAll(loadProducts(file.getAbsolutePath(), store, date));
        }
        return allProducts;
    }

    public List<Discount> loadAllDiscountsFromFolder(String folderPath) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("Invalid folder path: " + folderPath);
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv") && name.contains("discount"));
        if (files == null || files.length == 0) {
            throw new IOException("No discount CSV files found in folder: " + folderPath);
        }

        List<Discount> allDiscounts = new ArrayList<>();
        for (File file : files) {
            String filename = file.getName().replace(".csv", "");
            String[] parts = filename.split("_");
            if (parts.length < 1) continue;

            String store = parts[0];

            List<Discount> discounts = new CsvToBeanBuilder<Discount>(new FileReader(file))
                    .withType(Discount.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(';')
                    .build()
                    .parse();

            for (Discount d : discounts) {
                d.setStoreName(store);
            }

            allDiscounts.addAll(discounts);
        }

        return allDiscounts;
    }
}