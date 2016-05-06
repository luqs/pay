package com.cuize.order.service.dto;

import com.cuize.commons.meta.RequireField;

/**
 * 一票通核销接口接受参数
 * @author Wangwei
 *
 */
public class OneTicketPassInDto {
	@RequireField
	private String barCode;//环企二维码中的凭证号
	@RequireField
	private Integer shopId;//景点ID
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	public Integer getShopId() {
		return shopId;
	}
	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}
	
}
