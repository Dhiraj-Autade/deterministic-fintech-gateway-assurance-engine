# Global Fintech Transaction Core Gateway Assurance Framework

> **Enterprise-grade TestNG automation engineered for global commercial banking cores and digital wallet providers at scale.**

---

## 🏦 Project Overview

The **Global Fintech Transaction Core Gateway Assurance Framework** is a production-ready quality engineering platform purpose-built to eliminate multi-million dollar **ADMT** *(Application Development, Maintenance, and Testing)* migration bottlenecks across **global commercial banking cores** and **digital wallet providers**.

This framework delivers a unified, repeatable assurance layer for core transaction gateway flows, cross-border currency rails, and high-value Regulatory Compliance Engine Layer scenarios — without coupling test logic to volatile transaction data. It combines **Selenium UI control**, **RestAssured API gatekeeping**, **Jackson JSON data ingestion**, and **ExtentReports executive dashboards** into a single Maven-orchestrated pipeline that mirrors how elite QE organizations ship confidence to production.

Built for managers, architects, and automation engineers who demand **zero blind spots**, **zero script flakiness**, and **zero data sprawl** during core banking regression cycles.

---

## ❌ Problem Statements Face on the Field

### Problem 1: Integration Network Lag

Core transaction networks experience micro-outages and telemetry lag spikes, causing brittle testing threads to crash intermittently and trigger false-positive alerts. Hard-coded sleep patterns cannot absorb the unpredictable latency windows inherent to Mobile Money Network (MMN) Core Rail integrations.

---

### Problem 2: Parameter Matrix Sprawl

Testing dozens of individual compliance profiles, multi-currency combinations, and transaction boundaries creates a massive duplication matrix of copy-pasted script code. Every new regulatory rule, currency permutation, or simulation profile forces a Java recompile instead of a data edit.

---

### Problem 3: Blind Upstream System Dropouts

Booting heavy UI browser environments against backend ledger cores that are already offline wastes cloud compute and covers up the true root-cause error location. UI-level failures mask upstream microservice unavailability, delaying incident resolution and inflating regression costs.

---

## 💡 Proposed Engineering Solutions

### Solution 1: Dynamic Polling Synchronization

Code an elastic wait engine in `CheckoutPage.getFinalTransactionStatus()` that checks the DOM every **500 ms** up to a **15-second ceiling**, smoothly handling backend connection latency without brittle `Thread.sleep()` blocks. The engine waits for the loading phase to resolve before capturing the final gateway response string.

---

### Solution 2: Data-Driven Ingestion Matrix

Isolate all transaction variables — API keys, merchant short codes, MSISDNs, amounts, currencies, and simulation profiles — into an external structured schema at `src/test/resources/payment_data.json`. A single master TestNG runner processes endless permutations seamlessly via `@DataProvider`, decoupling automation logic from transaction parameters.

---

### Solution 3: API-Gatekeeper Layer

Intercept testing runs via a **< 0.5 s** pre-flight RestAssured health check (`CheckoutPage.isMpesaApiHealthy()`) to confirm backend microservice availability before launching automated UI threads. Non-200 responses trigger critical failure logging in ExtentReports and bypass the browser entirely, preserving compute and surfacing the true failure domain.

---

