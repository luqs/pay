/**
 * 
 */
package com.cuize.test.pay.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cuize.pay.service.impl.QueryOrderService;

/**
 * @author xyz(Auto-generated)
 * The Service class for the ko_product_stock database table.
 *
 */
@SuppressWarnings(value = { "all" })
public class ServiceTest extends BaseServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(ServiceTest.class);

	@Autowired
	private QueryOrderService service;
	
	@Test
	public void countByParams() throws Exception {
		System.out.println(service);
	}
	
}
