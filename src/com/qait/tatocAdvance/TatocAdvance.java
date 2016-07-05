package com.qait.tatocAdvance;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.qait.commonFunctions.GeneralActions;
import com.qait.commonFunctions.Utility;


public class TatocAdvance {

	public static void main(String[] args) throws Exception{

		GeneralActions action= new GeneralActions();
		WebDriver webdriver=action.getDriver(Utility.getConfigValue("browser"));
		action.setDriver(webdriver);
		action.getURL(Utility.getConfigValue("url"));

		//Goto Tatoc Advance Page
		webdriver.findElement(By.partialLinkText("tatoc")).click();
		webdriver.findElement(By.partialLinkText("Advanced Course")).click();
//------------------------------------------------------------------------------------------------------	    
		//Hover Menu
		Actions actions= new Actions(webdriver);
		WebElement menu2= webdriver.findElement(By.className("menutitle")); 
		actions.moveToElement(menu2);

		WebElement goNext= webdriver.findElement(By.xpath("//span[@class='menuitem' and text()='Go Next']"));
		actions.moveToElement(goNext);
		actions.click().build().perform();
		webdriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//------------------------------------------------------------------------------------------------------
		//Query Gate
		try {
			//Connection URL Syntax: "jdbc:mysql://ipaddress:portnumber/db_name"		
			String dbUrl = "jdbc:mysql://10.0.1.86/tatoc";
			
			//Database Username		
			String username = "tatocuser";
			
			//Database Password		
			String password = "tatoc01";
			
			//Load mysql jdbc driver		
			Class.forName("com.mysql.jdbc.Driver");
			
			//Create Connection to DB
			Connection con =  (Connection) DriverManager.getConnection(dbUrl, username, password);
			String symbol=webdriver.findElement(By.id("symboldisplay")).getText();
			
			//Create Statement Object		
			PreparedStatement pstmt= con.prepareStatement("select id from identity where symbol=?;");				
			pstmt.setString(1,symbol);
			
			// Execute the SQL Query. Store results in ResultSet		
			ResultSet rs= pstmt.executeQuery();
			int id=0;
			
			// While Loop to iterate through data for name		
			while(rs.next()){
				id = Integer.parseInt(rs.getString("id"));
			}
			
			pstmt= con.prepareStatement("select name, passkey from credentials where id=?;");
			pstmt.setInt(1, id);

			rs= pstmt.executeQuery();
			String name="";
			String passkey="";
			while(rs.next()) {
				name=rs.getString("name");
				passkey=rs.getString("passkey");
			}
			webdriver.findElement(By.id("name")).sendKeys(name);
			webdriver.findElement(By.id("passkey")).sendKeys(passkey);
			webdriver.findElement(By.id("submit")).click();
			con.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("Exception: "+ e);
		}
		webdriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		
//---------------------------------------------------------------------------------------------------		
		//Ooyala Video Player
		double totalTime=0;
		if(webdriver instanceof JavascriptExecutor) {
			JavascriptExecutor js = (JavascriptExecutor)webdriver;
			Thread.sleep(3000);
			totalTime=(double)js.executeScript("return player.getTotalTime();");
			js.executeScript("player.play();");
			Thread.sleep((long) (totalTime+5)*1000);
			webdriver.findElement(By.partialLinkText("Proceed")).click();
		}
		else {
			throw new IllegalStateException("driver doesnot support javaScript");
		}

//---------------------------------------------------------------------------------------------------
		//Restful
		String sessionId=webdriver.findElement(By.id("session_id")).getText();
		String [] sessionArr=sessionId.split(": ");

		//Rest Service to generate token: GET http://10.0.1.86/tatoc/advanced/rest/service/token/[Session ID] . Response is in JSON	    	
		URL geturl = new URL("http://10.0.1.86/tatoc/advanced/rest/service/token/" + sessionArr[1]);
		HttpURLConnection getconn = (HttpURLConnection) geturl.openConnection();
		getconn.setRequestMethod("GET");
		getconn.setRequestProperty("Accept", "application/json");

		if (getconn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ getconn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((getconn.getInputStream())));
		String output;
		String restful = new String();
		while ((output = br.readLine()) != null) {
			restful=restful.concat(output);
		}
		br.close();
		String response[]= restful.split(":\"");
		String token[]= response[1].split("\"");
		String jsonToken= token[0];

		try {
			URL posturl = new URL("http://10.0.1.86/tatoc/advanced/rest/service/register");
			HttpURLConnection postconn = (HttpURLConnection) posturl.openConnection();
			postconn.setDoOutput(true);
			postconn.setRequestMethod("POST");

			postconn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String input = "id="+sessionArr[1]+"& signature="+jsonToken+"&allow_access=1";

			DataOutputStream wr = new DataOutputStream(postconn.getOutputStream());
			wr.writeBytes(input);
			wr.flush();
			wr.close();

			int responseCode = postconn.getResponseCode();
			postconn.disconnect();
			Thread.sleep(3000);
			webdriver.findElement(By.partialLinkText("Proceed")).click();     
		}
		catch(Exception e) {
			System.out.println("exception: "+ e);
		}
//---------------------------------------------------------------------------------------------------
		//part5: File Handle

		webdriver.findElement(By.partialLinkText("Download File")).click();
		Thread.sleep(5000);
		
		File file = new File("/path-to-download-folder/file_handle_test.dat");

		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties prop = new Properties();

		//load properties file
		try {
			prop.load(fileInput);
		} catch (IOException e) {
			e.printStackTrace();
		}

		webdriver.findElement(By.id("signature")).sendKeys(prop.getProperty("Signature"));
		webdriver.findElement(By.className("submit")).click();

//---------------------------------------------------------------------------------------------------		  
		//closing webdriver
		Thread.sleep(3000);
		webdriver.close();
	}
}
