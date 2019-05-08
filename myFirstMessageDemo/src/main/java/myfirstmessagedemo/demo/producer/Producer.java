package myfirstmessagedemo.demo.producer;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

//@Component
public class Producer implements CommandLineRunner {

	@Autowired
	private Queue firstQueue;

	@Autowired
	private Queue secondQueue;

	@Autowired
	private JmsTemplate jmsTemplate;


	@Override
	public void run(String... args) throws Exception {

		for (int i = 0; i < 20; i++) {

			if(i%2 == 0){

				this.jmsTemplate.convertAndSend(this.firstQueue,"activemq firstQueue"+i);
			}else{

				this.jmsTemplate.convertAndSend(this.secondQueue,"activemq secondQueue"+i);
			}

			Thread.sleep(2000);
		}

	}
}
