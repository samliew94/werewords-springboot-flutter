/**
 * 
 *
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 */
package com.restcontrollers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.services.GameService;

/**
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/game")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class GameController 
{
	private GameService gameService;

	@Autowired
	public void setGameService(GameService gameService) {
		this.gameService = gameService;
	}
	
	@PostMapping("start")
	private ResponseEntity start(@RequestBody Map body)
	{
		System.out.println("POST /game/start " + body);
		
		try {
			return gameService.startGame(body);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getClass().getSimpleName());
		}
	}
	
	@GetMapping("randomwords")
	public ResponseEntity randomWords()
	{
		System.out.println("GET /game/randomwords");
		
		try {
			return gameService.randomWords();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getClass().getSimpleName());
		}
		
	}
	
	@PostMapping("selectword")
	public synchronized ResponseEntity selectWord(@RequestBody Map body)
	{
		System.out.println("POST /game/selectword " + body);
		
		try {
			return gameService.selectWord(body);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getClass().getSimpleName());
		}
	}
	
}
