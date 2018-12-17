package uitest;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import ru.stqa.selenium.factory.WebDriverPool;

/**
 * Base class for TestNG-based test classes
 */
public class TestNgTestBase {

    protected static URL gridHubUrl = null;
    protected static String baseUrl;
    protected static DesiredCapabilities capabilities;
    protected static boolean debugMode;

    protected WebDriver driver;

    @BeforeSuite
    public void initTestSuite() throws IOException {
        SuiteConfiguration config = new SuiteConfiguration();
        baseUrl = config.getProperty("site.url");
        capabilities = (DesiredCapabilities) config.getCapabilities();
        if (config.hasProperty("grid.url") && !"".equals(config.getProperty("grid.url"))) {
            gridHubUrl = new URL(config.getProperty("grid.url"));
        }
        if (gridHubUrl != null && config.hasProperty("remote.platform")
                && !"".equals(config.getProperty("remote.platform"))) {
            capabilities.setCapability("platform", config.getProperty("remote.platform"));
        }
        if (gridHubUrl != null && config.hasProperty("remote.version")
                && !"".equals(config.getProperty("remote.version"))) {
            capabilities.setCapability("version", config.getProperty("remote.version"));
        }
        if (config.hasProperty("remote.capabilities")) {
            JsonObject o = new JsonParser().parse(config.getProperty("remote.capabilities")).getAsJsonObject();
            Iterator<String> keys = o.keySet().iterator();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                capabilities.setCapability(key, o.get(key));
            }
        }
        if (config.hasProperty("debug")) {
            debugMode = Boolean.valueOf(config.getProperty("debug"));
        }
    }

    @BeforeMethod
    public void initWebDriver() {
        if (debugMode) {
            driver = DebugWebDriverPool.DEFAULT.getDriver(gridHubUrl, capabilities);
        } else {
            driver = WebDriverPool.DEFAULT.getDriver(gridHubUrl, capabilities);
        }
    }

    @AfterMethod
    public void tearDownWebDriver() {
        if (!debugMode) {
            driver.quit();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
        if (!debugMode) {
            WebDriverPool.DEFAULT.dismissAll();
        }
    }

}
