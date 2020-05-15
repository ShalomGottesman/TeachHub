package pat_manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;

import authentication.PAT_Token;
import utilities.EnviormentVariable;


public class PAT_Serializer {
	private File patVault;
	
	public PAT_Serializer() {
		patVault = new File(new EnviormentVariable().getVariable() + File.separator + "vault");
	}
	
	public String serialize(PAT_Token token) throws IOException {
		GsonBuilder gbuild = new GsonBuilder();
		String json = gbuild.create().toJson(token);
		String jsonFileName = token.getJsonName(); 
		File jsonFile = new File(patVault.toString() + File.separator + jsonFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile, false));
		bw.write(json);
		bw.close();
		return jsonFileName;
	}
	public PAT_Token deserialize(String fileName) throws IOException {
		File jsonFile = new File(patVault.toString() + File.separator + fileName);
		if(jsonFile.exists()) {
			String json = readFile(jsonFile);
			if(json == null) {
				return null;
			}
			GsonBuilder gbuild = new GsonBuilder();
			return gbuild.create().fromJson(json, PAT_Token.class);
		}
		return null;
	}
	
	private String readFile(File file) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file)); 
	    String line;
	    while ((line = br.readLine()) != null) {
	    	builder.append(line);
	    }
	    br.close();
	    return builder.toString();	    
	}

}
