package myfirstmessagedemo.demo.rabbitmq.producer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Producer4Amqp{

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	private RabbitTemplate amqpTemplate;

	private static AtomicInteger atomicInteger = new AtomicInteger(0);

	@Scheduled(fixedDelay = 3000L)
	public void send() {

		System.err.println("sending message"+atomicInteger.get());

		if(atomicInteger.get()%3 ==0){

			this.amqpTemplate.send("dex1","key1",MessageBuilder.withBody((atomicInteger.incrementAndGet()+" message").getBytes()).
					setContentEncoding(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).setMessageId(atomicInteger.get()+"").build());
		}else if(atomicInteger.get()%3 ==1){

			this.amqpTemplate.send("tex1","key2",MessageBuilder.withBody((atomicInteger.incrementAndGet()+" message").getBytes()).
					setContentEncoding(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).setMessageId(atomicInteger.get()+"").build());
		}else if(atomicInteger.get()%3 ==2){

			this.amqpTemplate.send("dex2","key3",MessageBuilder.withBody((atomicInteger.incrementAndGet()+" message").getBytes()).
					setContentEncoding(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).setMessageId(atomicInteger.get()+"").build());
		}


	}
}
