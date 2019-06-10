package mulley.sky.lyokobot.Commands.Commands.Utility;

import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class Feedback extends CommandCore {
    public Feedback() {
        commandName = "Feedback";
        helpMessage = "Send feedback, suggestions or bug reports directly to the bot owner";
        Usage = "feedback <message>";
    }

    public boolean executeCommand(MessageReceivedEvent event,String[] argsArray) {
        if(argsArray.length>5) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorName("Feedback");
            builder.withDescription("Guild: " + event.getGuild().getName()+" | Guild Owner: "+event.getGuild().getOwner().getName());
            builder.appendField("User: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getMessage().getContent().replace(ObjectManager.getGuild(event.getGuild().getLongID()).getPrefix()+"feedback", ""), false);
            event.getClient().getApplicationOwner().getOrCreatePMChannel().sendMessage(builder.build());
            event.getChannel().sendMessage("Feedback submitted, please note depending on your message my creator could get in touch for more information. Keep an eye out for `"+event.getClient().getApplicationOwner().getName()+"#"+event.getClient().getApplicationOwner().getDiscriminator()+"` adding you as a friend.");
        } else {
            event.getChannel().sendMessage("Your message is a bit too short, please add a bit more to your message (This is to prevent spam or trash mail)");
        }
        return true;
    }
}
