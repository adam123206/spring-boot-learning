package myfirstmessagedemo.demo.rabbitmq.consumer;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class Consumer4Amqp {

	@Autowired
	private RabbitTemplate amqpTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;

 	@RabbitListener(queues = {"firstQueue"})
	public void received(byte[] message){

 		System.out.println("consumer 1 received message from firstQueue" + new String(message));
	}

	@RabbitListener(queues = "secondQueue")
	public void recevied2(Message message){

		System.out.println("consumer 2 received message from secondQueue" + message);
	}
}
