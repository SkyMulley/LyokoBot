package mulley.sky.lyokobot.Commands.Commands.LWManagement;

import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.Logic.Runnables.CreateLyokoWarrior;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class Register extends CommandCore {
    public Register () {
        commandName = "Register";
        helpMessage = "Create your LyokoWarrior to start playing";
        Usage = "register";
        addAlias("create");
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argArray) {
        if(ObjectManager.isLyokoWarrior(event.getAuthor())) {
            event.getChannel().sendMessage("You already have an Avatar registered, you can't have multiple!");
            return false;
        }
        if(Main.getObjectManager().isMakingLW(event.getAuthor())) {
            event.getChannel().sendMessage("You're already in the process of registering an avatar, check your DM's to finish it off");
            return false;
        }
        Main.getObjectManager().addCLW(new CreateLyokoWarrior(event.getAuthor(),event));
        return true;
    }
}
