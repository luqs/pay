package com.cuize.order.web.api;

import java.util.List;

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
import com.cuize.commons.dao.order.domain.OrderDetail;
import com.cuize.order.service.dto.CommonInDto;
import com.cuize.order.service.dto.ListQrInDto;
import com.cuize.order.service.impl.ListQrService;
import com.cuize.order.web.helper.JosnRPCBizHelper;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Controller
public class ListQrController {

	private static final Logger _LOG = LoggerFactory.getLogger(ListQrController.class);

	@Autowired
	private ListQrService service;
	
	@ResponseBody
	@RequestMapping(value = "/listQr", method = RequestMethod.POST)
	public JSONObject listQr(Object obj, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		_LOG.info("########################### BEGIN INVOKE ListQrController ###########################");
		CommonInDto<ListQrInDto> inDto = JSON.parseObject(
				JosnRPCBizHelper.getForwardData(request).toJSONString(),
				new TypeReference<CommonInDto<ListQrInDto>>(){});
		JSONRPC2Response jsonresp = new JSONRPC2Response(inDto.getId());
		List<OrderDetail> responseDto= service.listQr(inDto.getParams());
		jsonresp.setResult(responseDto);
		_LOG.info("****** ResponseBody="+jsonresp.toJSONString());
		_LOG.info("########################### END INVOKE ListQrController ###########################\n\n");
		return jsonresp.toJSONObject();

	}
	
}
