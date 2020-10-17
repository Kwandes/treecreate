package dev.hotdeals.treecreate.functional_tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTests
{
    private static WebDriver webDriver;
    private final static int port = 5000;

    @BeforeAll
    public static void setup()
    {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        webDriver = new ChromeDriver(options);
        webDriver.get("http://localhost:" + port);
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    @Test
    @Order(2)
    @DisplayName("Login screen shows after clicking on the loginBtn")
    public void getLoginScreenTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        WebElement loginBtn = webDriver.findElement(By.id("loginButton"));
        WebElement loginScreen = webDriver.findElement(By.id("loginModal"));
        loginBtn.click();
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(
                webDriver1 -> loginScreen.getAttribute("style").contains("block")
        );
        assertThat(loginScreen.getAttribute("style").contains("block")).isTrue();

    }

    @Test
    @Order(1)
    @DisplayName("Login button is visible")
    public void getLoginButtonTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        WebElement loginBtn = webDriver.findElement(By.id("loginButton"));
        assertThat(loginBtn.getAttribute("style").contains("block")).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Account info page is shown after logging in")
    void SuccessfulLoginTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        WebElement loginBtn = webDriver.findElement(By.id("loginButton"));
        WebElement loginEmail = webDriver.findElement(By.id("loginEmail"));
        WebElement loginPassword = webDriver.findElement(By.id("loginPassword"));
        WebElement loginSubmitButton = webDriver.findElement(By.id("loginSubmitButton"));
        WebElement profileButton = webDriver.findElement(By.id("profileButton"));

        loginBtn.click();
        loginEmail.sendKeys("test@treecreate.dk");
        loginPassword.sendKeys("pass");
        loginSubmitButton.click();

        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(
                webDriver1 -> profileButton.getAttribute("style").contains("inline-block")
        );

        profileButton.click();

        assertThat(webDriver.getTitle()).isEqualTo("Account Information");
    }

    @Test
    @Order(4)
    @DisplayName("Account Information page contains proper info - Phone Number")
    void accountInfoPagePhoneNumberTest()
    {
        webDriver.get("http://localhost:" + port + "/account/info");
        WebElement inputPhoneNumber = webDriver.findElement(By.id("inputPhoneNumber"));
        assertThat(inputPhoneNumber.getAttribute("value")).isEqualTo("12345678");

    }

    @Test
    @Order(4)
    @DisplayName("Account Information page contains proper info - Email")
    void accountInfoPageEmailTest()
    {
        webDriver.get("http://localhost:" + port + "/account/info");
        WebElement inputEmail = webDriver.findElement(By.id("inputEmail"));
        assertThat(inputEmail.getAttribute("value")).isEqualTo("test@treecreate.dk");
    }

    @Test
    @Order(4)
    @DisplayName("Account Information page contains proper info - Name")
    void accountInfoPageNameTest()
    {
        webDriver.get("http://localhost:" + port + "/account/info");
        WebElement inputName = webDriver.findElement(By.id("inputName"));
        assertThat(inputName.getAttribute("value")).isEqualTo("tester");
    }

    @Test
    @Order(4)
    @DisplayName("Account Information page contains proper info - Street Address")
    void accountInfoPageStreetAddressTest()
    {
        webDriver.get("http://localhost:" + port + "/account/info");
        WebElement inputStreetAddress = webDriver.findElement(By.id("inputStreetAddress"));
        assertThat(inputStreetAddress.getAttribute("value")).isEqualTo("Yeetgade 69");
    }

    @Test
    @Order(4)
    @DisplayName("Account Information page contains proper info - City")
    void accountInfoPageCityTest()
    {
        webDriver.get("http://localhost:" + port + "/account/info");
        WebElement inputCity = webDriver.findElement(By.id("inputCity"));
        assertThat(inputCity.getAttribute("value")).isEqualTo("Copenhagen");
    }

    @Test
    @Order(4)
    @DisplayName("Account Information page contains proper info - Postcode")
    void accountInfoPagePostcodeTest()
    {
        webDriver.get("http://localhost:" + port + "/account/info");
        WebElement inputPostcode = webDriver.findElement(By.id("inputPostcode"));
        assertThat(inputPostcode.getAttribute("value")).isEqualTo("69");
    }

    @Test
    @Order(5)
    @DisplayName("Login button is changed to the Profile button")
    void getProfileButtonEnabledTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        WebElement profileButton = webDriver.findElement(By.id("profileButton"));
        assertThat(profileButton.getAttribute("style").contains("inline-block")).isTrue();

    }

    @Test
    @Order(6)
    @DisplayName("Can log out")
    void logoutTest()
    {
        webDriver.get("http://localhost:" + port + "/account/info");
        WebElement logoutButton = webDriver.findElement(By.id("logoutButton"));
        logoutButton.click();
        new WebDriverWait(webDriver, Duration.ofSeconds(10).getSeconds()).until(
                webDriver1 -> webDriver.getTitle().equals("About Us")
        );
        assertThat(webDriver.getTitle()).isEqualTo("About Us");
    }

    @Test
    @Order(7)
    @DisplayName("Profile button is changed to Login button after logging out")
    void profileButtonDisabledTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        WebElement loginBtn = webDriver.findElement(By.id("loginButton"));
        assertThat(loginBtn.getAttribute("style").contains("block")).isTrue();
    }
    
    @AfterAll
    public static void cleanup()
    {
        if (webDriver != null)
        {
            webDriver.quit();
        }
    }
}
