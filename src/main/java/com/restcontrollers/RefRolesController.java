/**
 * 
 *
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 */
package com.restcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.RefRoles;

/**
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/refroles")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class RefRolesController {
	@Autowired
	private SessionFactory sessionFactory;

	@GetMapping("all")
	public synchronized ResponseEntity get() 
	{
		try (Session session = sessionFactory.openSession()) 
		{
			List<Object[]> refRoles = session.createQuery("SELECT rr.refRoleId, rr.roleName, rr.count FROM RefRoles rr").getResultList();
			
			List<Map> results = new ArrayList();
			
			for (int i = 0; i < refRoles.size(); i++) {
				Object[] x = refRoles.get(i);
				Object refRoleId = x[0];
				Object roleName = x[1];
				Object roleCount = x[2];
				
				Map map = new HashMap();
				map.put("refRoleId", refRoleId);
				map.put("roleName", roleName);
				map.put("roleCount", roleCount);
				
				results.add(map);
			}
			
			return ResponseEntity.ok(results);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.internalServerError().build();
	}
	
	@PutMapping("edit")
	public synchronized ResponseEntity edit(@RequestBody Map body)
	{
		int refRoleId = Integer.valueOf(body.get("refRoleId").toString());
		boolean operation = Boolean.valueOf(body.get("operation").toString());
		System.out.println("edit: " + body);
		
		Transaction tx = null;
		
		try (Session session = sessionFactory.openSession())
		{
			RefRoles refRole = session.get(RefRoles.class, refRoleId);
			if(refRole == null)
				return ResponseEntity.badRequest().body("Can't find role to edit!");
			
			Integer max = refRole.getMax();
			Integer isEditable = refRole.getIsEditable();
			
			if (isEditable == 0)
				return ResponseEntity.badRequest().body("Uneditable role!");
			
			int count = refRole.getCount();
			
			if (operation)
			{
				if (count + 1 > max)
					return ResponseEntity.badRequest().body("Role count exceeds max possible value (" + max + ")");
				
				refRole.setCount(count + 1);
			}			
			else
			{
				if (count - 1 <= 0)					
					refRole.setCount(0);
				else
					refRole.setCount(count - 1);
			}
			
			tx = session.beginTransaction();
			session.saveOrUpdate(refRole); // is this even necessary?
			tx.commit();
			
			return ResponseEntity.ok(null);
			
		} catch (Exception e) {
			
			if (tx != null)
				tx.rollback();
			
			e.printStackTrace();
		}
		
		return ResponseEntity.internalServerError().build();
	}
	
}
