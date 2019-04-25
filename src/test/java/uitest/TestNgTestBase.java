package uitest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
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
    protected static boolean screenshotOnFailure;
    protected static String timestampStarted;
    protected static String endpointBrowserName;

    /**
     * @deprecated
     *             <p>
     *             As of release 0.25.0, replaced by {@link #getDriver()}.Fix by
     *             adding the following line to the beginning of your test method:
     *             </p>
     *             <p>
     *             WebDriver driver = getDriver();
     *             </p>
     */
    @Deprecated
    protected WebDriver driver;

    private Map<String, WebDriver> drivers = new HashMap<String, WebDriver>();

    @BeforeSuite
    public void initTestSuite() throws IOException {
        SuiteConfiguration config = new SuiteConfiguration();
        baseUrl = config.getProperty("site.url");
        capabilities = (DesiredCapabilities) config.getCapabilities();
        timestampStarted = config.getTimestampStarted();
        endpointBrowserName = config.getEndpointBrowserName();
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
        if (config.hasProperty("screenshotOnFailure")) {
            screenshotOnFailure = Boolean.valueOf(config.getProperty("screenshotOnFailure"));
        }
    }

    /**
     * <p>
     * Returns an instance of WebDriver for the calling test method. This method can
     * only be called from within test methods.
     * </p>
     * <p>
     * Note: this instance is disposed automatically after test run
     * </p>
     */
    protected WebDriver getDriver() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTraceElements.length; i++) {
            WebDriver driver = drivers.get(stackTraceElements[i].getMethodName());
            if (driver != null) {
                return driver;
            }
        }
        throw new Error("No WebDriver instance was created for this test method.");
    }

    @BeforeMethod
    public void initWebDriver(Method method) {
        if (debugMode) {
            driver = DebugWebDriverPool.DEFAULT.getDriver(gridHubUrl, capabilities);
        } else {
            driver = WebDriverPool.DEFAULT.getDriver(gridHubUrl, capabilities);
        }
        drivers.put(method.getName(), driver);
    }

    @AfterMethod
    public void tearDownWebDriver(Method method) {
        WebDriver driver = drivers.get(method.getName());
        if (driver != null) {
            if (!debugMode) {
                driver.quit();
            }
            drivers.remove(method.getName());
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
        if (!debugMode) {
            WebDriverPool.DEFAULT.dismissAll();
        }
    }

    @AfterMethod
    public void takeScreenshotOnFailure(ITestResult result) {
        if (screenshotOnFailure) {
            try {
                if (result.getStatus() == ITestResult.FAILURE) {
                    String uniqueName = result.getInstanceName() + result.getName();
                    String hash = Hex.encodeHexString(MessageDigest.getInstance("MD5").digest(uniqueName.getBytes()));
                    takeScreenshot(String.format("%s-%s-%s", timestampStarted, endpointBrowserName, hash));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void takeScreenshot(String fileName) {
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String path = String.format("reports%1$sscreenshots%1$s%2$s.jpg", File.separator, fileName);
            FileUtils.copyFile(scrFile, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
