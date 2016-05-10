package com.cuize.pay.service.dto;

import com.cuize.commons.meta.RequireField;


public class QueryOrderInDto {
	@RequireField
	private String orderNo;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
