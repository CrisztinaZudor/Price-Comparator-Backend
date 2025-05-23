package com.pricecomparator.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.List;


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

        return allDiscounts.stream()
                .sorted(Comparator.comparingInt(Discount::getPercentageOfDiscount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @GetMapping("/new-discounts")
    public List<Discount> getNewDiscounts(@RequestParam String folderPath) throws IOException {
        List<Discount> allDiscounts = csvLoaderService.loadAllDiscountsFromFolder(folderPath);

        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        return allDiscounts.stream()
                .filter(d -> {
                    LocalDate fromDate = d.getFromDate();
                    return fromDate != null &&
                            !fromDate.isBefore(yesterday) &&
                            !fromDate.isAfter(now);
                })
                .sorted(Comparator.comparing(Discount::getFromDate).reversed())
                .collect(Collectors.toList());
    }

    @GetMapping("/price-history")
    public List<Product> getPriceHistory(
            @RequestParam String folderPath,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String productCategory,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String productName
    ) throws IOException {
        List<Product> allProducts = csvLoaderService.loadAllProductsFromFolder(folderPath);

        return allProducts.stream()
                .filter(p -> brand == null || p.getBrand().equalsIgnoreCase(brand))
                .filter(p -> productCategory == null || p.getProductCategory().equalsIgnoreCase(productCategory))
                .filter(p -> storeName == null || p.getStoreName().equalsIgnoreCase(storeName))
                .filter(p -> productName == null || p.getProductName().equalsIgnoreCase(productName))
                .sorted(Comparator.comparing(Product::getProductId).thenComparing(Product::getDate))
                .collect(Collectors.toList());
    }

    @GetMapping("/price-history-chart")
    public Map<String, Object> getPriceHistoryChart(
            @RequestParam String folderPath,
            @RequestParam String productId
    ) throws IOException {
        List<Product> allProducts = csvLoaderService.loadAllProductsFromFolder(folderPath);

        List<Product> filtered = allProducts.stream()
                .filter(p -> productId.equals(p.getProductId()))
                .sorted(Comparator.comparing(Product::getDate))
                .collect(Collectors.toList());

        List<String> labels = filtered.stream()
                .map(p -> p.getDate() + " (" + p.getStoreName() + ")")
                .collect(Collectors.toList());

        List<Double> prices = filtered.stream()
                .map(Product::getPrice)
                .collect(Collectors.toList());

        return Map.of(
                "labels", labels,
                "datasets", List.of(
                        Map.of(
                                "label", "Price",
                                "data", prices
                        )
                )
        );
    }


}
