package myfirstmessagedemo.demo.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

@SpringBootApplication
@EnableKafka
@EnableScheduling
@ComponentScan(value = {"*.demo.kafka.*"})
public class KafkaDemoApplication {


	@Bean
	public NewTopic topicOne(){

		return new NewTopic("topicOne",10,(short)2);
	}

	@Bean
	public NewTopic topicTwo(){

		return new NewTopic("topicTwo",10,(short)2);
	}

	/**
	 * 自动绑定到broker，创建topic。
	 * @return
	 */
	@Bean
	public KafkaAdmin admin(){

		Map<String ,Object> config = new HashMap<>();
		config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
				StringUtils.arrayToCommaDelimitedString(new String[]{"192.129.27.84:9092","192.129.27.84:9093"}));
		return new KafkaAdmin(config);
	}

	@Bean
	public Map<String ,Object> producerConfig(){

		Map<String ,Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.129.27.84:9092，192.129.27.84:9094");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.CLIENT_ID_CONFIG,"zhongzhong");
		return config;
	}

	@Bean
	public ProducerFactory<Integer,String> factory(){

		return new DefaultKafkaProducerFactory<>(producerConfig());
	}

	@Bean
	public KafkaTemplate<Integer,String> template(){

		return new KafkaTemplate(factory());
	}

	/**
	 * 两种类型的listenerContainner，KafkaMessageListenerContainer监听所有topic上的消息，
	 * 使用一个线程消费消息；ConcurrentMessageListenerContainer 代理多个KafkaMessageListenerContainer实例在多个线程上
	 * @return
	 */
	/*@Bean
	public KafkaMessageListenerContainer messageListenerContainer(){

		ContainerProperties properties = new ContainerProperties("topicOne");
		properties.setMessageListener(new MessageListener<Integer,String>() {


			@Override
			public void onMessage(ConsumerRecord<Integer, String> integerStringConsumerRecord) {

					System.err.println("get message from topicOne : "+integerStringConsumerRecord.value());
			}
		});
		return new KafkaMessageListenerContainer(consumerFactory(),properties);
	}*/

	@Bean
	public ConsumerFactory<Integer,String> consumerFactory(){

		Map<String,Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.129.27.84:9092，192.129.27.84:9094");
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,IntegerDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
		config.put(ConsumerConfig.CLIENT_ID_CONFIG,"zhongzhong");
		config.put(ConsumerConfig.GROUP_ID_CONFIG,"zhongzhong");
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
		DefaultKafkaConsumerFactory<Integer,String> factory = new DefaultKafkaConsumerFactory<>(config);
		return factory;
	}

	@Bean
	public ConcurrentMessageListenerContainer concurrentMessageListenerContainer(){

		ContainerProperties properties = new ContainerProperties("topicTwo");
		properties.setMessageListener(new MessageListener<Integer,String>() {


			@Override
			public void onMessage(ConsumerRecord<Integer, String> integerStringConsumerRecord) {

				System.err.println("get message from topicTwo : "+integerStringConsumerRecord.value());
			}
		});
		ConcurrentMessageListenerContainer container = new ConcurrentMessageListenerContainer(consumerFactory(),properties);
		container.setConcurrency(2);
		return container;
	}

	@Bean
	KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
	kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(3);
		factory.getContainerProperties().setPollTimeout(3000);
		return factory;
	}

	public static void main(String[] args){

		SpringApplication.run(KafkaDemoApplication.class,args);
	}
}
