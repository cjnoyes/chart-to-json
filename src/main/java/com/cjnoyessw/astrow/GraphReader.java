package com.cjnoyessw.astrow;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GraphReader {

	static final int [] header = {0x41,0xfa,0x0d,0x53,0xfb,3,0x54,0xfd,5};
	
	static final String[] types= { "", "Natal", "Compatability", "Transits", "Progressed",
		    "Numeric", "Sun Return", "Moon Return", "Relocation", "Composite",
		    "Fixed Star", "Arabic Parts", "Heliocentric", "Problem", "Composite Transit",
		    "Solar Arc", "Harmonic", "", "", "", "", "","", "", "","",
			"Natal", "Compatability", "Transits", "Progressed",
		    "Natal", "", "", "Relocation", "Composite", "", "", "", "",
			"Composite Transits", "Solar Arc", "Harmonic", "", "", "", "", "",
		    "", "", "", ""};

   static final String [] long_types = { "", "Birth Output", "Compatability Output",
		    "Transits Output", "Progressed Birth Output",
		    "Numeric Birth Output", "Solar Return", "Moon Return",
		    "Relocation",  "Composite Output", "Fixed Star Output",
		    "Arabic Parts Output", "Heliocentric Birth Output",
		    "Astrological Problem Output", "Composite Transits", "Solar Arc",
		    "Harmonic", "", "", "", "","", "", "", "", "", 	
			"Birth Graphics", "Compatability Graphics",
		    "Transits Graphics", "Progressed Birth Graphics",
		    "Natal Graphics", "", "", "Reloction Graphics",
		    "Composite Graphics File", "", "", "", "",
		    "Composite Transits Graphics", "Solar Arc Graphics", "Harmonic Graphics", "", "", "", "", "", "", "",
		    "", ""
		};
	
    
	
	public GraphReader() {
		
	}
	
	public GraphReader(String file) {
		this.file = file;
	}
	
	protected boolean checkHeader(byte [] ary) {
		//for (int i = 0; i < header.length; i++ ) {
		//	if (ary[i]!=((byte)header[i])) {
		//		return false;
		//	}
		//}
		return true;
	}
	
	private String readString(int len) throws Exception {
		byte [] buff = new byte[300];
		int l = stream.read(buff, 0, len);
		for (int i = 0; i < len; i++) {
			if (buff[i]==0) {
				l = i;
				break;
			}
		}
		return new String(buff,0,l,"ISO-8859-1");
	}
	
	protected void open() throws Exception {
		FileInputStream in = new FileInputStream(file);
		base = new DataInputStream(in);
		stream = new EndianInputStream(base);
	}
	
	protected void close() throws Exception {
		stream.close();
	}
	
	protected Map<String,Object> readFileHeader() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		byte [] buff = new byte[200];
		int len = stream.read(buff, 0, 10);
		if (len != 10 ||!checkHeader(buff)) {
			throw new Exception("Unsupported file type");
		}
		map.put("name", readString(51));
		map.put("comment", readString(51));
		map.put("filename", readString(256));
		int type = stream.readLittleShort();
		map.put("type", type);
		stream.read(buff,0,8);
		map.put("strType", types[type]);
		map.put("strLongType", long_types[type]);
		this.type = type;
		return map;
	}
	
	protected Map<String,Object> readHeader() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		int version = stream.readLittleUnsignedShort();
		map.put("version",version);
		int ftype = stream.readLittleShort();
		map.put("type",ftype);
		map.put("strType", types[ftype]);
		map.put("strLongType", long_types[ftype]);
		return map;
	}
	
	protected List<Map<String,Integer>> readAspects(int count) throws Exception {
		List<Map<String,Integer>> list = new ArrayList<Map<String,Integer>>();
		for (int i =0; i < count; i++) {
			Map<String,Integer> map = new HashMap<String,Integer>();
			int first = stream.readLittleUnsignedShort();
			int second = stream.readLittleUnsignedShort();
			short aspect = stream.readLittleShort();
			map.put("first", first);
			map.put("second", second);
			map.put("aspect", (int)aspect);
			list.add(map);
		}
		return list;
	}
	
	protected List<Map<String,Integer>> readDataPoints(int count) throws Exception {
		List<Map<String,Integer>> list = new ArrayList<Map<String,Integer>>();
		for (int i =0; i < count; i++) {
			Map<String,Integer> map = new HashMap<String,Integer>();
			int min = stream.readLittleShort();
			int house = stream.readLittleShort();
			int decan = stream.readLittleShort();
			map.put("min", min);
			map.put("house", house);
			map.put("decan", decan);
			list.add(map);
		}
		return list;
	}
	
	protected Map<String,Object> readData() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
	
		HashMap<String,Object> natal = new HashMap<String,Object>();
		HashMap<String,Object> partner = new HashMap<String,Object>();
		List<HashMap<String, Object>> transits = new ArrayList<HashMap<String,Object>>();
		map.put("strType", types[type]);
		map.put("strLongType", long_types[type]);
		map.put("natal", natal);
		map.put("other", partner);
		short val = stream.readShort();
		short val2 = stream.readShort();
		
		String name1 = readString(51);
		String name2 = readString(51);
		String date1 = readString(11);
		String date2 = readString(11);
		String time1 = readString(9);
		String time2 = readString(9);
		String tropside = readString(15);
		String houseproc = readString(21);
		
		natal.put("name1", name1);
		natal.put("name2", name2);
		natal.put("date1", date1);
		natal.put("date2", date2);
		natal.put("time1", time1);
		natal.put("time2", time2);
		natal.put("tropside", tropside);
		natal.put("houseproc", houseproc);
		
		partner.put("name1", name1);
	    partner.put("name2", name2);
		partner.put("date1", date1);
		partner.put("date2", date2);
		partner.put("time1", time1);
		partner.put("time2", time2);
		partner.put("tropside", tropside);
		partner.put("houseproc", houseproc);
		
		natal.put("birthtimeknown", val);
		partner.put("birthtimeknown", val2);
		
		
		natal.put("housecusps", readArray(12));
		partner.put("housecusps", readArray(12));
	
		map.put("maxpt", stream.readLittleShort());
	    natal.put("minutes", readArray(30));
		partner.put("minutes", readArray(30));
		natal.put("numaspects", stream.readLittleShort());
		short numcharts = stream.readLittleShort();
		map.put("numcharts", numcharts);
		map.put("numdata", stream.readLittleShort());
		map.put("numtext", stream.readLittleShort());
		map.put("numother", stream.readLittleShort());
		map.put("aspectglyphs", stream.readLittleShort());
		map.put("layoutoptions", stream.readLittleUnsignedShort());
		natal.put("numgridaspects", stream.readLittleShort());
		partner.put("numgridaspects", stream.readLittleShort());
		natal.put("aspects", readAspects((Short)natal.get("numaspects")));
		
		for (int i = 0; i < numcharts; i++) {
			HashMap<String,Object> transit = new HashMap<String,Object>();
			transit.put("date", readString(12));
			transit.put("maxpt",stream.readLittleShort());
			transit.put("minutes", readArray(29));
			int numasp = stream.readLittleShort();
			transit.put("numaspect", numasp );
			transit.put("aspects",readAspects(numasp));
			transits.add(transit);
		}
		if (numcharts > 0) {
		   map.put("transits",transits);
		}
		map.put("datapoints", readDataPoints((Short)map.get("numdata")));
		natal.put("gridaspects", readAspects((Short)natal.get("numgridaspects")));
		partner.put("gridaspects", readAspects((Short)partner.get("numgridaspects")));
		return map;
	}
	
	protected int [] readArray(int len) throws Exception {
		int [] ary = new int[len];
		for (int i = 0; i < len; i++) {
			ary[i]=stream.readLittleShort();
		}
		return ary;
	}
	
	public Map<String,Object> read() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			open();
			map.put("fileheader", readFileHeader());
			map.put("chartheader", readHeader());
			map.put("data", readData());
		}
		finally {
			if (stream != null) {
				close();
			}
		}
		return map;
	}
	
	
	DataInputStream base;
	EndianInputStream stream;
	String file;
	int type;
}
