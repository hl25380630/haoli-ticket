package com.haoli.ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haoli.sdk.web.domain.JsonResponse;
import com.haoli.ticket.domain.DamiInfo;
import com.haoli.ticket.service.TicketService;

@RestController
public class TicketController {
	
	@Autowired
	TicketService ticketService;
	
	/**
	 * 大麦网抢票接口
	 */
	@PostMapping("/damai/ticket/buy")
	public JsonResponse<String> buyDamaiTicket(@RequestBody DamiInfo params) throws Exception{
		ticketService.buyDamaiTicket(params);
		return JsonResponse.success();
	}
	
}
