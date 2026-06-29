package pages;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckoutPage {

    private static final String MPESA_HEALTH_ENDPOINT = "https://reqres.in";
    private static final int API_CONNECTION_TIMEOUT_MS = 3000;
    private static final String GATEWAY_TIMEOUT_MESSAGE =
            "[FAILED] - Transaction halted due to Simulated Mobile Money Network (MMN) Core Rail Timeout";
    private static final String LOADING_PHASE_MARKER = "Awaiting Customer PIN Entry on Device";

    private final WebDriver driver;

    private final By apiKeyField = By.id("apiKey");
    private final By shortCodeField = By.id("shortCode");
    private final By transactionTypeDropdown = By.id("transactionType");
    private final By phoneField = By.id("phoneNumber");
    private final By amountField = By.id("amount");
    private final By referenceField = By.id("accountReference");
    private final By simulatorDropdown = By.id("simulationType");
    private final By processButton = By.id("processButton");
    private final By statusMessage = By.id("statusBox");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isMpesaApiHealthy() {
        try {
            RestAssuredConfig config = RestAssuredConfig.config().httpClient(
                    HttpClientConfig.httpClientConfig()
                            .setParam("http.connection.timeout", API_CONNECTION_TIMEOUT_MS)
                            .setParam("http.socket.timeout", API_CONNECTION_TIMEOUT_MS)
            );

            Response response = RestAssured
                    .given()
                    .config(config)
                    .when()
                    .get(MPESA_HEALTH_ENDPOINT);

            return response.getStatusCode() == 200;
        } catch (Exception ex) {
            return false;
        }
    }

    public void executePaymentFlow(String apiKey,
                                   String shortCode,
                                   String transactionType,
                                   String phoneNumber,
                                   String amount,
                                   String accountReference,
                                   String simulationType) {
        WebElement apiKeyInput = driver.findElement(apiKeyField);
        apiKeyInput.clear();
        apiKeyInput.sendKeys(apiKey);

        WebElement shortCodeInput = driver.findElement(shortCodeField);
        shortCodeInput.clear();
        shortCodeInput.sendKeys(shortCode);

        new Select(driver.findElement(transactionTypeDropdown)).selectByValue(transactionType);

        WebElement phoneInput = driver.findElement(phoneField);
        phoneInput.clear();
        phoneInput.sendKeys(phoneNumber);

        WebElement amountInput = driver.findElement(amountField);
        amountInput.clear();
        amountInput.sendKeys(amount);

        WebElement referenceInput = driver.findElement(referenceField);
        referenceInput.clear();
        referenceInput.sendKeys(accountReference);

        new Select(driver.findElement(simulatorDropdown)).selectByValue(simulationType);

        driver.findElement(processButton).click();
    }

    public String getFinalTransactionStatus() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.pollingEvery(Duration.ofMillis(500));

            wait.until(ExpectedConditions.visibilityOfElementLocated(statusMessage));
            wait.until(driver -> {
                String statusText = driver.findElement(statusMessage).getText().trim();
                return !statusText.isEmpty()
                        && !statusText.contains(LOADING_PHASE_MARKER)
                        && statusText.contains("Response Code");
            });

            return driver.findElement(statusMessage).getText().trim();
        } catch (TimeoutException ex) {
            return GATEWAY_TIMEOUT_MESSAGE;
        }
    }
}
