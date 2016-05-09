package com.cuize.pay.web.helper;

import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

/**
 * 通用 controller异常处理类，原则上 controller里面不需要再try catch了
 * @author luqingsong
 *
 */
@ControllerAdvice
public class ExceptionAdvice {

	private static final Logger _LOG = LoggerFactory.getLogger(ExceptionAdvice.class);

	@ExceptionHandler
	public @ResponseBody JSONObject handleBusinessException(Exception ex, HttpServletRequest request) {
		_LOG.error("/ @ExceptionHandler", ex);
		JSONRPC2Request jsonrpc2req = JosnRPCBizHelper.getForwardData(request);
		JSONRPC2Error jsonrpc2Error = JSONRPC2Error.INTERNAL_ERROR;
		jsonrpc2Error = new JSONRPC2Error(jsonrpc2Error.getCode(), jsonrpc2Error.getMessage(), jsonrpc2Error.getData());
		if (ex instanceof JSONRPC2ParseException) {
			JSONRPC2ParseException e = (JSONRPC2ParseException) ex;
			jsonrpc2Error = new JSONRPC2Error(e.getCauseType(), e.getMessage());
			_LOG.error("/ @ExceptionHandler" + ToStringBuilder.reflectionToString(e));
		} else {
			jsonrpc2Error = new JSONRPC2Error(JSONRPC2Error.INTERNAL_ERROR.getCode(), ex.getMessage());
		}
		Object id = 0;
		if (jsonrpc2req != null) {
			id = jsonrpc2req.getID();
		}
		return new JSONRPC2Response(jsonrpc2Error, id).toJSONObject();
	}
	
}
