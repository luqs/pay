package com.cuize.order.service.result;


public class TicketDetailResultInner{
		private ShopProduct product;
		
		private String parkName;
		
		private Integer ticketSystype;
		
		private Integer gateSystype;

		public ShopProduct getProduct() {
			return product;
		}

		public void setProduct(ShopProduct product) {
			this.product = product;
		}

		public String getParkName() {
			return parkName;
		}

		public void setParkName(String parkName) {
			this.parkName = parkName;
		}

		public Integer getTicketSystype() {
			return ticketSystype;
		}

		public void setTicketSystype(Integer ticketSystype) {
			this.ticketSystype = ticketSystype;
		}

		public Integer getGateSystype() {
			return gateSystype;
		}

		public void setGateSystype(Integer gateSystype) {
			this.gateSystype = gateSystype;
		}
	}