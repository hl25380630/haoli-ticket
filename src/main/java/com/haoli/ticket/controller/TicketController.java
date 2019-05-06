package com.haoli.ticket.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.haoli.sdk.web.domain.JsonResponse;
import com.haoli.ticket.service.TicketService;

@RestController
public class TicketController {
	
	@Autowired
	TicketService ticketService;
	
	@GetMapping("/ticket/buy")
	public JsonResponse<String> buy() throws Exception{
		Map<String, Object> exParams = new HashMap<String, Object>();
		exParams.put("damai", "1");
		exParams.put("channel", "damai_app");
		exParams.put("umpChannel", "10002");
		exParams.put("atomSplit", "1");
		exParams.put("serviceVersion", "1.8.5");
		String exParamsJstr = JSONObject.toJSONString(exParams);
		Map<String, Object> buyParams = new HashMap<String, Object>();
		buyParams.put("exParams", exParamsJstr);
		buyParams.put("buyParam", "592432015785_1_4078608812862");
		buyParams.put("buyNow", "true");
		buyParams.put("spm", "a2oeg.project.projectinfo.dbuy");
		ticketService.buy(buyParams);
		return JsonResponse.success();
	}
	
	@PostMapping("/login")
	public JsonResponse<JSONObject> login(@RequestBody Map<String, Object> params) throws Exception{
		JSONObject jobj = ticketService.login(params);
		return new JsonResponse<JSONObject>(jobj);
	}
}
