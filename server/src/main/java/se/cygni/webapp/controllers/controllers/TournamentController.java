package se.cygni.webapp.controllers.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.cygni.texasholdem.dao.model.TournamentLog;
import se.cygni.texasholdem.game.util.TournamentUtil;
import se.cygni.texasholdem.server.room.Tournament;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.webapp.controllers.controllers.model.StartTournament;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Controller
public class TournamentController {

    @Autowired
    SessionManager sessionManager;

    public static final String TOURNAMENT_LIST = "tournamentList";
    public static final String TOURNAMENT_CURRENT = "tournamentCurrent";
    public static final String TOURNAMENT_CURRENT_START = "tournamentCurrentStart";

    @RequestMapping(value = "/tournament", method = RequestMethod.GET)
    public String home(@RequestParam(value = "id", required = false) String tournamentId, Locale locale, Model model) {

        List<TournamentLog> tournamentLogs = TournamentUtil.createTournamentLogs(sessionManager.listFinishedOrStartedTournaments());

        TournamentLog latest = TournamentUtil.createTournamentLog(sessionManager.getAvailableTournament());
        tournamentLogs.add(0, latest);

        if (tournamentId != null) {
            TournamentLog currentTournament = TournamentUtil.createTournamentLog(sessionManager.getTournament(tournamentId));

            if (currentTournament != null) {
                latest = currentTournament;
            }
        }

        Collections.sort(tournamentLogs, new Comparator<TournamentLog>() {
            @Override
            public int compare(TournamentLog tl, TournamentLog tl1) {
                Long l = Long.valueOf(tl.getTournamentCounter());
                Long l1 = Long.valueOf(tl1.getTournamentCounter());
                // Reverse order
                return l1.compareTo(l);
            }
        });

        model.addAttribute(TOURNAMENT_LIST, tournamentLogs);
        model.addAttribute(TOURNAMENT_CURRENT, latest);
        model.addAttribute(TOURNAMENT_CURRENT_START, new StartTournament(latest.getId()));

        return "tournament";
    }

    @RequestMapping(value = "/tournament/details/{tournamentId}", method = RequestMethod.GET)
    public @ResponseBody
    TournamentLog getTournamentLog(
            @PathVariable String tournamentId) {

        return TournamentUtil.createTournamentLog(sessionManager.getTournament(tournamentId));
    }


    @RequestMapping(value = "/tournament/subview", method = RequestMethod.GET)
    public String updateSubview(@RequestParam(value = "id", required = false) String tournamentId, Locale locale, Model model) {
        TournamentLog latest = null;

        if (tournamentId != null) {
            TournamentLog currentTournament = TournamentUtil.createTournamentLog(sessionManager.getTournament(tournamentId));

            if (currentTournament != null) {
                latest = currentTournament;
            }
        }

        model.addAttribute(TOURNAMENT_CURRENT, latest);
        model.addAttribute(TOURNAMENT_CURRENT_START, new StartTournament(latest.getId()));

        return "tournament_ajax_update";
    }

    @RequestMapping(value = "/tournament/start/{tournamentId}", method = RequestMethod.GET)
    public @ResponseBody
        String start(
            @PathVariable String tournamentId) {

        Tournament tournament = sessionManager.getTournament(tournamentId);
        tournament.startTournament();
        return "OK";
    }
}
