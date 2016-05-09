package com.cuize.pay.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cuize.commons.dao.order.domain.OrderDetail;
import com.cuize.commons.dao.order.domain.OrderDetailExample;
import com.cuize.commons.dao.order.domain.OrderDetailPackdtl;
import com.cuize.commons.dao.order.domain.OrderDetailPackdtlExample;
import com.cuize.commons.dao.order.mapper.OrderDetailMapper;
import com.cuize.commons.dao.order.mapper.OrderDetailPackdtlMapper;
import com.cuize.commons.meta.Constant;
import com.cuize.commons.utils.BeanInitialUtils;
import com.cuize.commons.utils.DateUtils;
import com.cuize.pay.service.dto.OneTicketPassInDto;
import com.cuize.pay.service.dto.OneTicketPassOutDto;

/**
 * 一票通核销
 * @author Wangwei
 *
 */
@Service
@Transactional(value="orderTransactionManager",rollbackFor=Exception.class)
public class OneTicketVerificationService {
	private static final Logger LOG = LoggerFactory.getLogger(OneTicketVerificationService.class);
	@Autowired
	private OrderDetailMapper orderDetailMapper;
	@Autowired
	private OrderDetailPackdtlMapper orderDetailPackdtlMapper;
	/**
	 * 核销方法
	 * @param inDto
	 * @return
	 * @throws Exception 
	 */
	public OneTicketPassOutDto verification(OneTicketPassInDto inDto) throws Exception{
		BeanInitialUtils.checkRequire(inDto);
		String successInfo="";//成功提示信息
		LOG.info("开始核销一票通二维码,二维码的barcode="+inDto.getBarCode()+";店铺ID为："+inDto.getShopId()+"======");
		OneTicketPassOutDto outDto=new OneTicketPassOutDto();//返回值
		OrderDetailExample orderDetailExample=new OrderDetailExample();
		orderDetailExample.createCriteria().andBarcodeValueEqualTo(inDto.getBarCode());
		List<OrderDetail> orderDetailList = orderDetailMapper.selectByExample(orderDetailExample);
		LOG.info("查询到的订单明细的记录数为：【"+(orderDetailList!=null?orderDetailList.size():0)+"】条记录");
		if(orderDetailList==null || orderDetailList.size()!=1){
			LOG.info("核销失败，失败愿意：二维码【"+inDto.getBarCode()+"】对应的订单明细不唯一");
			outDto.setErrorCode(1);
			return outDto;
		}
		//判断是否支付
		LOG.info("开始校验是否支付,订单是否核销过====");
		OrderDetail orderDetail=orderDetailList.get(0);
		LOG.info("订单明细ID：【"+(orderDetail!=null?orderDetail.getId():null)+"】,订单明细状态:【"+(orderDetail!=null?orderDetail.getStat():null)+"】");
		String hxTime="";//核销时间
		//1：未支付  2：已支付   3：已核销
		if(orderDetail!=null){
			hxTime=getHxTime(orderDetail.getId(),inDto.getShopId());
			Integer orderDetailStatus=orderDetail.getStat();
			if(orderDetailStatus==1){
				LOG.info("核销失败，失败原因：订单未支付=======");
				outDto.setErrorCode(2);
				return outDto;
			}
			if(orderDetailStatus==3){
				LOG.info("核销失败，失败原因：已核销过========");
				outDto.setErrorCode(3);
				outDto.setVerifyTime(hxTime);
				return outDto;
			}
		}
		//判断订单明细的有效期
		Date playDate = orderDetail.getTicketDay();//游玩时间
		LOG.info("游玩时间："+new SimpleDateFormat("yyyy-MM-dd").format(playDate));
		//是否到游玩开始时间
		Date today=new Date();
		int beginPlayFlag=dateCompare(playDate,today);//是否到达游玩时间
		int validDays=orderDetail.getValidDays();//有效天数
		Date lastPlayDate=DateUtils.getThedayBeforeOrAfterSomeDay(playDate,validDays);//最后游玩日期
		int lastPlayFlag=dateCompare(today,lastPlayDate);
		if(beginPlayFlag==1){
			//没到游玩时间
			LOG.info("核销失败，失败原因：还没到游玩时间!");
			outDto.setErrorCode(6);
			return outDto;
		}
		if(lastPlayFlag==1){
			//已过有效期
			LOG.info("核销失败，失败原因：已过游玩最后期限");
			outDto.setErrorCode(7);
			return outDto;
		}
		
		//以上是对套票的校验,下面对套票中的票种进行判断
		//首先取出当前套票明细下的所有票种信息
		OrderDetailPackdtlExample packProductExample=new OrderDetailPackdtlExample();
		packProductExample.createCriteria().andOrderDetailIdEqualTo(orderDetail.getId()).andShopIdEqualTo(inDto.getShopId());
		List<OrderDetailPackdtl> orderDetailPackDtlList=orderDetailPackdtlMapper.selectByExample(packProductExample);
		LOG.info("景点ID:【"+inDto.getShopId()+"】的OrderDetailPackdtl数量为:【"+(orderDetailPackDtlList!=null?orderDetailPackDtlList.size():0)+"】条记录!");
		if(orderDetailPackDtlList==null || orderDetailPackDtlList.size()!=1){
			LOG.info("核销失败，失败原因：没有查到店铺ID为【"+inDto.getShopId()+"】的打包产品信息，或者查到的产品不唯一");
			outDto.setErrorCode(4);
			return outDto;
		}
		OrderDetailPackdtl orderDetailPackdtl=orderDetailPackDtlList.get(0);
		LOG.info("【OrderDetailPackdtl】的ID为【"+orderDetailPackdtl.getId()+"】");
		if(orderDetailPackdtl.getStat()==3){
			LOG.info("核销失败，失败原因：id为【"+orderDetailPackdtl.getId()+"】的OrderDetailPackdtl已经核销！");
			outDto.setErrorCode(3);
			outDto.setVerifyTime(hxTime);
			return outDto;
		}
		successInfo="本店 "+orderDetailPackdtl.getShopName()+" 产品: "+orderDetailPackdtl.getShopProductName()+" 数量："+orderDetailPackdtl.getCounts()+" 核销成功！";
		//开始更新
		//更新OrderDetailPackdtl
		OrderDetailPackdtlExample updateOrderDetailPackdtlExample=new OrderDetailPackdtlExample();
		updateOrderDetailPackdtlExample.createCriteria().andIdEqualTo(orderDetailPackdtl.getId()).andVersionEqualTo(orderDetailPackdtl.getVersion());
		orderDetailPackdtl.setStat(Constant.ORDER_STATUS_FINISH);
		orderDetailPackdtl.setVersion(orderDetailPackdtl.getVersion()+1);
		orderDetailPackdtl.setUpdateTime(new Date());
		int updatePackDtlCount = orderDetailPackdtlMapper.updateByExample(orderDetailPackdtl, updateOrderDetailPackdtlExample);
		if(updatePackDtlCount!=1){
			LOG.info("核销失败，失败原因：更新OrderDetailPackdtl失败！");
			outDto.setErrorCode(8);
			return outDto;
		}
		//更新orderdetail
		OrderDetailPackdtlExample detailPackExample=new OrderDetailPackdtlExample();
		detailPackExample.createCriteria().andOrderDetailIdEqualTo(orderDetail.getId());
		List<OrderDetailPackdtl> orderDetailPackdtlList=orderDetailPackdtlMapper.selectByExample(detailPackExample);
		boolean isCanUpdateOrderDetail=true;//是否可以更新订单详情
		if(orderDetailPackdtlList!=null && orderDetailPackdtlList.size()>0){
			for(int i=0;i<orderDetailPackdtlList.size();i++){
				OrderDetailPackdtl pack=orderDetailPackdtlList.get(i);
				if(pack.getStat()!=3){
					isCanUpdateOrderDetail=false;
					break;
				}
			}
		}else{
			isCanUpdateOrderDetail=false;
		}
		if(isCanUpdateOrderDetail){
			OrderDetailExample updateDetailExample=new OrderDetailExample();
			updateDetailExample.createCriteria().andIdEqualTo(orderDetail.getId()).andVersionEqualTo(orderDetail.getVersion());
			orderDetail.setStat(Constant.ORDER_STATUS_FINISH);
			orderDetail.setVersion(orderDetail.getVersion()+1);
			orderDetail.setUpdateTime(new Date());
			int updateOrderDetailCounts = orderDetailMapper.updateByExample(orderDetail, updateDetailExample);
			if(updateOrderDetailCounts!=1){
				LOG.info("核销失败，失败原因：更新OrderDetail失败！");
				outDto.setErrorCode(8);
				return outDto;
			}
		}
		LOG.info("恭喜你，核销成功!!!!!!!!!!!");
		outDto.setErrorCode(0);
		outDto.setSuccessInfo(successInfo);
		return outDto;
	}
	
