package com.cai.qa.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import com.cai.qa.core.Consts;
import com.cai.qa.core.GlobalObjectFile;
import com.cai.qa.core.Utility;

public class WebDriverUtil implements WebUtil {
	private WebDriver browserObject;
	private static final Log log = LogFactory.getLog(WebDriverUtil.class);

	/**
	 * Get Browser for execution
	 * 
	 * @return WebDriver object
	 */
	public WebDriver getBrowserObject() {
		return browserObject;
	}

	/**
	 * Set Browser for execution
	 * 
	 * @param browserObject
	 */
	private void setBrowserObject(WebDriver browserObject) {
		if(browserObject==null)
			return;
		this.browserObject = browserObject;
	}
	 
	
	public static WebDriver getBrowserForTestRun(){
		String driverType = (Utility
				.getPropertyValue(Consts.APPLICATION_BROWSER) == null) ? "IE"
				: Utility.getPropertyValue(Consts.APPLICATION_BROWSER);
		log.debug("Set Browser Type:"+driverType);
		if (driverType.equalsIgnoreCase("Chrome")) {
			return getChromeDriver();
		} else if (driverType.equalsIgnoreCase("Firefox")) {
			return getFirefoxDriver();
		} else {
			return getIEDriver();
		}
		
	}

	/**
	 * Initialize the Browser
	 * 
	 * @param browserObject
	 */
	public WebDriverUtil(WebDriver browserObject) {
		setBrowserObject(browserObject);
	}

	/**
	 * Set Google Chrome browser for execution
	 */
	private static WebDriver getChromeDriver() {
		String fileName = Utility.getClassPathResourcePath("chromedriver.exe");
		log.debug("setChromeDriver fileName:" + fileName);
		if (fileName != null) {
			System.setProperty("webdriver.chrome.driver", fileName);
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("start-maximized");
			chromeOptions.addArguments("test-type");
			chromeOptions.addArguments("chrome.switches");
			chromeOptions.addArguments("--disable-extensions");
			return (new ChromeDriver(chromeOptions));
		}
		return null;
	}

	/**
	 * Set Google Internet Explorer browser for execution
	 */
	private static WebDriver getIEDriver() {
		String fileName = Utility
				.getClassPathResourcePath("IEDriverServer.exe");
		log.debug("setIEDriver fileName:" + fileName);
		if (fileName != null) {
			System.setProperty("webdriver.ie.driver", fileName);
			DesiredCapabilities capabilities = DesiredCapabilities
					.internetExplorer();
			capabilities
					.setCapability(
							InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
							true);
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			capabilities.setCapability(CapabilityType.SUPPORTS_FINDING_BY_CSS,
					true);
			capabilities.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
			return (new InternetExplorerDriver(capabilities));
		}
		return null;
	}

	/**
	 * Set Google Firefox browser for execution
	 */
	private static WebDriver getFirefoxDriver() {
		log.debug("setFirefoxDriver");
		return (new FirefoxDriver());
	}

	/**
	 * Load Application URL
	 * 
	 * @param strURL
	 */
	public void loadURL(String strURL) {
		this.browserObject.get(strURL);
		this.browserObject.manage().window().maximize();
		log.debug("loadURL:" + strURL);
	}

	/**
	 * navigate to URL
	 * 
	 * @param strURL
	 */
	public void navigateURL(String strURL) {
		this.browserObject.navigate().to(strURL);
		log.debug("navigateURL:" + strURL);
	}

	/**
	 * Get Current browser URL
	 * 
	 * @return URL
	 */
	public String getCurrentURL() {
		log.debug("getCurrentURL:" + this.browserObject.getCurrentUrl());
		return this.browserObject.getCurrentUrl();
	}

	/**
	 * find WebElement in browser
	 * 
	 * @param by
	 * @return WebElement
	 */
	public WebElement findElement(By by) {
		try {
			if (this.browserObject.findElements(by).size() != 0)
				return (WebElement) this.browserObject.findElement(by);
		} catch (Exception e) {
			log.debug("findElement Exception:" + e);
			return null;
		}
		return null;
	}

