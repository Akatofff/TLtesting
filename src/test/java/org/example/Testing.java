package org.example;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Testing {
    public final String DRIVER_PATH = "C:/Users/Акатов/Desktop/Testing/chromedriver.exe";
    public final String DRIVER_TYPE = "webdriver.chrome.driver";
    public WebDriver driver;
    public String BASE_URL = "https://travelline.ru/";
    public final String LINKS_ATTRIBUTE = "href";
    public final String LINKS_TAG = "a";
    Set<String> allLinks = new HashSet<>();
    FileWriter broken = new FileWriter("broken.txt");
    FileWriter working = new FileWriter("working.txt");

    public Testing() throws IOException {
    }

    @Before

    public void driverSetup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications", "--ignore-certificate-errors", "--disable-extensions");
        System.setProperty(DRIVER_TYPE, DRIVER_PATH);
        driver = new ChromeDriver(options);
    }

    @org.junit.Test

    public void testMethod() {
        brokenLinks(BASE_URL);
    }

    public void brokenLinks(String rootUrl) {
        driver.get(rootUrl);
        var currentPageLinks = driver.findElements(By.tagName(LINKS_TAG))
                .stream().map(e -> e.getAttribute(LINKS_ATTRIBUTE)).collect(Collectors.toSet());
        currentPageLinks.removeAll(allLinks);
        allLinks.addAll(currentPageLinks);
        for (var link : currentPageLinks) {
            try {
                URL url = new URL(link);
                HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
                httpURLConnect.setConnectTimeout(5000);
                httpURLConnect.connect();

                    if (httpURLConnect.getResponseCode() >= 400) {
                        try {
                            broken.write("Broken Link: " + link + " | Response: " + httpURLConnect.getResponseMessage() + "\n");
                            broken.flush();
                        } catch (Exception e) {
                        }
                        System.out.println(link + " - " + httpURLConnect.getResponseMessage() + " - is a broken link");
                    } else {
                        if (link.contains("travelline.ru")) {
                            brokenLinks(link);
                        }
                        try {
                            working.write("Working link: " + link + " | Response: " + httpURLConnect.getResponseMessage() + "\n");
                            working.flush();
                            System.out.println(link + " - " + httpURLConnect.getResponseMessage());
                        } catch (Exception e) {
                        }
                    }


            } catch (Exception e) {
            }
        }
    }

    @After

    public void closeDriver() throws IOException {
        driver.close();
        working.close();
        broken.close();
    }
}