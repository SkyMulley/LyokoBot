package mulley.sky.lyokobot;

import mulley.sky.lyokobot.Logic.Objects.Guild;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.Logic.Runnables.AttackRunnable;
import mulley.sky.lyokobot.Logic.Runnables.CreateLyokoWarrior;
import mulley.sky.lyokobot.Logic.Runnables.ResetLyokoWarrior;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ObjectManager {
    private List<CreateLyokoWarrior> CLW = new ArrayList<>();
    private List<ResetLyokoWarrior> RLW = new ArrayList<>();
    private List<AttackRunnable> AR = new ArrayList<>();
    public ObjectManager(){}

    public static Guild getGuild(long id) {
        for(Guild iguild : Main.getRegisteredGuilds()) {
            if(iguild.getGuild().getLongID()==id) { return iguild;}
        }
        return null;
    }

    public static boolean isLyokoWarrior(IUser user) {
            for (LyokoWarrior lyokoWarrior : Main.getLyokoWarriors()) {
                if (user.equals(lyokoWarrior.getUser())) {
                    return true;
                }
            }
            return false;
    }

    public static LyokoWarrior getLyokoWarrior(IUser user) {
        for( LyokoWarrior lyokoWarrior : Main.getLyokoWarriors()) {
            if(user.equals(lyokoWarrior.getUser())) {
                return lyokoWarrior;
            }
        }
        return null;
    }

    public boolean isMakingLW(IUser user) {
        for(CreateLyokoWarrior clw : CLW) {
            if(clw.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    public void addCLW(CreateLyokoWarrior lw) {
        CLW.add(lw);
    }
    public void removeCLW(CreateLyokoWarrior lw) {
        CLW.remove(lw);
    }

    public boolean isResettingLW(IUser user) {
        for(ResetLyokoWarrior rlw : RLW) {
            if(rlw.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    public void addRLW(ResetLyokoWarrior lw) {
        RLW.add(lw);
    }

    public void removeRLW(ResetLyokoWarrior lw) {
        RLW.remove(lw);
    }
}