	/**
	 * get All WebElements from browser
	 * 
	 * @param by
	 * @return all WebElements
	 */
	public List<WebElement> getAllWebElements(By by) {
		try {
			if (this.browserObject.findElements(by).size() != 0)
				return this.browserObject.findElements(by);
		} catch (Exception e) {
			log.debug("getAllWebElements Exception:" + e);
			return null;
		}
		return null;
	}

	/**
	 * Find WebElement And Set Text
	 * 
	 * @param by
	 * @param strText
	 */

	public void findElementAndSetText(By by, String strText) {
		if (strText == null || strText.isEmpty())
			return;
		WebElement webElemnet = findElement(by);
		if (webElemnet != null) {
			try {
				webElemnet.clear();
				webElemnet.sendKeys(strText.trim());
			} catch (Exception e) {
				log.debug("findElementAndSetText Exception:" + e);
			}
		}
	}

	/**
	 * Find WebElement, Set the Text and Auto Complete the other fileds data
	 * 
	 * @param by
	 * @param strText
	 */
	public void findElementAndSetTextForAutoComplete(By by, String strText) {
		if (strText == null || strText.isEmpty())
			return;
		findElementAndSetText(by, strText);
		findElementAndSendKeys(by, Keys.ARROW_DOWN);
		findElementAndSendKeys(by, Keys.TAB);
	}

	/**
	 * Find WebElement And send keys
	 * 
	 * @param by
	 * @param keys
	 */
	public void findElementAndSendKeys(By by, Keys keys) {
		if (keys == null)
			return;
		WebElement webElemnet = findElement(by);
		if (webElemnet != null) {
			try {
				webElemnet.sendKeys(keys);
			} catch (Exception e) {
				log.debug("findElementAndSetText Exception:" + e);
			}
		}
	}

	/**
	 * Find WebElement And clear the data
	 * 
	 * @param by
	 */
	public void findElementAndClear(By by) {

		WebElement webElement = findElement(by);
		if (webElement != null) {
			try {
				webElement.clear();
			} catch (Exception e) {
				log.debug("findElementAndClear Exception:" + e);
			}
		}
	}

	/**
	 * Find WebElement And Click
	 * 
	 * @param by
	 */
	public void findElementAndClick(By by) {
		WebElement webElement = findElement(by);
		if (webElement != null) {
			try {
				webElement.click();
			} catch (Exception e) {
				log.debug("findElementAndClick Exception:" + e);
			}
		}
	}

	/**
	 * Find WebElement And Select Radio / Checkbox value
	 * 
	 * @param by
	 * @param isSelect
	 */
	public void findElementAndSelectRadioButtonOrCheckbox(By by,
			boolean isSelect) {
		WebElement webElement = findElement(by);
		if (webElement != null) {
			try {
				boolean actualStatus = webElement.isSelected();
				if (isSelect) {
					if (!actualStatus) {
						webElement.click();
					}
				} else {
					if (actualStatus) {
						webElement.click();
					}
				}
			} catch (Exception e) {
				log.debug("findElementAndSelectRadioButtonOrCheckbox Exception:"
						+ e);
			}
		}
	}

	/**
	 * Find WebElement And Select value in List Box
	 * 
	 * @param by
	 * @param strText
	 */
	public void findElementAndSelectText(By by, String strText) {
		if (strText == null || strText.isEmpty())
			return;
		WebElement webElemnet = findElement(by);
		if (webElemnet != null) {
			try {
				// System.out.println(strText.trim());
				new Select(webElemnet).selectByVisibleText(strText.trim());
			} catch (Exception e) {
				log.debug("findElementAndSelectText Exception:" + e);
				try {
					List<WebElement> allOptions = new Select(webElemnet)
							.getOptions();
					System.out.print("{");
					for (WebElement option : allOptions) {
						System.out.print("'" + option.getAttribute("value")
								+ "':'" + option.getText() + "'	");
					}
					System.out.println("}");
				} catch (Exception x) {
					log.debug("findElementAndSelectText Exception:" + x);
				}
			}
		}
	}

