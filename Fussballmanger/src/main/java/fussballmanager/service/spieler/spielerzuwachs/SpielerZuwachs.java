package fussballmanager.service.spieler.spielerzuwachs;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

import fussballmanager.service.saison.Saison;
import fussballmanager.service.saison.spieltag.Spieltag;

@Entity
public class SpielerZuwachs {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	Saison saison;
	
	@OneToOne(fetch = FetchType.LAZY)
	Spieltag spieltag;
	
	private double zuwachs;
	
	Trainingslager trainingslager;
	
	ZuwachsFaktorAlter zuwachsFaktorAlter;
	
	public final double defaultZuwachs = 2.0;
	
	public final int maximaleErfahrung = 75;
	
	public SpielerZuwachs(Saison saison, Spieltag spieltag) {
		this.saison = saison;
		this.spieltag = spieltag;
	}
	
	public SpielerZuwachs() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Saison getSaison() {
		return saison;
	}

	public void setSaison(Saison saison) {
		this.saison = saison;
	}

	public Spieltag getSpieltag() {
		return spieltag;
	}

	public void setSpieltag(Spieltag spieltag) {
		this.spieltag = spieltag;
	}

	public double getZuwachs() {
		return zuwachs;
	}

	public void setZuwachs(double zuwachs) {
		this.zuwachs = zuwachs;
	}

	public Trainingslager getTrainingslager() {
		return trainingslager;
	}

	public void setTrainingslager(Trainingslager trainingslager) {
		this.trainingslager = trainingslager;
	}

	public double getDefaultZuwachs() {
		return defaultZuwachs;
	}

	public int getMaximaleErfahrung() {
		return maximaleErfahrung;
	}
}
