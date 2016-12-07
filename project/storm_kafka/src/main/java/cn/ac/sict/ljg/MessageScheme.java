package cn.ac.sict.ljg;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

 
import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
      /***
       * 
       *  将kafka消息转化为tuple。
       *
       */
public class MessageScheme implements Scheme {
	 
	private static final long serialVersionUID = 8530686402928320533L;
	private static final Logger logger = LoggerFactory.getLogger(MessageScheme.class);
	@Override
	public List<Object> deserialize(ByteBuffer  byteBuffer) {
		byte[] array = byteBuffer.array();
		String msg;
		try {
			msg = new String(array,"UTF-8");
			logger.info("get one message is {}",msg);
			return new Values(msg);
		} catch (UnsupportedEncodingException e) {
		 
			e.printStackTrace();
		}
		return null; 
		
	}

	@Override
	public Fields getOutputFields() {
		 
		return new Fields("msg");
	}

}
