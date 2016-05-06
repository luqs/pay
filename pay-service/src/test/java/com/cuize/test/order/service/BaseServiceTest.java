package com.cuize.test.order.service;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles(value="local")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring-context-service.xml","classpath*:/spring-datasource.xml"})
public class BaseServiceTest {

	// private static ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-context.xml");
	// private static UserInfoService userService = (UserInfoService) context.getBean("userInfoService");
	Logger logger = org.slf4j.LoggerFactory.getLogger(BaseServiceTest.class);

}
