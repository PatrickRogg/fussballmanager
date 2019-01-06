package fussballmanager.service.benachrichtigung;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fussballmanager.service.saison.SaisonService;
import fussballmanager.service.saison.spieltag.SpieltagService;
import fussballmanager.service.spiel.SpielService;
import fussballmanager.service.spieler.AufstellungsPositionsTypen;
import fussballmanager.service.spieler.Spieler;
import fussballmanager.service.spieler.SpielerService;
import fussballmanager.service.spieler.spielerzuwachs.SpielerZuwachs;
import fussballmanager.service.spieler.spielerzuwachs.SpielerZuwachsRepository;
import fussballmanager.service.spieler.spielerzuwachs.SpielerZuwachsService;
import fussballmanager.service.team.Team;
import fussballmanager.service.team.TeamService;
import fussballmanager.service.user.User;

@Service
@Transactional
public class BenachrichtigungService {

	private static final Logger LOG = LoggerFactory.getLogger(BenachrichtigungService.class);

	@Autowired
	BenachrichtigungRepository benachrichtigungRepository;
	
	@Autowired
	SpielerService spielerService;
	
	@Autowired
	SaisonService saisonService;
	
	@Autowired
	SpieltagService spieltagService;
	
	@Autowired
	TeamService teamService;
	
	@Autowired
	SpielService spielService;
	
	public Benachrichtigung findeBenachrichtigung(Long id) {
		return benachrichtigungRepository.getOne(id);
	}
	
	public List<Benachrichtigung> findeBenachrichtigungen() {
		return benachrichtigungRepository.findAll();
	}
	
	public List<Benachrichtigung> findeAlleBenachrichtigungenEinesUsers(User user) {
		List<Team> teamsDesEmpfaengers = teamService.findeAlleTeamsEinesUsers(user);
		return benachrichtigungRepository.findByEmpfaengerIn(teamsDesEmpfaengers);
	}
	
	public List<Benachrichtigung> findeAlleBenachrichtigungenEinesUserUndBenachrichtigungsTyp(User user,
			BenachrichtigungsTypen benachrichtigungsTyp) {
		List<Team> teamsDesEmpfaengers = teamService.findeAlleTeamsEinesUsers(user);
		return benachrichtigungRepository.findByBenachrichtungsTypAndEmpfaengerIn(benachrichtigungsTyp, teamsDesEmpfaengers);
	}
	
	public List<Benachrichtigung> findeAlleUngelesenenBenachrichtigungenEinesUsers(User user) {
		List<Team> teamsDesEmpfaengers = teamService.findeAlleTeamsEinesUsers(user);
		return benachrichtigungRepository.findByEmpfaengerInAndGelesen(teamsDesEmpfaengers, false);
	}
	
	public List<Benachrichtigung> findeBenachrichtigungenNachSeite(User user, int seite, BenachrichtigungsTypen benachrichtigungsTyp) {
		int seitenLaenge = 10;
		int ersteNachricht = (seite - 1) * seitenLaenge;
		int letzteNachricht = (seite - 1) * seitenLaenge + seitenLaenge;
		List<Benachrichtigung> alleBenachrichtigungenEinesUsers = new ArrayList<Benachrichtigung>();
		if(benachrichtigungsTyp == null) {
			alleBenachrichtigungenEinesUsers = findeAlleBenachrichtigungenEinesUsers(user);
		} else {
			alleBenachrichtigungenEinesUsers = findeAlleBenachrichtigungenEinesUserUndBenachrichtigungsTyp(user, benachrichtigungsTyp);
		}
		
		List<Benachrichtigung> result = new ArrayList<>();
		
		Collections.reverse(alleBenachrichtigungenEinesUsers);
		if(letzteNachricht > alleBenachrichtigungenEinesUsers.size()) {
			result = alleBenachrichtigungenEinesUsers.subList(ersteNachricht, alleBenachrichtigungenEinesUsers.size());
		} else {
			result = alleBenachrichtigungenEinesUsers.subList(ersteNachricht, letzteNachricht);
		}
		return result;
	}

	public void legeBenachrichtigungAn(Benachrichtigung benachrichtigung) {
		User empfaenger = benachrichtigung.getEmpfaenger().getUser();
		List<Benachrichtigung> alleBenachrichtigungenDesEmpfaengers = findeAlleBenachrichtigungenEinesUsers(empfaenger);
		Collections.sort(alleBenachrichtigungenDesEmpfaengers);
		if(alleBenachrichtigungenDesEmpfaengers.size() >= 30) {
			for(Benachrichtigung b : alleBenachrichtigungenDesEmpfaengers) {
				if(b.isGelesen()) {
					loescheBenachrichtigung(b);
					alleBenachrichtigungenDesEmpfaengers.remove(b);
					if(alleBenachrichtigungenDesEmpfaengers.size() < 30) {
						break;
					}
				}
			}
		}
		while(alleBenachrichtigungenDesEmpfaengers.size() >= 30) {
			loescheBenachrichtigung(alleBenachrichtigungenDesEmpfaengers.get(0));
			alleBenachrichtigungenDesEmpfaengers.remove(0);
		}
		
		benachrichtigungRepository.save(benachrichtigung);
	}
	
