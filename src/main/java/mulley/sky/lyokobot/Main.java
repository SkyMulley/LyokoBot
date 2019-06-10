package mulley.sky.lyokobot;

import mulley.sky.lyokobot.Commands.MessageListener;
import mulley.sky.lyokobot.Logic.Objects.Guild;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static DBManager dbManager;
    private static List<Guild> registeredGuilds;
    private static List<LyokoWarrior> lyokoWarriors;
    private static List<LyokoWarrior> virtualizedLyokoWarriors = new ArrayList<>();
    private static IDiscordClient cli;
    private static ObjectManager objectManager = new ObjectManager();
    public static void main(String[] args) {
        System.out.println("Bootup Stage 1 - Discord Connection Starting");
        String token = "TOKENGOESHERE";
        cli = new ClientBuilder()
                .withToken(token)
                .build();
        cli.getDispatcher().registerListener(new MessageListener());
        cli.login();
        System.out.println("Bootup Stage 1 - Complete");
        System.out.println("Bootup Stage 2 - SQL Connection");
        dbManager = new DBManager(cli);
        System.out.println("Bootup Stage 2 - Complete");
        try {
            Thread.sleep(3000);
        }catch (Exception e){}
        System.out.println("Bootup Stage 3 - Object Registration");
        registeredGuilds = dbManager.getGuilds();
        System.out.println("Guilds in DB: "+registeredGuilds.size()+" Guilds Existing: "+cli.getGuilds().size());
        for(IGuild guild : cli.getGuilds()) {
            boolean found = false;
            for(Guild regGuild : registeredGuilds) {
                if (guild.equals(regGuild.getGuild())) {
                    found = true;
                }
            }
            if(!found) {
                dbManager.createGuild(guild.getLongID());
                registeredGuilds.add(new Guild(guild,"lb!",cli,""));
            }
        }
        lyokoWarriors = dbManager.getPlayers();
        System.out.println("Players in DB: "+lyokoWarriors.size());
        System.out.println("Bootup Stage 3 - Complete");
        cli.getDispatcher().registerListener(new Listener());
        new TimeManager().startRunning();
    }

    public static List<Guild> getRegisteredGuilds() {return registeredGuilds;}
    public static List<LyokoWarrior> getLyokoWarriors() {return lyokoWarriors;}
    public static DBManager getDbManager() {return dbManager;}
    public static IDiscordClient getDiscordClient() {return cli;}
    public static ObjectManager getObjectManager() {return objectManager;}
    public static List<LyokoWarrior> getVirtualizedLyokoWarriors() {return virtualizedLyokoWarriors;}
}
