package com.haoli.ticket.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HeaderIterator;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.haoli.sdk.web.domain.JsonResponse;


@RestController
public class HttpClientDemo {
	
	
	@PostMapping("/login2")
	public JsonResponse<JSONObject> doPost(@RequestBody Map<String, String> map) throws Exception{
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
        String redirectUrl = data.getString("returnUrl");
        StringBuilder sb = new StringBuilder("https://passport.damai.cn/dologin.htm?");
        sb.append("st=" + st);
        sb.append("&redirectUrl=" + redirectUrl);
        String getLoginUrl = sb.toString();
        HttpGet httpGet = new HttpGet(getLoginUrl);
        CloseableHttpResponse response2 = httpClient.execute(httpGet);
        String content2 = EntityUtils.toString(response2.getEntity());
        List<Cookie> cookies = cookieStore.getCookies();
        Map<String, Object> result = new HashMap<String, Object>();
        for(Cookie cookie : cookies) {
        	String name = cookie.getName();
        	String value = cookie.getValue();
        	result.put(name, value);
        }
        return new JsonResponse<JSONObject>(JSONObject.parseObject(content));
    }
	
	
	@PostMapping("/login3")
	public JsonResponse<String> login3(@RequestBody Map<String, Object> params) throws Exception{ 
		// 先访问首页，得到cookie 
		// cookie信息自动保存在HttpClient中 
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()) 
		.setRedirectStrategy(new LaxRedirectStrategy()).build(); 
		HttpPost postMethod = new HttpPost("https://passport.damai.cn/login?ru=https%3A%2F%2Fwww.damai.cn%2F");
	    httpClient.execute(postMethod);
	    // 携带cookie访问登录网面
	    // 设置登录的账号与密码
	    HttpUriRequest login = RequestBuilder.post().setUri("https://ipassport.damai.cn/newlogin/login.do")
	            .addParameter("loginId", "15811133865").addParameter("password2", "9ae52469b131f68e844fcd2e3a0b6f4f12dc4a3a033f87f1bb6ff0eabeeafdadeae935b24da4f852c24f5059c7f7702aaa301dca87eea3bedd2822ee9cc5564197887581c0ddf92467cc86713e84f4f6fbd113fdcdc1290f6f9d0692ed577609ce9cdbb8afc43a11833a74ad2694a23a1efbd146170cf3159c48803e81f27ab8")
	            .addParameter("keepLogin","true").addParameter("appEntrance","damai").addParameter("appName","damai")
	            .build();
	    // httpclient访问登录网页,并得到响应对象
	    CloseableHttpResponse response = httpClient.execute(login);
	    // 响应文本
	    String content = EntityUtils.toString(response.getEntity());
	    EntityUtils.consume(response.getEntity());
	    // 输出响应页面源代码
	    System.out.println(content);
	    // 输出为302，也就是说网页发生了重定向
	    // 得到重定向后的
	    HeaderIterator redirect = response.headerIterator("location");
	    while (redirect.hasNext()) {
	        // 使用get请求，访问登陆后的页面
	        HttpGet getMethod = new HttpGet(redirect.next().toString());
	        CloseableHttpResponse response2 = httpClient.execute(getMethod);
	        // 得到返回文本
	        String content1 = EntityUtils.toString(response.getEntity());
	        EntityUtils.consume(response.getEntity());
	        // 打印请求文本
	        System.out.println("响应请求文本是:" + content1);
	    }
	    return new JsonResponse<String>(content);
	}

}
