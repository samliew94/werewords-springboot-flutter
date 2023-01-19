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
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.models.Players;
import com.models.RefRoles;
import com.util.File;

/**
 * Singleton!
 * @author Sam Liew 18 Jan 2023 9:26:55 PM
 *
 */
@Service
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class GameService {

	private Session session;
	
	private Random random = new SecureRandom();
	
	private int gameId = 0;
	
	private List<String> wordList = new CopyOnWriteArrayList();
	
	private String magicWord = "";

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	public void setSession(EntityManager entityManager) {
		session = entityManager.unwrap(Session.class);
	}
	

	
	public synchronized ResponseEntity startGame(Map body) throws Exception
	{
		String username = ((String) body.get("username")).toLowerCase();
		
		Players curPlayer = session.get(Players.class, username);
		
		Map result = new HashMap();
		
		if (curPlayer.getIsMayor() == 1)
			result.put("route", "pickword");
		else
			result.put("route", "role");
		
		return ResponseEntity.ok(result);
	}
	
	public ResponseEntity randomWords() throws Exception
	{
		if (wordList.isEmpty())
			File.convert("wordlist.txt").forEach(x->wordList.add(x));
		
		Collections.shuffle(wordList, random);
		
		List<String> options = new ArrayList();
		for (int i = 0; i < 100; i++)
			options.add(wordList.get(i));
		
		Collections.sort(options);
		
		return ResponseEntity.ok(options);
	}
	
	
	public ResponseEntity selectWord(Map body) throws Exception
	{
		String selectedWord = ((String) body.get("selectedWord")).toLowerCase();
		String username = ((String) body.get("username")).toLowerCase();
		
		String query = "";
		
		Players player = session.get(Players.class, username);
		
		if (player.getIsMayor() == 0)
			return ResponseEntity.badRequest().body("You are no longer the Mayor!");
		
		query = "FROM RefRoles rr WHERE rr.count > 0";
		
		List<RefRoles> refRoles = session.createQuery(query, RefRoles.class).getResultList();
		
		int totalRoles = 0;
		for (RefRoles refRole : refRoles)
			totalRoles += refRole.getCount();
		
		int totalPlayers = session.createQuery("FROM Players").getResultList().size();
		
		if (totalRoles != totalPlayers) {
			
			String msg = "Total Roles = " + totalRoles;
			msg += "\nTotal Players = " + totalPlayers;
			msg += "\nTotal Roles must equal Total Players";
			return ResponseEntity.badRequest().body(msg);
		}
		
		context.getBean(RolesService.class).randomRoles();
		
		wordList.removeIf(x->x.equalsIgnoreCase(selectedWord));
		
		magicWord = selectedWord;
		
		gameId += 1;
		
		Map result = new HashMap();
		result.put("route", "role");
		
		return ResponseEntity.ok(result);
	}
	
	public int getGameId() {
		return gameId;
	}
	
	public String getMagicWord() {
		return magicWord;
	}
	
}

