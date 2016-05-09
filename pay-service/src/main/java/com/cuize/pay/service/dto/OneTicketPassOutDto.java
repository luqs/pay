package com.cuize.pay.service.dto;

public class OneTicketPassOutDto extends CommonOutDto {
	private Integer errorCode;//1:二维码有误   2:订单没有支付  3:已核销  4:打包产品不唯一   5:打包产品已核销   6,没到游玩时间  7:已过游玩时间 
	private String verifyTime;//核销时间
	private String successInfo;//提示信息： 本店+产品名称+数量 已核销

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(String verifyTime) {
		this.verifyTime = verifyTime;
	}

	public String getSuccessInfo() {
		return successInfo;
	}

	public void setSuccessInfo(String successInfo) {
		this.successInfo = successInfo;
	}
	
}
