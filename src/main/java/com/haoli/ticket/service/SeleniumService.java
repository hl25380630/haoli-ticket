package com.haoli.ticket.service;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
        ChromeDriver browser = new ChromeDriver(options);
        browser.manage().window().maximize();
        String loginUrl = "https://passport.damai.cn/login?ru=https%3A%2F%2Fwww.damai.cn%2F" ;
        browser.get(loginUrl);
//        List<Cookie> cookieList = damaiClient.getCookieList();
//        for(Cookie cookie : cookieList) {
//        	String name = cookie.getName();
//        	String value = cookie.getValue();
//        	String path = cookie.getPath();
//        	org.openqa.selenium.Cookie scookie = new org.openqa.selenium.Cookie(name, value);
//        	browser.manage().addCookie(scookie);
//        }
//        browser.navigate().refresh();
        Thread.sleep(1000);
        
        
        
        
        browser.close();
        browser.quit();
    }

    
	

}
