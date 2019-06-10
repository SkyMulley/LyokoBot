package mulley.sky.lyokobot.Logic.Runnables;

import mulley.sky.lyokobot.Commands.MessageListener;
import mulley.sky.lyokobot.Logic.Enums.CLASS;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CreateLyokoWarrior {
    private IUser user;
    private MessageReceivedEvent cli;
    private IMessage message;
    private ScheduledFuture future;
    public CreateLyokoWarrior(IUser user, MessageReceivedEvent cli) {
        this.user = user;
        this.cli = cli;
        start(user,cli);
        cli.getClient().getDispatcher().registerListener(this);
    }
    private void start(IUser user, MessageReceivedEvent cli) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName("Register a new Lyoko Warrior");
        builder.withAuthorIcon(cli.getClient().getOurUser().getAvatarURL());
        builder.withDescription("Welcome to the Lyoko Warrior registration process, this process just requires you to pick your class. Then you'll be good to go, here are your choices. When you pick one just reply back with its name.\n**MAKE SURE YOU PICK RIGHT, ONCE YOU PICK YOUR CLASS THERE IS NO CHANGING IT UNLESS YOU RESET YOUR LYOKOWARRIOR**");
        builder.withColor(255,165,0);
        for(CLASS classs : CLASS.values()) {
            builder.appendField(classs.getName(),"Primary Attack: "+classs.getPrimaryAttack()+"\nSecondary Attack: "+classs.getSecondaryAttack()+"\nDefense: "+classs.getDefense(),false);
        }
        cli.getChannel().sendMessage(cli.getAuthor().mention());
        message = cli.getChannel().sendMessage(builder.build());
        start();
    }

    @EventSubscriber
    public void checkMessages(MessageReceivedEvent event) {
        if(event.getChannel().equals(cli.getChannel())) {
            if(event.getAuthor().equals(cli.getAuthor())) {
                for (CLASS classs : CLASS.values()) {
                    if (event.getMessage().getContent().equalsIgnoreCase(classs.getName())) {
                        Main.getLyokoWarriors().add(new LyokoWarrior(event.getAuthor(), classs, 1, 0, 0, 0, 0, 0,0));
                        Main.getDbManager().createLW(event.getAuthor().getLongID(), classs.getName());
                        message.edit("Your LyokoWarrior has been registered successfully!");
                        Main.getObjectManager().removeCLW(this);
                        cli.getClient().getDispatcher().unregisterListener(this);
                        future.cancel(true);
                        try {
                            event.getMessage().delete();
                        } catch (Exception e) {}
                    }
                }
            }
        }
    }

    private void start() {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        future = service.scheduleAtFixedRate(() -> {
            message.edit("LyokoWarrior creation cancelled, user took longer than 60 seconds");
            Main.getObjectManager().removeCLW(this);
            cli.getClient().getDispatcher().unregisterListener(this);
            future.cancel(false);
        },1,1, TimeUnit.MINUTES);
    }
    public IUser getUser() {return user;}
}
