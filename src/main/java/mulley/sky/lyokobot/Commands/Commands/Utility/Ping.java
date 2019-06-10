package mulley.sky.lyokobot.Commands.Commands.Utility;

import mulley.sky.lyokobot.Commands.CommandCore;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Ping extends CommandCore {
    public Ping () {
        commandName = "Ping";
        helpMessage = "Find out the latency between the server and client";
        Usage = "ping";
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argArray) {
        LocalDateTime sentTime = LocalDateTime.ofInstant(event.getMessage().getTimestamp(), ZoneId.systemDefault());
        IMessage probe = event.getChannel().sendMessage("Waiting for reply..");
        LocalDateTime repliedTime = LocalDateTime.ofInstant(probe.getTimestamp(), ZoneId.systemDefault());
        long ping = Duration.between(sentTime,repliedTime).toMillis();
        probe.edit(String.format("Pong! %s (%d ms)",event.getAuthor().mention(),ping));
        return true;
    }
}