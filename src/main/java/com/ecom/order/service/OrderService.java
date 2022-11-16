package com.ecom.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecom.order.common.Payment;
import com.ecom.order.common.TransactionRequest;
import com.ecom.order.common.TransactionResponse;
import com.ecom.order.entity.Order;
import com.ecom.order.repository.OrderRepository;

@Service
@RefreshScope
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	@Lazy
	private RestTemplate template;
	
	@Value("${services.payment.endpoints.endpoint.uri}")
	private String endpointURL;

	public TransactionResponse saveOrder(TransactionRequest request) {
		Order orderRequest = request.getOrder();
		Order orderResponse= orderRepository.save(orderRequest);
		Payment paymentRequest = new Payment();
		paymentRequest.setOrderId(orderResponse.getId());
		paymentRequest.setAmount(orderResponse.getAmount());
		Payment paymentResponse = template.postForEntity(endpointURL, paymentRequest, Payment.class)
				.getBody();
		String msg = "success".equals(paymentResponse.getPaymentStatus())
				? "Order and the payment has been made succesfully"
				: "Order is in cart as the payment failed";
		return new TransactionResponse(orderResponse, paymentResponse, msg);
	}

}
