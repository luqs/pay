package com.cuize.order.service.dto;

import com.cuize.commons.meta.RequireField;



public class CheckOrderPayInDto {
		@RequireField
		private Integer orderId;
		@RequireField
		private String orderNo;

		public Integer getOrderId() {
			return orderId;
		}

		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		
}
