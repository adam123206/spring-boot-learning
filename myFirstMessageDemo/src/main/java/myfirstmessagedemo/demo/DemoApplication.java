package myfirstmessagedemo.demo;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

//@SpringBootApplication
//@EnableJms
public class DemoApplication {

	@Bean
	public Queue firstQueue(){

		return new ActiveMQQueue("firstQueue");
	}

	@Bean
	public Queue secondQueue(){

		return new ActiveMQQueue("secondQueue");
	}

	@Bean
	public MessageConverter getMessageConverter(){

		return new SimpleMessageConverter();
	}

	@Bean
	public ConnectionFactory getConnectionFactory(){

		return new CachingConnectionFactory(getActiveMQConnectionFactory());
	}

	public ActiveMQConnectionFactory getActiveMQConnectionFactory(){

		return new ActiveMQConnectionFactory("admin","admin","tcp://192.129.27.84:61616");
	}

	@Bean
	public JmsTemplate jmsTemplate(){

		JmsTemplate jmsTemplate =  new JmsTemplate(getConnectionFactory());
		jmsTemplate.setMessageConverter(getMessageConverter());
		return jmsTemplate;
	}

	/***
	 * 用于JmsListener containerFactory 消费者
	 * @param configurer
	 * @return
	 */
	@Bean
	public DefaultJmsListenerContainerFactory myFactory(DefaultJmsListenerContainerFactoryConfigurer configurer){

		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory,getConnectionFactory());
		factory.setMessageConverter(getMessageConverter());

		return factory;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
