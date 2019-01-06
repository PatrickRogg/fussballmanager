package fussballmanager;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fussballmanager.service.auktionshaus.AuktionshausEintragService;
import fussballmanager.service.saison.SaisonService;
import fussballmanager.service.saison.spieltag.SpieltagService;
import fussballmanager.service.spiel.Spiel;
import fussballmanager.service.spiel.SpielService;
import fussballmanager.service.spiel.SpieleTypen;
import fussballmanager.service.spieler.SpielerService;
import fussballmanager.service.spieler.spielerzuwachs.SpielerZuwachsService;
import fussballmanager.service.team.Team;
import fussballmanager.service.team.TeamService;
import fussballmanager.spielsimulation.SpielSimulation;

@Service
@Transactional
public class ScheduldTasks {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScheduldTasks.class);
	
	//TODO spieltag, saison
	
	@Autowired
	SpielSimulation spielSimulation;
	
	@Autowired
	SpielService spielService;
	
	@Autowired
	SpieltagService spieltagService;
	
	@Autowired
	TeamService teamService;
	
	@Autowired
	SaisonService saisonService;

	@Autowired
	SpielerService spielerService;
	
	@Autowired
	SpielerZuwachsService spielerZuwachsService;
	
	@Autowired
	AuktionshausEintragService auktionshausEintragService;

	@Scheduled(fixedRate = 1000)
	public void ueberpruefeAktionshausEintraege() {
		auktionshausEintragService.ueberpruefeAlleAuktionshausEintraege();
	}
	
//	@Scheduled(cron = "0 15-59 12 * * ?", zone="Europe/Berlin")
//	public void simuliereTurnierspielErsteHalbzeit() {
//		spielSimulation.simuliereSpielMinuteAllerSpieleErsteHalbzeit(SpieleTypen.TURNIERSPIEL);
//	}
//	
//	@Scheduled(cron = "0 15-59 13 * * ?", zone="Europe/Berlin")
//	public void simuliereTurnierspielZweiteHalbzeit() {
//		spielSimulation.simuliereSpielMinuteAllerSpieleZweiteHalbzeit(SpieleTypen.TURNIERSPIEL);
//	}
//	
//	@Scheduled(cron = "0 15-59 15 * * ?", zone="Europe/Berlin")
//	public void simuliereFreundschaftsspielErsteHalbzeit() {
//		spielSimulation.simuliereSpielMinuteAllerSpieleErsteHalbzeit(SpieleTypen.FREUNDSCHAFTSSPIEL);
//	}
//	
//	@Scheduled(cron = "0 15-59 16 * * ?", zone="Europe/Berlin")
//	public void simuliereFreundschaftsspielZweiteHalbzeit() {
//		spielSimulation.simuliereSpielMinuteAllerSpieleZweiteHalbzeit(SpieleTypen.FREUNDSCHAFTSSPIEL);
//	}
//	
//	@Scheduled(cron = "0 15-59 18 * * ?", zone="Europe/Berlin")
//	public void simuliereLigaspielErsteHalbzeit() {
//		spielSimulation.simuliereSpielMinuteAllerSpieleErsteHalbzeit(SpieleTypen.LIGASPIEL);
//	}
//	
//	@Scheduled(cron = "0 15-59 19 * * ?", zone="Europe/Berlin")
//	public void simuliereLigaspielZweiteHalbzeit() {
//		spielSimulation.simuliereSpielMinuteAllerSpieleZweiteHalbzeit(SpieleTypen.LIGASPIEL);
//	}
	
//	@Scheduled(cron = "0 15-59 21 * * ?", zone="Europe/Berlin")
//	public void simulierePokalspielErsteHalbzeit() {
//		spielSimulation.simuliereSpielMinuteAllerSpieleErsteHalbzeit(SpieleTypen.POKALSPIEL);
//	}
//	
//	@Scheduled(cron = "0 15-59 22 * * ?", zone="Europe/Berlin")
//	public void simulierePokalspielZweiteHalbzeit() {
//		spielSimulation.simuliereSpielMinuteAllerSpieleZweiteHalbzeit(SpieleTypen.POKALSPIEL);
//	}
	
