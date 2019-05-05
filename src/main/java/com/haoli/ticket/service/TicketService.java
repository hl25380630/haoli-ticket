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
	
	public static void main(String[] args) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("keyword", "周杰伦");
//		params.put("tsg", 0);
//		params.put("order", 0);
//		params.put("pageSize", 20);
//		params.put("currPage", 1);
		params.put("spm", "a2oeg.home.card_0.ditem_2.1eeb23e1SHWj77");
		params.put("id", "593131142099");
		TicketService ts = new TicketService();
		String data = ts.getDetail(params);
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
				for(JSONObject skuJobj : skuJobjList) {
					String skuName = skuJobj.getString("skuName");
					String skuId = skuJobj.getString("skuId");
					System.out.println("skuName: " + skuName);
					System.out.println("skuId: " + skuId);
					System.out.println("\n\t");
				}
			}
		}
		return text;
	}

}
