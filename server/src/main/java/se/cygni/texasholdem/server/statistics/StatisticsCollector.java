package se.cygni.texasholdem.server.statistics;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StatisticsCollector {

    private long startUp = System.currentTimeMillis();

    private AtomicLong noofConnections = new AtomicLong();

    private EventBus eventBus;

    private List<GameLog> gameLogs = new ArrayList<GameLog>();

    @Autowired
    public StatisticsCollector(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Subscribe
    public void onRegisterForPlay(final RegisterForPlayWrapper requestWrapper) {
        noofConnections.incrementAndGet();
    }

    @Subscribe
    public void addGameLog(final GameLog gameLog) {
        gameLogs.add(gameLog);
    }

    public long getTotalNoofConnectionsMade() {
        return noofConnections.longValue();
    }

    public long getUpTime() {
        return System.currentTimeMillis() - startUp;
    }

    public String getUpTimeAsText() {

        PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" day", " days")
                .appendSeparator(" and ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .appendSeparator(" and ")
                .appendSeconds()
                .appendSuffix(" second", " seconds")
                .toFormatter();

        Period period = new Period();
        period = period.plusMillis((int) getUpTime());

        return daysHoursMinutes.print(period.normalizedStandard());
    }

    public GameLog getLastGameLog() {
        if (gameLogs.size() == 0)
            return null;

        return gameLogs.get(
                gameLogs.size() -1);
    }

}
