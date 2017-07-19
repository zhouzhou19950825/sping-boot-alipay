package com.upic;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.upic.alipay.utils.AliPayUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpicAlibabaPayDemoApplicationTests {
	
	@Autowired
	private AliPayUtils a;
	@Test
	public void contextLoads() throws UnsupportedEncodingException {
		/**
		 * 1500442466258
		 */
		Map<String,String> parms=new HashMap<String,String>();
		parms.put("body", "测试数据");	
		parms.put("subject", "App支付测试Java");
		long order=System.currentTimeMillis();
		parms.put("outTradeNo", order+"");
		System.out.println(order);
		parms.put("timeoutExpress", "100m");
		parms.put("totalAmount", "0.01");
		parms.put("productCode", "QUICK_MSECURITY_PAY");
		AlipayTradeAppPayResponse appPay = a.appPay(parms, "https://www.baidu.com");
		System.out.println(URLDecoder.decode(appPay.getBody(), "UTF-8"));
		System.out.println(appPay.getTradeNo());
		System.out.println(appPay.isSuccess());
	}
	@Test
	public void queryOrder() throws UnsupportedEncodingException, AlipayApiException {
		/**
		 * 1500442466258
		 */
//		Map<String,String> parms=new HashMap<String,String>();
//		parms.put("body", "测试数据");
//		parms.put("subject", "App支付测试Java");
//		long order=System.currentTimeMillis();
//		parms.put("outTradeNo", order+"");
//		System.out.println(order);
//		parms.put("timeoutExpress", "30m");
//		parms.put("totalAmount", "0.01");
//		parms.put("productCode", "QUICK_MSECURITY_PAY");
//		AlipayTradeAppPayResponse appPay = a.appPay(parms, "https://www.baidu.com");
//		System.out.println(URLDecoder.decode(appPay.getBody(), "UTF-8"));
//		System.out.println(appPay.getCode());
		String json="{\"out_trade_no\":\"1500443454028\"}";
		AlipayTradeQueryResponse testQuery = a.testQuery(json);
		System.out.println(testQuery.getBody());
	}
}
