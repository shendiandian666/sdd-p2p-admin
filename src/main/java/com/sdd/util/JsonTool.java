package com.sdd.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTool {

	private static ObjectMapper mapper;
	
	public static synchronized ObjectMapper instance(boolean createNew){
		if(createNew){
			return new ObjectMapper();
		}else if(mapper == null){
			mapper = new ObjectMapper();
		}
		return mapper;
	}
	
	public static String beanToJson(Object obj) throws Exception {
		try {
			ObjectMapper objectMapper = instance(false);
			String json = objectMapper.writeValueAsString(obj);
			return json;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static Map<String, Object> jsonToMap(String str) throws Exception {
		ObjectMapper objectMapper = instance(false);
		str = str.replaceAll("'", "\"");
		Map<String, Object> map = objectMapper.readValue(str, Map.class);
		return map;
	}
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = instance(false);
		Map map = objectMapper.readValue("{\"1\":\"支付宝\",\"2\":\"微信\",\"3\":\"QQ钱包\",\"4\":\"京东钱包\",\"5\":\"applePa\"}", Map.class);
	     System.out.println(map);
	}
	
}
