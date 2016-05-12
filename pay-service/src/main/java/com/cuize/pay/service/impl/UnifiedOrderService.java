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
import com.cuize.pay.service.dto.UnifiedOrderInDto;
import com.cuize.pay.service.dto.UnifiedOrderOutDto;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Service
@Transactional(value="orderTransactionManager",rollbackFor=Exception.class)
public class UnifiedOrderService{
	private static final Logger _LOG = LoggerFactory.getLogger(UnifiedOrderService.class);
	
	@Autowired
	private GlobalConfig config;
	
	/**
	 * 产品库存入库接口
	 * @author luqingsong
	 */
	public UnifiedOrderOutDto unifiedOrder(UnifiedOrderInDto inDto)
			throws Exception {
		BeanInitialUtils.checkRequire(inDto);
		
		/*请求微信预支付接口，获取prepay_id,并且给页面返回js调用微信支付所需要的参数*/
		SortedMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("appid", config.getAppid());
		parameters.put("body", inDto.getBody());
		parameters.put("detail", inDto.getDetail());
		parameters.put("fee_type", "CNY");
		parameters.put("mch_id", config.getMchid());
		parameters.put("attach", inDto.getAttach());
		parameters.put("nonce_str", WXPayUtil.createNoncestr());
		parameters.put("notify_url", config.getNotifyurl());
		parameters.put("openid", inDto.getOpenid());
		parameters.put("out_trade_no", inDto.getOut_trade_no());
		parameters.put("spbill_create_ip", inDto.getSpbill_create_ip());
		parameters.put("total_fee", inDto.getTotal_fee());
		parameters.put("trade_type", "JSAPI");
		String sign = WXPayUtil.createSign("UTF-8",config.getApikey(), parameters);
		
		parameters.put("sign", sign);
		String requestXML = WXPayUtil.getPrepayXml(parameters);
		_LOG.info("******* wx prepay request xml:"+requestXML);
		
		String resXml = Request
				.Post(config.getPrepayurl())
				.bodyString(requestXML,ContentType.parse("application/xml; charset=UTF-8"))
				.execute().returnContent()
				.asString(Charset.forName("utf-8"));
		_LOG.info("******* wx prepay response xml:"+resXml);
		//得到预支付接口的响应参数，并计算paySign传给页面，供js调用微信支付
		UnifiedOrderOutDto outDto = WXPayUtil.xml2bean(resXml, UnifiedOrderOutDto.class);
		
		return outDto;
	}
}
