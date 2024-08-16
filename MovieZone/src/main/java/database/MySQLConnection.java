package database;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import api.TMDB_API;
import entity.Movie;
import entity.Movie.MovieBuilder;

public class MySQLConnection implements DBConnection {
	
	private Connection conn;
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBinfo.URL);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setLikedMovies(String userId, List<Integer> movieIds) {
		if (conn == null) {
			return;
		}
		
		try {
			String sql = "INSERT IGNORE INTO likes (user_id, movie_id) VALUES (?, ?)";		
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (Integer movieId : movieIds) {
				stmt.setString(1, userId);
				stmt.setInt(2, movieId);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void unsetLikedMovies(String userId, List<Integer> movieIds) {
		if (conn == null) {
			return;
		}
		
		try {
			String sql = "DELETE FROM likes WHERE user_id = ? AND movie_id = ?";		
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (Integer movieId : movieIds) {
				stmt.setString(1, userId);
				stmt.setInt(2, movieId);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<Integer> getLikedMovieIds(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<Integer> likedMovieIds = new HashSet<>();
		
		try {
			String sql = "SELECT * FROM likes WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
				
			while (rs.next()) {
				likedMovieIds.add(rs.getInt("movie_id"));
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return likedMovieIds;	 	
	}

	
	@Override
	public Set<Movie> getLikedMovies(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<Movie> likedMovies = new HashSet<>();
		Set<Integer> movieIds = getLikedMovieIds(userId);
		
		try {
			String sql = "SELECT * FROM movies WHERE movie_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (Integer movieId : movieIds) {
				stmt.setInt(1, movieId);

				ResultSet rs = stmt.executeQuery();
				
				MovieBuilder builder = new MovieBuilder();
				
				while (rs.next()) {
					builder.setMovieId(rs.getInt("movie_id"));
					builder.setTitle(rs.getString("title"));
					builder.setRating(rs.getDouble("rating"));
					builder.setReleaseDate(rs.getString("release_date"));
					builder.setPosterUrl(rs.getString("poster_url"));
					builder.setOverview(rs.getString("overview"));				
					builder.setGenres(getGenres(movieId));
					likedMovies.add(builder.build());
				}
			} 
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return likedMovies;
		
	}

	@Override
	public Set<String> getGenres(Integer movieId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<String> genres = new HashSet<>();
		
		try {
			String sql = "SELECT genre FROM genres WHERE movie_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, movieId);
			ResultSet rs = stmt.executeQuery();
				
			while (rs.next()) {
				genres.add(rs.getString("genre"));
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return genres;	
	}

	@Override
	public List<Movie> searchMovies(String term) throws UnsupportedEncodingException {
		TMDB_API tmdbAPI = new TMDB_API();
		List<Movie> movies = tmdbAPI.search(term);
		for (Movie movie : movies) {
			saveMovie(movie);  
		}
		return movies;
	}
	
	@Override
	public List<Movie> trendingMovies() {
		TMDB_API tmdbAPI = new TMDB_API();
		List<Movie> movies = tmdbAPI.trending();
		for (Movie movie : movies) {
			saveMovie(movie);  
		}
		return movies;
	}

	@Override
	public void saveMovie(Movie movie) {
		if (conn == null) {
			return;
		}
		
		try {
			String sql = "INSERT IGNORE INTO movies VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, movie.getId());
			stmt.setString(2, movie.getTitle());
			stmt.setDouble(3, movie.getRating());
			stmt.setString(4, movie.getReleaseDate());
			stmt.setString(5, movie.getPosterUrl());
			stmt.setString(6, movie.getOverview());
			stmt.execute();
			
			sql = "INSERT IGNORE INTO genres VALUES (?, ?)";
			stmt = conn.prepareStatement(sql);
			for (String genre : movie.getGenres()) {
				stmt.setInt(1, movie.getId());
				stmt.setString(2, genre);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			return false;
		}
		
		try {
			String sql = "SELECT password FROM users WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				return password.equals(rs.getString("password"));
			}
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean register(String userId, String password) {
		if (conn == null) {
			return false;
		}
		
		try {
			String sql = "SELECT password FROM users WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				return false;
			}
			
			sql = "INSERT IGNORE INTO users (user_id, password) VALUES (?, ?)";		
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, password);
			stmt.execute();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	
}
