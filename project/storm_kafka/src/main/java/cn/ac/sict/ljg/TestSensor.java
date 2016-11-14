package cn.ac.sict.ljg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TestSensor {
	public static void main(String[] args) { 
		Random random =new Random();
		   
		   while(true){
		   long  start = System.currentTimeMillis();
	       for(int i=0;i<100000;i++){
	       SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss.SSS");
	       String TimeStamp = simpleDateFormat.format(new Date());
	       double d= random.nextDouble()*2; 
	   	   BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
	       double concentration= bg.doubleValue();
	       String data = TimeStamp+"|"+concentration;
  	      System.out.println(data);
	       }
	       long  end = System.currentTimeMillis();
	       System.out.println((end-start)*0.001);
	       try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   }
	    }
	}

