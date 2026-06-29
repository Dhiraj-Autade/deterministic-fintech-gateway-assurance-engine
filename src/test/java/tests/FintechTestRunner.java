package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CheckoutPage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class FintechTestRunner {

    private static final String SUCCESS_RESPONSE =
            "Response Code [200] OK - Callback Received: [0] Success (Transaction Completed)";
    private static final String TIMEOUT_RESPONSE =
            "Response Code [504] Gateway Timeout - Mobile Money Network (MMN) Core Rail Unreachable";

    private static ExtentReports extent;
    private WebDriver driver;
    private CheckoutPage checkoutPage;
    private ExtentTest extentTest;

    @BeforeSuite
    public static void initExtentReport() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/index.html");
        sparkReporter.config().setDocumentTitle("Global Fintech Transaction Core Gateway Assurance Dashboard");
        sparkReporter.config().setReportName("Enterprise Transaction Core Gateway — Test Execution Report");
        sparkReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.STANDARD);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Framework", "Global Fintech Transaction Core Gateway Assurance Framework");
        extent.setSystemInfo("Region", "Global Enterprise — Mobile Money Network (MMN) Core Rail Mock Gateway");
        extent.setSystemInfo("Engine", "TestNG + Selenium + RestAssured + Jackson");
        extent.setSystemInfo("Assurance Layer Context", "MIGRATION RESILIENCE GATEWAYS ACTIVE");
        extent.setSystemInfo("Engineering Analysis",
                "Chaining Phase 1 (Smoke) and Phase 2 (Regression) sequentially isolates structural configuration "
                        + "faults from arithmetic transactional boundary parameters. This dual-gate pipeline mitigates "
                        + "core regression risks, ensuring zero transaction dropouts and maximum system availability "
                        + "during high-throughput cloud migration deployment windows.");
    }

    @AfterSuite
    public static void flushExtentReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        checkoutPage = new CheckoutPage(driver);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @DataProvider(name = "paymentScenarios")
    public Object[][] paymentScenarios(ITestContext context) throws IOException {
        String filePath = context.getCurrentXmlTest().getParameter("suiteJsonPath");
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = "payment_data.json";
        }

        ObjectMapper mapper = new ObjectMapper();

        try (InputStream jsonStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (jsonStream == null) {
                throw new IOException(filePath + " not found on classpath (src/test/resources/)");
            }

            JsonNode scenarios = mapper.readTree(jsonStream);
            Object[][] dataMatrix = new Object[scenarios.size()][11];

            for (int i = 0; i < scenarios.size(); i++) {
                JsonNode row = scenarios.get(i);
                if (row.has("scenarioName")) {
                    dataMatrix[i] = mapRegressionSuiteRow(row, filePath);
                } else {
                    dataMatrix[i] = mapPaymentDataRow(row, filePath);
                }
            }

            return dataMatrix;
        }
    }

    @Test(dataProvider = "paymentScenarios")
    public void executeFintechPaymentScenario(String testCaseId,
                                              String scenario,
                                              String apiKey,
                                              String shortCode,
                                              String transactionType,
                                              String phoneNumber,
                                              String amount,
                                              String accountReference,
                                              String simulationType,
                                              String expectedStatus,
                                              String sourceMatrix) {
        extentTest = extent.createTest(testCaseId + " — " + scenario);
        extentTest.info("Active data matrix: <b>" + sourceMatrix + "</b>");
        extentTest.info("Scenario started: <b>" + scenario + "</b>");
        extentTest.info("Dataset row → apiKey=" + maskApiKey(apiKey)
                + ", shortCode=" + shortCode
                + ", transactionType=" + transactionType
                + ", phoneNumber=" + phoneNumber
                + ", amount=" + amount
                + ", accountReference=" + accountReference
                + ", simulationType=" + simulationType);

        if (!checkoutPage.isMpesaApiHealthy()) {
            extentTest.fail("<b>CRITICAL API FAILURE</b> — Mobile Money Network (MMN) Core Rail integration endpoint health check returned non-200. Browser workflow bypassed.");
            if (driver != null) {
                driver.quit();
                driver = null;
            }
            Assert.fail("API gatekeeper health check failed — aborting scenario " + testCaseId);
            return;
        }

        extentTest.pass("API gatekeeper health check passed (HTTP 200). Proceeding to browser workflow.");

        String checkoutAbsolutePath = Paths.get(System.getProperty("user.dir"), "checkout.html")
                .toAbsolutePath()
                .toString()
                .replace(File.separator, "/");
        driver.get("file:///" + checkoutAbsolutePath);
        extentTest.info("Loaded local checkout gateway: file:///" + checkoutAbsolutePath);

        checkoutPage.executePaymentFlow(
                apiKey,
                shortCode,
                transactionType,
                phoneNumber,
                amount,
                accountReference,
                simulationType
        );
        extentTest.info("Digital wallet push form submitted — polling status box with 500 ms interval (15 s ceiling).");

        String capturedStatus = checkoutPage.getFinalTransactionStatus();
        extentTest.info("Captured transaction status: <b>" + capturedStatus + "</b>");

        try {
            Assert.assertEquals(capturedStatus, expectedStatus,
                    "Transaction status mismatch for " + testCaseId + " [" + scenario + "]");
            extentTest.pass("<span style='color:green;'><b>PASS</b></span> — Expected and actual status aligned: " + expectedStatus);
        } catch (AssertionError assertionError) {
            extentTest.fail("<span style='color:red;'><b>FAIL</b></span> — Expected: <b>" + expectedStatus
                    + "</b> | Actual: <b>" + capturedStatus + "</b>");
            throw assertionError;
        }
    }

    private static Object[] mapPaymentDataRow(JsonNode row, String filePath) {
        return new Object[]{
                row.get("testCaseId").asText(),
                row.get("scenario").asText(),
                jsonText(row, "apiKey", "sk_live_51NxDefault"),
                jsonText(row, "shortCode", "400222"),
                jsonText(row, "transactionType", "CustomerPayBillOnline"),
                row.get("msisdn").asText(),
                formatAmount(row.get("amount")),
                row.get("accountReference").asText(),
                row.get("simulationType").asText(),
                row.get("expectedOutcome").asText(),
                filePath
        };
    }

    private static Object[] mapRegressionSuiteRow(JsonNode row, String filePath) {
        String scenarioName = row.get("scenarioName").asText();
        return new Object[]{
                scenarioName,
                scenarioName,
                jsonText(row, "apiKey", "sk_live_regression_default"),
                jsonText(row, "shortCode", "400222"),
                jsonText(row, "transactionType", "CustomerPayBillOnline"),
                row.get("phoneNumber").asText(),
                row.get("amount").asText(),
                row.get("customerName").asText(),
                row.get("simulationType").asText(),
                resolveExpectedStatus(row.get("expectedStatus").asText()),
                filePath
        };
    }

    private static String resolveExpectedStatus(String expectedStatus) {
        if ("Payment Successful".equals(expectedStatus)) {
            return SUCCESS_RESPONSE;
        }
        if (expectedStatus.contains("Gateway Timeout") || expectedStatus.contains("Transaction Halted")) {
            return TIMEOUT_RESPONSE;
        }
        return expectedStatus;
    }

    private static String jsonText(JsonNode row, String key, String defaultValue) {
        JsonNode node = row.get(key);
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        return node.asText();
    }

    private static String formatAmount(JsonNode amountNode) {
        if (amountNode == null || amountNode.isNull()) {
            return "0.00";
        }
        if (amountNode.isIntegralNumber()) {
            return amountNode.asText();
        }
        double value = amountNode.asDouble();
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "****";
        }
        return apiKey.substring(0, 8) + "****";
    }
}
