package com.cuize.pay.service.impl;

import java.nio.charset.Charset;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuize.commons.utils.BeanInitialUtils;
import com.cuize.commons.utils.WXPayUtil;
import com.cuize.pay.service.dto.GlobalConfig;
import com.cuize.pay.service.dto.QueryOrderInDto;
import com.cuize.pay.service.dto.QueryOrderOutDto;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Service
@Transactional(value = "orderTransactionManager", rollbackFor = Exception.class)
public class QueryOrderService {
	private static final Logger _LOG = LoggerFactory
			.getLogger(QueryOrderService.class);

	@Autowired
	private GlobalConfig config;

	/**
	 * 产品库存入库接口
	 * 
	 * @author luqingsong
	 */
	public QueryOrderOutDto queryOrder(QueryOrderInDto inDto)
			throws Exception {
		BeanInitialUtils.checkRequire(inDto);

		String orderNo = inDto.getOrderNo();

		SortedMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("appid", config.getAppid());
		parameters.put("mch_id", config.getMchid());
		parameters.put("nonce_str", WXPayUtil.createNoncestr());
		parameters.put("out_trade_no", orderNo);
		String sign = WXPayUtil.createSign("UTF-8",config.getApikey(), parameters);
		parameters.put("sign", sign);
		String requestXML = WXPayUtil.getPrepayXml(parameters);
		_LOG.info("******* wx queryOrder request xml:" + requestXML);

		String resXml = Request
				.Post(config.getQueryOrderurl())
				.bodyString(requestXML,
						ContentType.parse("application/xml; charset=UTF-8"))
				.execute().returnContent().asString(Charset.forName("utf-8"));
		_LOG.info("******* wx queryOrder response xml:" + resXml);

		QueryOrderOutDto outDto = WXPayUtil.xml2bean(resXml, QueryOrderOutDto.class);
		return outDto;
	}
}
