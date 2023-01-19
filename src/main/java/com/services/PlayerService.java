/**
 * 
 *
 * @author Sam Liew 18 Jan 2023 9:26:55 PM
 */
package com.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.models.Players;
import com.models.Roles;

/**
 * @author Sam Liew 18 Jan 2023 9:26:55 PM
 *
 */
@Scope("prototype")
@Service
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class PlayerService {

	private Session session;

	@Autowired
	public void setSession(EntityManager entityManager) {
		session = entityManager.unwrap(Session.class);
	}
	
	/**
	 * 
	 *
	 * @author Sam Liew 18 Jan 2023 9:28:13 PM
	 */
	public ResponseEntity addPlayer(Map body, HttpServletRequest request) throws Exception
	{
		String username = ((String) body.get("username")).toLowerCase();
		String ipAddress = request.getRemoteAddr();
		
		String query = "";
		
		Players player = null;
		
		// production mode (prevents same user from registering different username. Only FIRST name pass)
		query = "FROM Players WHERE ipAddress = :ipAddress";
		player = session.createQuery(query, Players.class)
				.setParameter("ipAddress", ipAddress)
				.getResultStream().findFirst().orElse(null);
		
		if (player != null && !player.getUsername().equalsIgnoreCase(username))
		{
			String msg = "Duplicate username \"" + player.getUsername() + "\" {" + ipAddress+"}";
			msg += "\nYou must relog as \""+player.getUsername()+"\"";
			return ResponseEntity.badRequest().body(msg);
		}
		
		// debug mode
//		player = session.get(Players.class, username); 
		
		int size = session.createQuery("FROM Players").getResultList().size();
		
		if (player == null) {
			player = new Players();
			player.setUsername(username);
			player.setIsAdmin(size == 0 ? 1 : 0);
			player.setIsMayor(size == 0 ? 1 : 0);
			player.setIpAddress(ipAddress);
		}
		
		session.saveOrUpdate(player);
		
		Map result = new HashMap();
		result.put("username", player.getUsername());
		
		if (player.getIsAdmin() == 1)
			result.put("route", "lobby");
		else {
			
			if (player.getIsMayor() == 1)
				result.put("route",	"pickword");
			else
				result.put("route",	"role");
			
		}

		return ResponseEntity.ok(result);
		
	}
	
	public ResponseEntity all() throws Exception
	{
		String query = "";
		
		query = "FROM Players";
		
		List<Players> players = session.createQuery(query, Players.class).getResultList();

		List<Map> results = new ArrayList();

		for (int i = 0; i < players.size(); i++) {
			Players x = players.get(i);
			
			Map map = new HashMap();
			map.put("username", x.getUsername());
			map.put("isMayor", x.getIsMayor());
			results.add(map);
		}
		
		return ResponseEntity.ok(results);
		
	}
	
	public ResponseEntity isMayor(String username) throws Exception {
		
		username = username.toLowerCase();
		String query = "";
		
		query = "FROM Players WHERE isMayor = 1 AND username = :username";
		
		int size = session.createQuery(query).setParameter("username", username).getResultList().size();
		
//		if (size == 0)
//			return gameController.seeRole(username);
//		
//		Map result = new HashMap();
//		result.put("newRoute", "/pickword");
		
		ResponseEntity response = new ResponseEntity(HttpStatus.FOUND);
		return response;
		
	}
	
	public ResponseEntity makeMayor(Map body) throws Exception 
	{
		String username = ((String) body.get("username")).toLowerCase();
		String targetUsername = ((String) body.get("targetUsername")).toLowerCase();
		
		Players player = session.get(Players.class, username);
		Players targetPlayer = session.get(Players.class, targetUsername);
		
		if (player.getIsAdmin() == 0)
			return ResponseEntity.badRequest().body("Only host can appoint mayor!");
		
		if (targetPlayer == null)
			return ResponseEntity.badRequest().body("Username " + targetPlayer + " not found!");
		
		String query = "FROM Players WHERE isMayor = 1";
		
		List<Players> curMayors = session.createQuery(query, Players.class).getResultList();
		
		curMayors.forEach(x->{
			x.setIsMayor(0);
			session.saveOrUpdate(x);
		});
		
		targetPlayer.setIsMayor(1);
		session.saveOrUpdate(targetPlayer);
		
		return ResponseEntity.ok(targetPlayer.getUsername());
	}
	
	public ResponseEntity del(Map body) throws Exception
	{
		String username = ((String) body.get("username")).toLowerCase();
		String targetUsername = ((String) body.get("targetUsername")).toLowerCase();
		
		Players player = session.get(Players.class, username);
		Players targetPlayer = session.get(Players.class, targetUsername);
		
		if (player.getIsAdmin() == 0)
			return ResponseEntity.badRequest().body("Only admin can remove player!");
		
		if (targetPlayer == null)
			return ResponseEntity.badRequest().body("Username " + targetPlayer + " not found!");
		
		Roles role = session.createQuery("FROM Roles WHERE player = :player", Roles.class)
				.setParameter("player", targetPlayer)
				.getResultStream().findFirst().orElse(null);
		
		if (role != null)
			session.delete(role);
		
		session.delete(targetPlayer);
		
		return ResponseEntity.ok(null);
	}
	
	
	
}