	/**
	 * Find WebElement And Select value in List Box
	 * 
	 * @param by
	 * @param strText
	 */
	public void findElementAndSelectListValueUsingPartialText(By by,
			String strText) {
		if (strText == null || strText.isEmpty())
			return;
		WebElement webElemnet = findElement(by);
		if (webElemnet != null) {
			try {
				List<WebElement> allOptions = new Select(webElemnet)
						.getOptions();
				for (WebElement option : allOptions) {
					if (option.getText().indexOf(strText.trim()) >= 0) {
						strText = option.getText();
						break;
					}
				}
				new Select(webElemnet).selectByVisibleText(strText.trim());
			} catch (Exception e) {
				log.debug("findElementAndSelectListValueUsingPartialText Exception:"
						+ e);
				try {
					List<WebElement> allOptions = new Select(webElemnet)
							.getOptions();
					System.out.print("{");
					for (WebElement option : allOptions) {
						System.out.print("'" + option.getAttribute("value")
								+ "':'" + option.getText() + "'	");
					}
					System.out.println("}");
				} catch (Exception x) {
					log.debug("findElementAndSelectListValueUsingPartialText Exception:"
							+ x);
				}
			}
		}
	}

	/**
	 * Find WebElement And Select value in List Box
	 * 
	 * @param by
	 * @param strValue
	 */
	public void findElementAndSelectValue(By by, String strValue) {
		if (strValue == null || strValue.isEmpty())
			return;
		WebElement webElemnet = findElement(by);
		if (webElemnet != null) {
			try {
				new Select(webElemnet).selectByValue(strValue.trim());
			} catch (Exception e) {
				log.debug("findElementAndSelectValue Exception:" + e);
			}
		}
	}

	/**
	 * Find WebElement And Select value in List Box based on index
	 * 
	 * @param by
	 * @param intIndex
	 */
	public void findElementAndSelectIndex(By by, int intIndex) {
		WebElement webElemnet = findElement(by);
		if (webElemnet != null) {
			try {
				new Select(webElemnet).selectByIndex(intIndex);
			} catch (Exception e) {
				log.debug("findElementAndSelectIndex Exception:" + e);
			}
		}
	}

	/**
	 * Find WebElement And Get Attribute value
	 * 
	 * @param by
	 * @param strAttributeName
	 * @return
	 */
	public String findElementAndGetAttribute(By by, String strAttributeName) {
		WebElement webElement = findElement(by);
		if (webElement != null) {
			return webElement.getAttribute(strAttributeName);
		}
		log.debug("findElementAndGetAttribute No webElement found!");
		return null;
	}

	/**
	 * Find WebElement And Get text
	 * 
	 * @param by
	 * @return text
	 */
	public String findElementAndGetText(By by) {
		WebElement webElement = findElement(by);
		if (webElement != null) {
			return webElement.getText();
		}
		log.debug("findElementAndGetText No webElement found!");
		return null;
	}

	/**
	 * Check if WebElement exists
	 * 
	 * @param by
	 * @return true/false
	 */
	public boolean isObjectExists(By by) {
		if (findElement(by) != null)
			return true;
		log.debug("isObjectExists No webElement found!");
		return false;
	}

	/**
	 * browser alerts: accept Alert
	 */
	public void acceptAlert() {
		try {
			Alert alert = this.browserObject.switchTo().alert();
			alert.accept();
		} catch (Exception e) {
			log.debug("acceptAlert Exception:" + e);
		}
	}

	/**
	 * browser alerts: Check for Existance
	 * 
	 * @return
	 */
	public boolean checkAlertExistance() {
		try {
			@SuppressWarnings("unused")
			Alert alert = this.browserObject.switchTo().alert();
			return true;
		} catch (Exception e) {
			log.debug("acceptAlert Exception:" + e);
			return false;
		}
	}

