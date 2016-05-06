package com.cuize.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuize.commons.dao.order.domain.Order;
import com.cuize.commons.dao.order.domain.OrderDetail;
import com.cuize.commons.dao.order.domain.OrderDetailExample;
import com.cuize.commons.dao.order.domain.OrderExample;
import com.cuize.commons.dao.order.mapper.OrderDetailMapper;
import com.cuize.commons.dao.order.mapper.OrderMapper;
import com.cuize.commons.meta.Constant;
import com.cuize.commons.utils.BeanInitialUtils;
import com.cuize.commons.utils.DateUtils;
import com.cuize.order.service.dto.OrderVerifyInDto;
import com.cuize.order.service.dto.OrderVerifyOutDto;

@Service
@Transactional(value="orderTransactionManager",rollbackFor=Exception.class)
public class OrderVerifyService {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderDetailMapper orderDetailMapper;

	public OrderVerifyOutDto queryOrderByOrderNo(OrderVerifyInDto inDto) throws Exception {
		BeanInitialUtils.checkRequire(inDto);	
		OrderVerifyOutDto outDto=new OrderVerifyOutDto();
		OrderExample example=new OrderExample();
		example.createCriteria().andOrderNoEqualTo(inDto.getOrderNo());
		List<Order> orders=orderMapper.selectByExample(example);
		//订单不存在的情况
		if(orders==null || orders.size()!=1){
			outDto.setErrorCode(1);
			return outDto;
		}
		Order o=orders.get(0);
		//订单还未支付
		if(o.getStat()==Constant.ORDER_STATUS_CREATE){
			outDto.setErrorCode(2);
			return outDto;
		}
		
		//订单状态为4表示已被退款
		if(o.getStat()==Constant.ORDER_STATUS_CANCEL){
			outDto.setErrorCode(6);
			return outDto;
		}
		//验证订单号和手机号是否匹配 
		
		OrderDetailExample qrExample = new OrderDetailExample();
		qrExample.createCriteria().andOrderIdEqualTo(Integer.valueOf(o.getId())).andShopIdEqualTo(Integer.valueOf(inDto.getShopid()));
		List<OrderDetail> orderQrList = orderDetailMapper.selectByExample(qrExample);
		//无效的二维码
		if(orderQrList==null || orderQrList.size()!=1){
			outDto.setErrorCode(4);
			return outDto;
		}
		OrderDetail qr=orderQrList.get(0);//二维码信息
		//二维码已核销过
		if(qr.getStat()==Constant.ORDER_STATUS_FINISH){
			outDto.setErrorCode(5);
			return outDto;
		}
		
		
		Date ticketDay = qr.getTicketDay();
		if(ticketDay!=null){
			int flag=dateCompare(ticketDay);
			if(flag!=0){
				outDto.setErrorCode(flag);//游玩过时
				return outDto;
			}
		}
		
		OrderDetailExample upateExample=new OrderDetailExample();
		upateExample.createCriteria().andIdEqualTo(qr.getId()).andVersionEqualTo(qr.getVersion());
		qr.setStat(Constant.ORDER_STATUS_FINISH);//修改状态     1:未支付  2:已支付  3：已核销
		qr.setVersion(qr.getVersion()+1);
		qr.setUpdateTime(new Date());
	    
		int updateCount = orderDetailMapper.updateByExample(qr, upateExample);//更新二维码
		
		if(updateCount!=1){
			outDto.setErrorCode(7);//更新失败
			return outDto;
		}
		
		
		Integer personTicketCount = qr.getPersonTicketType();
		Integer ticketCount=qr.getCounts();
		
		outDto.setChildTicketCount(0);
		outDto.setAdultTickeCount(0);
		outDto.setOldTicketCount(0);
		if(personTicketCount==1){//儿童票
			outDto.setChildTicketCount(ticketCount);			
		}else if(personTicketCount==2){//成人票
			outDto.setAdultTickeCount(ticketCount);
		}else if(personTicketCount==3){//老年票
			outDto.setOldTicketCount(ticketCount);
		}
		outDto.setErrorCode(0);
		return outDto;
	}
	
	/**
	 * 比较票的游玩时间过期与否
	 * @param ticketDay 游玩时间
	 * @return 0:门票可以入园游玩    6：门票没到游玩时间  8：门票游玩时间已过
	 */
	private int dateCompare(Date ticketDay){
		String ticketDayStr=DateUtils.formatDate(ticketDay, "yyyyMMdd");//游玩时间字符串
		Date nextTicketDay=DateUtils.getThedayBeforeOrAfterSomeDay(ticketDay, 1);
		String nextTicketDayStr=DateUtils.formatDate(nextTicketDay,"yyyyMMdd");
		Date today=new Date();
		String todayStr=DateUtils.formatDate(today, "yyyyMMdd");
		int compareRes1=DateUtils.compare(todayStr, ticketDayStr);
		int compareRes2=DateUtils.compare(todayStr, nextTicketDayStr);
		//过期的情况
		if(compareRes1==1 && compareRes2==1){
			return 8; 
		}else if(compareRes1==-1){
			//未到游玩时间
		    return 6;
		}
		return 0;
	}
	
}
