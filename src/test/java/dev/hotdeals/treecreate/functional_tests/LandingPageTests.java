/*
    Functional frontend tests based of selenium
    These tests require the main app to be running before they can be run. Therefore, they are excluded from normal testing runs.
    In order to run them, you must use `mvn -Dtest=dev.hotdeals.treecreate.functional_tests.** test`
 */

package dev.hotdeals.treecreate.functional_tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class LandingPageTests
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
    public void getIndexTitleTest()
    {
        webDriver.get("http://localhost:" + port);
        assertThat(webDriver.getTitle()).isEqualTo("About Us");
    }

    @Test
    public void getAboutUsTitleTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        assertThat(webDriver.getTitle()).isEqualTo("About Us");
    }

    @Test
    @DisplayName("Contains a navigation bar")
    public void getNavbarTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        WebElement navbar = webDriver.findElement(By.id("navBar"));
    }

    @Test
    @DisplayName("Contains an About Us section")
    public void getAboutUsTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        WebElement aboutUs = webDriver.findElement(By.id("aboutUs"));
    }

    @Test
    @DisplayName("Contains a footer")
    public void getFooterTest()
    {
        webDriver.get("http://localhost:" + port + "/aboutUs");
        WebElement footer = webDriver.findElement(By.id("footer"));
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