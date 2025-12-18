package com.self.transfers.service;

import com.self.core.payments.ws.core.events.DepositRequestedEvent;
import com.self.core.payments.ws.core.events.WithdrawalRequestedEvent;
import com.self.transfers.entity.TransferEntity;
import com.self.transfers.repository.TransferRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.net.ConnectException;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    @Value("${withdraw-money-topic}")
    private String withdrawMoneyTopic;

    @Value("${deposit-money-topic}")
    private String depositMoneyTopic;

	private KafkaTemplate<String, Object> kafkaTemplate;
	private RestTemplate restTemplate;
    private TransferRepository transferRepository;

    @Autowired
    public TransferServiceImpl(KafkaTemplate<String, Object> kafkaTemplate,
                               RestTemplate restTemplate,
                               TransferRepository transferRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
        this.transferRepository = transferRepository;
    }

    /**
     * And the reason you can use this annotation for Kafka transaction is because there is a very good integration between Spring Framework and Apache Kafka.
     * To work with Kafka transactions, spring framework will use object that is called Kafka Transaction Manager.
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
     *
     * By default, transactional annotation does not roll back transaction. By default, it rolls back transaction for unchecked (runtime) exceptions and for errors.
     * But it does not roll back transaction on checked exceptions and checked exceptions are the ones that you need to declare and handle in your method,
     * but it is actually configurable.
     * You can configure transactional annotation to roll back transaction for specific exception using rollback for attribute.
     */
    @Transactional(value = "kafkaTransactionManager", rollbackFor = {TransferServiceException.class, ConnectException.class})
	@Override
	public boolean transfer(TransferRestModel transferRestModel) {
		WithdrawalRequestedEvent withdrawalEvent = new WithdrawalRequestedEvent(transferRestModel.getSenderId(),
				transferRestModel.getRecepientId(), transferRestModel.getAmount());
		DepositRequestedEvent depositEvent = new DepositRequestedEvent(transferRestModel.getSenderId(),
				transferRestModel.getRecepientId(), transferRestModel.getAmount());

		try {
			kafkaTemplate.send(withdrawMoneyTopic, withdrawalEvent);
			log.info("Sent event to withdrawal topic.");

			// Business logic that causes and error
			callRemoteServce();

			kafkaTemplate.send(depositMoneyTopic, depositEvent);
			log.info("Sent event to deposit topic");

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new TransferServiceException(ex);
		}

		return true;
	}

    @Transactional(value = "transactionManager")
    @Override
    public boolean transferWithDatabaseOperation(TransferRestModel transferRestModel) {
        WithdrawalRequestedEvent withdrawalEvent = new WithdrawalRequestedEvent(transferRestModel.getSenderId(),
                transferRestModel.getRecepientId(), transferRestModel.getAmount());
        DepositRequestedEvent depositEvent = new DepositRequestedEvent(transferRestModel.getSenderId(),
                transferRestModel.getRecepientId(), transferRestModel.getAmount());

        try {
            //save to db
            TransferEntity transferEntity = new TransferEntity();
            transferEntity.setAmount(transferRestModel.getAmount());
            transferEntity.setSenderId(transferRestModel.getSenderId());
            transferEntity.setRecepientId(transferRestModel.getRecepientId());
            transferRepository.save(transferEntity);

            //send to kafka
            kafkaTemplate.send(withdrawMoneyTopic, withdrawalEvent);
            log.info("Sent event to withdrawal topic.");

            // Business logic that causes and error
            callRemoteServce();

            kafkaTemplate.send(depositMoneyTopic, depositEvent);
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
