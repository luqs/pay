package com.cuize.order.web.api;

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
import com.cuize.order.service.dto.CommonInDto;
import com.cuize.order.service.dto.CommonOutDto;
import com.cuize.order.service.dto.WxPayNotifyInDto;
import com.cuize.order.service.impl.WxPayNotifyService;
import com.cuize.order.web.helper.JosnRPCBizHelper;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Controller
public class WxNotifyController {

	private static final Logger _LOG = LoggerFactory
			.getLogger(WxNotifyController.class);

	@Autowired
	private WxPayNotifyService service;

	@ResponseBody
	@RequestMapping(value = "/wxPayNotify", method = RequestMethod.POST)
	public JSONObject wxNotify(Object obj, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		_LOG.info("########################### BEGIN INVOKE WxNotifyController ###########################");
		CommonInDto<WxPayNotifyInDto> indto = JSON.parseObject(
				JosnRPCBizHelper.getForwardData(request).toJSONString(),
				new TypeReference<CommonInDto<WxPayNotifyInDto>>(){});
		JSONRPC2Response jsonresp = new JSONRPC2Response(indto.getId());
		service.wxPayNotify(indto.getParams());
		jsonresp.setResult(new CommonOutDto());
		_LOG.info("****** ResponseBody="+jsonresp.toJSONString());
		_LOG.info("########################### END INVOKE WxNotifyController ###########################\n\n");
		return jsonresp.toJSONObject();
	}

}
