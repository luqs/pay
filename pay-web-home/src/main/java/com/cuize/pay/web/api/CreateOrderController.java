package com.cuize.pay.web.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cuize.pay.service.dto.CommonInDto;
import com.cuize.pay.service.dto.CreateOrderInDto;
import com.cuize.pay.service.dto.CreateOrderOutDto;
import com.cuize.pay.service.impl.CreateOrderService;
import com.cuize.pay.web.helper.JosnRPCBizHelper;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Controller
public class CreateOrderController {

	private static final Logger _LOG = LoggerFactory.getLogger(CreateOrderController.class);

	@Autowired
	private CreateOrderService service;
	
	@ResponseBody
	@RequestMapping(value = "/createOrder", method = RequestMethod.POST)
	public JSONObject createOrder(Object obj, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		_LOG.info("########################### BEGIN INVOKE CreateOrderController ###########################");
		CommonInDto<CreateOrderInDto> inDto = JSON.parseObject(
				JosnRPCBizHelper.getForwardData(request).toJSONString(),
				new TypeReference<CommonInDto<CreateOrderInDto>>(){});
		JSONRPC2Response jsonresp = new JSONRPC2Response(inDto.getId());
		//获取手机的IP
		inDto.getParams().setPhoneIp(request.getRemoteAddr());
		CreateOrderOutDto responseDto= service.createOrder(inDto.getParams());
		jsonresp.setResult(responseDto);
		_LOG.info("****** ResponseBody="+jsonresp.toJSONString());
		_LOG.info("########################### END INVOKE CreateOrderController ###########################\n\n");
		return jsonresp.toJSONObject();

	}
	
}
