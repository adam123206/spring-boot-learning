package myfirstmessagedemo.demo.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

//@Component
public class Consumer {

	@JmsListener(destination = "firstQueue",containerFactory = "myFactory")
	public void receiveMessage(String text){

		System.err.println("received firstQueue "+text);
	}

	@JmsListener(destination = "secondQueue")
	public void receiveSecondQueue(String message){

		System.err.println("received second queue "+ message);
	}
}
