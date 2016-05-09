package com.cuize.pay.service.helper;

import java.nio.charset.Charset;
import java.rmi.ServerException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cuize.commons.dao.order.domain.Order;
import com.cuize.commons.dao.order.domain.OrderDetail;
import com.cuize.commons.dao.order.domain.OrderDetailExample;
import com.cuize.commons.dao.order.domain.OrderExample;
import com.cuize.commons.dao.order.domain.OrderOptlog;
import com.cuize.commons.dao.order.mapper.OrderDetailMapper;
import com.cuize.commons.dao.order.mapper.OrderMapper;
import com.cuize.commons.dao.order.mapper.OrderOptlogMapper;
import com.cuize.commons.meta.Constant;
import com.cuize.commons.meta.ServiceException;
import com.cuize.commons.utils.DateUtils;
import com.cuize.commons.utils.QrcodeUtils;
import com.cuize.pay.service.dto.GlobalConfig;
import com.cuize.pay.service.result.HQCreateOrderResult;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Service
@Transactional(value="orderTransactionManager",rollbackFor = Exception.class)
public class OrderService {
	private static final Logger _LOG = LoggerFactory
			.getLogger(OrderService.class);

	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderDetailMapper orderDetailMapper;
	@Autowired
	private OrderOptlogMapper orderOptLogMapper;
	@Autowired
	private GlobalConfig config;

	/**
	 * 产品库存入库接口
	 * 
	 * @author luqingsong
	 */
	public void updateOrderPaidAndCreateBarCode(Order order, String transactionId,
			String optUser, String reason) throws Exception {

		if (order.getStat() == Constant.ORDER_STATUS_PAID) {
			_LOG.info("*******order【" + order.getId() + "】already pay success");
		} else {
			OrderExample update = new OrderExample();
			update.createCriteria()
					.andIdEqualTo(Integer.valueOf(order.getId()))
					.andVersionEqualTo(order.getVersion());

			order.setStat(Constant.ORDER_STATUS_PAID);// 已经支付
			order.setPayTransactionId(transactionId);
			order.setVersion(order.getVersion() + 1);
			order.setUpdateTime(new Date());
			int updateRs = orderMapper.updateByExample(order, update);

			if (updateRs != 1) {
				String msg = "******* update 【" + updateRs
						+ "】  records by orderNo 【" + order.getOrderNo() + "】fail";
				_LOG.error(msg);
				throw new ServiceException(msg);
			}

			// 修改detail状态
			OrderDetailExample detailExam = new OrderDetailExample();
			detailExam.createCriteria().andOrderIdEqualTo(order.getId());
			List<OrderDetail> detailLst = orderDetailMapper
					.selectByExample(detailExam);
			for (OrderDetail detail : detailLst) {
				//如果是先支付后出票，则调用环企出票接口
				if(detail.getTicketSystype()==Constant.THIRD_SYSTYPE_HUANQI_PAYFIRST){
					HQCreateOrderResult response= createHQOrder(order.getOrderNo()+"_"+detail.getShopProductId(), detail.getTicketDay(), order.getUserName(), 
							detail.getThirdProductId(), detail.getCounts(), order.getMobilephone(), detail.getRemark());
					_LOG.info("*******【环企下单】" + " ticketId【"+detail.getThirdProductId()+"】");
					if(response.getResult().getStatus()){
						detail.setThirdOrderNo(response.getOrderno());
						detail.setBarcodeValue(response.getBarcode());
					}else{
						throw new ServerException("环企下单失败："+response.getResult().getErrormessage());
					}
				}
				
				String path = QrcodeUtils.createQRcode(
						detail.getBarcodeValue(), config.getQrpath());
				path = path.replaceAll(config.getQrpath(),
						config.getQrBaseUrl());
				OrderDetailExample updateExample = new OrderDetailExample();
				updateExample.createCriteria().andIdEqualTo(detail.getId())
						.andVersionEqualTo(detail.getVersion());

				detail.setBarcodeUrl(path);
				detail.setUpdateTime(new Date());
				detail.setVersion(detail.getVersion() + 1);
				detail.setStat(Constant.ORDER_STATUS_PAID);
				orderDetailMapper.updateByExampleSelective(detail,
						updateExample);
			}
			// 记录操作日志
			OrderOptlog orderOptLog = new OrderOptlog();
			orderOptLog.setOptDesc(reason);
			orderOptLog.setOptType(Constant.ORDEROPT_TYPE_UPDATE);
			orderOptLog.setOptUser(optUser);
			orderOptLog.setOrderId(Integer.valueOf(order.getId()));
			orderOptLogMapper.insertSelective(orderOptLog);

			_LOG.info("*******update order【" + order.getId()
					+ "】  set pay success");
		}

	}
	
	public HQCreateOrderResult createHQOrder(String orderNo,Date ticketDay, String userName,String ticketId,Integer counts,String mobilePhone,String remark) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		Date ticketEndDay = cal.getTime();// 游玩结束时间
		String beginDay = DateUtils.formatDate(ticketDay, "yyyy-MM-dd");
		String endDay = DateUtils.formatDate(ticketEndDay, "yyyy-MM-dd");
		
		String parameter = "{\"otaaccount\":\"" + config.getOtaaccount()
				+ "\"," + "        \"otapassword\":\""
				+ config.getOtapassword() + "\","
				+ "        \"TicketId\":\"" + ticketId + "\","
				+ "        \"StartDate\":\"" + beginDay + "\","
				+ "        \"EndDate\":\"" + endDay + "\","
				+ "        \"BookNumber\": \"" + counts
				+ "\"," + "        \"BookName\":\"" + userName
				+ "\"," + "        \"BookMobile\":\""
				+ mobilePhone + "\"," + "        \"BookIC\": \""
				+ "310101197204140010" + "\"," + "        \"Remark\":\""
				+ remark + "\",\"OTAOrderNo\":\"" + orderNo
				+ "\"}";
		
		String resJson = Request
				.Post(config.getHqMkOrderUrl())
				.bodyString(parameter,ContentType.parse("application/json; charset=UTF-8"))
				.execute().returnContent()
				.asString(Charset.forName("utf-8"));
		HQCreateOrderResult response= JSON.parseObject(
				resJson,new TypeReference<HQCreateOrderResult>(){});
		return response;
	}
}
