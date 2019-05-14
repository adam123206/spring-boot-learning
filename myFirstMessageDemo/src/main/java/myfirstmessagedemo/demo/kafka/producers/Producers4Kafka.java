package myfirstmessagedemo.demo.kafka.producers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import myfirstmessagedemo.demo.kafka.MyMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class Producers4Kafka {

	@Autowired
	private KafkaTemplate<Integer,String> template;

	private static AtomicInteger count = new AtomicInteger(0);

	@Scheduled(fixedRate = 3000L)
	public void sendMessage(){

		if(count.get()%2==0){

			Map<String ,Object> map = new HashMap<>();
			map.put(KafkaHeaders.TOPIC,"topicOne");
			map.put(KafkaHeaders.PARTITION_ID,1);
			map.put(KafkaHeaders.MESSAGE_KEY,1);
			ListenableFuture<SendResult<Integer, String>> future =  this.template.send(MyMessage.create("kafka.message"+count.get(),map));
			future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>(){

				@Override
				public void onSuccess(SendResult<Integer, String> integerStringSendResult) {

					System.out.println("sending message to kafka success"+count.get());
				}

				@Override
				public void onFailure(Throwable throwable) {

					System.out.println(throwable.getCause());
				}
			});
		}else if(count.get() %2==1){

			ProducerRecord<Integer ,String> record = new ProducerRecord<Integer, String>("topicTwo",1,
					2,"message.test."+count.get());

			ListenableFuture<SendResult<Integer, String>> future = this.template.send(record);
			future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>(){

				@Override
				public void onSuccess(SendResult<Integer, String> integerStringSendResult) {
					System.out.println("sending message to kafka topic two  success"+count.get());
				}

				@Override
				public void onFailure(Throwable throwable) {

					System.out.println(throwable.getCause());
				}
			});
		}
		count.incrementAndGet();
	}
}
