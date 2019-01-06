package fussballmanager.service.saison.spieltag;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import fussballmanager.service.saison.Saison;

@Entity
public class Spieltag {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO, generator="native")
	@GenericGenerator(name = "native", strategy = "native")
	private long id;

	private int spieltagNummer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Saison saison;
	
	private boolean aktuellerSpieltag = false;
	
	public Spieltag(int spieltagNummer, Saison saison) {
		this.spieltagNummer = spieltagNummer;
		this.saison = saison;
	}
	
	public Spieltag() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSpieltagNummer() {
		return spieltagNummer;
	}

	public void setSpieltagNummer(int spieltagNummer) {
		this.spieltagNummer = spieltagNummer;
	}

	public Saison getSaison() {
		return saison;
	}

	public void setSaison(Saison saison) {
		this.saison = saison;
	}

	public boolean isAktuellerSpieltag() {
		return aktuellerSpieltag;
	}

	public void setAktuellerSpieltag(boolean istAktuellerSpieltag) {
		this.aktuellerSpieltag = istAktuellerSpieltag;
	}
}
