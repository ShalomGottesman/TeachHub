package sandbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;

public class Sandbox2 {
	static String[] collaboratorMask = {"ShalomGottesman", "drleff", "profdiament", "patriciamg90", "rosenfa", "vekelly09"};
	static File repoNameFile = new File("C:\\Users\\Administrator\\MYGIT\\TA_Work\\COM1320-S2020\\AllReposToPull.txt");
	public static void main(String[] args) throws IOException {
		Set<String> mask = getMask();
	
		Github github = new RtGithub("ShalomGottesman", "");
		String[] repos = getAllRepos();
		for (String str : repos) {
			Set<String> collaborators = getAllColabs(github, str);
			Set<String> reducedCollaborators = mask(collaborators, mask);
			//System.out.print(str +": ");
			for (String col : reducedCollaborators) {
				System.out.print(col +" ");
			}
			System.out.println();			
		}
	}
	
	private static Set<String> mask(Set<String> allCollabs, Set<String> mask) {
		Set<String> reduced = new HashSet<String>();
		for (String collab : allCollabs) {
			if(!mask.contains(collab)){
				reduced.add(collab);
			}
		}
		return reduced;
	}
	
	private static Set<String> getMask(){
		Set<String> mask = new HashSet<String>();
		for (int x = 0; x < collaboratorMask.length; x++) {
			mask.add(collaboratorMask[x]);
		}
		return mask;
	}
	
	private static Set<String> getAllColabs(Github github, String repoName) throws IOException{
		try {
			Iterator<JsonValue> iter = github.entry().uri().path("/repos/Yeshiva-University-CS/" + repoName + "/collaborators")
					.back().method(Request.GET)
					.body().set(Json.createArrayBuilder().build()).back()
					.fetch().as(RestResponse.class)
			        .assertStatus(HttpURLConnection.HTTP_OK)
			        .as(JsonResponse.class)
			        .json().readArray().iterator();
			Set<String> collaboratorSet = new HashSet<String>();
			while (iter.hasNext()) {
				JsonObject val = (JsonObject) iter.next();
				collaboratorSet.add(val.getString("login"));
			}
			return collaboratorSet;
		} catch (AssertionError e) {
			return new HashSet<String>();
		}
	}
	
	private static String[] getAllRepos() {
		Scanner sc = null;
		try {
			sc = new Scanner(repoNameFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ArrayList<String> list = new ArrayList<String>();
		while (sc.hasNext()) {
			list.add(sc.nextLine());
		}
		String[] repos = list.toArray(new String[0]);
		return repos;
	}

}
