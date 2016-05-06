package com.cuize.order.web.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cuize.commons.meta.ServiceException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

/**
 * 该类主要是处理和php通讯的类，
 * @author shuwei.wei
 *
 */
public class JosnRPCBizHelper {

	private static final Logger _LOG = LoggerFactory.getLogger(JosnRPCBizHelper.class);

	private static final String FORWARD_DATA_FLAG = "jsonrpc2req";

	public static final JSONRPC2Request setForwardData(HttpServletRequest request) throws Exception {
		JSONRPC2Request jsonrpc2req = convertFromBody(request);
		request.setAttribute("jsonrpc2req", jsonrpc2req);
		return jsonrpc2req;
	}

	public static final JSONRPC2Request getForwardData(HttpServletRequest request) {
		JSONRPC2Request jsonrpc2req = (JSONRPC2Request) request.getAttribute(FORWARD_DATA_FLAG);
		return jsonrpc2req;
	}
	

	private static final JSONRPC2Request convertFromBody(HttpServletRequest request) throws Exception{
		JSONRPC2Request jsonrpc2req = null;
		
			String body = getRequestBody(request);
			_LOG.info("****** RequestBody=" + body);
			jsonrpc2req = JSONRPC2Request.parse(body);
		
		if (jsonrpc2req == null) {
			throw new ServiceException(JSONRPC2Error.INVALID_PARAMS);
		}

		return jsonrpc2req;
	}

	/**
	 * 从request中抓取body信息
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static final String getRequestBody(HttpServletRequest request) throws IOException {
		// 相应的实体消息内容的读取
		BufferedReader reader = request.getReader();

		StringBuilder sb = new StringBuilder();
		List<String> readLines = IOUtils.readLines(reader);
		if (readLines != null && readLines.size() > 0) {
			for (String content : readLines) {
				sb.append(content);
			}
		}
		reader.close();
		return sb.toString();
	}

}
