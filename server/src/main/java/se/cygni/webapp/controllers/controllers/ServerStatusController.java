package se.cygni.webapp.controllers.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.cygni.texasholdem.dao.model.ServerStatus;
import se.cygni.texasholdem.game.GamePlayer;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.server.statistics.StatisticsCollector;
import se.cygni.texasholdem.util.PlayerTypeConverter;

import java.util.List;
import java.util.Locale;

@Controller
public class ServerStatusController {

    @Autowired
    SessionManager sessionManager;

    @Autowired
    StatisticsCollector statisticsCollector;

    @RequestMapping(value = "/serverstatus", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        model.addAttribute("uptime", statisticsCollector.getUpTimeAsText());
        model.addAttribute("noofPlayers", sessionManager.getNoofPlayers());
        model.addAttribute("totalNoofConnections", statisticsCollector.getTotalNoofConnectionsMade());

        List<GamePlayer> players = PlayerTypeConverter.listOfBotPlayers(sessionManager.listPlayers());
        model.addAttribute("players", players);

        return "serverstatus";
    }

    @RequestMapping(value = "/serverstatus/json", method = RequestMethod.GET)
    public
    @ResponseBody
    ServerStatus getServerStatus() {
        return new ServerStatus(
                statisticsCollector.getUpTimeAsText(),
                sessionManager.getNoofPlayers(),
                statisticsCollector.getTotalNoofConnectionsMade());
    }
}
