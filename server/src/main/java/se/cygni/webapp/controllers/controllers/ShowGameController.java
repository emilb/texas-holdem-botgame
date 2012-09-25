package se.cygni.webapp.controllers.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.texasholdem.dao.model.stats.StatsActions;
import se.cygni.texasholdem.dao.model.stats.StatsChips;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.server.statistics.StatisticsCollector;
import se.cygni.webapp.controllers.controllers.model.GameNavigation;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class ShowGameController {

    @Autowired
    SessionManager sessionManager;

    @Autowired
    StatisticsCollector statisticsCollector;

    @RequestMapping(value = "/showgame", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {

        GameLog gamelog = statisticsCollector.getLastGameLog();
        model.addAttribute("gameLog", gamelog);
        model.addAttribute("position", getGameNavigation(gamelog));
        model.addAttribute("tableIds", getReversedListOfTableIds());

        return "showgame";
    }

    @RequestMapping(value = "/showgame/table/{tableId}", method = RequestMethod.GET)
    public String showTable(@PathVariable long tableId, Model model) {

        GameLog gamelog = statisticsCollector.getLastGameLog(tableId);
        model.addAttribute("tableId", tableId);
        model.addAttribute("gameLog", gamelog);
        model.addAttribute("position", getGameNavigation(gamelog));
        model.addAttribute("tableIds", getReversedListOfTableIds());

        return "showgame";
    }

    @RequestMapping(value = "/timemachine/table/{tableId}/gameround/{gameRound}", method = RequestMethod.GET)
    public @ResponseBody GameLog getGameLog(
            @PathVariable long tableId,
            @PathVariable int gameRound) {

        GameLog gameLog = null;

        if (tableId < 0 && gameRound < 0) {
            gameLog = statisticsCollector.getLastGameLog();
        } else if (tableId >= 0 && gameRound < 0) {
            gameLog = statisticsCollector.getLastGameLog(tableId);
        } else {
            gameLog = statisticsCollector.getGameLogAtPos(tableId, gameRound);
        }

        if (gameLog != null)
            return gameLog;

        return statisticsCollector.getLastGameLog();
    }

    @RequestMapping(value = "/timemachine/statsAction/table/{tableId}/gameround/{gameRound}", method = RequestMethod.GET)
    public @ResponseBody StatsActions getStatsActions(
            @PathVariable long tableId,
            @PathVariable int gameRound) {

        return statisticsCollector.getStatsActions(tableId, gameRound);
    }

    @RequestMapping(value = "/timemachine/statsChip/table/{tableId}/gameround/{gameRound}", method = RequestMethod.GET)
    public @ResponseBody StatsChips getStatsChips(
            @PathVariable long tableId,
            @PathVariable int gameRound) {

        return statisticsCollector.getStatsChips(tableId, gameRound);
    }

    private List<Long> getReversedListOfTableIds() {
        List<Long> reversedListOfTableIds = statisticsCollector.listTableIds();
        Collections.reverse(reversedListOfTableIds);

        return reversedListOfTableIds;
    }

    private GameNavigation getGameNavigation(GameLog gameLog) {
        GameNavigation position = new GameNavigation();

        if (gameLog != null) {
            position.setTableId(gameLog.tableCounter);
            position.setNext(gameLog.logPosition < statisticsCollector.getNoofGameLogs(gameLog.tableCounter) - 1 ? gameLog.logPosition + 1 : gameLog.logPosition);
            position.setPosition(gameLog.logPosition);
            position.setPrevious(gameLog.logPosition > 1 ? gameLog.logPosition - 1 : 0);
        }

        return position;
    }
}
