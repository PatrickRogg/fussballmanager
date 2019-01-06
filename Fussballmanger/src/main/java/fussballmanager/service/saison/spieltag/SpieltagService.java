package fussballmanager.service.saison.spieltag;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fussballmanager.service.personal.PersonalService;
import fussballmanager.service.saison.Saison;
import fussballmanager.service.saison.SaisonService;
import fussballmanager.service.spiel.Spiel;
import fussballmanager.service.spiel.SpielService;
import fussballmanager.service.spiel.SpieleTypen;
import fussballmanager.service.spieler.SpielerService;
import fussballmanager.service.spieler.spielerzuwachs.SpielerZuwachsService;
import fussballmanager.service.team.TeamService;
import fussballmanager.service.team.stadion.StadionService;
import fussballmanager.service.user.UserService;

@Service
@Transactional
public class SpieltagService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SpieltagService.class);
	
	@Autowired
	SpieltagRepository spieltagRepository;

	@Autowired
	SaisonService saisonService;
	
	@Autowired
	SpielService spielService;
	
	@Autowired
	SpielerZuwachsService spielerZuwachsService;
	
	@Autowired
	SpielerService spielerService;
	
	@Autowired
	TeamService teamService;
	
	@Autowired
	StadionService stadionService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PersonalService personalService;
	
	public Spieltag findeSpieltag(Long id) {
		return spieltagRepository.getOne(id);
	}
	
	public Spieltag findeSpieltagDurchSaisonUndSpieltagNummer(Saison saison, int spieltagNummer) {
		return spieltagRepository.findBySaisonAndSpieltagNummer(saison, spieltagNummer);
	}
	
	public List<Spieltag> findeNaechstenSiebenSpieltage() {
		Spieltag aktuellerSpieltag = findeAktuellenSpieltag();
		List<Spieltag> naechstenSiebenSpieltage = new ArrayList<>();
		for(int i = aktuellerSpieltag.getSpieltagNummer()+1; i < aktuellerSpieltag.getSpieltagNummer()+8; i++) {
			naechstenSiebenSpieltage.add(findeSpieltagDurchSaisonUndSpieltagNummer(saisonService.findeAktuelleSaison(), i));
		}
		return naechstenSiebenSpieltage;
	}
	
	public List<Spieltag> findeAlleSpieltage() {
		return spieltagRepository.findAll();
	}
	
	public Spieltag findeAktuellenSpieltag() {
		return spieltagRepository.findByAktuellerSpieltagTrue();
	}
	
	public Spieltag findeNaechstenSpieltag() {
		return findeSpieltagDurchSaisonUndSpieltagNummer(saisonService.findeAktuelleSaison(), findeAktuellenSpieltag().getSpieltagNummer() + 1);
	}
	
	public List<Spieltag> findeAlleSpieltageEinerSaison(Saison saison) {
		return spieltagRepository.findBySaison(saison);
	}
	
	public void legeSpieltagAn(Spieltag spieltag) {
		spieltagRepository.save(spieltag);	
	}

	public void aktualisiereSpieltag(Spieltag spieltag) {
		spieltagRepository.save(spieltag);
	}
	
	public void loescheSpieltag(Spieltag spieltag) {
		spieltagRepository.delete(spieltag);
	}

	public void erstelleAlleSpieltageFuerEineSaison(Saison saison) {
		//Erster Spieltag der Saison
		Spieltag ersterSpieltagDerSaison = new Spieltag(0, saison);
		ersterSpieltagDerSaison.setAktuellerSpieltag(true);
		
		legeSpieltagAn(ersterSpieltagDerSaison);
		LOG.info("Spieltag: {}, Saison: {}, Status: {}", 0, saison.getSaisonNummer(), findeSpieltagDurchSaisonUndSpieltagNummer(saison, 0).isAktuellerSpieltag());
		
		for(int i = 1; i < saison.getSpieltage(); i++) {
			legeSpieltagAn(new Spieltag(i, saison));
			LOG.info("Spieltag: {}, Saison: {}, Status: {}", i, saison.getSaisonNummer(), findeSpieltagDurchSaisonUndSpieltagNummer(saison, i).isAktuellerSpieltag());
		}
	}
	
	public void wechsleAktuellenSpieltag() {
		Spieltag alterSpieltag = findeAktuellenSpieltag();
		Spieltag neuerSpieltag = findeNaechstenSpieltag();
		neuerSpieltag.setAktuellerSpieltag(true);
		aktualisiereSpieltag(neuerSpieltag);
		LOG.info("Neuer Spieltag: {}, Status: {}", neuerSpieltag.getSpieltagNummer(), neuerSpieltag.isAktuellerSpieltag());
		
		alterSpieltag.setAktuellerSpieltag(false);
		aktualisiereSpieltag(alterSpieltag);
		LOG.info("Alter Spieltag: {}, Status: {}", alterSpieltag.getSpieltagNummer(), alterSpieltag.isAktuellerSpieltag());
	}
	
	public void wechsleSpieltag() {
		if(findeAktuellenSpieltag().getSpieltagNummer() >= saisonService.findeAktuelleSaison().getSpieltage() - 1) {
			// aktuellen Spieltag der alten Saison auf false setzten
			Spieltag aktuellerSpieltagDerAltenSaison = findeAktuellenSpieltag();
			aktuellerSpieltagDerAltenSaison.setAktuellerSpieltag(false);
			aktualisiereSpieltag(aktuellerSpieltagDerAltenSaison);
			saisonService.legeSaisonAn(new Saison(saisonService.findeLetzteSasion().getSaisonNummer() + 1));
		} else {
			aufgabenSpieltagWechsel();
		}
	}
	
	public void aufgabenSpieltagWechsel() {
		wechsleAktuellenSpieltag();
		//TODO optimieren
		userService.aufgabenBeiSpieltagWechsel();
		teamService.aufgabenBeiSpieltagWechsel();
		spielerService.aufgabenBeiSpieltagWechsel();
		personalService.aufgabenBeiSpieltagWechsel();
		stadionService.aufgabenBeiSpieltagWechsel();
	}

	public List<Spieltag> findeAlleAktuellenSpieltageFuerTurnier() {
		LocalTime aktuelleZeit = LocalTime.now(ZoneId.of("Europe/Berlin"));
		Spieltag aktuellerSpieltag = findeAktuellenSpieltag();
		List<Spieltag> alleAktuellenSpieltage = 
				spieltagRepository.findBySpieltagNummerGreaterThanAndSaison(aktuellerSpieltag.getSpieltagNummer(), saisonService.findeAktuelleSaison());
		
		if(SpieleTypen.TURNIERSPIEL.getSpielBeginn().isAfter(aktuelleZeit)) {
			alleAktuellenSpieltage.add(aktuellerSpieltag);
		}
		return alleAktuellenSpieltage;
	}
	
	public List<Spieltag> findeAlleAktuellenSpieltageFuerFreundschaftsspiel() {
		LocalTime aktuelleZeit = LocalTime.now(ZoneId.of("Europe/Berlin"));
		Spieltag aktuellerSpieltag = findeAktuellenSpieltag();
		List<Spieltag> alleAktuellenSpieltage = 
				spieltagRepository.findBySpieltagNummerGreaterThanAndSaison(aktuellerSpieltag.getSpieltagNummer(), saisonService.findeAktuelleSaison());
		
		if(SpieleTypen.FREUNDSCHAFTSSPIEL.getSpielBeginn().isAfter(aktuelleZeit)) {
			alleAktuellenSpieltage.add(aktuellerSpieltag);
		}
		return alleAktuellenSpieltage;
	}
	
	public List<Spieltag> ermittleFreieSpieltageFuerZweiTeams(List<Spiel> alleFreundschaftsspieleTeam1, List<Spiel> alleFreundschaftsspieleTeam2) {
		List<Spieltag> alleSpieltageNachAktuellem = findeAlleAktuellenSpieltageFuerFreundschaftsspiel();
		List<Spieltag> freieSpieltage = alleSpieltageNachAktuellem;
		
		LOG.info("Freundschaftsspielsize: {}, {}", alleFreundschaftsspieleTeam1.size(),alleFreundschaftsspieleTeam1.size());
		//TODO optimieren
		//TODO vergangeSpieltage rausnehmen
		for(Spieltag spieltag : alleSpieltageNachAktuellem) {
			for(Spiel spiel1 : alleFreundschaftsspieleTeam1) {
				if(spiel1.getSpieltag().equals(spieltag)) {
					freieSpieltage.remove(spieltag);
					continue;
				}
			}
			
			for(Spiel spiel2 : alleFreundschaftsspieleTeam2) {
				if(spiel2.getSpieltag().equals(spieltag)) {
					freieSpieltage.remove(spieltag);
					continue;
				}
			}
		}
		return freieSpieltage;
	}
}
