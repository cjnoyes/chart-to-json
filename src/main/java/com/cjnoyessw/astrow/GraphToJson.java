package com.cjnoyessw.astrow;

import java.util.Map;
import org.json.*;
import java.io.StringWriter;


public class GraphToJson {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
        GraphReader reader = new GraphReader(args[0]);
        Map<String,Object> map =reader.read();
        StringWriter writer = new StringWriter();
        JSONWriter jwriter = new JSONWriter(writer);
        jwriter.object();
        for (String key: map.keySet()) {
        	jwriter.key(key).value(map.get(key));
        }
        jwriter.endObject();
        System.out.println(writer.toString());
        
	}

}
