package mulley.sky.lyokobot.Commands;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandCore {
    protected String commandName;
    protected String helpMessage;
    protected String Usage;
    protected boolean helpViewable = true;
    protected boolean adminOnly = false;
    protected boolean serverOwner = false;
    protected List<String> commandAliases = new ArrayList<>();

    public String getCommandName() {return commandName;}
    public String getHelpMessage() {return helpMessage;}
    public String getUsage() {return Usage;}
    public boolean isHelpViewable() {return helpViewable;}
    public boolean isAdminOnly() {return adminOnly;}
    public boolean isServerOwner() {return serverOwner;}
    public List<String> getAliases() {return commandAliases;}

    public void addAlias(String command) {commandAliases.add(command);}

    public boolean isAlias(String command) {
        if(commandAliases.contains(command)) {
            return true;
        }
        return false;
    }

    public boolean executeCommand(MessageReceivedEvent event, String[] argArray) {return true;}

    public void argsNotFound(MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withDescription("You haven't supplied the right args in your request. Check with the help command if you are unsure of the usage of this command.");
        builder.withFooterText("Not enough args");
        RequestBuffer.request(() -> event.getMessage().getChannel().sendMessage(builder.build()));
    }
}
