
package uitest;

import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import ru.stqa.selenium.factory.AbstractWebDriverPool;

/**
 * Webdriver pool implementation used in diagnostic mode
 */
public class DebugWebDriverPool extends AbstractWebDriverPool {

public static AbstractWebDriverPool DEFAULT = new DebugWebDriverPool();

    @Override
    public WebDriver getDriver(URL hub, Capabilities capabilities) {
        return this.newDriver(hub, capabilities);
    }

    @Override
    public void dismissDriver(WebDriver driver) {

    }

    @Override
    public void dismissAll() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
