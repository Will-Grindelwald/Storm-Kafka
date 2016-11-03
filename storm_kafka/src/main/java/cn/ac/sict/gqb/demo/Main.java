package cn.ac.sict.gqb.demo;



/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args ) 
    {
    	if(args.length<1){
    		System.out.println("ERROR:missing argument");
    	}
    	else {
			if (args[0].equals("p")){
				//kafka生产数据,约10s 发送1百万数据
				 ProducerData producerData=new ProducerData();
				 producerData.send();
			}
			else if(args[0].equals("s")){
				//storm
				StormTopology storm=new StormTopology();
				storm.kafkaConsumer(args[1]);
			}
		}  	   	
    }   	
}
