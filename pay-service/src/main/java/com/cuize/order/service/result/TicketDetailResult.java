package com.cuize.order.service.result;

import java.util.List;

public class TicketDetailResult {
	private TicketDetailResultInner product;
	
	private List<TicketDetailResultInner> packdtlLst;
	
	public TicketDetailResultInner getProduct() {
		return product;
	}

	public void setProduct(TicketDetailResultInner product) {
		this.product = product;
	}

	public List<TicketDetailResultInner> getPackdtlLst() {
		return packdtlLst;
	}

	public void setPackdtlLst(List<TicketDetailResultInner> packdtlLst) {
		this.packdtlLst = packdtlLst;
	}
}
