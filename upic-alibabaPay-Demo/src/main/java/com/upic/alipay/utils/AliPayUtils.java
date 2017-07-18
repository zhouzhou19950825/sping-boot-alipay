package com.upic.alipay.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

/**
 * 
 * @author DTZ
 *
 */
@Component
public class AliPayUtils {
	@Value("${ALIPAY.APP_ID}")
	private static String APP_ID;

	@Value("${ALIPAY.APP_PRIVATE_KEY}")
	private static String APP_PRIVATE_KEY;

	@Value("${ALIPAY.CHARSET}")
	private static String CHARSET;

	@Value("${ALIPAY.ALIPAY_PUBLIC_KEY}")
	private static String ALIPAY_PUBLIC_KEY;

	@Value("${ALIPAY.PAY_URL}")
	private static String PAY_URL;
	
	private static final String JSON="json";
	// 实例化客户端
	protected static AlipayClient alipayClient = new DefaultAlipayClient(PAY_URL, APP_ID, APP_PRIVATE_KEY, JSON,
			CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");

	/**
	 * APP 请求支付
	 * @param condi
	 * @param notifyUrl
	 * @return
	 */
	public static AlipayTradeAppPayResponse appPay(Map<String, String> condi, String notifyUrl) {
		// 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		// SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = null;
		try {
			model = (AlipayTradeAppPayModel) getModel(condi,new AlipayTradeAppPayModel());
		} catch (SecurityException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e1) {
			return  null;
		}
		// model.setBody("我是测试数据");
		// model.setSubject("App支付测试Java");
		// model.setOutTradeNo("");
		// model.setTimeoutExpress("30m");
		// model.setTotalAmount("0.01");
		// model.setProductCode("QUICK_MSECURITY_PAY");
		request.setBizModel(model);
		request.setNotifyUrl(notifyUrl);
		try {
			// 这里和普通的接口调用不同，使用的是sdkExecute
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			// System.out.println(response.getBody());//就是orderString
			// 可以直接给客户端请求，无需再做处理。
			return response;
		} catch (AlipayApiException e) {
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * 退款查询
	 * trade_no:支付宝交易号	
	 * out_trade_no:创建交易传入的商户订单号	
	 * out_request_no:本笔退款对应的退款请求号	
	 * refund_reason:发起退款时，传入的退款原因	
	 * total_amount:该笔退款所对应的交易的订单金额	
	 * refund_amount:本次退款请求，对应的退款金额	
	 * @throws AlipayApiException 
	 * @author DTZ
	 * @return AlipayTradeFastpayRefundQueryResponse
	 */
	public static AlipayTradeFastpayRefundQueryResponse searchByCondi(String jsonBody) throws AlipayApiException{
		AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
		request.setBizContent(jsonBody);
		AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
		return response.isSuccess()==true?response:null;
	}
	
	/**
	 * 订单查询
	 * out_trade_no
	 * trade_no
	 * @param jsonBody
	 * @throws AlipayApiException 
	 */
	public static AlipayTradeQueryResponse testQuery(String jsonBody) throws AlipayApiException{
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		request.setBizContent(jsonBody);
		AlipayTradeQueryResponse response = alipayClient.execute(request);
		return response.isSuccess()==true?response:null;
	}
	/**
	 * 条件分装
	 * @param condi
	 * @return
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private static Object getModel(Map<String, String> condi,Object entity)
			throws SecurityException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Field[] fields = Class.forName(entity.getClass().getCanonicalName()).getDeclaredFields();
		if (fields.length > 0 && condi != null && condi.size() > 0) {
			for (Field field : fields) {
				String fieldName = field.getName();
				if (condi.containsKey(fieldName)) {
					String methodName = getMethodName(fieldName, MethodHelper.SET_METHOD);
					Object obj = condi.get(fieldName);
					Method method = entity.getClass().getDeclaredMethod(methodName, field.getType());
					method.invoke(entity, obj);
				}
			}
			return entity;
		}
		return null;
	}

	/**
	 * @author DTZ
	 * @param key
	 *            属性名
	 * @param MethodType
	 *            获取方法类型（set or get）
	 * @return 方法名称，反射使用
	 */
	public static String getMethodName(String key, String MethodType) {
		String methodName = "";
		if (key != null && !"".equals(key)) {
			String[] arr = key.split("");
			for (int i = 0; i < arr.length; i++) {
				String temp = arr[i];
				if (i == 1) {
					methodName += temp.toUpperCase();
				} else {
					methodName += temp;
				}
			}
		}
		return MethodType + methodName;
	}

	private class MethodHelper {

		public static final String SET_METHOD = "set";

		public static final String GET_METHOD = "get";

	}
}
