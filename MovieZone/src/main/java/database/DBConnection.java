package database;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import entity.Movie;

public interface DBConnection {
	
	public void close();
	
	public void setLikedMovies(String userId, List<Integer> movieIds);
	
	public void unsetLikedMovies(String userId, List<Integer> movieIds);
	
	public Set<Integer> getLikedMovieIds(String userId);
	
	public Set<Movie> getLikedMovies(String userId);
	
	public Set<String> getGenres(Integer movieId);
	
	public List<Movie> searchMovies(String term) throws UnsupportedEncodingException;
	
	public List<Movie> trendingMovies();
	
	public void saveMovie(Movie movie);
	
	public boolean verifyLogin(String userId, String password);
	
	public boolean register(String userId, String password);

}
