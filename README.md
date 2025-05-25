# Price Comparator - Market (Java Backend)

This backend project it allows users to compare product prices across major Romanian supermarkets (Lidl, Kaufland, Profi) based on real-world CSV data.

---

## Project Structure

- `TestController.java`: REST controller exposing all required endpoints
- `CsvLoaderService.java`: Service class that reads and parses CSV product/discount data
- `Product.java` / `Discount.java`: Domain models
- `BasketRequest.java`: Payload model for daily basket optimization
- `PriceAlertRequest.java`: Payload model for custom price alerts
- `LocalDateConverter.java`: Converts date strings to `LocalDate`
- `resources/products/`: Sample product CSVs (e.g., `lidl_2025-05-08.csv`)
- `resources/discounts/`: Sample discount CSVs (e.g., `profi_discounts_2025-05-08.csv`)

---

## Assumptions

- The application does not use a database; it processes CSV files on request.
- Only `.csv` files are supported.
- Matching is done by `productName` (case-insensitive) for simplicity.

---

## Build & Run Instructions

1. **Clone the repository**

```bash
git clone https://github.com/CrisztinaZudor/Price-Comparator-Backend.git
cd Price-Comparator-Backend
```

2. **Build the project**

```bash
mvn clean install
```

3. **Run the application**

```bash
mvn spring-boot:run
```

---

## Features Implemented

### Daily Shopping Basket Monitoring
- `/optimize-basket`: Accepts a product list and returns the cheapest options by value per unit.

### Best Discounts
- `/best-discounts`: Top 10 products with the highest percentage discounts across all stores.

### New Discounts
- `/new-discounts`: Discounts added in the last 24 hours.

### Dynamic Price History Graphs
- `/price-history`: Get product price history with filters.
- `/price-history-chart`: Returns Chart.js-friendly JSON data structure.

### Product Substitutes & Recommendations
- `/substitutes`: Sorted list of the same product from other stores with lower value per unit.

### Custom Price Alert
- `/price-alerts`: Input target prices, and receive matching product entries below threshold.

---

## Example CURL Requests

```bash
# Get best value per unit products
curl "http://localhost:8080/best-price?folderPath=src/main/resources/products"

# Get best discounts
curl "http://localhost:8080/best-discounts?folderPath=src/main/resources/discounts"

# Get new discounts in the last 24h
curl "http://localhost:8080/new-discounts?folderPath=src/main/resources/discounts"

# Get price history with filters
curl "http://localhost:8080/price-history?folderPath=src/main/resources/products&brand=Barilla"

# Get price history in chart form
curl "http://localhost:8080/price-history-chart?folderPath=src/main/resources/products&productId=P001"

# Post a basket to optimize
curl -X POST http://localhost:8080/optimize-basket   -H "Content-Type: application/json"   -d '{"folderPath":"src/main/resources/products","productNames":["lapte zuzu", "spaghetti nr.5"]}'

# Post custom price alerts
curl -X POST http://localhost:8080/price-alerts   -H "Content-Type: application/json"   -d '{"folderPath":"src/main/resources/products", "targetPrices":{"lapte zuzu": 10.0, "piper negru măcinat": 6.0}}'

# Get substitutes for a product
curl "http://localhost:8080/substitutes?folderPath=src/main/resources/products&productName=lapte zuzu"
```


