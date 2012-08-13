package se.cygni.texasholdem.server.session.se.cygni.texasholdem.server.statistics;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.cygni.texasholdem.server.eventbus.RegisterForPlayWrapper;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class StatisticsCollector {

    private long startUp = System.currentTimeMillis();

    private AtomicLong noofConnections = new AtomicLong();

    private EventBus eventBus;

    @Autowired
    public StatisticsCollector(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Subscribe
    public void onRegisterForPlay(final RegisterForPlayWrapper requestWrapper) {
        noofConnections.incrementAndGet();
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
        period = period.plusMillis((int)getUpTime());

        return daysHoursMinutes.print(period.normalizedStandard());
    }


}