	public void aktualisiereBenachrichtigung(Benachrichtigung benachrichtigung) {
		benachrichtigungRepository.save(benachrichtigung);
	}
	
	public void loescheBenachrichtigung(Benachrichtigung benachrichtigung) {
		benachrichtigungRepository.delete(benachrichtigung);
	}

	public void erstelleFreundschaftsspielAnfrage(Team absender, List<Team> empfaenger, BenachrichtigungsTypen benachrichtigungsTyp) {
		spieltagService.findeAktuellenSpieltag();
		LocalTime aktuelleUhrzeit = LocalTime.now(ZoneId.of("Europe/Berlin"));
		
		for(Team team : empfaenger) {
			Benachrichtigung benachrichtigung = new Benachrichtigung();
			benachrichtigung.setAbsender(absender);
			benachrichtigung.setEmpfaenger(team);
			benachrichtigung.setSpieltag(spieltagService.findeAktuellenSpieltag());
			benachrichtigung.setUhrzeit(aktuelleUhrzeit);
			benachrichtigung.setBenachrichtigungsText(freundschaftspielAnfrageText(absender, team, benachrichtigungsTyp));
			benachrichtigung.setBenachrichtungsTyp(benachrichtigungsTyp);
			benachrichtigung.setAntwortTyp(AntwortTypen.ANNEHMEN);
			legeBenachrichtigungAn(benachrichtigung);
		}
	}

