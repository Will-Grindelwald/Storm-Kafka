package cn.ac.sict.gqb.demo;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class MessageScheme implements Scheme {

	/**
	 * 转换为字符串
	 */
	private static final long serialVersionUID = 4909505642226169653L;

	public List<Object> deserialize(ByteBuffer arg0) {
		Charset charset = null;
		CharsetDecoder decoder = null;
		CharBuffer charBuffer = null;
		try {
			charset = Charset.forName("UTF-8");
			decoder = charset.newDecoder();
			charBuffer = decoder.decode(arg0.asReadOnlyBuffer());
			return new Values(charBuffer.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Fields getOutputFields() {
		return new Fields("message");
	}

}