	/**
	 * Get Driver type
	 * 
	 * @return FF/IE/Chrome
	 */
	public String getDriverType() {
		if (this.browserObject instanceof FirefoxDriver) {
			return "FF";
		} else if (this.browserObject instanceof ChromeDriver) {
			return "Chrome";
		} else if (this.browserObject instanceof InternetExplorerDriver) {
			return "IE";
		}
		return null;
	}

	/**
	 * Execute JavaScript statement
	 * 
	 * @param scriptStatement
	 */
	public void JavaScriptExecutor(String scriptStatement) {
		try {
			((org.openqa.selenium.JavascriptExecutor) this.browserObject)
					.executeScript(scriptStatement);
		} catch (Exception e) {
			log.debug("JavaScriptExecutor Exception:" + e);
		}
	}

	/**
	 * Execute JavaScript statement
	 * 
	 * @param scriptStatement
	 * @param by
	 */
	public void JavaScriptExecutor(String scriptStatement, By by) {
		// arguments[0].click()
		try {
			WebElement element = findElement(by);
			if (element != null) {
				((org.openqa.selenium.JavascriptExecutor) this.browserObject)
						.executeScript(scriptStatement, element);
			}
		} catch (Exception e) {
			log.debug("JavaScriptExecutor Exception:" + e);
		}
	}

	/**
	 * Execute JavaScript statement and get Return value
	 * 
	 * @param scriptStatement
	 * @return Object
	 */
	public Object JavaScriptExecutorAndGetReturnValue(String scriptStatement) {
		try {
			log.debug("JavaScriptExecutorAndGetReturnValue scriptStatement:"
					+ scriptStatement);
			Object object = ((org.openqa.selenium.JavascriptExecutor) this.browserObject)
					.executeScript(scriptStatement);
			return object;
		} catch (Exception e) {
			log.debug("JavaScriptExecutorAndGetReturnValue Exception:" + e);
			return null;
		}
	}

	/**
	 * mouse hover On Element
	 * 
	 * @param by
	 */
	public void hoverOnElement(By by) {
		try {
			Actions actions = new Actions(this.browserObject);
			WebElement webElemnet = findElement(by);

			if (webElemnet != null) {
				// actions.moveToElement(webElemnet).build().perform();
				actions.clickAndHold(webElemnet).perform();
			}
		} catch (Exception e) {
			log.debug("hoverOnElement Exception:" + e);
		}
	}

	/**
	 * print WebElement Attributes for the list of attributes
	 * 
	 * @param by
	 * @param listOfAttributes
	 */
	public void printWebElementAttributes(By by, List<String> listOfAttributes) {
		List<WebElement> listOfObjects = this.getAllWebElements(by);
		if (listOfObjects != null) {
			System.out.println(listOfObjects.size());
			Iterator<WebElement> listOfObjectsIterator = listOfObjects
					.iterator();
			while (listOfObjectsIterator.hasNext()) {
				WebElement element = listOfObjectsIterator.next();

				Iterator<String> listOfAttributesIterator = listOfAttributes
						.iterator();
				while (listOfAttributesIterator.hasNext()) {
					String attributeName = listOfAttributesIterator.next();
					System.out.print(element.isDisplayed() + ";"
							+ attributeName + ":"
							+ element.getAttribute(attributeName) + ";");
				}
				System.out.println();
			}
		}
	}

