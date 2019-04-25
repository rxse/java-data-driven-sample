package uitest;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads test suite configuration from resource files.
 */
public class SuiteConfiguration {

    private static final String DEBUG_PROPERTIES = "/debug.properties";

    private Properties properties;

    public SuiteConfiguration() throws IOException {
        this(System.getProperty("application.properties", DEBUG_PROPERTIES));
    }

    public SuiteConfiguration(String fromResource) throws IOException {
        properties = new Properties();
        properties.load(SuiteConfiguration.class.getResourceAsStream(fromResource));
    }

    public Capabilities getCapabilities() throws IOException {
        String capabilitiesFile = properties.getProperty("capabilities");
        String browserName = "";
        Properties capsProps = new Properties();
        capsProps.load(SuiteConfiguration.class.getResourceAsStream(capabilitiesFile));

        DesiredCapabilities capabilities = new DesiredCapabilities();

        if (hasProperty("opera.binary")) {
            Map<String, Object> operaOptions = new HashMap<String, Object>();
            operaOptions.put("binary", getProperty("opera.binary"));
            capabilities.setCapability(OperaOptions.CAPABILITY, operaOptions);
        }

        for (String name : capsProps.stringPropertyNames()) {
            String value = capsProps.getProperty(name);
            if (name.equals("browserName")) {
                browserName = value;
            }
            if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                capabilities.setCapability(name, Boolean.valueOf(value));
            } else if (value.startsWith("file:")) {
                capabilities.setCapability(name,
                        new File(".", value.substring(5)).getCanonicalFile().getAbsolutePath());
            } else {
                if (name.equals("androidPackage")) {
                    Map<String, Object> chromeOptions = new HashMap<String, Object>();
                    chromeOptions.put(name, value);
                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                } else {
                    capabilities.setCapability(name, value);
                }
            }
        }

        String headless = properties.getProperty("headless");
        if (headless != null && headless.equals("true")) {
            switch (browserName) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
                break;

            case "chrome":
                final ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");
                capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                break;
            }
        } else {
            if (hasProperty("debug") && Boolean.valueOf(getProperty("debug")) && browserName.equals("chrome")
                    && hasProperty("selocityPath") && getProperty("selocityPath").length() > 0) {
                String selocityPath = getProperty("selocityPath");
                final ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--load-extension=" + selocityPath);
                chromeOptions.addArguments("--auto-open-devtools-for-tabs");
                Map<String, Object> prefs = new HashMap<String, Object>();
                prefs.put("devtools.preferences.currentDockState", "\"undocked\"");
                chromeOptions.setExperimentalOption("prefs", prefs);
                capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            }
        }

        return capabilities;
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    public String getTimestampStarted() {
        return hasProperty("timestampStarted") ? getProperty("timestampStarted") : "";
    }

    public String getEndpointBrowserName() {
        return hasProperty("endpointBrowserName") ? getProperty("endpointBrowserName") : "";
    }
}
