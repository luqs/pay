package com.cuize.pay.service.dto;

import com.cuize.commons.meta.RequireField;

public class ListQrInDto {
	@RequireField
	private String openid;
	@RequireField
	private Integer qrStatus;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public Integer getQrStatus() {
		return qrStatus;
	}

	public void setQrStatus(Integer qrStatus) {
		this.qrStatus = qrStatus;
	}

}
