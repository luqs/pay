package com.cuize.pay.service.impl;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.rmi.ServerException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;






import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cuize.commons.dao.order.domain.Order;
import com.cuize.commons.dao.order.domain.OrderDetail;
import com.cuize.commons.dao.order.domain.OrderDetailPackdtl;
import com.cuize.commons.dao.order.domain.OrderOptlog;
import com.cuize.commons.dao.order.domain.ProductStock;
import com.cuize.commons.dao.order.domain.ProductStockExample;
import com.cuize.commons.dao.order.mapper.OrderDetailMapper;
import com.cuize.commons.dao.order.mapper.OrderDetailPackdtlMapper;
import com.cuize.commons.dao.order.mapper.OrderMapper;
import com.cuize.commons.dao.order.mapper.OrderOptlogMapper;
import com.cuize.commons.dao.order.mapper.ProductStockMapper;
import com.cuize.commons.meta.Constant;
import com.cuize.commons.meta.ServiceException;
import com.cuize.commons.utils.BeanInitialUtils;
import com.cuize.commons.utils.DateUtils;
import com.cuize.commons.utils.RandomUtil;
import com.cuize.commons.utils.WXPayUtil;
import com.cuize.pay.service.dto.CommonInDto;
import com.cuize.pay.service.dto.CreateOrderInDto;
import com.cuize.pay.service.dto.CreateOrderInDtoInner;
import com.cuize.pay.service.dto.CreateOrderOutDto;
import com.cuize.pay.service.dto.GlobalConfig;
import com.cuize.pay.service.helper.OrderService;
import com.cuize.pay.service.result.CommonResult;
import com.cuize.pay.service.result.HQCreateOrderResult;
import com.cuize.pay.service.result.ShopProduct;
import com.cuize.pay.service.result.TicketDetailResult;
import com.cuize.pay.service.result.TicketDetailResultInner;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;

/**
 * 产品库存入库接口
 * 
 * @author luqingsong
 *
 */
@Service
@Transactional(value="orderTransactionManager",rollbackFor=Exception.class)
public class CreateOrderService{
	private static final Logger _LOG = LoggerFactory.getLogger(CreateOrderService.class);
	
	@Autowired 
	private OrderMapper orderMapper;
	@Autowired 
	private OrderDetailMapper orderDetailMapper;
	@Autowired 
	private OrderDetailPackdtlMapper packdtlMapper;
	@Autowired 
	private OrderOptlogMapper orderOptLogMapper;
	@Autowired 
	private ProductStockMapper stockMapper;
	@Autowired
	private GlobalConfig config;
	@Autowired
	private OrderService orderStatusService;
	
