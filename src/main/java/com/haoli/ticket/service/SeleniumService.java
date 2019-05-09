package com.haoli.ticket.service;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;



public class SeleniumService {
	
	public static void main(String[] args) throws Exception {
		SeleniumService ss = new SeleniumService();
		ss.test();
	}
	

    public void test() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        ChromeDriver browser = new ChromeDriver();
        browser.manage().window().maximize();
        String loginUrl = "https://passport.damai.cn/login?ru=https%3A%2F%2Fwww.damai.cn%2F" ;
        browser.get(loginUrl);
        Thread.sleep(1000);
        browser.switchTo().frame("alibaba-login-box");
        WebElement userNameElement = browser.findElementById("fm-login-id");
        userNameElement.sendKeys("18515672163");
        WebElement passwordElement = browser.findElementById("fm-login-password");
        passwordElement.sendKeys("Li134679258!");
        
        WebElement dragger = browser.findElementByCssSelector("#nc_1_n1z");
        if(dragger != null) {
            Actions action = new Actions(browser);
            action.clickAndHold(dragger).build().perform();
            for (int i = 0; i < 257; i++) {
                action.moveByOffset(1, 0).perform();
            }
        }

        browser.close();
        browser.quit();
    }

}
