package com.pricecomparator.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TestController {

    @Autowired
    private CsvLoaderService csvLoaderService;

    private void validateFolderPath(String folderPath) {
        if (folderPath == null || folderPath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder path must be provided.");
        }
        if (!Files.exists(Paths.get(folderPath))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder path does not exist: " + folderPath);
        }
    }

    @GetMapping("/load-products")
    public List<Product> loadProducts(
            @RequestParam String filePath,
            @RequestParam String storeName,
            @RequestParam String date
    ) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File path does not exist: " + filePath);
        }
        return csvLoaderService.loadProducts(filePath, storeName, date);
    }

    @GetMapping("/best-price")
    public List<Product> getBestPriceProducts(@RequestParam String folderPath) throws IOException {
        validateFolderPath(folderPath);
        List<Product> all = csvLoaderService.loadAllProductsFromFolder(folderPath);
        return all.stream()
                .collect(Collectors.groupingBy(Product::getProductId))
                .values()
                .stream()
                .map(group -> group.stream().min(Comparator.comparing(Product::getValuePerUnit)).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @GetMapping("/best-discounts")
    public List<Discount> getBestDiscounts(@RequestParam String folderPath) throws IOException {
        validateFolderPath(folderPath);
        List<Discount> allDiscounts = csvLoaderService.loadAllDiscountsFromFolder(folderPath);
        return allDiscounts.stream()
                .sorted(Comparator.comparingInt(Discount::getPercentageOfDiscount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @GetMapping("/new-discounts")
    public List<Discount> getNewDiscounts(@RequestParam String folderPath) throws IOException {
        validateFolderPath(folderPath);
        List<Discount> allDiscounts = csvLoaderService.loadAllDiscountsFromFolder(folderPath);
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);
        return allDiscounts.stream()
                .filter(d -> {
                    LocalDate fromDate = d.getFromDate();
                    return fromDate != null && !fromDate.isBefore(yesterday) && !fromDate.isAfter(now);
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
        validateFolderPath(folderPath);
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
        validateFolderPath(folderPath);
        List<Product> allProducts = csvLoaderService.loadAllProductsFromFolder(folderPath);
        List<Product> filtered = allProducts.stream()
                .filter(p -> productId.equals(p.getProductId()))
                .sorted(Comparator.comparing(Product::getDate))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No price history found for product ID: " + productId);
        }

        List<String> labels = filtered.stream()
                .map(p -> p.getDate() + " (" + p.getStoreName() + ")")
                .collect(Collectors.toList());
        List<Double> prices = filtered.stream()
                .map(Product::getPrice)
                .collect(Collectors.toList());

        return Map.of("labels", labels, "datasets", List.of(Map.of("label", "Price", "data", prices)));
    }

    @PostMapping("/optimize-basket")
    public List<Product> optimizeBasket(@RequestBody BasketRequest request) throws IOException {
        validateFolderPath(request.getFolderPath());
        if (request.getProductNames() == null || request.getProductNames().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product names must be provided.");
        }
        List<Product> allProducts = csvLoaderService.loadAllProductsFromFolder(request.getFolderPath());
        return request.getProductNames().stream()
                .map(productName -> allProducts.stream()
                        .filter(p -> productName.equalsIgnoreCase(p.getProductName()))
                        .min(Comparator.comparing(Product::getValuePerUnit))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @PostMapping("/price-alerts")
    public List<Product> getPriceAlerts(@RequestBody PriceAlertRequest request) throws IOException {
        validateFolderPath(request.getFolderPath());
        if (request.getTargetPrices() == null || request.getTargetPrices().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target prices must be provided.");
        }
        List<Product> allProducts = csvLoaderService.loadAllProductsFromFolder(request.getFolderPath());
        return request.getTargetPrices().entrySet().stream()
                .flatMap(entry -> {
                    String productName = entry.getKey();
                    double targetPrice = entry.getValue();
                    return allProducts.stream()
                            .filter(p -> p.getProductName().equalsIgnoreCase(productName))
                            .filter(p -> p.getPrice() <= targetPrice);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/substitutes")
    public List<Product> getSubstitutes(
            @RequestParam String folderPath,
            @RequestParam String productName
    ) throws IOException {
        validateFolderPath(folderPath);
        List<Product> allProducts = csvLoaderService.loadAllProductsFromFolder(folderPath);
        List<Product> substitutes = allProducts.stream()
                .filter(p -> productName.equalsIgnoreCase(p.getProductName()))
                .sorted(Comparator.comparing(Product::getValuePerUnit))
                .collect(Collectors.toList());

        if (substitutes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No substitutes found for: " + productName);
        }

        return substitutes;
    }
}