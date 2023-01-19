/**
 * 
 *
 * @author Sam Liew 18 Jan 2023 9:26:55 PM
 */
package com.services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.models.Players;
import com.models.RefRoles;
import com.models.Roles;

/**
 * Singleton!
 * @author Sam Liew 18 Jan 2023 9:26:55 PM
 *
 */
@Service
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class RolesService {

	private Session session;
	
	private Random random = new SecureRandom();
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	public void setSession(EntityManager entityManager) {
		session = entityManager.unwrap(Session.class);
	}
	
	public ResponseEntity seeRole(String username) throws Exception {
		
		GameService gameService = context.getBean(GameService.class);
		
		String query = "";
		
		query = "FROM Roles WHERE player.username = :username";
		
		Roles role = session.createQuery(query, Roles.class).setParameter("username", username).getResultStream().findFirst().orElse(null);
		
		if (role == null)
			return ResponseEntity.badRequest().body("Username " + username + " not found\nGame not started?");
		
		Players player = role.getPlayer();
		RefRoles refRole = role.getRefRole();
		
		Map result = new HashMap();
		result.put("gameId", gameService.getGameId());
		result.put("roleName", refRole.getRoleName());
		result.put("isMayor", player.getIsMayor());
		
		if (player.getIsMayor() == 1) 
			result.put("magicWord", gameService.getMagicWord());
		
		if (refRole.getRoleName().equalsIgnoreCase("werewolf")) {
			query = "SELECT player.username FROM Roles WHERE refRole.roleName = 'werewolf' AND player.username != :username";
			List<String> otherWerewolves = session.createQuery(query).setParameter("username", username).getResultList();
			result.put("otherWerewolves", otherWerewolves);
		}
		
		return ResponseEntity.ok(result);
		
	}
	
	public void randomRoles() throws Exception
	{
		String query = "";
		
		query = "FROM Players";
		List<Players> allPlayers = session.createQuery(query, Players.class).getResultList();
		
		query = "FROM RefRoles rr WHERE rr.count > 0";
		List<RefRoles> refRoles = session.createQuery(query, RefRoles.class).getResultList();
		
		List<Integer> refRolesIds = new ArrayList();
		
		for (RefRoles refRole : refRoles) {
			int x = refRole.getCount();
			
			while (x > 0) {
				x -= 1;
				refRolesIds.add(refRole.getRefRoleId());
			}
		}
		
		List<Roles> roles = new ArrayList();
		
		Collections.shuffle(allPlayers, random);
		Collections.shuffle(refRolesIds, random);
		
		while (allPlayers.size() > 0)
		{				
			Players player = allPlayers.remove(0);
			int refRoleId = refRolesIds.remove(0);
			
			RefRoles refRole = refRoles.stream().filter(x->x.getRefRoleId().equals(refRoleId)).findFirst().get();
			
			Roles role = new Roles();
			role.setPlayer(player);
			role.setRefRole(refRole);
			roles.add(role);
		}
		
		session.createQuery("FROM Roles", Roles.class).getResultList().forEach(x->session.delete(x)); // delete old roles
		roles.forEach(x->session.saveOrUpdate(x)); // insert new roles
	}
	
	public ResponseEntity all() throws Exception
	{
		List<Roles> roles = session.createQuery("FROM Roles", Roles.class).getResultList();
		
		List<Map> results = new ArrayList();
		
		for (int i = 0; i < roles.size(); i++) {
			Roles r = roles.get(i);
			
			Map map = new HashMap();
			map.put("username", r.getPlayer().getUsername());
			map.put("roleName", r.getRefRole().getRoleName());
			
			results.add(map);
		}
		
		return ResponseEntity.ok(results);
	}

}