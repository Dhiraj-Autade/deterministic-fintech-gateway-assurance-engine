# Deterministic Fintech Gateway Assurance Engine

> A reliable automation safety gate for banking transaction testing.

**Stack:** Java 11 | Selenium 4 | TestNG 7.9 | RestAssured 5.4 | ExtentReports 5.1

## Section 1: Project Overview

The **Deterministic Fintech Gateway Assurance Engine** is a powerful test automation framework built with **Java, Selenium, TestNG, and RestAssured**.

Its purpose is to protect core banking transaction paths, reduce frustrating test failures, and help teams ship software updates with more confidence.

The framework checks data, validates backend health, drives a browser-based payment flow, and writes clear reports for the people who need to trust the result.

In simple terms, it turns transaction testing into a controlled safety gate instead of a risky manual process.

### Why this stands out

- It focuses on reliability first, which is exactly what financial automation needs.
- It separates test logic from test data, so the framework is easy to extend.
- It gives both engineers and non-engineers a clear result through the HTML report.

## Section 2: Problem Statements vs. Implemented Solutions

- **Problem 1: Integration Network Lag**  
  Core transaction networks can have micro-outages and slow connections, which makes rigid test scripts fail even when the application is still recoverable.  
  **Solution 1: Dynamic Polling Synchronization**  
  The page object uses an elastic wait loop that checks the screen every 500ms for up to 15 seconds instead of relying on fixed sleeps.  
  **Real-World Utility**  
  This helps banks test the same kind of unstable timing seen in live digital wallet rails where traffic and server response times change constantly.

- **Problem 2: Parameter Matrix Sprawl**  
  Testing many currency rules, accounts, transaction types, and boundary values can create messy copy-pasted code that is hard to maintain.  
  **Solution 2: Data-Driven Ingestion Matrix**  
  The framework keeps scenarios in external JSON resource files, so the test logic stays separate from the input data.  
  In this workspace, the smoke data lives in `src/test/resources/payment_data.json` and the regression data lives in `src/test/resources/regression_suite.json`.  
  **Real-World Utility**  
  This lets risk or business analysts scale micro-payments, caps, and boundary checks without needing to edit Java code.

- **Problem 3: Blind Upstream System Failures**  
  Opening a browser when the backend is already down wastes machine power and hides the real root cause.  
  **Solution 3: Pre-Flight API Gatekeeper**  
  Before the browser launches, the framework sends a fast RestAssured health check that takes less than 0.5 seconds.  
  **Real-World Utility**  
  Teams immediately learn whether the backend service is dead or whether the web page itself has a problem.

## Section 3: Pipeline Architecture
<img width="465" height="771" alt="image" src="https://github.com/user-attachments/assets/bd8cb8d6-598d-4839-b29a-24311d447da7" />

```text
1) Data Matrices
   JSON files hold raw payment parameters and test outcomes.
   They are injected into the run through testng.xml suite settings.

        |
        v

2) TestRunner Brain (FintechTestRunner.java)
   Starts suite setup, reads the data rows, and controls the data provider.
   It decides which scenario runs next and records each result.

        |
        v

3) API Validation Shield (RestAssured health check)
   Sends a quick background ping before any browser session starts.
   It allows healthy runs to continue and stops dead runs early.

        |
        v

4) Web Controller Engine (CheckoutPage.java POM)
   Fills the on-screen form fields and selects the right options.
   It also manages the smart wait loop that watches for the final status.

        |
        v

5) Interactive Target (checkout.html React page)
   Simulates transaction speed, lag spikes, and timeout behavior.
   This is the browser page that the automation framework drives.

        |
        v

6) Results Dashboard (test-output/index.html)
   Flushes clean charts, pass or fail status, and readable logs.
   It gives the team a simple report they can review and share.
```

## Section 4: Comprehensive File Inventory

- **pom.xml**  
  Handles external tool downloads for Selenium, TestNG, RestAssured, ExtentReports, and Jackson.  
  It also defines the Java build setup and the test execution plugins.

- **checkout.html**  
  A responsive single-page React web app styled with Tailwind CSS.  
  It models a payment gateway and includes built-in lag simulators for testing.

- **src/test/resources/payment_data.json**  
  A plain-text data file that holds 4 core smoke test paths for fast commit checks.  
  It feeds the high-speed validation flow for the main transaction scenarios.

- **src/test/resources/regression_suite.json**  
  A plain-text data file that holds 10 detailed transaction rows for boundary testing.  
  It stresses payment limits, currency edges, and behavior under heavier test coverage.

- **src/test/java/pages/CheckoutPage.java**  
  Holds the screen locator IDs, the background health check, the form entry steps, and the fluent wait logic.  
  It is the page object that keeps browser actions clean and reusable.

- **src/test/java/tests/FintechTestRunner.java**  
  Runs suite setup and cleanup, reads the datasets, and updates the final dashboard report.  
  It is the main test runner that ties the whole flow together.

- **testng.xml**  
  Links and sequences the execution blocks so Phase 1 runs smoke tests and Phase 2 runs regression tests.  
  It is the single command entry point for the full suite.

## Section 5: Importance in Real-World Applications

This framework turns chaotic testing routines into a predictable, self-documenting pipeline.

That matters in real financial systems because it helps teams prove system availability, protect data boundaries, remove manual testing hours, and avoid costly production surprises.

It also saves money by stopping useless browser runs when the backend is already failing, which reduces cloud compute waste across large CI pipelines.

For banks, payment teams, and digital wallet platforms, this kind of deterministic testing is not just helpful. It is a practical way to keep release quality, compliance confidence, and transaction reliability under control.

### How to run it

1. Open the project in a terminal at the repository root.
2. Run `mvn clean test`.
3. Open `test-output/index.html` to review the report.

### What success looks like

- Smoke scenarios pass quickly and confirm the core payment path is stable.
- Regression scenarios cover more limits, currencies, and edge cases.
- The report makes failures easy to trace back to data, backend health, or UI behavior.

### Why the design works

- Selenium handles the browser actions cleanly.
- TestNG controls the suite order and data-driven execution.
- RestAssured blocks dead backend runs early.
- JSON files keep the test inputs simple to update.
