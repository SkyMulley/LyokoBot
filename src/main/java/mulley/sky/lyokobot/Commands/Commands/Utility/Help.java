package mulley.sky.lyokobot.Commands.Commands.Utility;

import com.vdurmont.emoji.EmojiManager;
import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;

public class Help extends CommandCore {
    private List<CommandCore> GC;
    public Help(List<CommandCore> GC) {
        commandName = "Help";
        helpViewable = false;
        this.GC = GC;
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argsArray) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName("Help Menu");
        builder.withAuthorIcon(event.getClient().getOurUser().getAvatarURL());
        builder.withColor(255, 255, 255);
        String prefix = ObjectManager.getGuild(event.getGuild().getLongID()).getPrefix();
        if(argsArray.length==2) {
            boolean found = false;
            for(CommandCore GG : GC) {
                    if (GG.getCommandName().equalsIgnoreCase(argsArray[1]) || GG.isAlias(argsArray[1])) {
                        if(GG.isHelpViewable()) {
                            builder.withDescription("More help for the command `" + GG.getCommandName() + "`");
                            builder.appendField("Help Message", GG.getHelpMessage(), false);
                            builder.appendField("Usage", prefix+GG.getUsage(), false);
                            String message = "`";
                            for (String alias : GG.getAliases()) {
                                message = message + alias + "\n";
                            }
                            message = message + "`";
                            builder.appendField("Command Aliases", message, false);
                            event.getChannel().sendMessage(builder.build());
                            return true;
                        } else {
                            event.getChannel().sendMessage("You don't have permission to look at the advanced help for this command");
                            return false;
                        }
                    }
            }
            event.getChannel().sendMessage("No commands where found by that name. Please check your spelling and try again");
            return false;
        }else  {
            String message = "";
            for (CommandCore GG : GC) {
                if (GG.isHelpViewable()) {
                    message = message + "\n**" + GG.getCommandName() + " - **" + GG.getHelpMessage() + "\nUsage: " + prefix + GG.getUsage();
                }
            }
            builder.appendField("Commands", message, false);
            event.getChannel().sendMessage(builder.build());
        }
        return true;
    }
}
