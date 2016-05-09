package com.cuize.pay.service.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GlobalConfig {
	@Value("${app.name:order}")
	private String appName;

	@Value("${env}")
	private String env;

	@Value("${timestamp}")
	private String buildTimestamp;

	@Value("${batch.succ.mobiles}")
	private String getBatchSuccMobiles;

	@Value("${batch.fail.mobiles}")
	private String getBatchFailMobiles;

	@Value("${ds.env}")
	private String dsEnv;
	
	@Value("${wx.pay.appid}")
	private String appid;
	
	@Value("${wx.pay.mchid}")
	private String mchid;

	@Value("${wx.pay.appsecret}")
	private String appsecret;
	
	@Value("${wx.pay.apikey}")
	private String apikey;
	
	@Value("${wx.pay.notifyurl}")
	private String notifyurl;
	
	@Value("${wx.api.prepayurl}")
	private String prepayurl;
	
	@Value("${wx.api.queryOrderurl}")
	private String queryOrderurl;
	
	@Value("${wx.api.accessTokenUrl}")
	private String accessTokenUrl;
	
	@Value("${path.qrpath}")
	private String qrpath;
	
	@Value("${otaaccount}")
	private String otaaccount;
	
	@Value("${otapassword}")
	private String otapassword;
	
	@Value("${hq.mkorder.url}")
	private String hqMkOrderUrl;//环企下单接口URL
	
	@Value("${pic.server.qrBaseUrl}")
	private String qrBaseUrl;
	
	@Value("${system.url.product}")
	private String productSystemUrl;

	public String getProductSystemUrl() {
		return productSystemUrl;
	}

	public String getQrBaseUrl() {
		return qrBaseUrl;
	}

	public String getAppName() {
		return appName;
	}

	public String getEnv() {
		return env;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	public String getGetBatchSuccMobiles() {
		return getBatchSuccMobiles;
	}

	public String getGetBatchFailMobiles() {
		return getBatchFailMobiles;
	}

	public String getDsEnv() {
		return dsEnv;
	}

	public String getAppid() {
		return appid;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public String getApikey() {
		return apikey;
	}

	public String getMchid() {
		return mchid;
	}

	public String getNotifyurl() {
		return notifyurl;
	}

	public String getPrepayurl() {
		return prepayurl;
	}

	public String getQueryOrderurl() {
		return queryOrderurl;
	}

	public String getQrpath() {
		return qrpath;
	}

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public String getOtaaccount() {
		return otaaccount;
	}

	public String getOtapassword() {
		return otapassword;
	}

	public String getHqMkOrderUrl() {
		return hqMkOrderUrl;
	}
	
}