	public void benachrichtigungAngenommen(Benachrichtigung benachrichtigung) {
		LocalTime aktuelleUhrzeit = LocalTime.now(ZoneId.of("Europe/Berlin"));
		Benachrichtigung neueBenachrichtigung = new Benachrichtigung();
		neueBenachrichtigung.setSpieler(benachrichtigung.getSpieler());
		if(benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.FREUNDSCHAFTSSPIELALLEGEGENALLE) ||
				benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.FREUNDSCHAFTSSPIELALLEGEGENEIN) ||
				benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.FREUNDSCHAFTSSPIELEINGEGENALLE) ||
				benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.FREUNDSCHAFTSSPIELEINGEGENEIN)) {
			spielService.erstelleFreundschaftsspiele(benachrichtigung.getBenachrichtungsTyp(), benachrichtigung.getAbsender(), benachrichtigung.getEmpfaenger());
			neueBenachrichtigung.setBenachrichtigungsText(freundschaftspielAnfrageAngenommenText(neueBenachrichtigung));
		}
		if(benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.SPIELERANGEBOT)) {
			neueBenachrichtigung.setBenachrichtigungsText(spielerAngebotAngenommenText(neueBenachrichtigung));
			Spieler spieler = spielerService.findeSpieler(benachrichtigung.getSpieler().getId());
			spieler.setAufstellungsPositionsTyp(AufstellungsPositionsTypen.ERSATZ);
			spieler.setTeam(benachrichtigung.getAbsender());
			spielerService.aktualisiereSpieler(spieler);
		}
				
		neueBenachrichtigung.setAbsender(benachrichtigung.getEmpfaenger());
		neueBenachrichtigung.setEmpfaenger(benachrichtigung.getAbsender());
		neueBenachrichtigung.setBenachrichtungsTyp(benachrichtigung.getBenachrichtungsTyp());
		neueBenachrichtigung.setSpieltag(spieltagService.findeAktuellenSpieltag());
		neueBenachrichtigung.setUhrzeit(aktuelleUhrzeit);
		
		legeBenachrichtigungAn(neueBenachrichtigung);
	}

	public void benachrichtigungAbgelehnt(Benachrichtigung benachrichtigung) {
		LocalTime aktuelleUhrzeit = LocalTime.now(ZoneId.of("Europe/Berlin"));
		Benachrichtigung neueBenachrichtigung = new Benachrichtigung();
		neueBenachrichtigung.setSpieler(benachrichtigung.getSpieler());
		if(benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.FREUNDSCHAFTSSPIELALLEGEGENALLE) ||
				benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.FREUNDSCHAFTSSPIELALLEGEGENEIN) ||
				benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.FREUNDSCHAFTSSPIELEINGEGENALLE) ||
				benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.FREUNDSCHAFTSSPIELEINGEGENEIN)) {
			neueBenachrichtigung.setBenachrichtigungsText(freundschaftspielAnfrageAbgelehntText(neueBenachrichtigung));	
		}
		if(benachrichtigung.getBenachrichtungsTyp().equals(BenachrichtigungsTypen.SPIELERANGEBOT)) {
			neueBenachrichtigung.setBenachrichtigungsText(spielerAngebotAbgelehntText(neueBenachrichtigung));	
		}
		
		neueBenachrichtigung.setAbsender(benachrichtigung.getEmpfaenger());
		neueBenachrichtigung.setEmpfaenger(benachrichtigung.getAbsender());
		neueBenachrichtigung.setBenachrichtungsTyp(benachrichtigung.getBenachrichtungsTyp());
		neueBenachrichtigung.setSpieltag(spieltagService.findeAktuellenSpieltag());
		neueBenachrichtigung.setUhrzeit(aktuelleUhrzeit);
		legeBenachrichtigungAn(neueBenachrichtigung);
	}

	public void benachrichtigungZuWenig(Benachrichtigung benachrichtigung) {
		LocalTime aktuelleUhrzeit = LocalTime.now(ZoneId.of("Europe/Berlin"));
		Benachrichtigung neueBenachrichtigung = new Benachrichtigung();
		neueBenachrichtigung.setSpieler(benachrichtigung.getSpieler());
		neueBenachrichtigung.setAbsender(benachrichtigung.getEmpfaenger());
		neueBenachrichtigung.setEmpfaenger(benachrichtigung.getAbsender());
		neueBenachrichtigung.setBenachrichtungsTyp(benachrichtigung.getBenachrichtungsTyp());
		neueBenachrichtigung.setSpieltag(spieltagService.findeAktuellenSpieltag());
		neueBenachrichtigung.setUhrzeit(aktuelleUhrzeit);
		neueBenachrichtigung.setBenachrichtigungsText(spielerAngebotZuWenigText(neueBenachrichtigung));
		legeBenachrichtigungAn(neueBenachrichtigung);
	}
	
	private String spielerAngebotAngenommenText(Benachrichtigung neueBenachrichtigung) {
		return "Dein Angebot für " + neueBenachrichtigung.getSpieler().getName() + " (" + neueBenachrichtigung.getSpieler().getPosition().getPositionsName() + ") - "
				+ neueBenachrichtigung.getSpieler().getSpielerReinStaerke().getReinStaerke() + " vom Team: " + neueBenachrichtigung.getSpieler().getTeam().getName() + 
				" wurde angenommen.";
	}
	
	private String spielerAngebotAbgelehntText(Benachrichtigung neueBenachrichtigung) {
		return "Dein Angebot für " + neueBenachrichtigung.getSpieler().getName() + " (" + neueBenachrichtigung.getSpieler().getPosition().getPositionsName() + ") - "
				+ neueBenachrichtigung.getSpieler().getSpielerReinStaerke().getReinStaerke() + " vom Team: " + neueBenachrichtigung.getSpieler().getTeam().getName() + 
				" wurde abgelehnt.";
	}
	
	private String spielerAngebotZuWenigText(Benachrichtigung neueBenachrichtigung) {
		return "Dein Angebot für " + neueBenachrichtigung.getSpieler().getName() + " (" + neueBenachrichtigung.getSpieler().getPosition().getPositionsName() + ") - "
				+ neueBenachrichtigung.getSpieler().getSpielerReinStaerke().getReinStaerke() + " vom Team: " + neueBenachrichtigung.getSpieler().getTeam().getName() + 
				" war zu gering.";
	}

	private String freundschaftspielAnfrageText(Team absender, Team team, BenachrichtigungsTypen benachrichtigungsTyp) {
		String s; 
		s = "Das Team " + absender.getName() + " hat dir eine Freundschaftsspielanfrage gesendet. Es möchte " 
		+ benachrichtigungsTyp.getBezeichnung() + " vereinbaren.";
		return s;
	}
	
	private String freundschaftspielAnfrageAngenommenText(Benachrichtigung benachrichtigung) {
		String s; 
		s = "Das Team " + benachrichtigung.getAbsender().getName() + " hat deine Freundschaftsspielanfrage angenommen.";
		return s;
	}
	
	private String freundschaftspielAnfrageAbgelehntText(Benachrichtigung benachrichtigung) {
		String s; 
		s = "Das Team " + benachrichtigung.getAbsender().getName() + " hat deine Freundschaftsspielanfrage abgelehnt.";
		return s;
	}

	public BenachrichtigungsTypen ermittleBenachrichtigungsTypAusFreundschaftsspielTyp(
			FreunschaftsspieleAnfrageTypen freundschaftsspielAnfrageTyp) {
		if(freundschaftsspielAnfrageTyp.equals(FreunschaftsspieleAnfrageTypen.ALLETEAMSALLETEAMS)) {
			return BenachrichtigungsTypen.FREUNDSCHAFTSSPIELALLEGEGENALLE;
		}
		if(freundschaftsspielAnfrageTyp.equals(FreunschaftsspieleAnfrageTypen.ALLETEAMSEINTEAM)) {
			return BenachrichtigungsTypen.FREUNDSCHAFTSSPIELALLEGEGENEIN;			
					}
		if(freundschaftsspielAnfrageTyp.equals(FreunschaftsspieleAnfrageTypen.EINTEAMALLETEAMS)) {
			return BenachrichtigungsTypen.FREUNDSCHAFTSSPIELEINGEGENALLE;
		}
		return BenachrichtigungsTypen.FREUNDSCHAFTSSPIELEINGEGENEIN;
	}
}