## 🏗️ Implementation & Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│           GLOBAL FINTECH TRANSACTION CORE GATEWAY ASSURANCE PIPELINE        │
└─────────────────────────────────────────────────────────────────────────────┘

  ┌──────────────────────┐
  │  payment_data.json   │  ◄── Data-Driven Ingestion Matrix
  │  (Test Resources)    │      • USD / EUR permutations
  └──────────┬───────────┘      • NORMAL | NETWORK_LAG | GATEWAY_TIMEOUT
             │
             │  Jackson Databind parse
             ▼
  ┌──────────────────────┐
  │  FintechTestRunner   │  ◄── TestNG Master Controller
  │       .java          │      • @DataProvider scenario loop
  └──────────┬───────────┘      • @BeforeSuite / @AfterSuite lifecycle
             │
             │  Pre-flight health intercept
             ▼
  ┌──────────────────────┐
  │  RestAssured API     │  ◄── API-Gatekeeper Layer (< 0.5s ping)
  │     Gatekeeper       │      • HTTP 200 → proceed to UI
  └──────────┬───────────┘      • Non-200 → CRITICAL FAIL + skip browser
             │
             │  Health check passed
             ▼
  ┌──────────────────────┐
  │   CheckoutPage.java  │  ◄── Page Object Model (POM)
  │   (Locator + Wait)   │      • apiKey | phoneNumber | amount
  └──────────┬───────────┘      • accountReference | simulationType
             │                    • processButton | statusBox
             │  file:/// checkout.html
             ▼
  ┌──────────────────────┐
  │   checkout.html      │  ◄── Local React Frontend (Tailwind UI)
  │  (Browser Target)    │      • 10-second ledger freeze simulation
  └──────────┬───────────┘      • Enterprise Transaction Core Gateway
             │
             │  Smart Poll (500ms × 15s ceiling)
             ▼
  ┌──────────────────────┐
  │  ExtentReports       │  ◄── Dashboard UI
  │  test-output/        │      • PASS (green) / FAIL (red) per scenario
  │    index.html        │      • Pie-chart summary + drill-down logs
  └──────────────────────┘
             │
             │  @AfterSuite → extent.flush()
             ▼
        [ EXECUTIVE REPORT READY ]
```

---

## 📂 Project File Inventory

| File | Purpose |
|---|---|
| **`pom.xml`** | Central Maven repository management downloading Selenium (4.18.1), TestNG (7.9.0), RestAssured (5.4.0), ExtentReports (5.1.1), and Jackson Databind (2.17.0). Configures Java 11 compilation and Surefire TestNG suite execution. |
| **`checkout.html`** | Aesthetic local single-page React WebApp with premium Tailwind styling embedding the custom 10-second ledger freeze simulation. Renders the Enterprise Transaction Core Gateway form with stable element IDs for Selenium scraping. |
| **`payment_data.json`** | Plain-text data array repository storing multi-scenario simulation keys, currencies (USD/EUR), amounts, and expected gateway response strings. |
| **`CheckoutPage.java`** | Page Object Model mapping out locator IDs, executing the RestAssured health ping, and holding the fluent 500 ms / 15 s wait loops. |
| **`FintechTestRunner.java`** | Central TestNG execution controller launching the lifecycle configuration (`@BeforeMethod`, `@AfterMethod`, `@BeforeSuite`, `@AfterSuite`) and mapping the JSON data provider loop. |
| **`testng.xml`** | Core XML orchestration layout routing test suites at structured verbosity level **2** for enterprise console feedback during execution runs. |

---

## 📊 Verified Execution Outcomes & Results

All four regression scenarios have been verified end-to-end through the Extent HTML visual dashboard at `test-output/index.html`, confirming **100% green pass metrics**:

| ID | Scenario | Currency | Simulation | Verified Outcome |
|---|---|---|---|---|
| TC-001 | Standard Pass | USD | NORMAL | Response Code [200] OK — Transaction Completed |
| TC-002 | Resilience Spike | USD | NETWORK_LAG | Response Code [200] OK — successful lag navigation (10 s delay) |
| TC-003 | Compliance Drop | USD | GATEWAY_TIMEOUT | Response Code [504] — automatic routing bypass during simulated timeout |
| TC-004 | Cross-Border Validation | EUR | NORMAL | Response Code [200] OK — cross-border data entry validated |

### Run Locally

Open the project in VS Code, then execute the full suite from the integrated terminal:

```bash
mvn clean test
```

Upon completion, open the generated ExtentReports dashboard to review pie-chart pass/fail distribution and per-scenario drill-down logs:

```bash
start test-output/index.html
```

The dashboard surfaces API gatekeeper status, form submission traces, captured transaction results, and colour-coded PASS/FAIL blocks for audit-ready stakeholder review.

---

<p align="center">
  <strong>Global Fintech Transaction Core Gateway Assurance Framework</strong><br/>
  Built for Global Enterprise Confidence · Delivered by Quality Engineering Excellence
</p>
