package rpc;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import database.DBConnection;
import database.DBGetConnection;
import entity.Movie;

/**
 * Servlet implementation class Trending
 */
@WebServlet("/trending")
public class Trending extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Trending() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		
		DBConnection connection = DBGetConnection.getConnection();
		Set<Integer> liked = connection.getLikedMovieIds(userId);
		List<Movie> movies = connection.trendingMovies();
		connection.close();
		
		JSONArray array = new JSONArray();
		try {
			Iterator iter = movies.iterator();

	         while(iter.hasNext()) {
	            Movie movie = (Movie)iter.next();
	            JSONObject obj = movie.toJSONObject();
	            obj.put("liked", liked.contains((Integer)(movie.getId())));
	            array.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
