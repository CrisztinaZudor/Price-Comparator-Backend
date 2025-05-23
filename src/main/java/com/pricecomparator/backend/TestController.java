package com.pricecomparator.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class TestController {

    @Autowired
    private CsvLoaderService csvLoaderService;

    @GetMapping("/load-products")
    public List<Product> loadProducts(
            @RequestParam String filePath,
            @RequestParam String storeName,
            @RequestParam String date
    ) throws IOException {
        return csvLoaderService.loadProducts(filePath, storeName, date);
    }

    @GetMapping("/best-price")
    public List<Product> getBestPriceProducts(@RequestParam String folderPath) throws IOException {
        List<Product> all = csvLoaderService.loadAllProductsFromFolder(folderPath);

        // Group by productId and find lowest price per unit
        return all.stream()
                .collect(Collectors.groupingBy(Product::getProductId))
                .values()
                .stream()
                .map(group -> group.stream()
                        .min(Comparator.comparing(Product::getValuePerUnit))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @GetMapping("/best-discounts")
    public List<Discount> getBestDiscounts(@RequestParam String folderPath) throws IOException {
        List<Discount> allDiscounts = csvLoaderService.loadAllDiscountsFromFolder(folderPath);

        // Sort by percentage descending and return top 10 (or more if needed)
        return allDiscounts.stream()
                .sorted(Comparator.comparingInt(Discount::getPercentageOfDiscount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

}
