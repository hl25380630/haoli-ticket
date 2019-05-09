package com.haoli.ticket.service;

import java.util.Collections;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haoli.ticket.domain.DamaiClient;


@RestController
public class SeleniumService {
	
	private Logger logger = LoggerFactory.getLogger(SeleniumService.class);
	
	@Autowired
	private DamaiClient damaiClient;
    
	public static void main(String[] args) throws Exception {
		SeleniumService ss = new SeleniumService();
		ss.test();
	}

	
	@GetMapping("/test")
    public void test() throws Exception {
        System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        ChromeOptions options=new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        ChromeDriver browser = new ChromeDriver(options);
        browser.manage().window().maximize();
        String loginUrl = "https://passport.damai.cn/login?ru=https%3A%2F%2Fwww.damai.cn%2F" ;
        browser.get(loginUrl);
        Thread.sleep(1000);
        browser.switchTo().frame("alibaba-login-box");
        
        
//        WebElement dragger = browser.findElement(By.cssSelector("#nc_1_n1z"));
//        if(dragger!=null) {
//            Actions action = new Actions(browser);
//            action.clickAndHold(dragger).build().perform();
//            int total = 600;
//            for (int i = 0; i < total; i++) {
//            	if(i%2==0) {
//            		action.moveByOffset(2, 0).perform();
//            	}else {
//            		action.moveByOffset(-1, 0).perform();
//            	}
//                
//            }
//        }
        
        Thread.sleep(1000);
        browser.close();
        browser.quit();
    }

    
	

}
