package cn.ac.sict.ljc.sensorSimu_terminal;

import java.io.IOException;
import java.util.Properties;

import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;

/**
 * 
 */
public class Main {

	public static void main(String[] args)
			throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {

		Properties configProps = new Properties();
		try {
			configProps.load(Main.class.getClassLoader().getResourceAsStream("sysConfig.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR 1: no Config file.");
			return;
		}

		// 配置 cn.ac.sict.ljc.sensor.temper.SensorTemperTopology
		SensorTopology sensorTopology = new SensorTopology(configProps);
		if (args == null || args.length == 0)
			sensorTopology.submit(null); // 无参则本地模式
		else
			sensorTopology.submit(args[0]); // 有参则集群模式

	}
}
