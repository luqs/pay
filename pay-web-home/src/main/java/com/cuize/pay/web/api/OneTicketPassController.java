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
import com.cuize.pay.service.dto.OneTicketPassInDto;
import com.cuize.pay.service.dto.OneTicketPassOutDto;
import com.cuize.pay.service.impl.OneTicketVerificationService;
import com.cuize.pay.web.helper.JosnRPCBizHelper;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import net.minidev.json.JSONObject;

/**
 * 一票通核销接口
 * @author Wangwei
 *
 */
@Controller
public class OneTicketPassController {
	private static final Logger _LOG = LoggerFactory.getLogger(OneTicketPassController.class);
	@Autowired
	private OneTicketVerificationService service;
	@ResponseBody
	@RequestMapping(value = "/oneTicketSolution", method = RequestMethod.POST)
	public JSONObject verify(Object obj, Model model,
			HttpServletRequest request, HttpServletResponse response)
	        throws Exception{
		_LOG.info("########################### BEGIN INVOKE OrderVerifyController ###########################");
		CommonInDto<OneTicketPassInDto> inDto=JSON.parseObject(
				JosnRPCBizHelper.getForwardData(request).toJSONString(),
				new TypeReference<CommonInDto<OneTicketPassInDto>>(){});
		JSONRPC2Response jsonresp = new JSONRPC2Response(inDto.getId());
		OneTicketPassOutDto responseDto=service.verification(inDto.getParams());
		jsonresp.setResult(responseDto);
		_LOG.info("########################### END INVOKE OrderVerifyController ###########################\n\n");
		return jsonresp.toJSONObject();
	}
}