	/**
	 * Read Web Table Data into Map
	 * 
	 * @param by
	 * @return
	 */
	public Map<String, List<String>> getWebTableData(By by) {
		try {
			Map<String, List<String>> tableData = new HashMap<String, List<String>>();
			WebElement htmltable = findElement(by);
			List<WebElement> rows = htmltable.findElements(By.tagName("tr"));
			Iterator<WebElement> rowsItr = rows.iterator();
			int rowNum = 0;
			log.debug("getWebTableData rows.size:" + rows.size());
			while (rowsItr.hasNext()) {
				WebElement row = rowsItr.next();
				if (!row.isDisplayed())
					continue;
				rowNum++;
				List<WebElement> headers = row.findElements(By.tagName("th"));
				List<WebElement> columns = row.findElements(By.tagName("td"));
				if (headers.size() > 0) {
					Iterator<WebElement> headersItr = headers.iterator();
					List<String> rowData = new ArrayList<String>();
					while (headersItr.hasNext()) {
						WebElement headerNode = headersItr.next();
						String strText = headerNode.getText();
						rowData.add(strText);
					}
					tableData.put("th", rowData);
				}

				if (columns.size() > 0) {
					Iterator<WebElement> columnsItr = columns.iterator();
					List<String> rowData = new ArrayList<String>();
					while (columnsItr.hasNext()) {
						WebElement columnNode = columnsItr.next();
						String strText = columnNode.getText();
						rowData.add(strText);
					}
					tableData.put("row" + rowNum, rowData);
				}
			}
			return tableData;
		} catch (Exception e) {
			log.debug("getWebTableData Exception:" + e);
			return null;
		}
	}

	/**
	 * Close Browser and Cleanup
	 */
	public void closeBrowser() {
		this.browserObject.close();
		this.browserObject.quit();
		this.browserObject = null;
	}

	/**
	 * Get By Object from JSON file
	 * 
	 * @param strFieldId
	 * @return
	 */
	public By getWebObject(String strFieldId) {
		By by = GlobalObjectFile.getWebObject(strFieldId);
		if (by == null) {
			log.debug("No Global Object found! " + strFieldId);
			return null;
		}
		log.debug("Global Object found! " + strFieldId);
		return by;
	}

	/**
	 * Click on Web Object
	 * 
	 * @param strFieldId
	 */
	public void click(String strFieldId) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndClick(by);
		}
	}

	/**
	 * Enter Text for Web Edit Object
	 * 
	 * @param strFieldId
	 * @param strText
	 */
	public void setText(String strFieldId, String strText) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndSetText(by, strText);
		}
	}

	/**
	 * Send Keys for Web Object
	 * 
	 * @param strFieldId
	 * @param keys
	 */
	public void sendKeys(String strFieldId, Keys keys) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndSendKeys(by, keys);
		}
	}

	/**
	 * Java Script Executor
	 * 
	 * @param strFieldId
	 * @param scriptStatement
	 */
	public void JavaScriptExecutor(String strFieldId, String scriptStatement) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			JavaScriptExecutor(scriptStatement, by);
		}
	}

	/**
	 * select Radio Button
	 * 
	 * @param strFieldId
	 * @param isSelect
	 */
	public void selectRadioButton(String strFieldId, boolean isSelect) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndSelectRadioButtonOrCheckbox(by, isSelect);
		}
	}

	/**
	 * Select Checkbox
	 * 
	 * @param strFieldId
	 * @param isSelect
	 */
	public void selctCheckbox(String strFieldId, boolean isSelect) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndSelectRadioButtonOrCheckbox(by, isSelect);
		}
	}

	/**
	 * Select List Box Value
	 * 
	 * @param strFieldId
	 * @param strText
	 */
	public void selectListValue(String strFieldId, String strText) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndSelectText(by, strText);
		}
	}

	/**
	 * Select List Box Value Using Partial Text
	 * 
	 * @param strFieldId
	 * @param strText
	 */
	public void selectListValueUsingPartialText(String strFieldId,
			String strText) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndSelectListValueUsingPartialText(by, strText);
		}
	}

	/**
	 * Select List Box Value Using Index
	 * 
	 * @param strFieldId
	 * @param intIndex
	 */
	public void selectListValueUsingIndex(String strFieldId, int intIndex) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndSelectIndex(by, intIndex);
		}
	}

	/**
	 * Select List Box Value Using Value
	 * 
	 * @param strFieldId
	 * @param strValue
	 */
	public void selectListValueByValue(String strFieldId, String strValue) {
		By by = getWebObject(strFieldId);
		if (by != null) {
			findElementAndSelectValue(by, strValue);
		}
	}

}
