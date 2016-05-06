package com.cuize.order.service.dto;

public class MonitorInfo {
	private String appName;
	private String buildTimestamp;
	private String serverAddr;
	private String serverName;
	private String localAddr;
	private String localName;
	private String remoteAddr;
	private String remoteHost;
	private String remoteName;
	private String env;
	private String dsEnv;
	private String[] defaultProfiles;
	private String[] activeProfiles;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	public void setBuildTimestamp(String buildTimestamp) {
		this.buildTimestamp = buildTimestamp;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getLocalAddr() {
		return localAddr;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getRemoteName() {
		return remoteName;
	}

	public void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}

	public String[] getDefaultProfiles() {
		return defaultProfiles;
	}

	public void setDefaultProfiles(String[] defaultProfiles) {
		this.defaultProfiles = defaultProfiles;
	}

	public String[] getActiveProfiles() {
		return activeProfiles;
	}

	public void setActiveProfiles(String[] activeProfiles) {
		this.activeProfiles = activeProfiles;
	}

	public String getDsEnv() {
		return dsEnv;
	}

	public void setDsEnv(String dsEnv) {
		this.dsEnv = dsEnv;
	}

}
