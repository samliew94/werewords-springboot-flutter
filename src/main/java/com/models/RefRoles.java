/**
 * 
 *
 * @author Sam Liew 2 Jan 2023 10:41:11 PM
 */
package com.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author Sam Liew 2 Jan 2023 10:41:11 PM
 *
 */
@Entity
@Data
public class RefRoles 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer refRoleId;
	private String roleName;
	private String roleDescription;
	private Integer count = 1; // how many of these roles will appear in game?
	private Integer max = 1; // some roles like seer, can only have one.
	private Integer isEditable = 1; // canchange the value?
	
	
}

