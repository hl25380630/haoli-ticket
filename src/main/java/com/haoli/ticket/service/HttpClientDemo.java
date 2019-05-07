package com.haoli.ticket.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
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
	
	public static void main(String[] args) {
		HttpClientDemo hcd = new HttpClientDemo();
		List<String> confrimOrder_1 = hcd.buildConfrimOrder();
		JSONObject hierarchy = hcd.buildHierarchy(confrimOrder_1);
		JSONObject data = new JSONObject();
		JSONObject result = new JSONObject();
		result.put("hierarchy", hierarchy);
		for(String s : confrimOrder_1) {
			if(s.contains("dmTicketBuyer_")) {
				String dmTicketBuyer_id = s.split("_")[1];
				JSONObject dmTicketBuyer = hcd.buildTicketBuyer(dmTicketBuyer_id);
				data.put(s, dmTicketBuyer);
			}
		}
		result.put("data", data);
		System.out.println(result.toJSONString());
	}
	
	
	public JSONObject buildTicketBuyer(String id) {
		JSONObject dmTicketBuyer = new JSONObject();
		JSONObject fields = this.buildTicketBuyerFields();
		dmTicketBuyer.put("ref", "854e4c1");
		dmTicketBuyer.put("submit", true);
		dmTicketBuyer.put("id", id);
		dmTicketBuyer.put("tag", "dmTicketBuyer");
		dmTicketBuyer.put("fields", fields);
		dmTicketBuyer.put("type", "biz");
		return dmTicketBuyer;
	}
	
	public JSONObject buildTicketBuyerFields() {
		JSONObject fields = new JSONObject();
		fields.put("buyerTotalNum", 1);
		fields.put("tipTemplate", "请选择1位观演人");
		fields.put("idTypes", "5,4,1,6,3");
		fields.put("errorMessage", "请选择观演人");
		fields.put("title", "观演人");
		fields.put("isDisplay", true);
		fields.put("selectBuyerBtnText", "选择观演人");
		List<JSONObject> ticketBuyerList = this.buildTicketBuyerFieldsBuyerList();
		fields.put("ticketBuyerList", ticketBuyerList);
		return fields;
	}
	
	
	public List<JSONObject> buildTicketBuyerFieldsBuyerList() {
		List<JSONObject> ticketBuyerList = new ArrayList<JSONObject>();
		JSONObject buyer = new JSONObject();
		buyer.put("identityNo", "150402199206300614");
		buyer.put("ticketBuyerId", "1100031fb246cf370944657a20707cfc0ff2c5419452f");
		buyer.put("idType", 1);
		buyer.put("ticketBuyerName", "李昊");
		buyer.put("hashIdentityNo", "150************614");
		buyer.put("idTypeDesc", "身份证");
		buyer.put("isDisabled", false);
		buyer.put("isUsed", true);
		ticketBuyerList.add(buyer);
		return ticketBuyerList;
	}
	
	public JSONObject buildHierarchy(List<String> confrimOrder) {
		JSONObject structure = new JSONObject();
		structure.put("confirmOrder_1", confrimOrder);
		JSONObject hierarchy = new JSONObject();
		hierarchy.put("structure", structure);
		return hierarchy;
	}
	
	
	public List<String> buildConfrimOrder() {
		Random random = new Random();
		List<Integer> numberList = new ArrayList<Integer>();
		int root = random.nextInt(9)+1;
		int x = root*10 + root*100;
		numberList.add(x + random.nextInt(10));
		while(numberList.size() < 10) {
			int y = x+random.nextInt(10);
			if(numberList.contains(y)) {
				continue;
			}
			numberList.add(y);
		}
		Collections.shuffle(numberList);
		JSONObject structure = new JSONObject();
		List<String> tconfirmOrder_1 = new ArrayList<String>();
		List<String> confirmOrder_1 = new ArrayList<String>();
		String dmDeliveryWay = "dmDeliveryWay_33";
		String dmTicketBuyer = "dmTicketBuyer_33";
		String dmPayType = "dmPayType_33";
		String dmItem = "dmItem_33";
		String dmInvoice = "dmInvoice_33";
		String order = "order_" + RandomStringUtils.randomAlphanumeric(32).toLowerCase();
		String dmPayDetail = "dmPayDetail_33";
		String dmTerm = "dmTerm_33";
		String dmOrderPay = "dmOrderPay_33";
		String dmSubmitOrder = "dmSubmitOrder_33";
		String dmExtraAttributes = "dmExtraAttributes_33";
		tconfirmOrder_1.add(dmDeliveryWay);
		tconfirmOrder_1.add(dmTicketBuyer);
		tconfirmOrder_1.add(dmPayType);
		tconfirmOrder_1.add(dmItem);
		tconfirmOrder_1.add(dmInvoice);
		tconfirmOrder_1.add(dmPayDetail);
		tconfirmOrder_1.add(dmTerm);
		tconfirmOrder_1.add(dmOrderPay);
		tconfirmOrder_1.add(dmSubmitOrder);
		tconfirmOrder_1.add(dmExtraAttributes);
		for(int j = 0; j<10; j++) {
			String s = tconfirmOrder_1.get(j);
			s += String.valueOf(numberList.get(j));
			confirmOrder_1.add(s);
		}
		confirmOrder_1.add(order);
		return confirmOrder_1;
	}
	
	
	
	
	
	@PostMapping("/login2")
	public JsonResponse<Map<String, Object>> doPost(@RequestBody Map<String, String> map) throws Exception{
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
        Map<String, Object> result = new HashMap<String, Object>();
        for(Cookie cookie : cookies) {
        	String name = cookie.getName();
        	String value = cookie.getValue();
        	result.put(name, value);
        }
        return new JsonResponse<Map<String, Object>>(result);
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
