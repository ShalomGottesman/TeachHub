package runner;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.github.Repos.RepoCreate;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;

import utilities.Strings;

public class Sand {
	public static void main(String[] args) throws IOException {
		String token = "731f6ca100f6c857ace0c7ce44002884ea660868";
		String user = "TeachHubProf";
		Github hub = new RtGithub(user, token);
		String repoName = user+"6";
		RepoCreate createRepoSettings = new RepoCreate(repoName, true).withDescription(Strings.defaultRepoInitMsg).withHomepage(Strings.projectHomepage).withAutoInit(true);
		//hub.repos().create(createRepoSettings);
		
		String apiPath = "user/repos";
		hub.entry().uri().path(apiPath)
		.back().method(Request.POST)
        .body().set(createRepoSettings.json()).back()
        .fetch().as(RestResponse.class)
        .assertStatus(HttpURLConnection.HTTP_CREATED);
		
		Coordinates coords = new Coordinates.Simple(user, repoName);
		//Repo repo = hub.repos().get(coords);
		//repo.collaborators().add("TeachHubTA");
		
		
		String path = "/repos/" + coords.user() +"/"+ coords.repo() +"/collaborators/"+ "TeachHubTA";
		hub.entry().uri().path(path).back().method(Request.PUT)
		.body().back()
		.fetch().as(RestResponse.class)
		.assertStatus(HttpURLConnection.HTTP_CREATED);
	}

}
