/**
 * 
 *
 * @author Sam Liew 2 Jan 2023 11:22:07 PM
 */
package com.init;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.models.Players;
import com.models.RefRoles;
import com.models.Timer;
import com.models.Tokens;

/**
 * @author Sam Liew 2 Jan 2023 11:22:07 PM
 *
 */
@Configuration
public class Init {

	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * 
	 *
	 * @author Sam Liew 2 Jan 2023 11:23:03 PM
	 */
	public void execute() throws Exception
	{
		Transaction tx = null;

		try (Session session = sessionFactory.openSession())
		{
			tx = session.beginTransaction();
			refRoles(session);
			timer(session);
			tokens(session);
			tx.commit();
			System.out.println("Completed Init");
			
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}
		
	}
	
	

	/**
	 * 
	 *
	 * @author Sam Liew 3 Jan 2023 1:01:48 AM
	 */
	private void players(Session session) {
		List<Players> players = new ArrayList();
		
		Players p1 = new Players()/* {{setUsername("p1");}} */;
		Players p2 = new Players()/* {{setUsername("p2");}} */;
		Players p3 = new Players()/* {{setUsername("p3");}} */;
		Players p4 = new Players()/* {{setUsername("p4");}} */;
//		Players sai = new Players("sai");
//		Players yuk = new Players("yuk");
		p1.setUsername("fong");
		p2.setUsername("sai");
		p3.setUsername("yok");
		p4.setUsername("wong");
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
//		players.add(sai);
//		players.add(yuk);
		
		for (int i = 0; i < players.size(); i++) {
			Players x = players.get(i);
			session.saveOrUpdate(x);
		}
		
	}

	/**
	 * 
	 *
	 * @author Sam Liew 2 Jan 2023 11:23:39 PM
	 * @param session 
	 */
	private void refRoles(Session session) {
		
		RefRoles werewolf = new RefRoles();
		werewolf.setRoleName("werewolf");
		werewolf.setCount(2);
		werewolf.setMax(2);
		
		RefRoles seer = new RefRoles();
		seer.setRoleName("seer");
		
		RefRoles villager = new RefRoles();
		villager.setRoleName("villager");
		villager.setCount(2);
		villager.setMax(99);
		
		session.saveOrUpdate(werewolf);
		session.saveOrUpdate(seer);
		session.saveOrUpdate(villager);
	}
	
	/**
	 * 
	 *
	 * @author Sam Liew 9 Jan 2023 10:07:19 PM
	 */
	private void timer(Session session) {
		Timer timer = new Timer();
		session.saveOrUpdate(timer);
	}

	
	/**
	 * 
	 *
	 * @author Sam Liew 9 Jan 2023 10:07:43 PM
	 */
	private void tokens(Session session) {
		Tokens yesno = new Tokens();
		yesno.setTokenName("Yes/No");
		yesno.setTokenCount(36);
		yesno.setTokenColor("FFFF00");
		session.saveOrUpdate(yesno);
		
		Tokens maybe = new Tokens();
		maybe.setTokenName("Maybe");
		maybe.setTokenCount(16);
		maybe.setTokenColor("0000FF");
		session.saveOrUpdate(maybe);
		
		Tokens soClose = new Tokens();
		soClose.setTokenName("So Close");
		soClose.setTokenCount(1);
		soClose.setTokenColor("32CD32");
		session.saveOrUpdate(soClose);
		
		Tokens wayWayOff = new Tokens();
		wayWayOff.setTokenName("Way Way Off");
		wayWayOff.setTokenCount(1);
		wayWayOff.setTokenColor("964B00");
		session.saveOrUpdate(wayWayOff);
	}

}

