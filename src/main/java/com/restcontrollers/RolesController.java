/**
 * 
 *
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 */
package com.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.services.RolesService;

/**
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/roles")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class RolesController {
	
	private RolesService rolesService;

	@Autowired
	public void setRolesService(RolesService rolesService) {
		this.rolesService = rolesService;
	}

	@GetMapping("all")
	public synchronized ResponseEntity all() 
	{
		System.out.println("GET /roles/all");
		
		try {
			return rolesService.all();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getClass().getSimpleName());
		}
	}

	@GetMapping("seerole/{username}")
	public synchronized ResponseEntity seeRole(@PathVariable String username) 
	{
		System.out.println("GET /roles/seerole/"+username);
		
		try {
			return rolesService.seeRole(username);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(e.getClass().getSimpleName());
		}
	}
	
	
}
