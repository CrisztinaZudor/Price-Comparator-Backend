
# Price Comparator (Java Backend)

This backend project allows users to compare product prices across major Romanian supermarkets (Lidl, Kaufland, Profi) based on real-world CSV data.

---

## Project Structure

- `TestController.java`: REST controller exposing all required endpoints.
- `CsvLoaderService.java`: Service class that reads and parses CSV product/discount data.
- `Product.java` / `Discount.java`: Domain models.
- `BasketRequest.java`: Payload model for daily basket optimization.
- `PriceAlertRequest.java`: Payload model for custom price alerts.
- `LocalDateConverter.java`: Converts date strings to `LocalDate`.
- `resources/products/`: Sample product CSVs (e.g., `lidl_2025-05-08.csv`).
- `resources/discounts/`: Sample discount CSVs (e.g., `profi_discounts_2025-05-08.csv`).

---

## Assumptions

- The application does not use a database; it processes CSV files on request.
- Only `.csv` files are supported.
- Matching is done by `productName` (case-insensitive) for simplicity.

---

## ⚙Build & Run Instructions

### 1. Clone the repository

```bash
git clone https://github.com/CrisztinaZudor/Price-Comparator-Backend.git
cd Price-Comparator-Backend
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

---

## Features Implemented

### Daily Shopping Basket Monitoring
- **`POST /optimize-basket`**: Accepts a product list and returns the cheapest options by value per unit.

### Best Discounts
- **`GET /best-discounts`**: Top 10 products with the highest percentage discounts across all stores.

### New Discounts
- **`GET /new-discounts`**: Discounts added in the last 24 hours.

### Dynamic Price History Graphs
- **`GET /price-history`**: Get product price history with filters.
- **`GET /price-history-chart`**: Returns Chart.js-friendly JSON data structure.

### Product Substitutes & Recommendations
- **`GET /substitutes`**: Sorted list of the same product from other stores with lower value per unit.

### Custom Price Alert
- **`POST /price-alerts`**: Input target prices, and receive matching product entries below threshold.

---

## Example Requests

### 1. **GET Best Discounts**
```http
GET /best-discounts?folderPath=src/main/resources/discounts
```

### 2. **GET New Discounts (Last 24h)**
```http
GET /new-discounts?folderPath=src/main/resources/discounts
```

### 3. **GET Best Price per Unit**
```http
GET /best-price?folderPath=src/main/resources/products
```

### 4. **GET Price History with Filtering**
```http
GET /price-history?folderPath=src/main/resources/products&brand=Barilla
```

### 5. **GET Price History Chart**
```http
GET /price-history-chart?folderPath=src/main/resources/products&productId=P001
```

### 6. **POST Optimize Basket**
```http
POST /optimize-basket
Content-Type: application/json

{
  "folderPath": "src/main/resources/products",
  "productNames": ["lapte zuzu", "spaghetti nr.5"]
}
```

### 7. **POST Custom Price Alerts**
```http
POST /price-alerts
Content-Type: application/json

{
  "folderPath": "src/main/resources/products",
  "targetPrices": {
    "lapte zuzu": 10.0,
    "piper negru măcinat": 6.0
  }
}
```

### 8. **GET Product Substitutes**
```http
GET /substitutes?folderPath=src/main/resources/products&productName=lapte zuzu
```
