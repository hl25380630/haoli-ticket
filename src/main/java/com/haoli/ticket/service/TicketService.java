package com.haoli.ticket.service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.cookie.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import com.haoli.sdk.web.exception.ConditionException;
import com.haoli.ticket.domain.DamaiClient;
import com.haoli.ticket.domain.DamiInfo;


@RestController
public class TicketService {
	
	@Autowired
	private DamaiClient damaiClient;
	
	@Value("${damai.mainPage.url}")
	private String damaiMainPage;

	
    public void buyDamaiTicket(DamiInfo map) throws Exception {
    	//设置chrome driver位置
    	String chromeDriverPath = map.getChromeDriverPath();
    	String searchKey = map.getSearchKey();
    	String detailItemKey = map.getDetailItemKey();
    	List<String> priceDetailList = map.getPriceDetailList();
    	List<String> sessionDetailList = map.getSessionDetailList();
        System.setProperty("webdriver.chrome.driver",chromeDriverPath);
        //配置浏览器
        ChromeOptions options=new ChromeOptions();
        //是否已开发者模式打开浏览器
        //options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        ChromeDriver browser = new ChromeDriver(options);
        browser.manage().window().maximize(); //设置窗口最大化
        //大麦网首页
        browser.get(damaiMainPage);
        //模拟登录，讲cookie写入浏览器，绕过登录验证
        this.setCookie(browser);
        Thread.sleep(1000);
        //刷新页面达到登录效果
        browser.navigate().refresh();
        Actions action = new Actions(browser);
        //搜寻想要查找的内容
        browser.findElementByClassName("input-search").sendKeys(searchKey);
        WebElement searchButton = browser.findElementByClassName("btn-search");//点击搜索按钮
        action.click(searchButton).perform();
        List<WebElement> itemList = browser.findElementsByTagName("a");
        List<String> targetUrlList = new ArrayList<String>();
        Map<String, WebElement> elementMap = new HashMap<String, WebElement>();
        Map<String, String> urlTitleMap = new HashMap<String, String>();
        for(WebElement item : itemList) {
        	String url = item.getAttribute("href");
        	if(url.contains("https://detail.damai.cn/item.htm")) {
        		url = URLDecoder.decode(url, "UTF-8");
        		targetUrlList.add(url);
        		elementMap.put(url, item);
                String params = url.split("\\?")[1];
                String[] paramArray = params.split("\\&");
                String title = "";
                for(String param : paramArray) {
                	if(param.contains("clicktitle")) {
                		title = param.split("=")[1];
                	}
                }
                urlTitleMap.put(title, url);
        	}
        }
        String targetUrl = "";
        String targetTitle = "";
        for(Map.Entry<String, String> entry : urlTitleMap.entrySet()) {
        	String title = entry.getKey();
        	if(title.equals(detailItemKey)) {
        		targetTitle = title;
        		targetUrl = entry.getValue();
        	}
        }
        WebElement item = elementMap.get(targetUrl);
        //找到要购买的item，点击进入详情页
        action.click(item).perform();
        //根据列表页的标题内容切换到想要购买item的详情页
        Set<String> tabList = browser.getWindowHandles();
        for(String tab : tabList) {
        	browser.switchTo().window(tab);
        	String tabTile = browser.getTitle();
        	if(tabTile.contains(targetTitle)) {
        		break;
        	}
        }
        //记录商品详情页url
        String itemDetailPageUrl = browser.getCurrentUrl();
        List<WebElement> sessionList = browser.findElementsByClassName("select_right_list_item");
        Map<String, WebElement> priceElementMap = new HashMap<String, WebElement>();
        List<WebElement> elementList = browser.findElementsByClassName("sku_item");
        //选择要购买的场次，点击购买
        boolean flag1 = false;
        WebElement targetSession = sessionList.get(0);
        WebElement targetItem = elementList.get(0);
        label1:
        for(String sessionDetail : sessionDetailList) {
            for(WebElement session : sessionList) {
            	String sessionText = session.getText();
            	if(sessionText.contains(sessionDetail) && !sessionText.contains("缺货登记") && !sessionText.contains("无票") && !sessionText.contains("暂不可售")) {
                    for(String priceDetail : priceDetailList) {
                    	for(WebElement element : elementList) {
                    		String text = element.getText();
                        	if(text.contains(priceDetail) && !text.contains("缺货登记") && !text.contains("无票") && !text.contains("暂不可售") && !text.contains("开售登记")) {
                        		priceElementMap.put(priceDetail, element);
                        		targetItem = element;
                        		targetSession = session;
                        		flag1 = true;
                        		break label1;
                        	}
                    	}
                    }
            	}
            }
        }
        if(flag1 == false) {
        	throw new ConditionException("没有符合条件的商品");
        }
        action.click(targetSession).perform();
		action.click(targetItem).perform();
        //选择好要购买的商品后点提交
        List<WebElement> buyButtonList = browser.findElementsByClassName("buybtn");
        if(buyButtonList.size() ==1) {
        	action.click(buyButtonList.get(0)).perform();
        }else {
            for(WebElement element : buyButtonList) {
            	String text = element.getText();
            	if("立即购买".equals(text) || "立即预定".equals(text)) {
            		action.click(element).perform();
            	}
            }
        }
        //此时应切换到确认订单页
        for(String tab : browser.getWindowHandles()) {
        	browser.switchTo().window(tab);
        	String tabTile = browser.getTitle();
        	if(tabTile.contains("确认订单")) {
        		break;
        	}
        }
        //记录下单页面地址
        String submitPageUrl = browser.getCurrentUrl();
        boolean flag = true;
        while(flag) {
        	browser.get(submitPageUrl);
        	this.submitOrder(browser, action);
        	Thread.sleep(2000);
            for(String tab : browser.getWindowHandles()) {
            	browser.switchTo().window(tab);
            	String tabTitle = browser.getTitle();
            	if(tabTitle.contains("支付宝")) {
            		flag = false;
            		break;
            	}
            }
        }
        browser.close();
        browser.quit();
    }
    
    
    public void submitOrder(ChromeDriver browser, Actions action) throws Exception {
        List<WebElement> checkBoxList = browser.findElementsByClassName("next-checkbox-inner");
        for(WebElement checkBox : checkBoxList) {
        	action.click(checkBox).perform();
        }
        Thread.sleep(500);
        List<WebElement> submitButtonList = browser.findElementsByTagName("button");
        WebElement finalSubmitButton = null;
        for(WebElement submitButton : submitButtonList) {
        	String text = submitButton.getText();
        	if("同意以上协议并提交订单".equals(text)) {
        		finalSubmitButton = submitButton;
        	}
        }
        action.click(finalSubmitButton).perform();
    }
	
	public void setCookie(ChromeDriver browser) {
        List<Cookie> cookieList = damaiClient.getCookieList();
        for(Cookie cookie : cookieList) {
        	String name = cookie.getName();
        	String value = cookie.getValue();
        	String path = cookie.getPath();
        	Date expireDate = cookie.getExpiryDate();
        	String domain = "." + cookie.getDomain();
        	org.openqa.selenium.Cookie scookie = new org.openqa.selenium.Cookie(name, value, domain, path, expireDate, true);
        	browser.manage().addCookie(scookie);
        }
	}

}
