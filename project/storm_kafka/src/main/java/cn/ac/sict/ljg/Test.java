package cn.ac.sict.ljg;

import java.util.Arrays;

public class Test {
public static void main(String[] args) {
	String str = "sss|wwwww|ffff";
	String[] split = str.split("\\|");
	for(String s:split ){
		System.out.println(s);
	}
}
}
