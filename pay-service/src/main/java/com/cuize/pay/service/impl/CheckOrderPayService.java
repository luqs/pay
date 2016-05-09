package com.cuize.pay.service.impl;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuize.commons.dao.order.domain.Order;
import com.cuize.commons.dao.order.domain.OrderDetail;
import com.cuize.commons.dao.order.domain.OrderDetailExample;
import com.cuize.commons.dao.order.domain.OrderExample;
import com.cuize.commons.dao.order.mapper.OrderDetailMapper;
import com.cuize.commons.dao.order.mapper.OrderMapper;
import com.cuize.commons.dao.order.mapper.OrderOptlogMapper;
import com.cuize.commons.meta.Constant;
import com.cuize.commons.meta.ServiceException;
import com.cuize.commons.utils.BeanInitialUtils;
import com.cuize.commons.utils.WXPayUtil;
import com.cuize.pay.service.dto.CheckOrderPayInDto;
import com.cuize.pay.service.dto.CheckOrderPayOutDto;
import com.cuize.pay.service.dto.GlobalConfig;
import com.cuize.pay.service.helper.OrderService;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Service
@Transactional(value="orderTransactionManager",rollbackFor=Exception.class)
public class CheckOrderPayService{
	private static final Logger _LOG = LoggerFactory.getLogger(CheckOrderPayService.class);
	
	@Autowired 
	private OrderMapper orderMapper;
	@Autowired 
	private OrderDetailMapper orderDetailMapper;
	@Autowired 
	private OrderOptlogMapper orderOptLogMapper;
	@Autowired
	private GlobalConfig config;
	@Autowired
	private OrderService orderStatusService;
	
	/**
	 * 产品库存入库接口
	 * @author luqingsong
	 */
	public CheckOrderPayOutDto checkOrderPay(CheckOrderPayInDto inDto) throws Exception{
		BeanInitialUtils.checkRequire(inDto);	
		
		Integer orderId = inDto.getOrderId();
		String orderNo = inDto.getOrderNo();
		
		CheckOrderPayOutDto outDto= new CheckOrderPayOutDto();
		
		OrderExample example = new OrderExample();
		example.createCriteria()
			.andIdEqualTo(orderId)
			.andOrderNoEqualTo(orderNo);
		List<Order> orderLst = orderMapper.selectByExample(example);
		if(orderLst==null||orderLst.size()!=1){
			String msg="******* fund 【"+orderLst+"】 records by orderId 【"+orderId+"】 and orderNo【"+orderNo+"】";
			_LOG.error(msg);
			throw new ServiceException(msg);
		}else {
			Order order = orderLst.get(0);
			if(order.getStat()==Constant.ORDER_STATUS_PAID){
				outDto.setWxPayResult("SUCCESS");
				_LOG.info("*******order【"+order.getId()+"】already pay success");
			}else {
				SortedMap<String, String> parameters = new TreeMap<String, String>();
				parameters.put("appid", config.getAppid());
				parameters.put("mch_id", config.getMchid());
				parameters.put("nonce_str", WXPayUtil.createNoncestr());
				parameters.put("out_trade_no", orderNo);
				String sign = WXPayUtil.createSign("UTF-8", parameters);
				parameters.put("sign", sign);
				String requestXML = WXPayUtil.getPrepayXml(parameters);
				_LOG.info("******* wx queryOrder request xml:"+requestXML);
				
				String resXml = Request
						.Post(config.getQueryOrderurl())
						.bodyString(requestXML,ContentType.parse("application/xml; charset=UTF-8"))
						.execute().returnContent()
						.asString(Charset.forName("utf-8"));
				_LOG.info("******* wx queryOrder response xml:"+resXml);
				
				Map<String,String> resMap = WXPayUtil.xml2map(resXml);
				
				if("SUCCESS".equals(resMap.get("trade_state"))){
					orderStatusService.updateOrderPaidAndCreateBarCode(order,resMap.get("transaction_id"),"WXSystem","【同步微信支付状态】，更改订单状态为已支付 orderNo【"+orderNo+"】");
					outDto.setWxPayResult("SUCCESS");
				}else{
					outDto.setWxPayResult("FAIL");
				}
			}
			
			OrderDetailExample detailExam = new OrderDetailExample();
			detailExam.createCriteria().andOrderIdEqualTo(order.getId());
			List<OrderDetail> detailLst = orderDetailMapper
					.selectByExample(detailExam);
			outDto.setDetailLst(detailLst);
		}
		return outDto;
	}
	
	
	
	
	
	
	
	
	
	
}
