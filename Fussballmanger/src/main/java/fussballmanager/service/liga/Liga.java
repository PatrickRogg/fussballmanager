package fussballmanager.service.liga;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import fussballmanager.service.land.Land;

@Entity
public class Liga {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	
	private LigenNamenTypen ligaNameTyp;
	
	private final int groeße = 18;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Land land;
	
	public Liga(LigenNamenTypen ligaName, Land land) {
		this.ligaNameTyp = ligaName;
		this.land = land;
	}
	
	public Liga() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LigenNamenTypen getLigaNameTyp() {
		return ligaNameTyp;
	}

	public void setLigaNameTyp(LigenNamenTypen ligaName) {
		this.ligaNameTyp = ligaName;
	}

	public Land getLand() {
		return land;
	}

	public void setLand(Land land) {
		this.land = land;
	}

	public int getGroeße() {
		return groeße;
	}
}
