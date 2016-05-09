package com.cuize.pay.service.dto;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.cuize.commons.meta.RequireField;

public class CreateOrderInDtoInner{
			//1.游玩时间*
			@JSONField(format="yyyy-MM-dd")
			@RequireField
			private Date ticketDay;
			//2.订票数量*
			@RequireField
			private Integer quantity;
			//7.票ID*
			@RequireField
			private Integer ticketId;
			
			public Date getTicketDay() {
				return ticketDay;
			}
			public void setTicketDay(Date ticketDay) {
				this.ticketDay = ticketDay;
			}
			public Integer getQuantity() {
				return quantity;
			}
			public void setQuantity(Integer quantity) {
				this.quantity = quantity;
			}
			public Integer getTicketId() {
				return ticketId;
			}
			public void setTicketId(Integer ticketId) {
				this.ticketId = ticketId;
			}
		}