	/**
	 * 产品库存入库接口
	 * @author luqingsong
	 */
	public CreateOrderOutDto createOrder(CreateOrderInDto inDto)
			throws Exception {
		BeanInitialUtils.checkRequire(inDto);
		
		String orderNo = createOrderNo("10");
		Order order = new Order();
		order.setCertNo(inDto.getCertNo());
		order.setCertType(1);//1身份证
		order.setCnRemark(inDto.getDesc());
		order.setMobilephone(inDto.getMobilePhone());
		order.setUserName(inDto.getName());
		order.setOpenId(inDto.getOpenId());
		order.setStat(Constant.ORDER_STATUS_CREATE);
		order.setCreateTime(new Date());
		order.setOrderNo(orderNo);
		orderMapper.insertSelective(order);
		
		//新建订单明细并计算总价
		BigDecimal totalFee = new BigDecimal(0);
		StringBuffer wxPayBody = new StringBuffer();
		for(CreateOrderInDtoInner tic : inDto.getTicketList()){
			// 根据景点判断景点的闸机类型
			TicketDetailResult tickDetail = queryTicket(tic.getTicketId());
			if(tickDetail==null||tickDetail.getProduct()==null){
				throw new ServiceException("门票不存在【"+tic.getTicketId()+"】");
			}
			ShopProduct sp = tickDetail.getProduct().getProduct();
			if(sp==null||
					(Constant.PRODUCT_STATUS_SALES != sp.getPoductStatus()
						&&Constant.PRODUCT_STATUS_TEMP != sp.getPoductStatus())){
				throw new ServiceException("门票状态不可售【"+tic.getTicketId()+"】");
			}
			
			//=======体验票逻辑==========如果是临时体验票，控制库存========================
			if(sp.getPoductStatus()==Constant.PRODUCT_STATUS_TEMP){
				ProductStock stock = stockMapper.selectByPrimaryKey(sp.getId());
				if(stock==null||stock.getRemain()<=0){
					throw new ServiceException("门票【"+tic.getTicketId()+"】已经售完");
				}else{
					stock.setRemain(stock.getRemain()-1);
					stock.setUpdateTime(new Date());
					ProductStockExample example = new ProductStockExample();
					example.createCriteria()
						.andProductIdEqualTo(stock.getProductId())
						.andVersionEqualTo(stock.getVersion());
					stock.setVersion(stock.getVersion()+1);
					int res = stockMapper.updateByExample(stock, example);
					if(res<=0){
						throw new ServerException("更新门票【"+stock.getProductId()+"】库存失败");
					}
				}
			}
			//============================================================================
			
			totalFee = totalFee.add(new BigDecimal(sp.getSalesPrice()).multiply(new BigDecimal(tic.getQuantity())).multiply(new BigDecimal(100)));
			wxPayBody.append(sp.getProductName()+"   X "+tic.getQuantity()+"张 \n");
			Integer thirdSysType = tickDetail.getProduct().getTicketSystype();
			//插入订单明细
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setAccountPrice(sp.getAccountPrice());
			orderDetail.setDayTicketType(sp.getDayTicketType());
			orderDetail.setShopId(sp.getShopId());//景区Id
			orderDetail.setShopName(tickDetail.getProduct().getParkName());//景区名称
			orderDetail.setShopbuyprice(Double.valueOf(sp.getParkPrice()));
			orderDetail.setPersonTicketType(sp.getPersonTicketType());
			orderDetail.setCounts(tic.getQuantity());//数量
			orderDetail.setCnPrice(Double.valueOf(sp.getSalesPrice()));
			orderDetail.setSeasonTicketType(sp.getSeasonTicketType());
			orderDetail.setStat(Constant.ORDER_STATUS_CREATE);
			orderDetail.setTicketDay(tic.getTicketDay());
			orderDetail.setValidDays(sp.getValidDays());
			orderDetail.setShopProductId(tic.getTicketId());//门票ID
			orderDetail.setProductPacktype(sp.getProductPacktype());
			orderDetail.setThirdProductId(String.valueOf(sp.getTicketidHuanqi()));//第三方门票ID
			orderDetail.setShopProductName(sp.getProductName());//门票名称
			orderDetail.setOrderId(order.getId());//OrderId
			orderDetail.setTicketSystype(thirdSysType);//记录第三方票务系统类型
			orderDetail.setGateSystype(tickDetail.getProduct().getGateSystype());//记录第三方票务系统类型
			orderDetail.setCreateTime(new Date());
		
			if(Constant.PRODUCT_PACKTYPE_NOT == sp.getProductPacktype()){
				//非打包产品直接判断票务类型决定是环企订单还是hiyo订单，得到第三方单号后插入order_detail
				if (thirdSysType == Constant.THIRD_SYSTYPE_HIYO) {
					orderDetail.setBarcodeValue(orderNo+"_"+inDto.getMobilePhone()+"_"+sp.getShopId());
					
				} else if (thirdSysType == Constant.THIRD_SYSTYPE_HUANQI) {
					// 环企票务 調用環企下單接口
					HQCreateOrderResult response= orderStatusService.createHQOrder(orderNo+"_"+orderDetail.getShopProductId(), tic.getTicketDay(), inDto.getName(), 
							String.valueOf(sp.getTicketidHuanqi()), tic.getQuantity(), inDto.getMobilePhone(), inDto.getDesc());
					if (response != null && response.getResult().getStatus()) {
						orderDetail.setBarcodeValue(response.getBarcode());
						orderDetail.setThirdOrderNo(response.getOrderno());
					}else{
						throw new ServerException(response.getResult().getErrormessage());
					}
				} else if (thirdSysType == Constant.THIRD_SYSTYPE_SHENDA) {
					// 深大票務
					throw new ServerException("暂不支持的票务系统");
				}
				orderDetailMapper.insertSelective(orderDetail);
				
			} else if(Constant.PRODUCT_PACKTYPE_HUANQI == sp.getProductPacktype()){
				List<TicketDetailResultInner> packdtlProductLst= tickDetail.getPackdtlLst();
				//如果套票不包含任何子票
				if(packdtlProductLst==null||packdtlProductLst.size()<=0){
					throw new ServiceException("套票【"+tic.getTicketId()+"】没有包含任何票种");
				}
				//打包产品 不判断票务类型，只判断套票包含票种的票务类型，所以直接插入order_detail
				orderDetailMapper.insertSelective(orderDetail);
				
				//记录打包票中的门票系统类型，如果有多中票务类型则不允许下单
				//如果套票是环企类型，则套票中不可以再包含环企类型
				int valid = Constant.THIRD_SYSTYPE_HIYO;
				for(TicketDetailResultInner inner : packdtlProductLst){
					//如果套票的子票不是可售状态
					if(inner==null||inner.getProduct()==null
							|| (Constant.PRODUCT_STATUS_SALES != inner.getProduct().getPoductStatus()
								&& Constant.PRODUCT_STATUS_ONLYPACK != inner.getProduct().getPoductStatus()
								&& Constant.PRODUCT_STATUS_TEMP != inner.getProduct().getPoductStatus())){
						throw new ServiceException("套票【"+tic.getTicketId()+"】包含不可售门票");
					}
					Integer packTicketSystype = inner.getTicketSystype();
					Integer packPackType = inner.getProduct().getProductPacktype();
					if(packPackType==Constant.PRODUCT_PACKTYPE_HUANQI){
						throw new ServiceException("套票中不允许包含套票");
					}
					
					OrderDetailPackdtl oPackdtl = new OrderDetailPackdtl();
					oPackdtl.setAccountPrice(inner.getProduct().getAccountPrice());
					oPackdtl.setDayTicketType(inner.getProduct().getDayTicketType());
					oPackdtl.setShopId(inner.getProduct().getShopId());//景区Id
					oPackdtl.setShopName(inner.getParkName());//景区名称
					oPackdtl.setShopbuyprice(inner.getProduct().getParkPrice());
					oPackdtl.setPersonTicketType(inner.getProduct().getPersonTicketType());
					oPackdtl.setCounts(tic.getQuantity());//数量
					oPackdtl.setCnPrice(Double.valueOf(inner.getProduct().getSalesPrice()));
					oPackdtl.setSeasonTicketType(inner.getProduct().getSeasonTicketType());
					oPackdtl.setStat(Constant.ORDER_STATUS_CREATE);
					oPackdtl.setTicketDay(tic.getTicketDay());
					oPackdtl.setValidDays(inner.getProduct().getValidDays());
					oPackdtl.setShopProductId(inner.getProduct().getId());//门票ID
					oPackdtl.setThirdProductId(String.valueOf(inner.getProduct().getTicketidHuanqi()));//第三方门票ID
					oPackdtl.setShopProductName(inner.getProduct().getProductName());//门票名称
					oPackdtl.setOrderDetailId(orderDetail.getId());
					oPackdtl.setTicketSystype(packTicketSystype);
					oPackdtl.setGateSystype(inner.getGateSystype());
					oPackdtl.setCreateTime(new Date());
					
					if (packTicketSystype == Constant.THIRD_SYSTYPE_HIYO) {
						//如果套票中的当前子票是hiyo票务类型
						oPackdtl.setBarcodeValue(orderNo+"_"+inDto.getMobilePhone()+"_"+sp.getShopId());
					} else if (packTicketSystype == Constant.THIRD_SYSTYPE_HUANQI) {
						//如果套票中的当前子票是环企票务类型
						if(valid==Constant.THIRD_SYSTYPE_HIYO){
							valid = Constant.THIRD_SYSTYPE_HUANQI;
						}else{
							throw new ServiceException("套票所包含的门票属于多个票务系统");
						}
						// 环企票务 調用環企下單接口
						HQCreateOrderResult response= orderStatusService.createHQOrder(orderNo+"_"+orderDetail.getShopProductId()+"_"+oPackdtl.getShopProductId(), tic.getTicketDay(), inDto.getName(), 
								String.valueOf(inner.getProduct().getTicketidHuanqi()), tic.getQuantity(), inDto.getMobilePhone(), "打包票");
						if (response != null && response.getResult().getStatus()) {
							orderDetail.setBarcodeValue(response.getBarcode());
							oPackdtl.setBarcodeValue(response.getBarcode());
							oPackdtl.setThirdOrderNo(response.getOrderno());
						}else{
							throw new ServerException("环企下单失败"+response.getResult().getErrormessage());
						}
					} else if (thirdSysType == Constant.THIRD_SYSTYPE_SHENDA) {
						// //如果套票中的当前子票是深大票务类型
						throw new ServerException("暂不支持深大票务系统");
					}
					packdtlMapper.insertSelective(oPackdtl);
				}
				orderDetailMapper.updateByPrimaryKeySelective(orderDetail);
			}else{
				throw new ServiceException("没有定义pack type");
			}
			
		}
		
		//插入日志
		OrderOptlog orderOptLog = new OrderOptlog();
		orderOptLog.setOptDesc("【新建订单】用户【"+inDto.getMobilePhone()+"】新建订单，包括1条明细");
		orderOptLog.setOptType(Constant.ORDEROPT_TYPE_CREATE);
		orderOptLog.setOptUser(inDto.getWechatId());
		orderOptLog.setOrderId(order.getId());
		orderOptLog.setCreateTime(new Date());
		orderOptLogMapper.insertSelective(orderOptLog);
		_LOG.info("*******create order【"+order.getId()+"】 success");
		
		CreateOrderOutDto outDto= new CreateOrderOutDto();
		outDto.setOrderId(order.getId());
		outDto.setOrderNo(orderNo);
					
		/*请求微信预支付接口，获取prepay_id,并且给页面返回js调用微信支付所需要的参数*/
		SortedMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("appid", config.getAppid());
		parameters.put("body", wxPayBody.toString());
		parameters.put("detail", wxPayBody.toString());
		parameters.put("fee_type", "CNY");
		parameters.put("mch_id", config.getMchid());
		parameters.put("attach", String.valueOf(order.getId()));
		parameters.put("nonce_str", WXPayUtil.createNoncestr());
		parameters.put("notify_url", config.getNotifyurl());
		parameters.put("openid", inDto.getOpenId());
		parameters.put("out_trade_no", String.valueOf(order.getOrderNo()));
		parameters.put("spbill_create_ip", inDto.getPhoneIp());
		parameters.put("total_fee", String.valueOf(totalFee.intValue()));
		parameters.put("trade_type", "JSAPI");
		String sign = WXPayUtil.createSign("UTF-8", parameters);
		
		parameters.put("sign", sign);
		String requestXML = WXPayUtil.getPrepayXml(parameters);
		_LOG.info("******* wx prepay request xml:"+requestXML);
		
		String resXml = Request
				.Post(config.getPrepayurl())
				.bodyString(requestXML,ContentType.parse("application/xml; charset=UTF-8"))
				.execute().returnContent()
				.asString(Charset.forName("utf-8"));
		_LOG.info("******* wx prepay response xml:"+resXml);
		//得到预支付接口的响应参数，并计算paySign传给页面，供js调用微信支付
		Map<String,String> resMap = WXPayUtil.xml2map(resXml);
		if(!StringUtils.isEmpty(resMap.get("prepay_id"))){
			outDto.setAppId(config.getAppid());
			outDto.setNonceStr(WXPayUtil.createNoncestr());
			outDto.setPackage("prepay_id="+resMap.get("prepay_id"));
			outDto.setSignType("MD5");
			outDto.setTimeStamp(String.valueOf(DateUtils.getTimeInSeconds()));
			
			SortedMap<String, String> jsWxParam = new TreeMap<String, String>();
			jsWxParam.put("appId", outDto.getAppId());
			jsWxParam.put("timeStamp", String.valueOf(outDto.getTimeStamp()));
			jsWxParam.put("nonceStr", outDto.getNonceStr());
			jsWxParam.put("package", outDto.getPackage());
			jsWxParam.put("signType", outDto.getSignType());
			jsWxParam.put("key", config.getApikey());
			String paySign = WXPayUtil.createSign("UTF-8", jsWxParam);
			_LOG.info("******* generate js paySign【"+paySign+"】   of SortedMap:"+jsWxParam);
			outDto.setPaySign(paySign);
		}else{
			throw new ServerException("获取微信预支付Id失败，原因：【"+resMap.get("return_msg")+"】");
		}
		return outDto;
		
	}
	
