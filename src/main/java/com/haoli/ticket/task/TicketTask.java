package com.haoli.ticket.task;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.haoli.ticket.domain.DamiInfo;
import com.haoli.ticket.service.TicketService;

@Component
public class TicketTask {
	
	@Autowired
	TicketService ticketService;
	
	@Scheduled(cron = "0 20 12 ? * *")
	public void sendMsg() throws Exception {
		DamiInfo map = new DamiInfo();
		map.setChromeDriverPath("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
		map.setDetailItemKey("2019冯提莫重庆演唱会");
		List<String> priceDetailList = new ArrayList<String>();
		priceDetailList.add("299元");
		priceDetailList.add("499元");
		priceDetailList.add("699元");
		priceDetailList.add("899元");
		priceDetailList.add("1219元");
		map.setPriceDetailList(priceDetailList);
		map.setSearchKey("冯提莫");
		List<String> sessionDetailList = new ArrayList<String>();
		sessionDetailList.add("2019-08-03 周六 20:00");
		map.setSessionDetailList(sessionDetailList);
		ticketService.buyDamaiTicket(map);
	}


	
	

}
