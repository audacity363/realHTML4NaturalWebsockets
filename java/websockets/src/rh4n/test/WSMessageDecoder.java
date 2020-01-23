package rh4n.test;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import realHTML.JSONConverter.JSONConverter;

public class WSMessageDecoder implements Decoder.Text<Object> {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void init(EndpointConfig arg0) {
		System.out.println("Decoder registered");
	}

	@Override
	public Object decode(String arg0) throws DecodeException {
		return new JSONConverter(arg0);
	}

	@Override
	public boolean willDecode(String arg0) {
		try {
			new JSONConverter(arg0);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

}