//	@Scheduled(cron = "0 0 14 * * ?", zone="Europe/Berlin")
//	public void setzeErgebnisseUndSetzteAuswechselungenZurueckTurnierspiel() {
//		LocalTime aktuelleZeitMinusZweiStunden = LocalTime.now(ZoneId.of("Europe/Berlin")).minusHours(2);
//		List<Spiel> alleSpieleEinesSpieltages = spielService.findeAlleSpieleEinerSaisonUndSpieltages(saisonService.findeAktuelleSaison(), spieltagService.findeAktuellenSpieltag());
//		
//		for(Spiel spiel : alleSpieleEinesSpieltages) {
//			Team heimTeam = spiel.getHeimmannschaft();
//			Team gastTeam = spiel.getGastmannschaft();
//			if(!spiel.isVorbei() && (spiel.getSpielTyp().getSpielBeginn().isBefore(aktuelleZeitMinusZweiStunden))) {
//				spielService.anzahlToreEinesSpielSetzen(spiel);
//				spiel.setVorbei(true);
//				
//				heimTeam.setAnzahlAuswechselungen(3);
//				gastTeam.setAnzahlAuswechselungen(3);
//				
//				spielService.aktualisiereSpiel(spiel);
//				teamService.aktualisiereTeam(heimTeam);
//				teamService.aktualisiereTeam(gastTeam);
//			}
//		}
//	}
//	
//	@Scheduled(cron = "0 0 17 * * ?", zone="Europe/Berlin")
//	public void setzeErgebnisseUndSetzteAuswechselungenZurueckFreundschaftsspiel() {
//		LocalTime aktuelleZeitMinusZweiStunden = LocalTime.now(ZoneId.of("Europe/Berlin")).minusHours(2);
//		List<Spiel> alleSpieleEinesSpieltages = spielService.findeAlleSpieleEinerSaisonUndSpieltages(saisonService.findeAktuelleSaison(), spieltagService.findeAktuellenSpieltag());
//		
//		for(Spiel spiel : alleSpieleEinesSpieltages) {
//			Team heimTeam = spiel.getHeimmannschaft();
//			Team gastTeam = spiel.getGastmannschaft();
//			if(!spiel.isVorbei() && (spiel.getSpielTyp().getSpielBeginn().isBefore(aktuelleZeitMinusZweiStunden))) {
//				spielService.anzahlToreEinesSpielSetzen(spiel);
//				spiel.setVorbei(true);
//				
//				heimTeam.setAnzahlAuswechselungen(3);
//				gastTeam.setAnzahlAuswechselungen(3);
//				
//				spielService.aktualisiereSpiel(spiel);
//				teamService.aktualisiereTeam(heimTeam);
//				teamService.aktualisiereTeam(gastTeam);
//			}
//		}
//	}
//	
//	@Scheduled(cron = "0 00 20 * * ?", zone="Europe/Berlin")
//	public void setzeErgebnisseUndSetzteAuswechselungenZurueckLigaspiel() {
//		LocalTime aktuelleZeitMinusZweiStunden = LocalTime.now(ZoneId.of("Europe/Berlin")).minusHours(2);
//		List<Spiel> alleSpieleEinesSpieltages = spielService.findeAlleSpieleEinerSaisonUndSpieltages(saisonService.findeAktuelleSaison(), spieltagService.findeAktuellenSpieltag());
//
//		for(Spiel spiel : alleSpieleEinesSpieltages) {
//			Team heimTeam = spiel.getHeimmannschaft();
//			Team gastTeam = spiel.getGastmannschaft();
//			if(!spiel.isVorbei() && (spiel.getSpielTyp().getSpielBeginn().isBefore(aktuelleZeitMinusZweiStunden))) {
//				spielService.anzahlToreEinesSpielSetzen(spiel);
//				spiel.setVorbei(true);
//				
//				heimTeam.setAnzahlAuswechselungen(3);
//				gastTeam.setAnzahlAuswechselungen(3);
//				
//				spielService.aktualisiereSpiel(spiel);
//				teamService.aktualisiereTeam(heimTeam);
//				teamService.aktualisiereTeam(gastTeam);
//			}
//		}
//	}
//	
//	@Scheduled(cron = "0 0 23 * * ?", zone="Europe/Berlin")
//	public void setzeErgebnisseUndSetzteAuswechselungenZurueckPokalspiel() {
//		LocalTime aktuelleZeitMinusZweiStunden = LocalTime.now(ZoneId.of("Europe/Berlin")).minusHours(2);
//		List<Spiel> alleSpieleEinesSpieltages = spielService.findeAlleSpieleEinerSaisonUndSpieltages(saisonService.findeAktuelleSaison(), spieltagService.findeAktuellenSpieltag());
//		
//		for(Spiel spiel : alleSpieleEinesSpieltages) {
//			Team heimTeam = spiel.getHeimmannschaft();
//			Team gastTeam = spiel.getGastmannschaft();
//			if(!spiel.isVorbei() && (spiel.getSpielTyp().getSpielBeginn().isBefore(aktuelleZeitMinusZweiStunden))) {
//				spielService.anzahlToreEinesSpielSetzen(spiel);
//				spiel.setVorbei(true);
//				
//				heimTeam.setAnzahlAuswechselungen(3);
//				gastTeam.setAnzahlAuswechselungen(3);
//				
//				spielService.aktualisiereSpiel(spiel);
//				teamService.aktualisiereTeam(heimTeam);
//				teamService.aktualisiereTeam(gastTeam);
//			}
//		}
//	}
//	
//	@Scheduled(cron = "0 30 * * * ?", zone="Europe/Berlin")
//	public void erstelleNeueSpielerFuerTransfermarkt() {
//		spielerService.loescheSpielerVomTransfermarkt();
//		spielerService.erstelleSpielerFuerTransfermarkt();
//	}
//	
//	@Scheduled(cron = "0 59 23 * * ?", zone="Europe/Berlin")
//	public void wechsleDenSpieltag() {
//		spieltagService.wechsleAktuellenSpieltag();
//		spielerZuwachsService.legeSpielerZuwachsFuerAlleSpielerAn();
//	}
}
