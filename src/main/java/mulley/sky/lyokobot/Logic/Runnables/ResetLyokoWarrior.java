package mulley.sky.lyokobot.Logic.Runnables;

import mulley.sky.lyokobot.Commands.Commands.LWManagement.Reset;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ResetLyokoWarrior {
    private LyokoWarrior warrior;
    private IChannel channel;
    private IDiscordClient client;
    private IMessage message;
    private ScheduledFuture future;
    public ResetLyokoWarrior(LyokoWarrior warrior,IChannel channel, IDiscordClient client, IMessage message) {
        this.warrior = warrior;
        this.channel = channel;
        this.client = client;
        this.message = message;
        client.getDispatcher().registerListener(this);
        start();
    }

    @EventSubscriber
    public void gotConfirmedMessage(MessageReceivedEvent event) {
        if(event.getAuthor().equals(warrior.getUser())) {
            if(event.getChannel().equals(channel)) {
                if(event.getMessage().getContent().equalsIgnoreCase("confirm")) {
                    try {
                        event.getMessage().delete();
                    } catch (Exception e) {}
                    message.edit("LyokoWarrior Reset");
                    Main.getDbManager().deleteLyokoWarrior(event.getAuthor().getLongID());
                    Main.getLyokoWarriors().remove(warrior);
                    client.getDispatcher().unregisterListener(this);
                    Main.getObjectManager().removeRLW(this);
                    future.cancel(true);
                }
            }
        }
    }

    private void start() {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        future = service.scheduleAtFixedRate(() -> {
            message.edit("LyokoWarrior reset cancelled, user took longer than 60 seconds");
            Main.getObjectManager().removeRLW(this);
            client.getDispatcher().unregisterListener(this);
            future.cancel(false);
        },1,1, TimeUnit.MINUTES);
    }

    public IUser getUser() {
        return warrior.getUser();
    }
}
