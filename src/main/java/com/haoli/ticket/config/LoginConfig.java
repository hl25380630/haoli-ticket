package com.haoli.ticket.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSONObject;
import com.haoli.ticket.domain.DamaiClient;

@Configuration
public class LoginConfig {
	
	private Logger logger = LoggerFactory.getLogger(LoginConfig.class);

    @Value("${damai.userName}")
    private String userName;
    
    @Value("${damai.password}")
    private String password;
    
    @Value("${damai.userAgent}")
    private String userAgent;
	
    @Value("${damai.ua}")
    private String ua;
    
    @Value("${damai.csrf_token}")
    private String csrfToken;
    
    @Value("${damai.hsiz}")
    private String hsiz;
    
    @Value("${damai.umidToken}")
    private String umidToken;
	
	@Value("${damai.nocAppKey}")
	private String nocAppKey;
	
	@Value("${damai.csessionid}")
	private String csessionid;
	
	@Value("${damai.sig}")
	private String sig;
	
    
	@Bean
	public DamaiClient generateDamaiClient() throws Exception{
		DamaiClient damaiClient = new DamaiClient();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Cookie> cookieList = this.login();
        for(Cookie cookie : cookieList) {
        	String name = cookie.getName();
        	String value = cookie.getValue();
        	String domain = cookie.getDomain();
        	String path = cookie.getPath();
        	Map<String, Object> cookieOne = new HashMap<String, Object>();
        	cookieOne.put("name", name);
        	cookieOne.put("value", value);
        	cookieOne.put("domain", domain);
        	cookieOne.put("path", path);
        	result.add(cookieOne);
        }
		logger.info("登录大麦网获取cookie，cookie为：" + result.toString());
		damaiClient.setCookieList(cookieList);
		return damaiClient;
	}

	
	public List<Cookie> login() throws Exception{
		Map<String, String> map = new HashMap<String, String>();
		map.put("loginId", userName);
		map.put("password2", password);
		map.put("keepLogin", "true");
		map.put("ua", ua);
		map.put("umidGetStatusVal", "255");
		map.put("screenPixel", "1536x864");
		map.put("navlanguage", "zh-CN");
		map.put("navUserAgent", userAgent);
		map.put("navPlatform", "Win32");
		map.put("appName", "damai");
		map.put("appEntrance", "damai");
		map.put("bizParams", "");
		map.put("csrf_token", csrfToken);
		map.put("fromSite", "-2");
		map.put("hsiz", hsiz);
		map.put("isMobile", "false");
		map.put("lang", "zh_CN");
		map.put("mobile", "false");
		map.put("umidToken", umidToken);

		
	    CloseableHttpClient httpClient = null;
	    HttpPost httpPost = null;
        CookieStore cookieStore = new BasicCookieStore();
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        httpPost = new HttpPost("https://ipassport.damai.cn/newlogin/login.do");
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> elem = (Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
        }
        if (list.size() > 0) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(entity);
        }
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String content = EntityUtils.toString(response.getEntity());
        JSONObject jobj = JSONObject.parseObject(content);
        JSONObject data = JSONObject.parseObject(JSONObject.parseObject(jobj.getString("content")).getString("data"));
        String st = data.getString("st");
        String redirectUrl = "https%253A%252F%252Fwww.damai.cn%252F";
        StringBuilder sb = new StringBuilder("https://passport.damai.cn/dologin.htm?");
        sb.append("st=" + st);
        sb.append("&redirectUrl=" + redirectUrl);
        sb.append("&platform=106002");
        String getLoginUrl = sb.toString();
        HttpGet httpGet = new HttpGet(getLoginUrl);
        CloseableHttpResponse response2 = httpClient.execute(httpGet);
        int code = response2.getStatusLine().getStatusCode();
        String content2 = EntityUtils.toString(response2.getEntity());
        List<Cookie> cookies = cookieStore.getCookies();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for(Cookie cookie : cookies) {
        	String name = cookie.getName();
        	String value = cookie.getValue();
        	String domain = cookie.getDomain();
        	String path = cookie.getPath();
        	Map<String, Object> cookieOne = new HashMap<String, Object>();
        	cookieOne.put("name", name);
        	cookieOne.put("value", value);
        	cookieOne.put("domain", domain);
        	cookieOne.put("path", path);
        	result.add(cookieOne);
        }
        return cookies;
    }

}
