// Ranorex Webtestit Page Object File

package uitest.pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

// Additional data: {"img":"screenshots/9b0a8b43-047d-9fc2-64d1-f3ba0dab2db6.png"}
public class LoginPageObject {

    // Additional data:
    // {"img":"screenshots/b7dd8dbd-94d9-f53e-7794-bc69f5e63951.png"}
    private By Username = By.cssSelector("#username");
    // Additional data:
    // {"img":"screenshots/36366582-8046-7168-e2b3-c6172a95b2ea.png"}
    private By Password = By.cssSelector("#password");
    // Additional data:
    // {"img":"screenshots/13113c31-f167-d032-a3b0-c4c36bcd59dd.png"}
    private By Login = By.cssSelector("[class='fa fa-2x fa-sign-in']");
    // Additional data:
    // {"img":"screenshots/2ff364ca-d546-0135-b8e0-555335c2ba8a.png"}
    private By ResultMessage = By.cssSelector("#flash");

    protected WebDriver driver;
    protected WebDriverWait wait;

    public LoginPageObject(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
    }

    public LoginPageObject open(String url) {
        this.driver.get(url);

        return this;
    }

    public String getTitle() {
        return this.driver.getTitle();
    }

    public LoginPageObject enterUsername(String text) {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(this.Username)).sendKeys(text);

        return this;
    }

    public LoginPageObject enterPassword(String text) {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(this.Password)).sendKeys(text);

        return this;
    }

    public LoginPageObject clickLogin() {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(this.Login)).click();

        return this;
    }

    public String getLoginMessage() {
        String loginMessage = this.wait.until(ExpectedConditions.visibilityOfElementLocated(this.ResultMessage))
                .getText().replaceAll("×", "").trim();

        return loginMessage;
    }

}
