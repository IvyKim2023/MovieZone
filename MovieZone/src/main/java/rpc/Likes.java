package rpc;

import java.io.IOException;
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

import com.mysql.cj.xdevapi.JsonArray;

import database.DBConnection;
import database.DBGetConnection;
import entity.Movie;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/liked")
public class Likes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Likes() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();

		DBConnection connection = DBGetConnection.getConnection();
		Set<Movie> likedMovies = connection.getLikedMovies(userId);
		System.out.println(likedMovies);
		
		JSONObject obj;
	      for(Iterator iter = likedMovies.iterator(); iter.hasNext(); array.put(obj)) {
	         Movie movie = (Movie)iter.next();
	         obj = movie.toJSONObject();

	         try {
	            obj.append("liked", true);
	         } catch (JSONException e) {
	            e.printStackTrace();
	         }
	      }

	      RpcHelper.writeJsonArray(response, array);
	}
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			
			JSONArray array = input.getJSONArray("liked");
			List<Integer> movieIds = new ArrayList<>();
			
			for (int i = 0; i < array.length(); ++i) {
				movieIds.add((Integer) array.get(i));
			}
			
			DBConnection connection = DBGetConnection.getConnection();
			connection.setLikedMovies(userId, movieIds);
			connection.close();
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			
			JSONArray array = input.getJSONArray("liked");
			List<Integer> movieIds = new ArrayList<>();
			for (int i = 0; i < array.length(); ++i) {
				movieIds.add((Integer) array.get(i));
			}
			
			DBConnection connection = DBGetConnection.getConnection();
			connection.unsetLikedMovies(userId, movieIds);
			connection.close();
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	}


