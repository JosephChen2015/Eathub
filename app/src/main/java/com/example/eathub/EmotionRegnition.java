package com.example.eathub;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLException;


public class EmotionRegnition {
	public static void main(String[] args)
	{
//		String fileName = "C:\\Users\\hostage\\Desktop\\intership\\1.jpg";
//		File file = new File(fileName);
//		byte[] buff = getBytesFromFile(file);
////		System.out.println(buff.toString());
//		EmotionRegnition e = new EmotionRegnition();
//		try {
//			System.out.println(getEmotion(buff));
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	}
	
	public static float getEmotion(byte[] image) throws Exception{
		HashMap<String, Double> example = new HashMap<String, Double>();
		example = get_emotion(image);
//		System.out.println("HashMap: "+example.toString());
		Double max = -1.0;
		String max_key = null; 
		
		for(Map.Entry entry : example.entrySet())
		{
		    if((Double)entry.getValue() > max)
		    {
		    	max = (Double) entry.getValue();
		    	max_key = (String) entry.getKey();
		    }
		 }
//		System.out.println("key: "+max_key);
		max_key = max_key.replace("\"", "");
		if(max_key.equals("face_num")&& max==0.0)
		{
			return 2;
		}
		switch (max_key)
		{
			case "sadness":
				return randFloat(0.0f, 0.1f);
			case "fear":
				return randFloat(0.1f, 0.2f);
			case "anger":
				return randFloat(0.2f, 0.3f);
			case "disgust":
				return randFloat(0.3f, 0.4f);
			case "neutral":
				return randFloat(0.4f, 0.6f);
			case "surprise":
				return randFloat(0.6f, 0.8f);
			case "happiness":
				return randFloat(0.8f, 1.0f);
			default:
				return randFloat(0.0f, 1.0f);
		}

	}
	
	public static float randFloat(Float d, Float e) {

	    Random rand = new Random();

	    return rand.nextFloat() * (e - d) + d;

	}
	
	public static HashMap<String, Double> getEmotions(String fullText)
	{
		String[] x = fullText.split(",");
		ArrayList<String> result = new ArrayList<String>();
		for (String i : x)
		{
			if (i.contains("\"face_num\""))
			{
				String str = i.substring(i.indexOf("\"face_num\""), i.length()-1);
				result.add(str);
			}
			if(i.contains("\"sadness\""))
			{
				String str = i.substring(i.indexOf("\"sadness\""), i.length());
				result.add(str);
			}
			
			if(i.contains("\"neutral\""))
			{
				String str = i.substring(i.indexOf("\"neutral\""), i.length());
				result.add(str);
			}
			if(i.contains("\"disgust\""))
			{
				String str = i.substring(i.indexOf("\"disgust\""), i.length());
				result.add(str);
			}
			if(i.contains("\"anger\""))
			{
				String str = i.substring(i.indexOf("\"anger\""), i.length());
				result.add(str);
			}
			if(i.contains("\"surprise\""))
			{
				String str = i.substring(i.indexOf("\"surprise\""), i.length());
				result.add(str);
			}
			if(i.contains("\"fear\""))
			{
				String str = i.substring(i.indexOf("\"fear\""), i.length());
				result.add(str);
			}
			if(i.contains("\"happiness\""))
			{
				String str = i.substring(i.indexOf("\"happiness\""), i.length()-2);
				result.add(str);
			}
		}
		
		HashMap<String, Double> real_result = new HashMap<String, Double>();
		for (String i: result)
		{
			String[] temp = i.split(":");
			real_result.put(temp[0], Double.parseDouble(temp[1]));
		}
		
		return real_result;
		
	}
	
	public static HashMap<String, Double> get_emotion (byte[] image) throws Exception
	{
//		File file = new File(fileName);
//		byte[] buff = getBytesFromFile(file);
		byte[] buff = image;
		HashMap<String, Double> result= new HashMap<String, Double>();
		String url = "https://api-us.faceplusplus.com/facepp/v3/detect";
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", "LS0Bh_atad6xwPGuv580kPTozshQerqi");
        map.put("api_secret", "6OhfMhFDozRoWMbciRbgmBH8KHSzagIs");
        map.put("return_attributes", "emotion");
        byteMap.put("image_file", buff);
        try{
            byte[] bacd = post(url, map, byteMap);
//            System.out.println(bacd.toString());
            String str = new String(bacd);
            result = getEmotions(str);
    		return result;
        }catch (Exception e) {
        	e.printStackTrace();
		}
		return result;
	}
	
	private final static int CONNECT_TIME_OUT = 30000;
    private final static int READ_OUT_TIME = 50000;
    private static String boundaryString = getBoundary();
    protected static byte[] post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
        HttpURLConnection conne;
        URL url1 = new URL(url);
        conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
        Iterator iter = map.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry) iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key
                    + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }
        if(fileMap != null && fileMap.size() > 0){
            Iterator fileIter = fileMap.entrySet().iterator();
            while(fileIter.hasNext()){
                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();
                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey()
                        + "\"; filename=\"" + encode(" ") + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.write(fileEntry.getValue());
                obos.writeBytes("\r\n");
            }
        }
        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();
        InputStream ins = null;
        int code = conne.getResponseCode();
        try{
            if(code == 200){
                ins = conne.getInputStream();
            }else{
                ins = conne.getErrorStream();
            }
        }catch (SSLException e){
            e.printStackTrace();
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int len;
        while((len = ins.read(buff)) != -1){
            baos.write(buff, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        ins.close();
        return bytes;
    }
    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }
    private static String encode(String value) throws Exception{
        return URLEncoder.encode(value, "UTF-8");
    }
    
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

}
