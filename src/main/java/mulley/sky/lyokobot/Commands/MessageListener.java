package mulley.sky.lyokobot.Commands;

import mulley.sky.lyokobot.Commands.Commands.Administration.Admin;
import mulley.sky.lyokobot.Commands.Commands.Administration.Query;
import mulley.sky.lyokobot.Commands.Commands.LWManagement.Register;
import mulley.sky.lyokobot.Commands.Commands.LWManagement.Reset;
import mulley.sky.lyokobot.Commands.Commands.LWManagement.Upgrade;
import mulley.sky.lyokobot.Commands.Commands.LWUtility.Materialise;
import mulley.sky.lyokobot.Commands.Commands.LWUtility.Virtualize;
import mulley.sky.lyokobot.Commands.Commands.Utility.Feedback;
import mulley.sky.lyokobot.Commands.Commands.Utility.Help;
import mulley.sky.lyokobot.Commands.Commands.Utility.Info;
import mulley.sky.lyokobot.Commands.Commands.Utility.Ping;
import mulley.sky.lyokobot.DBManager;
import mulley.sky.lyokobot.Logic.Objects.Guild;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageListener {
    private List<CommandCore> commandList = new ArrayList<>();
    private DBManager manager;

    public MessageListener() {
        getCommands();
    }

    @EventSubscriber
    public void onMessageRecieved(MessageReceivedEvent event) {
        if(!event.getChannel().isPrivate() || !event.getAuthor().isBot()) {
            Guild guild = new Guild();
            guild = ObjectManager.getGuild(event.getGuild().getLongID());
            if(guild.getBlacklisted().contains(event.getAuthor())) { return;}
            if(event.getAuthor().isBot()) {return;}
            if(event.getMessage().getContent().startsWith("<@"+event.getClient().getOurUser().getLongID()+">")) {
                event.getChannel().sendMessage("Hey there!\nMy prefix for this guild is: `"+guild.getPrefix()+"`\nUse `"+guild.getPrefix()+"help` for commands and their usages or `"+guild.getPrefix()+"tutorial` for more information on how to actually use me");
                return;
            }
            String[] argArray = event.getMessage().getContent().split(" ");
            if (argArray.length == 0 || !argArray[0].startsWith(guild.getPrefix())) {
                return;
            }
            String commandStr = argArray[0].substring(guild.getPrefix().length());
            for (CommandCore command : commandList) {
                if (commandStr.toLowerCase().contains(command.getCommandName().toLowerCase()) || command.isAlias(commandStr.toLowerCase())) {
                    if(command.isServerOwner()) {
                        if(!event.getAuthor().equals(event.getGuild().getOwner())) {
                            return;
                        }
                    }
                    if(command.isAdminOnly()) {
                        if(!event.getAuthor().equals(event.getClient().getApplicationOwner())) {
                            return;
                        }
                    }
                    try { Thread.sleep(500);}catch (Exception e){}
                    System.out.println(event.getAuthor().getName() + "(" + event.getAuthor().getLongID() + ") ran command " + command.getCommandName() + " on Guild " + event.getGuild().getName() + "(" + event.getGuild().getLongID() + ")");
                    command.executeCommand(event, argArray);
                }
            }
        }
    }

    private void getCommands() {
        commandList.add(new Ping());
        commandList.add(new Query());
        commandList.add(new Admin());
        commandList.add(new Info());
        commandList.add(new Register());
        commandList.add(new Reset());
        commandList.add(new Virtualize());
        commandList.add(new Materialise());
        commandList.add(new Upgrade());
        commandList.add(new Feedback());

        commandList.add(new Help(commandList));
    }
}
