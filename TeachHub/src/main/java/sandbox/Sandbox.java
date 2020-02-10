package sandbox;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.RtGithub;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;

public class Sandbox {
	
	public static void main(String[] args) throws IOException {
		
		
		Github github = new RtGithub("TeachHubProf", "1qaz2wsx#EDC$RFV5tgb^YHN");
		Coordinates c1 = new Coordinates.Simple("inviteTesting", "testing2");
		sendReadOnlyInvite(github, c1, "ShalomGottesman");
		System.out.println("done");
		
		//Coordinates c2 = new Coordinates.Simple("TESTING1qaz","repoTes");
		//System.out.println(contains(github, c1));//should be true
		//System.out.println(contains(github, c2));//should be false
		
		/*
		Repos repos = github.repos();
		Iterator<Repo> repoIterate = repos.iterate("").iterator();
		while (repoIterate.hasNext()) {
			System.out.println(repoIterate.next().toString());
		}
		
		Repo repo = repos.get(new Coordinates.Simple("blah", "blah"));
		repo.collaborators().add("blah");
		//System.out.println(repo.collaborators().add("blah"));
		
		
		Iterator<JsonValue> iter = github.entry().uri().path("/repos/TESTING1qaz/inviteTest1/invitations")
		.back().method(Request.GET)
		.body().set(Json.createArrayBuilder().build()).back()
		.fetch().as(RestResponse.class)
        .assertStatus(HttpURLConnection.HTTP_OK)
        .as(JsonResponse.class)
        .json().readArray().iterator();
		
		while (iter.hasNext()) {
			JsonObject val = (JsonObject) iter.next();
			System.out.println(val.getJsonObject("invitee").getString("login"));
		}
		
		Set<String> mySet = new HashSet<String>();
		mySet.add("blah");
		String[] strAry = mySet.toArray(new String[0]);
		*/
		
		
	}
	
	public static void sendReadOnlyInvite(Github github, Coordinates repoToInvitTo, String userToInvite) throws IOException {
		String path = "/repos/" + repoToInvitTo.user() +"/"+ repoToInvitTo.repo() +"/collaborators/"+ userToInvite;
		
		github.entry().uri().queryParam("permission", "read").path(path).back().method(Request.PUT)
		.body().back()
		.fetch().as(RestResponse.class)
		.assertStatus(HttpURLConnection.HTTP_CREATED);
	}
	
	public static boolean acceptInvitation(Github github, Coordinates coords) throws IOException {
		Iterator<JsonValue> iter = github.entry().uri().path("/user/repository_invitations").back().method(Request.GET)
				.body().back()
				.fetch().as(RestResponse.class)
		        .assertStatus(HttpURLConnection.HTTP_OK)
		        .as(JsonResponse.class)
		        .json().readArray().iterator();
		int idToAccept = 0;
		boolean match = false;
		String thisCoord = coords.user() +"/"+ coords.repo();
		while (iter.hasNext() && !match) {
			JsonObject obj = (JsonObject) iter.next();
			JsonObject repository = obj.getJsonObject("repository");
			String fullRepoName = repository.getString("full_name");
			System.out.println(thisCoord +":"+ fullRepoName);
			if (fullRepoName.equals(thisCoord)) {
				System.out.println("match");
				match = true;
				idToAccept = obj.getInt("id");
			}
		}
		RestResponse resp = github.entry().uri().path("/user/repository_invitations/" + idToAccept).back().method(Request.PATCH)
				.body().back()
				.fetch().as(RestResponse.class);
				
		return resp.status() == HttpURLConnection.HTTP_NO_CONTENT;
		
	}
	
	public static boolean contains(Github github, Coordinates coords) throws IOException {
		String repoPath = "/" + coords.user() +"/"+ coords.repo();
		RestResponse response = github.entry().uri().path("/repos" + repoPath)
		.back().method(Request.GET)
		.body().set(Json.createObjectBuilder().build()).back()
		.fetch().as(RestResponse.class);
		if (response.status() == HttpURLConnection.HTTP_OK) {
			return true;
		} else {
			return false;
		}
	}
	
	

}

class jsonObjectArrayClass {
	String inviteeLogin = "";
	
	jsonObjectArrayClass(){}
	
	public JsonArray jsonArray() {
		JsonArray array = Json.createArrayBuilder()
			.add(Json.createObjectBuilder()
					.add("invitee", Json.createObjectBuilder()
							.add("login", inviteeLogin)))
			.build();
		return array;
	}
	
}
