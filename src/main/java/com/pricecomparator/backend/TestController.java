package com.pricecomparator.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
}
