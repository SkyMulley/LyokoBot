package mulley.sky.lyokobot.Commands.Commands.LWUtility;

import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.EmbedBuilder;

public class Materialise extends CommandCore {
    public Materialise() {
        commandName = "Materialise";
        helpMessage = "Rematerialise from Lyoko.";
        Usage = "materialise";
        addAlias("devirtualize");
        addAlias("devirtualise");
        addAlias("stop");
        addAlias("rematerialise");
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argArray) {
        if(!ObjectManager.isLyokoWarrior(event.getAuthor())) {
            event.getChannel().sendMessage("You do not have a registered LyokoWarrior");
            return false;
        }
        LyokoWarrior warrior = ObjectManager.getLyokoWarrior(event.getAuthor());
        if(!warrior.getVirtualized()) {
            event.getChannel().sendMessage("You are not currently Virtualized!");
            return false;
        }
        warrior.setVirtualized(event.getChannel(),false);
        if(warrior.getHP()==100) {
            event.getChannel().sendMessage("You have returned from Lyoko, please wait 2 minutes before revirtualizing");
            warrior.setHP(98);
        } else {
            event.getChannel().sendMessage("You have returned from Lyoko, you must wait "+((100-warrior.getHP()))+" minutes before being able to revirtualize");
        }
        return true;
    }
}
