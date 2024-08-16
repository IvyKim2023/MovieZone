package entity;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Movie {
	private int movieId;
	private String title;
	private double rating;
	private String releaseDate;
	private Set<String> genres;
	private String posterUrl;
	private String overview;
	
	public int getId() {
		return movieId;
	}
	public String getTitle() {
		return title;
	}
	public double getRating() {
		return rating;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public Set<String> getGenres() {
		return genres;
	}
	public String getPosterUrl() {
		return posterUrl;
	}
	public String getOverview() {
		return overview;
	}

	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("movie_id", movieId);
			obj.put("title", title);
			obj.put("rating", rating);
			obj.put("release_date", releaseDate);
			obj.put("genres", new JSONArray(genres));
			obj.put("poster_url", posterUrl);
			obj.put("overview", overview);
		} catch (JSONException e) {
			e.printStackTrace();	
		}
		return obj;
	}
	
	public static class MovieBuilder {
		private int movieId;
		private String title;
		private double rating;
		private String releaseDate;
		private Set<String> genres;
		private String posterUrl;
		private String overview;
		
		public MovieBuilder setMovieId(int movieId) {
			this.movieId = movieId;
			return this;
		}
		public MovieBuilder setTitle(String title) {
			this.title = title;
			return this;
		}
		public MovieBuilder setRating(double rating) {
			this.rating = rating;
			return this;
		}
		public MovieBuilder setReleaseDate(String releaseDate) {
			this.releaseDate = releaseDate;
			return this;
		}
		public MovieBuilder setGenres(Set<String> genres) {
			this.genres = genres;
			return this;
		}
		public MovieBuilder setPosterUrl(String posterUrl) {
			this.posterUrl = posterUrl;
			return this;
		}
		public MovieBuilder setOverview(String overview) {
			this.overview = overview;
			return this;
		}
		
		public Movie build() {
			return new Movie(this);
		}
		
		
	}
	
	private Movie(MovieBuilder builder) {
		this.movieId = builder.movieId;
		this.title = builder.title;
		this.rating = builder.rating;
		this.releaseDate = builder.releaseDate;
		this.genres = builder.genres;
		this.posterUrl = builder.posterUrl;
		this.overview = builder.overview;
	}
	
	public static void main(String[] arg) {
		Movie movie = new Movie.MovieBuilder().build();
	}

}
