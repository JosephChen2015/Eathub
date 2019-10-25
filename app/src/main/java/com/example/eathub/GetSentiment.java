package com.example.eathub;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;

/*
 * Gson: https://github.com/google/gson
 * Maven info:
 *     groupId: com.google.code.gson
 *     artifactId: gson
 *     version: 2.8.1
 *
 * Once you have compiled or downloaded gson-2.8.1.jar, assuming you have placed it in the
 * same folder as this file (GetSentiment.java), you can compile and run this program at
 * the command line as follows.
 *
 * Execute the following two commands to build and run (change gson version if needed):
 * javac GetSentiment.java -classpath .;gson-2.8.1.jar -encoding UTF-8
 * java -cp .;gson-2.8.1.jar GetSentiment
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class Document {
    public String id, language, text;

    public Document(String id, String language, String text){
        this.id = id;
		this.language = language;
        this.text = text;
    }
}

class Documents {
    public List<Document> documents;

    public Documents() {
        this.documents = new ArrayList<Document>();
    }
    public void add(String id, String language, String text) {
        this.documents.add (new Document (id, language, text));
    }
}

public class GetSentiment {
    static String subscription_key = "6379c47f9e8742bfadacd7306884c61a";
    static String endpoint= "https://australiaeast.api.cognitive.microsoft.com/";
    


    static String path = "/text/analytics/v2.1/sentiment";
    
    public static float getTheSentiment (String voiceInput) {
    	
 
        Documents documents = new Documents ();
        documents.add ("1", "en", voiceInput);
   
        String text = new Gson().toJson(documents);
        byte[] encoded_text;
		try {
			encoded_text = text.getBytes("UTF-8");
		

        URL url = new URL(endpoint+path);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/json");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscription_key);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(encoded_text, 0, encoded_text.length);
        wr.flush();
        wr.close();

        StringBuilder response = new StringBuilder ();
        BufferedReader in = new BufferedReader(
        new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(line).getAsJsonObject();
//            System.out.println(json.toString());
            JsonArray document = json.getAsJsonArray("documents");
            return document.get(0).getAsJsonObject().get("score").getAsFloat();
            
        }
        in.close();
		} catch (Exception e) {
		    e.printStackTrace();
		    System.out.println("error");
		}
		return 1;
        
    }


}