	/**
	 * 获取核销时间
	 * @return
	 */
	private String getHxTime(Integer detailId,Integer shopId){
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		OrderDetailPackdtlExample example=new OrderDetailPackdtlExample();
		example.createCriteria().andOrderDetailIdEqualTo(detailId).andShopIdEqualTo(shopId);
		List<OrderDetailPackdtl> OrderDetailPackdtlList =  orderDetailPackdtlMapper.selectByExample(example);
		if(OrderDetailPackdtlList!=null && OrderDetailPackdtlList.size()>0){
			OrderDetailPackdtl packDtl=OrderDetailPackdtlList.get(0);
			Date updateTime = packDtl.getUpdateTime();
			if(updateTime!=null){
				String timeStr=format.format(updateTime);
				return timeStr;
			}
		}
		return "";
	}
	
	/**
	 * 日期比较
	 * date1>date2 返回1
	 * date1=date2返回0
	 * date1<date2返回-1
	 */
	private int dateCompare(Date date1,Date date2){
		String date1Str=DateUtils.formatDate(date1, "yyyyMMdd");//游玩时间字符串
		String date2Str=DateUtils.formatDate(date2,"yyyyMMdd");
		int compareRes1=DateUtils.compare(date1Str, date2Str);
		return compareRes1;
	}
}
