package cn.ac.sict.ljg.redisTest;

import java.util.Random;

import org.json.simple.JSONObject;

public class JsonTest {
      public static void main(String[] args) {
    	    Random random = new Random();
    	    long timeStamp = System.currentTimeMillis( )/1000;
    	    for(int i= 0;i<1000;i++){
    	    JSONObject msg =new JSONObject();
		    msg.put("time", timeStamp++);
			msg.put("number",  random.nextInt(50));
			System.out.println(msg.toString());
    	    }
	}
}
