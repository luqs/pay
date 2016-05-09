/**
 * 
 */
package com.cuize.test.pay.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cuize.pay.service.dto.ListQrInDto;
import com.cuize.pay.service.impl.ListQrService;

/**
 * @author xyz(Auto-generated)
 * The Service class for the ko_product_stock database table.
 *
 */
@SuppressWarnings(value = { "all" })
public class ServiceTest extends BaseServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(ServiceTest.class);

	@Autowired
	private ListQrService service;
	
	@Test
	public void countByParams() throws Exception {
		ListQrInDto inDto = new ListQrInDto();
		inDto.setOpenid("oLt3cswh4oeSGnWGOjg8NMYJzX2o");
		inDto.setQrStatus(1);
		List o =service.listQr(inDto);
		System.out.println(o.size());
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		getOrder();
	}
	
	public static void getOrder() throws NoSuchAlgorithmException {
		String s="<PWBRequest><transactionName>QUERY_ORDER_REQ</transactionName><header><application>SendCode</application><requestTime>2016-04-26</requestTime></header><identityInfo><corpCode>sdzfxshczly</corpCode><userName>shczly</userName></identityInfo><orderRequest><order><orderCode>test1001</orderCode></order></orderRequest></PWBRequest>";
		String msg="xmlMsg="+ s +"3815F398369ACC9E153E16E41F86CDD6";
		System.out.println(MD5Encode(msg, null));
	}

	public static void getQr() throws NoSuchAlgorithmException {
		String s="<PWBRequest><transactionName>QUERY_IMG_URL_REQ</transactionName><header><application>SendCode</application><requestTime>2016-04-26 14:55:53</requestTime></header><identityInfo><corpCode>sdzfxshczly</corpCode><userName>shczly</userName></identityInfo><orderRequest><order><orderCode>test1001</orderCode></order></orderRequest></PWBRequest>";
		String msg="xmlMsg="+ s +"3815F398369ACC9E153E16E41F86CDD6";
		System.out.println(MD5Encode(msg, null));
	}
	
	public static void pushOrder() throws NoSuchAlgorithmException {
		String s="<PWBRequest><transactionName>SEND_CODE_REQ</transactionName><header><application>SendCode</application><requestTime>2016-04-26</requestTime></header><identityInfo><corpCode>sdzfxshczly</corpCode><userName>shczly</userName></identityInfo><orderRequest><order><certificateNo>330182198804273139</certificateNo><linkName>张登兵</linkName><linkMobile>13625606135</linkMobile><orderCode>test1001</orderCode><orderPrice>120.00</orderPrice><src>weixin</src><groupNo></groupNo><payMethod>spot</payMethod><ticketOrders><ticketOrder><orderCode>test100101</orderCode><quantity>1</quantity><totalPrice>120.00</totalPrice><occDate>2016-04-26</occDate><goodsCode>20141110020692</goodsCode><goodsName>地藏圣像景区门票</goodsName><remark>地藏圣像景区门票1张</remark></ticketOrder></ticketOrders></order></orderRequest></PWBRequest>";
		String msg="xmlMsg="+ s +"3815F398369ACC9E153E16E41F86CDD6";
		System.out.println(MD5Encode(msg, null));
	}
	
	public static String MD5Encode(String origin, String charsetname) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception exception) {
		}
		return resultString;
	}

	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}
