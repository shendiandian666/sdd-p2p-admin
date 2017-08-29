package com.sdd.web.invest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sdd.service.invest.PostOrderService;
import com.sdd.web.WebController;

@Controller("investReportController")
@RequestMapping(value="/invest")
public class ReportController extends WebController {

	@Autowired
	private PostOrderService postOrderService;
	
	@RequestMapping(value = "/Report")
	public String getReport(Map<String, Object> map) throws Exception{
		Map<String, Object> reportData = postOrderService.reportData();
		map.put("reportData", reportData);
		return "/invest/report/report";
	}
}
