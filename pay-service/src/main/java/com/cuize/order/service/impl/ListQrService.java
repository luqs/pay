package com.cuize.order.service.impl;

import java.util.ArrayList;
import java.util.List;

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
import com.cuize.commons.utils.BeanInitialUtils;
import com.cuize.order.service.dto.ListQrInDto;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Service
@Transactional(value="orderTransactionManager",rollbackFor = Exception.class)
public class ListQrService {
	private static final Logger _LOG = LoggerFactory
			.getLogger(ListQrService.class);

	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderDetailMapper orderDetailMapper;

	/**
	 * 产品库存入库接口
	 * 
	 * @author luqingsong
	 */
	public List<OrderDetail> listQr(ListQrInDto inDto) throws Exception {
		BeanInitialUtils.checkRequire(inDto);
		
		OrderExample orderExample = new OrderExample();
		orderExample.createCriteria().andOpenIdEqualTo(inDto.getOpenid());
		orderExample.setOrderByClause("create_time desc");
		List<Order> orderList = orderMapper.selectByExample(orderExample);
		
		List<OrderDetail> detailLst =new ArrayList<OrderDetail>();
		for(Order order : orderList){ 
			//已经支付的订单
			OrderDetailExample detailExample = new OrderDetailExample();
			detailExample.createCriteria().andOrderIdEqualTo(order.getId()).andStatEqualTo(inDto.getQrStatus());
			List<OrderDetail> lst = orderDetailMapper.selectByExample(detailExample);
			detailLst.addAll(lst);
		}
		_LOG.info("*******list order【"+detailLst+"】 success");
		return detailLst;
	}
}
