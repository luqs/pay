package com.cuize.order.service.result;



/**
 * 接受请求参数的对象
 * @author luqingsong
 *
 */
public class CommonResult<T> {
	
	private String jsonrpc;

	private String id;
	
	private T result;
	
	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
