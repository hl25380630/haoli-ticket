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
		JSONObject result = hcd.buildOrderParams();
		System.out.println(result.toJSONString());
	}
	
	
	@PostMapping("/createOrder")
	public JsonResponse<List<Map<String, Object>>> createOrder(@RequestBody Map<String, String> map) throws Exception{
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
//        JSONObject orderParams = this.buildOrderParams();
//        String orderUrl = "https://buy.damai.cn/multi/trans/createOrder";
//        HttpPost orderPost = new HttpPost(orderUrl);
//        
//        List<NameValuePair> list3 = new ArrayList<NameValuePair>();
//        Iterator<Map.Entry<String, Object>> iterator3 = orderParams.toJavaObject(Map.class).entrySet().iterator();
//        while (iterator3.hasNext()) {
//            Entry<String, Object> elem = (Entry<String, Object>) iterator3.next();
//            String valueStr = String.valueOf(elem.getValue());
//            list3.add(new BasicNameValuePair(elem.getKey(),valueStr));
//        }
//        if (list3.size() > 0) {
//            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
//            orderPost.setEntity(entity);
//        }
//        while(true) {
//            CloseableHttpResponse response3 = httpClient.execute(orderPost);
//            String content3 = EntityUtils.toString(response3.getEntity());
//            System.out.println(content3);
//            if(JSONObject.parseObject(content3).get("resultCode").equals("0")) {
//            	break;
//            }
//        }
        return new JsonResponse<List<Map<String, Object>>>(result);
    }
	
	
	
	public JSONObject buildOrderParams() {
		List<String> confrimOrder_1 = this.buildConfrimOrder();
		JSONObject hierarchy = this.buildHierarchy(confrimOrder_1);
		JSONObject data = new JSONObject();
		JSONObject result = new JSONObject();
		result.put("hierarchy", hierarchy);
		for(String s : confrimOrder_1) {
			if(s.contains("dmTicketBuyer_")) {
				String dmTicketBuyer_id = s.split("_")[1];
				JSONObject dmTicketBuyer = this.buildTicketBuyer(dmTicketBuyer_id);
				data.put(s, dmTicketBuyer);
			}
			if(s.contains("dmTerm_")) {
				String dmTerm_id = s.split("_")[1];
				JSONObject dmTerm = this.buildDmTerm(dmTerm_id);
				data.put(s, dmTerm);
			}
			if(s.contains("dmItem_")) {
				String dmItem_id = s.split("_")[1];
				JSONObject dmItem = this.buildDmItem(dmItem_id);
				data.put(s, dmItem);
			}
			if(s.contains("dmDeliveryWay_")) {
				String dmDeliveryWay_id = s.split("_")[1];
				JSONObject dmDeliveryWay = this.buildDmDeliveryWay(dmDeliveryWay_id);
				data.put(s, dmDeliveryWay);
			}
		}
		result.put("data", data);
		return result;
	}
	
	
	public JSONObject buildDmDeliveryWay(String id) {
		JSONObject dmDeliveryWayFields = this.buildDmDeliveryWayFields();
 		JSONObject dmDeliveryWay = new JSONObject();
		dmDeliveryWay.put("ref", "610edbc");
		dmDeliveryWay.put("submit", true);
		dmDeliveryWay.put("id", id);
		dmDeliveryWay.put("tag", "dmDeliveryWay");
		dmDeliveryWay.put("fields", dmDeliveryWayFields);
		dmDeliveryWay.put("type", "biz");
		return dmDeliveryWay;
	}
	
	public JSONObject buildDmDeliveryWayFields() {
		JSONObject fields = new JSONObject();
		List<JSONObject> dmDeliveryWayList = this.buildDmDeliveryWayFieldsWayList();
		fields.put("title", "配送方式");
		fields.put("dmDeliveryWayList", dmDeliveryWayList);
		return fields;
	}
	
	public List<JSONObject> buildDmDeliveryWayFieldsWayList(){
		List<JSONObject> dmDeliveryWayList = new ArrayList<JSONObject>();
		JSONObject deliveryWay = new JSONObject();
		JSONObject dmPhone = new JSONObject();
		dmPhone.put("code", "中国大陆|+86;中国香港|+852;中国澳门|+853;中国台湾地区|+886");
		dmPhone.put("icon", "https://gw.alicdn.com/tfs/TB17jxYdDtYBeNjy1XdXXXXyVXa-48-48.png");
		dmPhone.put("title", "手机号：");
		dmPhone.put("selectCode", "");
		dmPhone.put("isDisplay", false);
		deliveryWay.put("dmPhone", dmPhone);
		
		deliveryWay.put("isDefault", true);
		
		JSONObject dmTicketAddress = new JSONObject();
		dmTicketAddress.put("title", "取票地址：");
		dmTicketAddress.put("isDisplay", false);
		deliveryWay.put("dmTicketAddress", dmTicketAddress);
		
		JSONObject dmShippingAddress = new JSONObject();
		dmShippingAddress.put("addressDetail", "北京北京市大兴区北京经济技术开发区观海苑8号楼2单元1602室");
		dmShippingAddress.put("phone", "18515672163");
		dmShippingAddress.put("addAddressTip", "");
		dmShippingAddress.put("name", "李昊");
		dmShippingAddress.put("title", "地址：");
		dmShippingAddress.put("isDisplay", true);
		dmShippingAddress.put("isUsed", true);
		dmShippingAddress.put("addressId", 91978806);
		dmShippingAddress.put("status", 0);
		deliveryWay.put("dmShippingAddress", dmShippingAddress);
		
		
		deliveryWay.put("deliveryTip", "");
		deliveryWay.put("deliveryType", 2);
		deliveryWay.put("title", "快递");
		
		JSONObject dmContact = new JSONObject();
		dmContact.put("title",  "联系人：");
		dmContact.put("isDisplay", false);
		deliveryWay.put("dmContact", dmContact);
		
		JSONObject dmEmail = new JSONObject();
		dmEmail.put("title",  "邮箱：");
		dmEmail.put("isDisplay", false);
		deliveryWay.put("dmEmail", dmEmail);
		
		JSONObject dmPostFee = new JSONObject();
		dmPostFee.put("amount", "10.00");
		dmPostFee.put("currency", "￥");
		dmPostFee.put("title",  "运费：");
		dmPostFee.put("isDisplay", true);
		deliveryWay.put("dmPostFee", dmPostFee);
		
		dmDeliveryWayList.add(deliveryWay);
		return dmDeliveryWayList;
		
	}
	
	public JSONObject buildDmItem(String id) {
		JSONObject dmItemFields = this.buildDmItemFields();
		JSONObject dmItem = new JSONObject();
		dmItem.put("ref", "f46382d");
		dmItem.put("submit", true);
		dmItem.put("id", id);
		dmItem.put("tag", "dmItem");
		dmItem.put("fields", dmItemFields);
		dmItem.put("type", "biz");
		return dmItem;
	}
	
	public JSONObject buildDmItemFields() {
		JSONObject fields = new JSONObject();
		fields.put("performShowTime", "2019.05.25 12:30-22:00");
		fields.put("splitChar", "|");
		fields.put("quantity", 1);
		fields.put("ticketPrice", "￥660.00票档");
		fields.put("liveDesc", "北京／2019.05.25 12:30-22:00／北京长阳音乐主题公园");
		fields.put("totalPrice", "660.00");
		fields.put("shopLogo", "//gw.alicdn.com/tfs/TB1CzD7SXXXXXXJaXXXXXXXXXXX-32-32.png");
		fields.put("seatTitle", "座位信息");
		fields.put("shopName", "大麦网官方旗舰店");
		fields.put("liveCity", "北京");
		fields.put("skuEntries", this.buildDmItemFieldsSkuEntries());
		fields.put("venueName", "北京长阳音乐主题公园");
		fields.put("itemId", 592432015785L);
		fields.put("picUrl", "http://img.alicdn.com/imgextra/i1/2251059038/O1CN01vgwaBm2GdS9yA4EQg_!!0-item_pic.jpg");
		fields.put("ticketQuantity", "×1张");
		fields.put("unit", "张");
		fields.put("refundTips", "购票须知");
		fields.put("currency", "￥");
		fields.put("projectName", "2019麦田音乐节-北京");
		return fields;
	}
	
	public List<JSONObject> buildDmItemFieldsSkuEntries(){
		List<JSONObject> skuEntries = new ArrayList<JSONObject>();
		JSONObject skuEntry = new JSONObject();
		skuEntry.put("quantity", 1);
		skuEntry.put("totalPrice", "660.00"	);
		skuEntry.put("price", "660.00");
		skuEntry.put("seatInfo", "");
		skuEntries.add(skuEntry);
		return skuEntries;
	}
	
	
	public JSONObject buildDmTerm(String id) {
		JSONObject dmTerm = new JSONObject();
		JSONObject dmTermFields = this.buildDmTermFields();
		dmTerm.put("ref", "a2380ef");
		dmTerm.put("submit", true);
		dmTerm.put("id", id);
		dmTerm.put("tag", "dmTerm");
		dmTerm.put("fields", dmTermFields);
		dmTerm.put("type", "biz");
		return dmTerm;
	}
	
	public JSONObject buildDmTermFields() {
		JSONObject fields = new JSONObject();
		List<JSONObject> termList = new ArrayList<JSONObject>();
		JSONObject term = new JSONObject();
		term.put("termURL", "https://x.damai.cn/markets/app/agreements");
		term.put("termName", "《大麦网订票服务条款》");
		term.put("termContent", "最终解释权归大麦所有");
		termList.add(term);
		fields.put("termList", termList);
		fields.put("isAgree", true);
		fields.put("termTip", "同意");
		return fields;
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
