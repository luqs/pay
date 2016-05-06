package com.cuize.order.service.dto;

import com.cuize.commons.meta.RequireField;

/**
 * 核销接口接受的参数
 * @author Wangwei
 *
 */
public class OrderVerifyInDto {
	@RequireField
	private String orderNo;// 订单号
	@RequireField
	private String telephone;// 手机号
	@RequireField
	private String shopid;// 景区ID
	private String ticketId;//产品ID
	private String ticketCount;//门票数量
	private String orderId;// 订单号
	
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getShopid() {
		return shopid;
	}

	public void setShopid(String shopid) {
		this.shopid = shopid;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getTicketCount() {
		return ticketCount;
	}

	public void setTicketCount(String ticketCount) {
		this.ticketCount = ticketCount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	
}
