package com.cuize.test.pay.web;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring-context.xml")
public class BaseAppTest {

	// private static ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-context.xml");
	// private static UserInfoService userService = (UserInfoService) context.getBean("userInfoService");
	Logger logger = org.slf4j.LoggerFactory.getLogger(BaseAppTest.class);

}
