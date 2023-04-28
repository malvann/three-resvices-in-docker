package component;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Sleeper {
    public void sleep(int minutes) {
        try {
            TimeUnit.MINUTES.sleep(minutes);
        } catch (InterruptedException e) {
            log.warn("Interrupt: ", e);
            Thread.currentThread().interrupt();
        }
    }
}
