package com.cuize.pay.web.gateway;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cuize.pay.web.helper.JosnRPCBizHelper;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

/**
 * 系统访问入口
 * @author wmm8101
 *
 */
@Controller
public class GatewayController {

	private static final Logger _LOG = LoggerFactory.getLogger(GatewayController.class);

	private static final Map<String, String> methodProviderMap = new HashMap<String, String>();
	
	//定义 JSONRPC2 中method 对应访问的controller
	static {
		methodProviderMap.put("pay.wx.queryOrder", "/queryOrder");
		methodProviderMap.put("pay.wx.unifiedOrder", "/unifiedOrder");
	}

	@RequestMapping(value = "gateway", method = RequestMethod.POST)
	public String gateway(Object obj, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		JSONRPC2Request jsonrpc2req = JosnRPCBizHelper.setForwardData(request);

		String result = methodProviderMap.get(jsonrpc2req.getMethod());
		if (StringUtils.isNotBlank(result)) {
			request.setAttribute("jsonrpc2req", jsonrpc2req);
			result = "forward:" + result;
		} else {
			_LOG.error("#jsonrpc2req.getMethod()=" + jsonrpc2req.getMethod());
			throw JSONRPC2Error.METHOD_NOT_FOUND;
		}
		return StringUtils.defaultIfEmpty(result, "/index");
	}

}
