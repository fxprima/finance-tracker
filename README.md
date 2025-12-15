# 📌 Finance Tracker — Personal Finance Analyzer

Spring Boot · MyBatis · TailwindCSS · CSV Import · PDF Export

Finance Tracker is a full end-to-end web application for managing personal financial transactions.
Users can import CSV files from apps like **Mony** or **MoneyTracker**, preview and analyze their financial insights, and optionally save them to the database for long-term tracking.

The application is built using **Spring Boot 3**, **MyBatis**, **SQLite**, and **Thymeleaf**, with a clean and modern UI powered by **TailwindCSS**.

---

# ✨ Features

### 🧾 Multi-Format CSV Import

* Supports **Mony** and **MoneyTracker** CSV formats
* CSV files are validated and parsed into a unified `TransactionRowDto` structure
* Preview screen before saving to the database
* Supports **guest mode** via session storage

### 📊 Insight Dashboard

* Income, spending, and balance summary
* Category and subcategory breakdown
* Daily & monthly charts
* Modern bento-style insights layout

### 📈 Forecasting (Exponential Smoothing)

* Forecast future expenses based on historical transactions
* Uses **Exponential Smoothing** (via `ForecastService`) on time-series data
* Useful for planning upcoming months and understanding spending trends

### 🗄 Database Save Options

* **Append mode** → saves only new transactions (automatic dedup)
* **Replace mode** → wipes existing user data and replaces with new import
* Deduplication uses business key: `date + amount + category + note + type`

### 🧰 Automatic Category Management

* Categories and subcategories auto-create if they don't exist
* Managed via MyBatis mappers

### 📄 PDF Export

* Export insights directly into a clean, formatted PDF file

### 🔐 Authentication

* User registration & login
* Password hashing
* Protected endpoints for saving data: `/import/save/**`

### 🎨 UI/UX

* TailwindCSS
* Dark mode toggle
* Custom modal & confirmation dialog components
* Fully responsive

---

# 🏛 Tech Stack

**Backend**

* Spring Boot 3.5.x
* MyBatis
* SQLite (easily switchable to PostgreSQL)
* Java 21
* Spring Security

**Frontend**

* Thymeleaf
* TailwindCSS
* Chart.js

**Tools**

* Maven
* Lombok
* Java Time API

---

# 📂 Project Structure

```
src/
 ├─ main/
 │   ├─ java/com/example/finance_tracker/
 │   │   ├─ controller/           # Web & auth controllers
 │   │   ├─ service/              # Business logic
 │   │   ├─ mapper/               # MyBatis XML mappers
 │   │   ├─ dto/                  # CSV & view DTOs
 │   │   ├─ model/                # Database models
 │   │   ├─ util/                 # Utilities (CSV, modal, formatters)
 │   │   └─ config/               # Security & MyBatis configuration
 │   │
 │   └─ resources/
 │       ├─ templates/            # Thymeleaf pages & fragments
 │       ├─ static/               # CSS, JS
 │       ├─ mapper/               # MyBatis XML
 │       ├─ schema.sql            # DB bootstrap
 │       └─ application.properties
 │
 └─ test/
     ├─ service/                  # Integration tests
     └─ FinanceTrackerApplicationTests.java
```

---

# 🚀 How to Run

### 1️⃣ Clone the repository

```
git clone https://github.com/username/finance-tracker.git
cd finance-tracker
```

### 2️⃣ Start the application

```
./mvnw spring-boot:run
```

### 3️⃣ Open in your browser

```
http://localhost:8080
```

## How to use the Application
### 1️⃣ Get the example data 
```azure
example-dummy-data/mony-format-dummy.csv
```
### 2️⃣ Drag to import bar
### 3️⃣ Import CSV

---

# 🧪 Testing

Existing integration tests include:

* `UserServiceImplTest`
* `CategoryServiceImplTest`
* `TransactionServiceImplTest`

Run all tests:

```
./mvnw test
```

---

# 🧠 Core Logic Highlights

## CSV Standardization

All imported CSVs are normalized into a single DTO:

```
public class TransactionRowDto {
    private LocalDate date;
    private double amount;
    private String category;
    private String subCategory;
    private TransactionType type;
    private String note;
}
```

## Forecasting with Exponential Smoothing

The application includes a built‑in forecasting module powered by the `ForecastService`.
It uses **daily expense time‑series data** and applies:

* **Holt’s Linear Trend (Double Exponential Smoothing)** when there is enough history (≥ 3 days)
* **Single Exponential Smoothing (SES)** as a fallback for very short series

### 📘 Methods Used

#### Holt’s Linear Trend (Double Exponential Smoothing)

Used when there are at least 3 daily points. Two components are updated:

* **Level (s)** — the smoothed value
* **Trend (b)** — the estimated slope over time

Implementation (simplified):

```
// initialization
s = x[0];
b = x[1] - x[0];

// update for each t
s = α * x[t] + (1 − α) * (s + b);
b = β * (s − prevS) + (1 − β) * b;

// next‑day forecast
Fₜ₊₁ = s + b;
```

With:

* `ALPHA = 0.3`
* `BETA  = 0.1`

#### Single Exponential Smoothing (SES)

Used as a fallback when the daily series is very short:

```
s = x[0];
for each t > 0:
    s = α * x[t] + (1 − α) * s;

Fₜ₊₁ = s;
```

With:

* `ALPHA = 0.3`

### How the monthly forecast is built

1. Build a **daily expense series** for the current month (or at least the last N days, e.g. 7 days).
2. Sum **actual expenses so far** in the current month.
3. Use Holt or SES to forecast **next‑day expense**.
4. Multiply that by the remaining days in the month.
5. Final forecast = **actualSoFar + forecastRemaining** → projected total expenses for the current month.

This allows the user to anticipate end‑of‑month spending based on their current trajectory.

## Dedup Logic (Append Mode)

````
Set<String> existingKeys = existingTransactions.stream()
    .map(this::businessKey)
    .collect(Collectors.toSet());

List<TransactionRowDto> filtered = imported.stream()
    .filter(t -> existingKeys.add(businessKey(t)))
    .toList();

Set<String> existingKeys = existingTransactions.stream()
.map(this::businessKey)
.collect(Collectors.toSet());

List<TransactionRowDto> filtered = imported.stream()
.filter(t -> existingKeys.add(businessKey(t)))
.toList();

```
````
````
# 🧭 Roadmap (Optional Enhancements)
- Budgeting per category
- Multi-account support (Cash, BCA, Dana, etc.)
- Transaction tagging
- Manual transaction input
- PostgreSQL + Docker deployment
- Advanced analytics module
````
---

## Preview
<img width="860" height="944" alt="2" src="https://github.com/user-attachments/assets/16edae54-ae19-4c7a-b7a1-f7f606958fd4" />
<img width="741" height="855" alt="3" src="https://github.com/user-attachments/assets/ce51adc7-d7c7-4c34-93a4-4c6bc2056eef" />
<img width="1439" height="908" alt="4" src="https://github.com/user-attachments/assets/6aee5046-bbfc-4194-9f76-8f83ca7fddeb" />
<img width="1485" height="848" alt="5" src="https://github.com/user-attachments/assets/2227e036-55a3-438e-9745-83afb53a0448" />
<img width="596" height="440" alt="6" src="https://github.com/user-attachments/assets/69307cd5-76ab-4db4-93dc-2f70de17d4b0" />



# 🙌 Author
Created by **Felix**

