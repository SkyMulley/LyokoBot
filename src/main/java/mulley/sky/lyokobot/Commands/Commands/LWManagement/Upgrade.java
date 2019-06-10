package mulley.sky.lyokobot.Commands.Commands.LWManagement;

import com.vdurmont.emoji.EmojiManager;
import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class Upgrade extends CommandCore {
    public Upgrade() {
        commandName = "Upgrade";
        helpMessage = "Use your earned skill points to upgrade your avatar";
        Usage = "upgrade (primary/secondary/defense)";
        addAlias("shop");
        addAlias("buy");
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argsArray) {
        if(!ObjectManager.isLyokoWarrior(event.getAuthor())) {
            event.getChannel().sendMessage("You do not have a registered LyokoWarrior");
            return false;
        }
        LyokoWarrior warrior = ObjectManager.getLyokoWarrior(event.getAuthor());
        if(warrior.getVirtualized()) {
            event.getChannel().sendMessage("You are currently virtualized, you cannot upgrade your Avatar while it is in use");
            return false;
        }
        if(argsArray.length==2) {
            if(warrior.getPoints()==0) {
                event.getChannel().sendMessage("You do not have any points to use for upgrading, level up to gain them!");
                return false;
            }
            if(argsArray[1].equalsIgnoreCase("primary")) {
                warrior.upgradePrimary();
                event.getChannel().sendMessage("Primary Points increased to "+warrior.getPrimaryAttack());
                return true;
            }
            if(argsArray[1].equalsIgnoreCase("secondary")) {
                warrior.upgradeSecondary();
                event.getChannel().sendMessage("Secondary Points increased to "+warrior.getSecondaryAttack());
                return true;
            }
            if(argsArray[1].equalsIgnoreCase("defense")) {
                warrior.upgradeDefense();
                event.getChannel().sendMessage("Defense Points increased to "+warrior.getDefense());
                return true;
            }
            if(argsArray[1].equalsIgnoreCase("luck")) {
                warrior.upgradeLuck();
                event.getChannel().sendMessage("Luck Points increased to "+warrior.getLuck());
                return true;
            }
        }else {
            String prefix = ObjectManager.getGuild(event.getGuild().getLongID()).getPrefix();
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorIcon(event.getClient().getOurUser().getAvatarURL());
            builder.withAuthorName("Upgrade Menu");
            builder.appendField(prefix + "upgrade primary", "Upgrade your Primary Attacks to do more damage\n**Current Primary Points: **" + warrior.getPrimaryAttack(), false);
            builder.appendField(prefix + "upgrade secondary", "Upgrade your Secondary Attacks to do more damage\n**Current Secondary Points: **" + warrior.getSecondaryAttack(), false);
            builder.appendField(prefix + "upgrade defense", "Upgrade your Defense to take less damage\n**Current Defense Points: **" + warrior.getDefense(), false);
            builder.appendField(prefix + "upgrade luck", "Upgrade your luck to increase your chances of landing attacks\n**Current Luck Points: **" + warrior.getLuck(), false);
            builder.withFooterText("Avaliable Skill Points to use: " + warrior.getPoints());
            event.getChannel().sendMessage(builder.build());
        }
        return true;
    }
}
