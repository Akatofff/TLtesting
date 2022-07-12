package org.example;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
public class Test {
    public final String DRIVER_PATH = "C:/Users/Акатов/Desktop/Testing/chromedriver.exe";
    public final String DRIVER_TYPE = "webdriver.chrome.driver";
    public WebDriver driver;
    public String BASE_URL = "https://www.travelline.ru/";
    public final String LINKS_ATTRIBUTE = "href";
    public final String LINKS_TAG = "a";
    @Before
    public void driverSetup(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications","--ignore-certificate-errors","--disable-extensions");
        System.setProperty(DRIVER_TYPE,DRIVER_PATH);
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get(BASE_URL);
    }

    @org.junit.Test
    public void BrokenLinks() throws Exception{
        List<WebElement> allLinks = driver.findElements(By.tagName(LINKS_TAG));
        for(WebElement link:allLinks){
            try {
                String urlLink = link.getAttribute(LINKS_ATTRIBUTE);
                URL url = new URL(urlLink);
                HttpURLConnection httpURLConnect=(HttpURLConnection)url.openConnection();
                httpURLConnect.setConnectTimeout(5000);
                httpURLConnect.connect();
                if(httpURLConnect.getResponseCode()>=400)
                {
                    try(FileWriter broken = new FileWriter("broken.txt")){
                        broken.write("Broken Link:" + urlLink);
                    }catch(Exception e){
                    }
                    System.out.println(urlLink+" - "+httpURLConnect.getResponseMessage()+"is a broken link");
                }
                else{
                    try(FileWriter working = new FileWriter("working.txt")){
                        working.write("Working links:" + urlLink);
                    }
                    System.out.println(urlLink+" - "+httpURLConnect.getResponseMessage());
                }


            }catch (Exception e) {
                }

        }

    }
    @After
    public void closeDriver(){
        driver.close();
    }

}