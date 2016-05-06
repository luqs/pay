package com.cuize.order.service.dto;

import java.util.List;

import com.cuize.commons.dao.order.domain.OrderDetail;

public class CheckOrderPayOutDto extends CommonOutDto {

	private String wxPayResult;
	
	private List<OrderDetail> detailLst;

	public String getWxPayResult() {
		return wxPayResult;
	}

	public void setWxPayResult(String wxPayResult) {
		this.wxPayResult = wxPayResult;
	}

	public List<OrderDetail> getDetailLst() {
		return detailLst;
	}

	public void setDetailLst(List<OrderDetail> detailLst) {
		this.detailLst = detailLst;
	}

}