	/**
	 * 生成一个订单号
	 * @param businessType 业务类型
	 * @return
	 */
	private String createOrderNo(String businessType){
		Date now=new Date();
		DateFormat format=new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		String dateStr=format.format(now);
		String numStr=RandomUtil.generateString(8);
		StringBuffer buffer=new StringBuffer();
		buffer.append(businessType).append(dateStr).append(numStr);
		String orderNo=buffer.toString();
		return orderNo;
	}
	
	private TicketDetailResult queryTicket(Integer ticketId) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ticketId", ticketId);
		JSONRPC2Request notifyReq = new JSONRPC2Request("product.ticket.queryTicketDetail",map, new Random().nextLong());
		String url = config.getProductSystemUrl();
		JSONRPC2Session session = new JSONRPC2Session(new URL(url));
		JSONRPC2Response jsonresp = session.send(notifyReq);
		if(jsonresp.getError()!=null){
			throw jsonresp.getError();
		}
		CommonResult<TicketDetailResult> commonResult = JSON.parseObject(jsonresp.toJSONString(),
				new TypeReference<CommonResult<TicketDetailResult>>(){});
		return commonResult.getResult();
	}
	
	public static void main(String[] args){
		try {
			String json="{\"id\":\"cz_4932714\",\"method\":\"order.ticket.createOrder\",\"params\":{\"desc\":\"\",\"mobilePhone\":\"15216763787\",\"ticketList\":[{\"ticketId\":6,\"ticketDay\":\"2016-04-16\",\"quantity\":\"1\"}],\"wechatId\":\"oLt3cs-FX-Z4PAC6JQx1gBM3wEvY\",\"name\":\"郭志勇测试\",\"certNo\":\"\",\"openId\":\"oLt3cs-FX-Z4PAC6JQx1gBM3wEvY\"},\"jsonrpc\":\"2.0\"}";
			CommonInDto<CreateOrderInDto> inDto = JSON.parseObject(json,
					new TypeReference<CommonInDto<CreateOrderInDto>>(){});
			
			
			System.out.println(inDto);
			
//			CommonInDto<CreateOrderInDto> in = new CommonInDto<CreateOrderInDto>();
//			in.setId("1");
//			in.setJsonrpc("rpc");
//			in.setMethod("123");
//			CreateOrderInDto j = new CreateOrderInDto();
//			j.setCertNo("cert");
//			j.setDesc("desc");
//			j.setMobilePhone("mob");
//			j.setName("name");
//			j.setOpenId("open");
//			j.setWechatId("open");
//			
//			List<Ticket> ticketList =new ArrayList<CreateOrderInDto.Ticket>();
//			Ticket t = new CreateOrderInDto().new Ticket();
//			t.setQuantity(1);
//			t.setTicketDay(new Date());
//			t.setTicketId(123);
//			ticketList.add(t);
//			
//			j.setTicketList(ticketList);
//			in.setParams(j);
//			System.out.println(JSON.toJSONString(in));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
