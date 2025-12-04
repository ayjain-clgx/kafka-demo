package com.self.transfers.service;

import com.self.core.payments.ws.core.events.DepositRequestedEvent;
import com.self.core.payments.ws.core.events.WithdrawalRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.self.transfers.error.TransferServiceException;
import com.self.transfers.model.TransferRestModel;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

	private KafkaTemplate<String, Object> kafkaTemplate;
	private Environment environment;
	private RestTemplate restTemplate;

	public TransferServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, Environment environment,
			RestTemplate restTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		this.environment = environment;
		this.restTemplate = restTemplate;
	}

    /**
     * And the reason you can use this annotation for Kafka transaction is because there is a very good integration between Spring Framework and Apache Kafka.
     * To work with Kafka transactions, spring framework will use object that is called Kafka Transaction Manager,
     * and we created an instance of this object in previous video lesson.
     * If you have only one transaction manager object, Spring Framework will find it and will use it to manage Kafka transactions.
     * But if you have multiple different transaction managers in your application, then you can tell this annotation which specific transaction manager to use for this method.
     * And to do that you will add value property and as a value you will provide the name of Transaction Manager.
     * <p>
     * So when you annotate this method with transactional annotation, then when the method is called Spring Framework will start a new transaction.
     * It will then execute all operations in this method as a single unit of work. And if everything goes smoothly and if there are no errors,
     * then spring will commit transaction and consumer microservices will receive Kafka messages.
     * But if there is an error, like for example, a call to a remote service can fail, then Spring framework will roll back transaction Kafka messages that were sent.
     * They will not be committed. And our consumer microservices, they will not receive these messages.
     * Provided that we configured those consumer microservices to read only committed messages.
     */
    @Transactional(value = "kafkaTransactionManager")
	@Override
	public boolean transfer(TransferRestModel transferRestModel) {
		WithdrawalRequestedEvent withdrawalEvent = new WithdrawalRequestedEvent(transferRestModel.getSenderId(),
				transferRestModel.getRecepientId(), transferRestModel.getAmount());
		DepositRequestedEvent depositEvent = new DepositRequestedEvent(transferRestModel.getSenderId(),
				transferRestModel.getRecepientId(), transferRestModel.getAmount());

		try {
			kafkaTemplate.send(environment.getProperty("withdraw-money-topic", "withdraw-money-topic"),
					withdrawalEvent);
			log.info("Sent event to withdrawal topic.");

			// Business logic that causes and error
			callRemoteServce();

			kafkaTemplate.send(environment.getProperty("deposit-money-topic", "deposit-money-topic"), depositEvent);
			log.info("Sent event to deposit topic");

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new TransferServiceException(ex);
		}

		return true;
	}

	private ResponseEntity<String> callRemoteServce() throws Exception {
		String requestUrl = "http://localhost:8082/response/200";
		ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);

		if (response.getStatusCode().value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
			throw new Exception("Destination Microservice not availble");
		}

		if (response.getStatusCode().value() == HttpStatus.OK.value()) {
			log.info("Received response from mock service: " + response.getBody());
		}
		return response;
	}

}
