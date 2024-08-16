package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.TMDB_API;
import database.DBConnection;
import database.DBGetConnection;
import entity.Movie;

/**
 * Servlet implementation class Recommend
 */
@WebServlet("/recommend")
public class Recommend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Recommend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");

		DBConnection connection = DBGetConnection.getConnection();
		Set<Movie> likedMovies = connection.getLikedMovies(userId);
		List<String> titles = new ArrayList<String>();
		for (Movie movie: likedMovies) {
			titles.add(movie.getTitle());
		}
		connection.close();
		
		System.out.println(likedMovies);
		
        JSONObject jsonPayload = new JSONObject();
        try {
			jsonPayload.put("titles", titles);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		URL url = new URL("http://localhost:8090/predict");
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/json; utf-8");
	    conn.setRequestProperty("Accept", "application/json");
	    conn.setDoOutput(true);
	    
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder responseStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseStringBuilder.append(responseLine.trim());
            }
        }
        
        JSONArray jsonResponse;
        JSONArray array = new JSONArray();
		try {
			jsonResponse = new JSONArray(responseStringBuilder.toString());
			
			TMDB_API tmdbAPI = new TMDB_API();
			List<Movie> recommendedMovies = tmdbAPI.getMovieList(jsonResponse);
		
			Iterator iter = recommendedMovies.iterator();

	         while(iter.hasNext()) {
	            Movie movie = (Movie)iter.next();
	            JSONObject obj = movie.toJSONObject();
	            if (!likedMovies.contains((Integer)(movie.getId()))) {
	            	array.put(obj);}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.print(array);
        RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
