package fussballmanager.mvc.kader;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fussballmanager.service.land.LandService;
import fussballmanager.service.liga.LigaService;
import fussballmanager.service.personal.PersonalService;
import fussballmanager.service.personal.PersonalTypen;
import fussballmanager.service.saison.SaisonService;
import fussballmanager.service.saison.spieltag.SpieltagService;
import fussballmanager.service.spieler.AufstellungsPositionsTypen;
import fussballmanager.service.spieler.Spieler;
import fussballmanager.service.spieler.SpielerService;
import fussballmanager.service.team.AusrichtungsTypen;
import fussballmanager.service.team.EinsatzTypen;
import fussballmanager.service.team.FormationsTypen;
import fussballmanager.service.team.Team;
import fussballmanager.service.team.TeamService;
import fussballmanager.service.user.UserService;

@Controller
public class KaderDetailsController {

	@Autowired
	LandService landService;
	
	@Autowired
	LigaService ligaService;
	
	@Autowired
	TeamService teamService;
	
	@Autowired
	SpielerService spielerService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	SaisonService saisonService;
	
	@Autowired
	SpieltagService spieltagService;
	
	@Autowired
	PersonalService personalService;
	
	@GetMapping("/team/{teamId}/kaderdetails")
	public String getKaderDetails(Model model, Authentication auth, @PathVariable("teamId") Long teamId) {
		Team team = teamService.findeTeam(teamId);
		List<Spieler> alleSpielerEinesTeams = spielerService.findeAlleSpielerEinesTeams(team);
		
		model.addAttribute("alleSpielerDesAktuellenTeams", alleSpielerEinesTeams);
		model.addAttribute("alleTrainerDesAktuellenTeams", 
				personalService.findeAllePersonalerEinesTeamsNachPersonalTyp(team, PersonalTypen.TRAINER));
		model.addAttribute("alleFormationsTypen", FormationsTypen.values());
		model.addAttribute("alleEinsatzTypen", EinsatzTypen.values());
		model.addAttribute("alleAusrichtungsTypen", AusrichtungsTypen.values());
		model.addAttribute("aufstellungsPositionsTypTrainingslager", AufstellungsPositionsTypen.TRAININGSLAGER);
		model.addAttribute("aufstellungsPositionsTypErsatz", AufstellungsPositionsTypen.ERSATZ);
		model.addAttribute("aufstellungsPositionsTypGesperrt", AufstellungsPositionsTypen.GESPERRT);
		model.addAttribute("aufstellungsPositionsTypVerletzt", AufstellungsPositionsTypen.VERLETZT);
		model.addAttribute("alleAufstellungsPositionsTypen", team.getFormationsTyp().getAufstellungsPositionsTypen());
		model.addAttribute("alleSpielerAufErsatzbank", spielerService.findeAlleSpielerEinesTeamsAufErsatzbank(team));
		model.addAttribute("einzuwechselnderSpieler", new Spieler());
		model.addAttribute("anzahlDerEinwechslungen", team.getAnzahlAuswechselungen());
		model.addAttribute("summeSpielerWerte", erstelleSummeDerSpielerWerteListe(alleSpielerEinesTeams));
		
		return "kader/kaderdetails";
	}
	
	public SummeSpielerWerte erstelleSummeDerSpielerWerteListe(List<Spieler> spielerDesTeams) {
		SummeSpielerWerte summeDerSpielerWerte = new SummeSpielerWerte();
		
		for(Spieler spieler : spielerDesTeams) {
			summeDerSpielerWerte.setAlter(summeDerSpielerWerte.getAlter() + spieler.getAlter());
			summeDerSpielerWerte.setErfahrung(summeDerSpielerWerte.getErfahrung() + spieler.getErfahrung());
			summeDerSpielerWerte.setGehalt(summeDerSpielerWerte.getGehalt() + spieler.getGehalt());
			summeDerSpielerWerte.setMotivation(summeDerSpielerWerte.getMotivation() + spieler.getMotivation());
			summeDerSpielerWerte.setReinStaerke(summeDerSpielerWerte.getReinStaerke() + spieler.getSpielerReinStaerke().getReinStaerke());
			summeDerSpielerWerte.setStaerke(summeDerSpielerWerte.getStaerke() + spieler.getSpielerStaerke());
			summeDerSpielerWerte.setZuwachs(summeDerSpielerWerte.getZuwachs() + spieler.getSpielerZuwachs());
		}
		return summeDerSpielerWerte;
	}
}
