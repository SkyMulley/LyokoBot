package mulley.sky.lyokobot.Commands.Commands.LWManagement;

import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.Logic.Runnables.ResetLyokoWarrior;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class Reset extends CommandCore {
    public Reset() {
        commandName = "Reset";
        helpMessage = "Reset your Lyoko Warrior so you can create a new one";
        Usage = "reset";
        addAlias("delete");
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argsArray) {
        if(!ObjectManager.isLyokoWarrior(event.getAuthor())) {
            event.getChannel().sendMessage("You do not have a LyokoWarrior created, so nothing will be reset!");
            return false;
        }
        if(Main.getObjectManager().isResettingLW(event.getAuthor())) {
            event.getChannel().sendMessage("You are already resetting your LyokoWarrior, finish that one first");
            return false;
        }
        LyokoWarrior lyokoWarrior = ObjectManager.getLyokoWarrior(event.getAuthor());
        if(lyokoWarrior.getVirtualized()) {
            event.getChannel().sendMessage("You're currently virtualized, devirtualize first then run this command again");
            return false;
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(255,0,0);
        builder.withAuthorName("Resetting LyokoWarrior");
        builder.withDescription("You are about to reset your Lyoko Warrior, this is a destructive action and it can never be recovered after reset. Reply with the message `CONFIRM` to reset your LyokoWarrior");
        builder.appendField("Class",lyokoWarrior.getLWClass().getName(),true);
        builder.appendField("Level",""+lyokoWarrior.getLevel(),true);
        IMessage message = event.getChannel().sendMessage(builder.build());
        Main.getObjectManager().addRLW(new ResetLyokoWarrior(lyokoWarrior,event.getChannel(),event.getClient(),message));
        return true;
    }
}
