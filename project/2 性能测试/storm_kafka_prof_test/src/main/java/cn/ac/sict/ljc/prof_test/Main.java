package cn.ac.sict.ljc.prof_test;

import java.io.IOException;
import java.util.Properties;

import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;

/**
 * 测试 storm-kafka 的接收速率
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

		// 配置 cn.ac.sict.ljc.demo.WordCountTopology
		ProfTestTopology wordCountTopology = new ProfTestTopology(configProps);
		if (args == null || args.length == 0)
			wordCountTopology.submit(null);
		else
			wordCountTopology.submit(args[0]);
	}
}
