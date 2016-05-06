package com.cuize.order.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuize.commons.dao.order.domain.Order;
import com.cuize.commons.dao.order.domain.OrderExample;
import com.cuize.commons.dao.order.mapper.OrderDetailMapper;
import com.cuize.commons.dao.order.mapper.OrderMapper;
import com.cuize.commons.dao.order.mapper.OrderOptlogMapper;
import com.cuize.commons.meta.Constant;
import com.cuize.commons.meta.ServiceException;
import com.cuize.order.service.dto.GlobalConfig;
import com.cuize.order.service.dto.WxPayNotifyInDto;
import com.cuize.order.service.helper.OrderService;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Service
@Transactional(value="orderTransactionManager",rollbackFor = Exception.class)
public class WxPayNotifyService {
	private static final Logger _LOG = LoggerFactory
			.getLogger(WxPayNotifyService.class);

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
	 * 
	 * @author luqingsong
	 */
	public void wxPayNotify(WxPayNotifyInDto indto) throws Exception {
		if ("SUCCESS".equals(indto.getReturn_code())
				&& "SUCCESS".equals(indto.getResult_code())) {

			Integer orderId = Integer.valueOf(indto.getAttach());
			String orderNo = indto.getOut_trade_no();

			OrderExample select = new OrderExample();
			select.createCriteria().andIdEqualTo(orderId)
					.andOrderNoEqualTo(orderNo);
			// 检查数据库订单是否存在
			List<Order> ccOrderLst = orderMapper.selectByExample(select);
			if (ccOrderLst == null || ccOrderLst.size() != 1) {
				String msg = "******* fund 【" + ccOrderLst + "】  records by orderId 【" + orderId + "】 and orderNo【" + orderNo + "】";
				_LOG.error(msg);
				throw new ServiceException(msg);
			}

			// 如果订单是已经支付状态，直接返回
			Order ccOrder = ccOrderLst.get(0);
			if (ccOrder.getStat() == Constant.ORDER_STATUS_PAID) {
				return;
			} else {
				// 生成二维码并更新订单为已支付状态
				orderStatusService.updateOrderPaidAndCreateBarCode(ccOrder, indto.getTransaction_id(), 
						"WXSystem", "【微信支付成功回调】，更改订单状态并生成二维码 orderId【" + orderId + "】 orderNo【" + orderNo + "】");
			}
		}
	}

}
