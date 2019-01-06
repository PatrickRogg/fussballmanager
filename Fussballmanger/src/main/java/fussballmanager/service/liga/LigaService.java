package fussballmanager.service.liga;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fussballmanager.service.land.Land;
import fussballmanager.service.land.LandService;
import fussballmanager.service.liga.Liga;
import fussballmanager.service.liga.LigaRepository;
import fussballmanager.service.saison.SaisonService;
import fussballmanager.service.saison.spieltag.SpieltagService;
import fussballmanager.service.team.Team;
import fussballmanager.service.team.TeamService;

@Service
@Transactional
public class LigaService {
	
	private static final Logger LOG = LoggerFactory.getLogger(LigaService.class);

	@Autowired
	LigaRepository ligaRepository;
	
	@Autowired
	TeamService teamService;
	
	@Autowired
	LandService landService;
	
	@Autowired
	SaisonService saisonService;
	
	@Autowired
	SpieltagService spieltagService;

	public Liga findeLiga(Long id) {
		return ligaRepository.getOne(id);
	}
	
	public List<Liga> findeAlleLigen() {
		return ligaRepository.findAll();
	}
	
	public List<Liga> findeAlleLigenEinesLandes(Land land) {
		return ligaRepository.findByLand(land);
	}
	
	public Liga findeNaechsteFreieHauptteamLiga() {
		for(Liga liga : findeAlleLigen()) {
			if(zaehleTeamsVonLiga(liga) < 18) {
				return liga;
			}
		}
		//TODO hier neue liga erstellen lassen
		return null;
	}
	
	public Liga findeLiga(String land, String ligaName) {
		for(Liga liga : findeAlleLigen()) {
			if(liga.getLigaNameTyp().getName().equals(ligaName) &&
					liga.getLand().getLandNameTyp().getName().equals(land)) {
				return liga;
			}
		}
		return null;
	}
	
	public void legeLigaAn(Liga liga) {
		ligaRepository.save(liga);
		
		teamService.erstelleDummyteams(liga);
	}
	
	public void aktualisiereLiga(Liga liga) {
		ligaRepository.save(liga);
	}
	
	public void loescheLiga(Liga liga) {
		ligaRepository.delete(liga);
	}

	private int zaehleTeamsVonLiga(Liga liga) {
		int counterTeamsVonLiga = 0;
		
		for(Team team : teamService.findeAlleTeamsEinerLiga(liga)) {
			if(team.getUser() != null) {
				counterTeamsVonLiga++;
				if(counterTeamsVonLiga == 18) {
					break;
				}
			}
		}
		return counterTeamsVonLiga;
	}

	public void legeHauptteamLigenAn(Land land) {
		for(LigenNamenTypen ligaNamenTypen : LigenNamenTypen.values()) {
			legeLigaAn(new Liga(ligaNamenTypen, landService.findeLand(land.getLandNameTyp())));
			LOG.info("Die Hauptteamliga: {} ist erstellt worden", ligaNamenTypen.getName());
		}
	}
}
