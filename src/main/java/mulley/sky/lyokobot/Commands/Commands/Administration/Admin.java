package mulley.sky.lyokobot.Commands.Commands.Administration;

import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.Logic.Objects.Guild;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Admin extends CommandCore {
    public Admin () {
        commandName = "Admin";
        helpMessage = "Allows Server Owners to change the bot prefix and blacklist users";
        Usage = "admin <blacklist/prefix> <newprefix/taggedplayer>";
        serverOwner = true;
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argArray) {
        Guild guild = ObjectManager.getGuild(event.getGuild().getLongID());
        if(argArray.length!=3) { argsNotFound(event);}
        if(argArray[1].equalsIgnoreCase("blacklist") || argArray[1].equalsIgnoreCase("prefix")) {
            if(argArray[1].equalsIgnoreCase("prefix")) {
                if(argArray[2].length()>5) {
                    event.getChannel().sendMessage("Prefix is too long, please choose one shorter or equal to 5 characters");
                    return true;
                }
                guild.setPrefix(argArray[2]);
                event.getChannel().sendMessage("Prefix set successfully");
            } else {
                IUser user = null;
                try {
                    long id = Long.parseLong(argArray[2].replaceAll("\\D+",""));
                    user = event.getMessage().getClient().getUserByID(id);
                } catch(Exception e) {
                    argsNotFound(event);
                    return false;
                }
                if(user.equals(event.getAuthor())) {
                    event.getChannel().sendMessage("You can't blacklist yourself! You're the Guild Owner!");
                    return true;
                }
                if(guild.getBlacklisted().contains(user)) {
                    guild.removeBlacklist(user);
                    event.getChannel().sendMessage("User has been removed from the blacklist");
                } else {
                    guild.addBlacklist(user);
                    event.getChannel().sendMessage("User has been added to the blacklist");
                }
            }
        } else {
            argsNotFound(event);
        }
        return true;
    }
}
