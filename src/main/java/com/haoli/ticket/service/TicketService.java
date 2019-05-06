package com.haoli.ticket.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haoli.sdk.web.util.HttpUtil;
import com.haoli.sdk.web.util.HttpUtil.HttpResponse;

@Service
public class TicketService {
	
	@Value("${damai.api.search}")
	private String searchUrl;
	
	@Value("${damai.api.getDetail}")
	private String detailUrl;
	
	@Value("${damai.api.buy}")
	private String buyUrl;
	
	public static void main(String[] args) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("spm", "a2oeg.home.card_0.ditem_2.1eeb23e1SHWj77");
		//592432015785：麦田音乐节， 592804006433：2019上海炉火音乐节，592029157846：【杭州站】西湖音乐节, 592383616752林宥嘉
		String orderBaseNo = "592685102712";
		params.put("id", orderBaseNo);
		TicketService ts = new TicketService();
		String data = ts.getDetail(params);
		String ticketId = orderBaseNo + "_1_" + "4253460047956";
		System.out.println(ticketId);
//		ts.buy(buyParams);
	}
	
	public JSONObject login(Map<String, Object> params) throws Exception {
		String loginUrl = "https://ipassport.damai.cn/newlogin/login.do";
		HttpResponse response = HttpUtil.postFormData(loginUrl, params);
		String data = response.getBody();
		String cookie = response.getCookie();
		JSONObject jobj = JSONObject.parseObject(data);
		return jobj;
	}
	
	public void buy(Map<String, Object> params) throws Exception {
		String buyUrl="https://buy.damai.cn/multi/trans/createOrder?feature={\"returnUrl\":\"https://orders.damai.cn/orderDetail\",\"serviceVersion\":\"1.8.5\"}";
		HttpResponse response = HttpUtil.get(buyUrl, new HashMap<String, Object>());
		String data = response.getBody();
		JSONObject jobj = JSONObject.parseObject(data);
	}
	
	public String search(Map<String, Object> params) throws Exception {
		HttpResponse response = HttpUtil.get(searchUrl, params);
		String data = response.getBody();
		JSONObject jobj = JSONObject.parseObject(data);
		JSONObject pageData = JSONObject.parseObject(String.valueOf(jobj.get("pageData")));
		return data;
	}
	
	public String getDetail(Map<String, Object> params) throws Exception {
		String detailUrl = "https://detail.damai.cn/item.htm";
		HttpResponse response = HttpUtil.get(detailUrl, params);
		String data = response.getBody();
		Document jsoupDocument =Jsoup.parse(data);
		Element element = jsoupDocument.getElementById("dataDefault");
		String text = element.text();
		JSONObject jobj = JSONObject.parseObject(text);
		JSONArray array = JSONArray.parseArray(String.valueOf(jobj.get("performBases")));
		List<JSONObject> jobjList = array.toJavaList(JSONObject.class);
		for(JSONObject obj : jobjList) {
			String performStr = String.valueOf(obj.get("performs"));
			JSONArray performArray= JSONArray.parseArray(performStr);
			List<JSONObject> performList = performArray.toJavaList(JSONObject.class);
			for(JSONObject perform : performList) {
				String skuListStr = String.valueOf(perform.get("skuList"));
				JSONArray skuArray = JSONArray.parseArray(skuListStr);
				List<JSONObject> skuJobjList = skuArray.toJavaList(JSONObject.class);
				System.out.println("演出场次："+ perform.get("performName"));
				for(JSONObject skuJobj : skuJobjList) {
					String skuName = skuJobj.getString("skuName");
					String skuId = skuJobj.getString("skuId");
					System.out.println("skuName: " + skuName + ", "+"skuId: " + skuId);
				}
				System.out.println("\n\t");
			}
		}
		return text;
	}

}
