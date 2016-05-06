package com.cuize.order.service.result;


public class HQCreateOrderResult {
	private String barcode;//凭证号
	private String orderno;//订单号
	private Result result;
	
	public class Result{
		private Boolean status;
		private String errormessage;
		public Boolean getStatus() {
			return status;
		}
		public void setStatus(Boolean status) {
			this.status = status;
		}
		public String getErrormessage() {
			return errormessage;
		}
		public void setErrormessage(String errormessage) {
			this.errormessage = errormessage;
		}
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
}
