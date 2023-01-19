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
import com.models.Timer;
import com.models.Tokens;

/**
 * @author Sam Liew 27 Dec 2022 11:13:11 AM
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/settings")
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class SettingsController
{
	@Autowired
	private SessionFactory sessionFactory;
	
	@GetMapping("all")
	public ResponseEntity all() throws Exception
	{
		System.out.println("/settings/all");
		
		try (Session session = sessionFactory.openSession()) 
		{
			String query = "";
			
			Map results = new HashMap();
			
			Integer minutes = session.get(Timer.class, 1).getMinutes();
			Map mapTimer = new HashMap();
			results.put("timer", minutes);
			
			List<Tokens> tokens = session.createQuery("FROM Tokens", Tokens.class).getResultList();
			
			List<Map> mapTokens = new ArrayList();
			for (int i = 0; i < tokens.size(); i++) {
				Tokens x = tokens.get(i);
				Integer tokenId = x.getTokenId();
				String tokenName = x.getTokenName();
				Integer tokenCount = x.getTokenCount();
				String tokenColor = x.getTokenColor();
				
				Map mapToken = new HashMap();
				mapToken.put("tokenId", tokenId);
				mapToken.put("tokenName", tokenName);
				mapToken.put("tokenCount", tokenCount);
				mapToken.put("tokenColor", tokenColor);
				mapTokens.add(mapToken);
			}
			results.put("tokens", mapTokens);
			
			List<Object[]> refRoles = session.createQuery("SELECT rr.refRoleId, rr.roleName, rr.count FROM RefRoles rr").getResultList();
			List<Map> mapRefRoles = new ArrayList();
			for (int i = 0; i < refRoles.size(); i++) {
				Object[] x = refRoles.get(i);
				Object refRoleId = x[0];
				Object roleName = x[1];
				Object roleCount = x[2];
				
				Map map = new HashMap();
				map.put("refRoleId", refRoleId);
				map.put("roleName", roleName);
				map.put("roleCount", roleCount);
				mapRefRoles.add(map);
			}
			
			results.put("refRoles", mapRefRoles);

			ResponseEntity response = ResponseEntity.ok(results);;
			
			return response;
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return ResponseEntity.internalServerError().body(null);
		
	}
	
	@PutMapping("edit/token")
	public synchronized ResponseEntity editToken(@RequestBody Map body)
	{
		System.out.println("/settings/edit/token");
		int tokenId = (int) body.get("tokenId");
		int operation = (int) body.get("operation");
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) 
		{
			String query = "";
			
			query = "FROM Tokens WHERE tokenId = :tokenId";
			
			Tokens token = session.createQuery(query, Tokens.class)
					.setParameter("tokenId", tokenId)
					.getResultStream().findFirst().orElse(null);
			token.setTokenCount(token.getTokenCount() + operation);
			
			int curCount = token.getTokenCount();
			
			if (curCount < 0)
				token.setTokenCount(0);
			else if (curCount > 99)
				token.setTokenCount(99);
			
			tx = session.beginTransaction();
			session.saveOrUpdate(token);
			tx.commit();
			
			return ResponseEntity.ok(null);
			
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			
			e.printStackTrace();
		}
		
		return ResponseEntity.internalServerError().body(null);
	}
	
	@PutMapping("edit/timer")
	public synchronized ResponseEntity editTimer(@RequestBody Map body)
	{
		System.out.println("/settings/edit/token = " + body);
		int operation = (int) body.get("operation");
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) 
		{
			String query = "";
			
			query = "FROM Timer";
			
			Timer timer = session.createQuery("FROM Timer", Timer.class).getResultStream().findFirst().orElse(null);
			timer.setMinutes(timer.getMinutes() + operation);
			
			int minutes = timer.getMinutes();
			
			if (minutes < 0)
				timer.setMinutes(0);
			else if (minutes > 99)
				timer.setMinutes(99);
			
			tx = session.beginTransaction();
			session.saveOrUpdate(timer);
			tx.commit();
			
			return ResponseEntity.ok(null);
			
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			
			e.printStackTrace();
		}
		
		return ResponseEntity.internalServerError().body(null);
	}
	
	@PutMapping("edit/refroles")
	public synchronized ResponseEntity editRefRoles(@RequestBody Map body)
	{
		System.out.println("/settings/edit/refroles = " + body);
		int refRoleId = (int) body.get("refRoleId");
		int operation = (int) body.get("operation");
		
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
			
			int count = refRole.getCount() + operation;
			
			if (count > max)
				return ResponseEntity.badRequest().body("Role count exceeds max possible value (" + max + ")");
			else if (count < 0)
				count = 0;
			
			refRole.setCount(count);
			
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
	
	@GetMapping("tokentimer")
	public synchronized ResponseEntity tokenTimer()
	{
		System.out.println("/settings/tokentimer");
		
		try (Session session = sessionFactory.openSession())
		{
			Timer timer = session.createQuery("FROM Timer", Timer.class).getResultStream().findFirst().orElse(null);
			
			Map results = new HashMap();
			results.put("timer", timer.getMinutes());
			
			List<Tokens> tokens = session.createQuery("FROM Tokens", Tokens.class).getResultList();
			List<Map> mapTokens = new ArrayList();
			for (int i = 0; i < tokens.size(); i++) {
				Tokens x = tokens.get(i);
				
				Map map = new HashMap();
				map.put("tokenId", x.getTokenId());
				map.put("tokenName", x.getTokenName());
				map.put("tokenCount", x.getTokenCount());
				map.put("tokenColor", x.getTokenColor());
				mapTokens.add(map);
			}
			
			results.put("tokens", mapTokens);
			
			return ResponseEntity.ok(results);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return ResponseEntity.internalServerError().build();
	}
	
	
}
