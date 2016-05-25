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
import com.cuize.commons.meta.JosnRPCBizHelper;
import com.cuize.pay.service.dto.CommonInDto;
import com.cuize.pay.service.dto.QueryOrderInDto;
import com.cuize.pay.service.impl.QueryOrderService;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import net.minidev.json.JSONObject;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Controller
public class QueryOrderController {

	private static final Logger _LOG = LoggerFactory
			.getLogger(QueryOrderController.class);

	@Autowired
	private QueryOrderService service;

	@ResponseBody
	@RequestMapping(value = "/queryOrder", method = RequestMethod.POST)
	public JSONObject createOrder(Object obj, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		_LOG.info("########################### START【PAY】 queryOrder ###########################");

		CommonInDto<QueryOrderInDto> inDto = JSON.parseObject(JosnRPCBizHelper
				.getForwardData(request).toJSONString(),
				new TypeReference<CommonInDto<QueryOrderInDto>>() {
				});
		JSONRPC2Response jsonresp = new JSONRPC2Response(inDto.getId());
		// 获取手机的IP
		service.queryOrder(inDto.getParams());
		jsonresp.setResult(new Object());
		_LOG.info("****** ResponseBody=" + jsonresp.toJSONString());

		_LOG.info("########################### END【PAY】 queryOrder ###########################\n\n");
		return jsonresp.toJSONObject();
	}

}
