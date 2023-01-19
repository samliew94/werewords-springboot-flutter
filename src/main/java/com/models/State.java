/**
 * 
 *
 * @author Sam Liew 27 Dec 2022 11:13:01 AM
 */
package com.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author Sam Liew 27 Dec 2022 11:13:01 AM
 *
 */
@Entity
@Data
public class State {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Integer stateId;
	private String stateName;
	private String stateDescription;
	
}

