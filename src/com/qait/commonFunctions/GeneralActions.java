package com.qait.commonFunctions;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class GeneralActions {
	public WebDriver webdriver;
	
	public void setDriver(WebDriver webdriver) {
		this.webdriver=webdriver;
	}
	
	public void getURL(String url) {
		this.webdriver.get(url);
	}
	
	public WebDriver getDriver(String browserName) {
		if(browserName.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver", "/home/vijaykumar/Downloads/chromeDriver/chromedriver");
			return new ChromeDriver();
		}
		if(browserName.equals("firefox")) {
			File binaryPath= new File(Utility.getConfigValue("binaryPath"));
			FirefoxBinary ffBinary= new FirefoxBinary(binaryPath);
			FirefoxProfile ffProfile= new FirefoxProfile();
			return new FirefoxDriver(ffBinary, ffProfile);
		}
		if(browserName.equals("ie")) {
			return new InternetExplorerDriver();
		}
		return null;
	}
}
