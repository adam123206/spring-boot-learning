package myfirstmessagedemo.demo.jms.consumer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

@Component
public class ConsumerByImpl implements MessageListener {

	@Override
	public void onMessage(Message message) {

		if(message instanceof TextMessage){

			System.out.println("received text massage");
		}
	}
}
