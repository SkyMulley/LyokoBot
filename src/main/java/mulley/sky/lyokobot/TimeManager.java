package mulley.sky.lyokobot;

import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.Logic.Runnables.AttackRunnable;
import sx.blah.discord.handle.obj.StatusType;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeManager {
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    private Random random = new Random();
    public void startRunning() {
        service.scheduleAtFixedRate(() -> {
            for(LyokoWarrior warrior : Main.getVirtualizedLyokoWarriors()) {
                if(!warrior.isAttacking()) {
                    if(warrior.recentlyAttackedToggle()) {
                        if(random.nextInt(2)+1==2) {
                            AttackRunnable runnable = new AttackRunnable(warrior);
                            runnable.start();
                            warrior.startAttacking(runnable);
                        }
                    }
                }
            }

            for(LyokoWarrior warrior : Main.getLyokoWarriors()) {
                if(!warrior.getVirtualized()) {
                    if (warrior.getHP() != 100) {
                        warrior.addHP(2);
                    }
                }
            }
        },2,2, TimeUnit.MINUTES);
    }
}
