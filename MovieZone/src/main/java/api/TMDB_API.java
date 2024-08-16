package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Movie;
import entity.Movie.MovieBuilder;

public class TMDB_API {
	private static final String URL = "https://api.themoviedb.org/3/";
//	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "783ccf22d1441b9e105a5acf37be5b7c";
	
	public static final Map<Integer, String> genreMap = new HashMap<>();
    static {
    	genreMap.put(28, "Action");
    	genreMap.put(12, "Adventure");
    	genreMap.put(16, "Animation");
    	genreMap.put(35, "Comedy");
    	genreMap.put(80, "Crime");
    	genreMap.put(99, "Documentary");
    	genreMap.put(18, "Drama");
    	genreMap.put(10751, "Family");
    	genreMap.put(14, "Fantasy");
    	genreMap.put(36, "History");
    	genreMap.put(27, "Horror");
    	genreMap.put(10402, "Music");
    	genreMap.put(9648, "Mystery");
    	genreMap.put(10749, "Romance");
    	genreMap.put(878, "Science Fiction");
    	genreMap.put(10770, "TV Movie");
    	genreMap.put(53, "Thriller");
    	genreMap.put(10752, "War");
    	genreMap.put(37, "Western");	
    }
	
	
	public List<Movie> trending() {
		String query = String.format("trending/movie/day?language=en-US&api_key=%s", API_KEY);
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + query).openConnection();
			int responseCode = connection.getResponseCode();
			
			System.out.println("\nSending 'GET' request to URL: " + URL + query);
			System.out.println("Response code: " + responseCode);
			
			if (responseCode != 200) {
				//
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("results")) {
				return new ArrayList<Movie>();
			}
			
			JSONArray r = obj.getJSONArray("results");
			
			return getMovieList(r);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		
		return new ArrayList<Movie>();
	}
	
	
	public List<Movie> search(String keyword) throws UnsupportedEncodingException {
		String query = String.format("search/movie?query=%s&include_adult=false&language=en-US&page=1&api_key=%s", 
                URLEncoder.encode(keyword, "UTF-8"), API_KEY);
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + query).openConnection();
			int responseCode = connection.getResponseCode();
			
			System.out.println("\nSending 'GET' request to URL: " + URL + query);
			System.out.println("Response code: " + responseCode);
			
			if (responseCode != 200) {
				//
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("results")) {
				return new ArrayList<Movie>();
			}
			
			JSONArray r = obj.getJSONArray("results");
			
			return getMovieList(r);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		
		return new ArrayList<Movie>();
	}
	
	
//	private void queryAPI(double lat, double lon) {
//		List<Movie> events = search(lat,lon, null);
//		try {
//			for (Movie event : events ) {
//				System.out.println(event.toJSONObject());
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();	
//		}
//		
//	}
//	
	
	private String getPosterUrl(JSONObject movie) throws JSONException {
		if (!movie.isNull("poster_path")) {
			String path = movie.getString("poster_path");
			return "https://image.tmdb.org/t/p/w185" + path;	
		}
	
		return "";	
	}
	
	private Set<String> getGenres(JSONObject movie) throws JSONException {
		Set<String> genres = new HashSet<>();
		Set<Integer> ids = new HashSet<>();
		
		if ((!movie.isNull("genre_ids"))) {
			JSONArray genreIds = movie.getJSONArray("genre_ids");
			for (int i = 0; i < genreIds.length(); i++) {
                ids.add(genreIds.getInt(i));
            }
			
			for (Integer id : ids) {
				genres.add(genreMap.get(id));
            }
		}
		
		return genres;
	}
		
	
	public List<Movie> getMovieList(JSONArray movies) throws JSONException {
		List<Movie> movieList = new ArrayList<>();
		
		for (int i = 0; i < movies.length(); ++i) {
			JSONObject movie = movies.getJSONObject(i);
			
			MovieBuilder builder = new MovieBuilder();
			
			if (!movie.isNull("id")) {
				builder.setMovieId(movie.getInt("id"));
			}
			
			if (!movie.isNull("title")) {
				builder.setTitle(movie.getString("title"));
			}
			
			if (!movie.isNull("vote_average")) {
				builder.setRating(movie.getDouble("vote_average"));
			}
			
			if (!movie.isNull("release_date")) {
				builder.setReleaseDate(movie.getString("release_date"));
			}
			
			if (!movie.isNull("overview")) {
				builder.setOverview(movie.getString("overview"));
			}
			
			builder.setGenres(getGenres(movie));
			builder.setPosterUrl(getPosterUrl(movie));
			
			movieList.add(builder.build());
		}
		
		return movieList;
		
		
	}
	
	public static void main(String[] args) {
		TMDB_API tmApi = new TMDB_API();
//		print(tmApi.trending());
	}
	
	
}
