package mulley.sky.lyokobot.Commands.Commands.LWUtility;

import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.Presence;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.EmbedBuilder;

public class Virtualize extends CommandCore {
    public Virtualize() {
        commandName = "Virtualize";
        helpMessage = "Virtualize yourself onto the Guilds Lyoko.";
        Usage = "virtualize";
        addAlias("virtualise");
        addAlias("start");
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argArray) {
        if(!ObjectManager.isLyokoWarrior(event.getAuthor())) {
            event.getChannel().sendMessage("You do not have a registered LyokoWarrior");
            return false;
        }
        LyokoWarrior warrior = ObjectManager.getLyokoWarrior(event.getAuthor());
        if(warrior.getVirtualized()) {
            event.getChannel().sendMessage("You are already virtualized on Guild "+warrior.getVirtualizedGuild().getGuild().getName());
            return false;
        }
        if(warrior.getHP()!=100) {
            event.getChannel().sendMessage("You are currently on cooldown, please wait another "+((100-warrior.getHP()))+" minutes before you can be virtualized again");
            return false;
        }
        warrior.setVirtualized(event.getChannel(),true);
        ObjectManager.getGuild(event.getGuild().getLongID()).addVirtualizedLW(warrior);
        Main.getVirtualizedLyokoWarriors().add(warrior);
        event.getChannel().sendMessage("You are now Virtualized on this Guild, keep an eye on this channel for battle notifications");
        return true;
    }
}
