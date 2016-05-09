package com.cuize.pay.web.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.cuize.pay.service.dto.OrderVerifyInDto;
import com.cuize.pay.service.dto.OrderVerifyOutDto;
import com.cuize.pay.service.impl.OrderVerifyService;
import com.cuize.pay.web.helper.JosnRPCBizHelper;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import net.minidev.json.JSONObject;
/**
 * 订单核销
 * @author Wangwei
 *
 */
@Controller
public class OrderVerifyController {
	private static final Logger _LOG = LoggerFactory.getLogger(OrderVerifyController.class);
	@Autowired
	private OrderVerifyService service;
	/**
	 * 订单核销
	 * @param obj
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public JSONObject verify(Object obj, Model model,
			HttpServletRequest request, HttpServletResponse response)
	        throws Exception{
		_LOG.info("########################### BEGIN INVOKE OrderVerifyController ###########################");
		CommonInDto<OrderVerifyInDto> inDto=JSON.parseObject(
				JosnRPCBizHelper.getForwardData(request).toJSONString(),
				new TypeReference<CommonInDto<OrderVerifyInDto>>(){});
		JSONRPC2Response jsonresp = new JSONRPC2Response(inDto.getId());
		OrderVerifyOutDto responseDto=service.queryOrderByOrderNo(inDto.getParams());
		jsonresp.setResult(responseDto);
		_LOG.info("########################### END INVOKE OrderVerifyController ###########################\n\n");
		return jsonresp.toJSONObject();
	}
}
