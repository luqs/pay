package com.cuize.pay.web.api;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cuize.pay.service.dto.UnifiedOrderInDto;
import com.cuize.pay.service.dto.UnifiedOrderOutDto;
import com.cuize.pay.service.impl.UnifiedOrderService;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Controller
public class UnifiedOrderController {

	private static final Logger _LOG = LoggerFactory.getLogger(UnifiedOrderController.class);

	@Autowired
	private UnifiedOrderService service;
	
	@ResponseBody
	@RequestMapping(value = "/unifiedOrder", method = RequestMethod.POST)
	public String createOrder(UnifiedOrderInDto inDto,HttpServletRequest request)
			throws Exception {
		
		_LOG.info("########################### START【PAY】 queryOrder ###########################");
		//获取手机的IP
		inDto.setSpbill_create_ip(request.getRemoteAddr());
		UnifiedOrderOutDto responseDto= service.unifiedOrder(inDto);
		String resJson = JSON.toJSONString(responseDto);
		_LOG.info("****** ResponseBody="+resJson);
		_LOG.info("########################### END【PAY】 queryOrder ###########################\n\n");
		return resJson;

	}
	
}
