package se.cygni.webapp.controllers.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.server.statistics.StatisticsCollector;
import se.cygni.webapp.controllers.controllers.model.GameNavigation;

import java.util.Locale;

@Controller
public class ShowGameController {

    private static Logger log = LoggerFactory
            .getLogger(ShowGameController.class);

    @Autowired
    SessionManager sessionManager;

    @Autowired
    StatisticsCollector statisticsCollector;


    @RequestMapping(value = "/showgame", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {

        GameLog gamelog = statisticsCollector.getLastGameLog();
        model.addAttribute("gamelog", gamelog);



        model.addAttribute("position", getGameNavigation(gamelog));
        return "showgame_vertical";
    }

    @RequestMapping(value = "/changegame", method = RequestMethod.POST)
    public String change(@ModelAttribute GameNavigation navigation, Model model) {

        log.info("game position: " + navigation.getPosition());

        GameLog gamelog = statisticsCollector.getGameLogAtPos(navigation.getPosition());

        model.addAttribute("gamelog", gamelog);
        model.addAttribute("position", getGameNavigation(gamelog));

        return "showgame_vertical";
    }

    private GameNavigation getGameNavigation(GameLog gameLog) {
        GameNavigation position = new GameNavigation();

        if (gameLog != null) {
            position.setNext(gameLog.logPosition < statisticsCollector.getNoofGameLogs()-1 ? gameLog.logPosition+1 : gameLog.logPosition);
            position.setPosition(gameLog.logPosition);
            position.setPrevious(gameLog.logPosition > 1 ? gameLog.logPosition-1 : 0);
        }

        return position;
    }
}
