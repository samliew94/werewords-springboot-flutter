/**
 * 
 *
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 */
package com.restcontrollers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.services.PlayerService;

/**
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/players")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class PlayerController {
	@Autowired
	private PlayerService playerService;

	@PostMapping("add")
	public synchronized ResponseEntity addPlayer(@RequestBody Map body, HttpServletRequest request) {
		System.out.println("POST /players/add " + body);

		try {
			return playerService.addPlayer(body, request);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getMessage());
		}

	}
	
	@GetMapping("all")
	public ResponseEntity all() throws Exception {
		System.out.println("GET /players/all");

		try {
			return playerService.all();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getMessage());
		}

	}

	@GetMapping("ismayor/{username}")
	public ResponseEntity isMayor(@PathVariable String username) throws Exception {
		System.out.println("GET /players/ismayor");

		try {
			return playerService.isMayor(username);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}

	}

	@PutMapping("makemayor")
	public synchronized ResponseEntity makeMayor(@RequestBody Map body) {
		System.out.println("PUT /players/makemayor " + body);

		try {
			return playerService.makeMayor(body);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getMessage());
		}

	}

	@DeleteMapping("del")
	public synchronized ResponseEntity del(@RequestBody Map body) {
		System.out.println("DELETE /players/del " + body);

		try {
			return playerService.del(body);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getMessage());
		}

	}

}
