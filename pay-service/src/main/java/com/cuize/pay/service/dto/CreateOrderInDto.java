package com.cuize.pay.service.dto;

import java.util.List;

import com.cuize.commons.meta.RequireField;


public class CreateOrderInDto {
	    private List<CreateOrderInDtoInner> ticketList;
		//3.姓名*
		@RequireField
		private String name;
		//4.手机*
		@RequireField
		private String mobilePhone;
		//5.身份证号
		private String certNo;
		//8.销售价格*
		@RequireField
		private String openId;
		//15. 用户的微信的用户ID
		private String wechatId;
		
		private String phoneIp;
		
		private String desc;
		
		public List<CreateOrderInDtoInner> getTicketList() {
			return ticketList;
		}
		public void setTicketList(List<CreateOrderInDtoInner> ticketList) {
			this.ticketList = ticketList;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMobilePhone() {
			return mobilePhone;
		}
		public void setMobilePhone(String mobilePhone) {
			this.mobilePhone = mobilePhone;
		}
		public String getCertNo() {
			return certNo;
		}
		public void setCertNo(String certNo) {
			this.certNo = certNo;
		}
		public String getOpenId() {
			return openId;
		}
		public void setOpenId(String openId) {
			this.openId = openId;
		}
		public String getWechatId() {
			return wechatId;
		}
		public void setWechatId(String wechatId) {
			this.wechatId = wechatId;
		}
		public String getPhoneIp() {
			return phoneIp;
		}
		public void setPhoneIp(String phoneIp) {
			this.phoneIp = phoneIp;
		}
}
