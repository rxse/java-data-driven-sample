// Ranorex Webtestit Test File

package uitest.tests;

import uitest.data.CredentialsDataProvider;
import uitest.TestNgTestBase;
import uitest.pageobjects.*;

import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

class LoginPage<TestParameters> extends TestNgTestBase implements ITest {
    private ThreadLocal<String> testName = new ThreadLocal<>();

    @BeforeMethod
    public void BeforeMethod(Method method, Object[] testData) {
        testName.set(String.format("%s [Username: '%s'; Pass: '%s']", method.getName(), testData[0], testData[1]));
    }

    @Override
    public String getTestName() {
        return testName.get();
    }

    // Providing the previously created Data Provider that will be used in the test
    @Test(dataProvider = "Credentials", dataProviderClass = CredentialsDataProvider.class)
    public void checkCredentials(String sUsername, String sPassword, Boolean shouldPass) throws InterruptedException {
        WebDriver driver = getDriver();

        // Initiating the Page Object
        LoginPageObject loginPO = new LoginPageObject(driver);

        // Open the Web Page
        loginPO.open("https://the-internet.herokuapp.com/login");

        // Enter the username, password and click on login
        loginPO.enterUsername(sUsername).enterPassword(sPassword).clickLogin();

        // Capture the login message after clicking the button
        String resultMessage = loginPO.getLoginMessage();

        // Indicate what username/password combination was used in case of test failure
        String iterationIndicator = String.format("For username '%s' and pass '%s', ", sUsername, sPassword);

        // Custom assert message
        Assert.assertEquals(resultMessage, shouldPass ? "You logged into a secure area!" : "Your username is invalid!",
                iterationIndicator);
    }
}