package myfirstmessagedemo.demo.kafka;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class MyMessage implements Message {

	private Object data;

	private MessageHeaders headers;

	private MyMessage(Object data,Map<String,Object> map){

		this.data = data;
		this.headers = new MessageHeaders(map);
	}

	public static MyMessage create(Object data,Map<String,Object> map){

		return new MyMessage(data,map);
	}

	@Override
	public Object getPayload() {
		return this.data;
	}

	@Override
	public MessageHeaders getHeaders() {
		return this.headers;
	}
}
