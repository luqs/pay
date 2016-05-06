package com.cuize.order.service.dto;



/**
 * 接受请求参数的对象
 * @author luqingsong
 *
 */
public class CommonInDto<T> {
	
	private String jsonrpc;

	private String method;
	
	private String id;
	
	private T params;
	
	public T getParams() {
		return params;
	}

	public void setParams(T params) {
		this.params = params;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
