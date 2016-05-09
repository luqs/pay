package com.cuize.pay.service.dto;

public class OrderVerifyOutDto extends CommonOutDto {
	private Integer errorCode;//错误代码    0：核销通过    1：订单不存在    2：订单未支付    3：订单号和手机号不匹配     4:无效的二维码   5：已入园  6:过期 7：更新失败
	
	private Integer adultTickeCount;//成人票
	
	private Integer childTicketCount;//儿童票
	
	private Integer oldTicketCount;//老人票

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public Integer getAdultTickeCount() {
		return adultTickeCount;
	}

	public void setAdultTickeCount(Integer adultTickeCount) {
		this.adultTickeCount = adultTickeCount;
	}

	public Integer getChildTicketCount() {
		return childTicketCount;
	}

	public void setChildTicketCount(Integer childTicketCount) {
		this.childTicketCount = childTicketCount;
	}

	public Integer getOldTicketCount() {
		return oldTicketCount;
	}

	public void setOldTicketCount(Integer oldTicketCount) {
		this.oldTicketCount = oldTicketCount;
	}
	
	
}
