package com.cuize.order.web.gateway;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cuize.order.service.dto.GlobalConfig;
import com.cuize.order.service.dto.MonitorInfo;

@RestController
public class MonitorController {
	@Autowired
	private GlobalConfig config;

	@Autowired
	private Environment env;

	private static final Logger _LOG = LoggerFactory.getLogger(MonitorController.class);

	@RequestMapping(value = "/monitor", method = RequestMethod.GET)
	public MonitorInfo getmonitor(Object obj, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		_LOG.info("####monitor");
		_LOG.info("####monitor.appName=" + config.getAppName());
		env.getActiveProfiles();

		MonitorInfo info = new MonitorInfo();
		info.setAppName(config.getAppName());
		info.setBuildTimestamp(config.getBuildTimestamp());
		info.setEnv(config.getEnv());
		info.setServerName(request.getServerName());
		info.setRemoteAddr(request.getRemoteAddr());
		info.setRemoteName(request.getRemoteHost());
		info.setLocalAddr(request.getLocalAddr());
		info.setLocalName(request.getLocalName());
		info.setActiveProfiles(env.getActiveProfiles());
		info.setDefaultProfiles(env.getDefaultProfiles());
		info.setDsEnv(config.getDsEnv());
		return info;
	}
}
