package myfirstmessagedemo.demo.rabbitmq;


import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRabbit
@ComponentScan(basePackages = {"*.demo.rabbitmq.*"})
public class DemoForRabbitmq {

	@Bean
	public ConnectionFactory connectionFactory(){

		CachingConnectionFactory factory =  new CachingConnectionFactory("192.129.27.84",5672);
		factory.setUsername("rabbitmq");
		factory.setPassword("12345678");
		factory.setChannelCacheSize(50);
		return factory;
	}

	/**
	 * 维护declarables，包括queue，exchange，binding对象
	 * @return
	 */
	@Bean
	public AmqpAdmin amqpAdmin(){

		RabbitAdmin amqpAdmin =  new RabbitAdmin(connectionFactory());
		return amqpAdmin;
	}

	@Bean
	public DirectExchange dex1(){

		return new DirectExchange("dex1");
	}

	@Bean
	public TopicExchange tex1(){

		return new TopicExchange("tex1");
	}

	@Bean
	public Queue firstQueue(){

		return new Queue("firstQueue");
	}

	@Bean
	public Queue secondQueue(){

		return new Queue("secondQueue");
	}

	@Bean
	public Binding b1(){

		return BindingBuilder.bind(firstQueue()).to(dex1()).with("key1");
	}
	@Bean
	public Binding b2(){

		return BindingBuilder.bind(secondQueue()).to(tex1()).with("key2");
	}

	@Bean
	public Declarables bs(){

		return new Declarables(new DirectExchange("dex2"),
				new Queue("thirdQueue"),
				new Binding("thirdQueue",DestinationType.QUEUE,"dex2","key3",null));
	}

	@Bean
	public RabbitTemplate amqpTemplate(){

		RabbitTemplate amqpTemplate = new RabbitTemplate(connectionFactory());

		RetryTemplate retryTemplate = new RetryTemplate();
		ExponentialRandomBackOffPolicy backOffPolicy = new ExponentialRandomBackOffPolicy();
		backOffPolicy.setInitialInterval(500);
		backOffPolicy.setMultiplier(10.0);
		backOffPolicy.setMaxInterval(10000);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		amqpTemplate.setMessageConverter(messageConverter());
		amqpTemplate.setRetryTemplate(retryTemplate);
		return amqpTemplate;
	}

	@Bean
	public SimpleMessageListenerContainer container(){

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		//reply 设置
		//	container.setMessageListener(amqpTemplate());
		container.setQueueNames("thirdQueue");
		container.setMessageListener(message -> System.out.println("received message from thirdQueue" +message));
		return container;
	}


	@Bean
	public MessageConverter messageConverter(){

		return new SimpleMessageConverter();
	}

	public static void main(String[] args){

		SpringApplication.run(DemoForRabbitmq.class,args);
	}
}
