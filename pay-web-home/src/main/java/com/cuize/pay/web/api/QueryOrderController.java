package com.cuize.pay.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cuize.pay.service.dto.QueryOrderInDto;
import com.cuize.pay.service.dto.QueryOrderOutDto;
import com.cuize.pay.service.impl.QueryOrderService;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Controller
public class QueryOrderController {

	private static final Logger _LOG = LoggerFactory.getLogger(QueryOrderController.class);

	@Autowired
	private QueryOrderService service;
	
	@ResponseBody
	@RequestMapping(value = "/queryOrder", method = RequestMethod.POST)
	public String queryOrder(QueryOrderInDto inDto)
			throws Exception {
		
		_LOG.info("########################### START【PAY】 queryOrder ###########################");
		QueryOrderOutDto responseDto= service.queryOrder(inDto);
		String resJson = JSON.toJSONString(responseDto);
		_LOG.info("****** ResponseBody="+resJson);
		_LOG.info("########################### END【PAY】 queryOrder ###########################\n\n");
		return resJson;

	}
	
}
