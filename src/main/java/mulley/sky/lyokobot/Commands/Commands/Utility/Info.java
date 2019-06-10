package mulley.sky.lyokobot.Commands.Commands.Utility;

import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;

public class Info extends CommandCore {
    public Info() {
        commandName = "Info";
        helpMessage = "Gets the Lyoko Warrior information about you or another player";
        Usage = "info (User)";
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event,String[] argsArray) {
        IUser user;
        if(argsArray.length==2) {
            if(argsArray[1].contains("@")) {
                long id = Long.parseLong(argsArray[1].replaceAll("\\D+", ""));
                user = event.getClient().getUserByID(id);
            } else {
                List<IUser> users = new ArrayList<>();
                for(IUser user1 : event.getClient().getUsers()) {
                    if(user1.getName().startsWith(argsArray[1])) {
                        users.add(user1);
                    }
                }
                if(users.size()==0) {
                    event.getChannel().sendMessage("No users with that name were found (Name is caps-sensitive)");
                    return false;
                }
                if(users.size()>1) {
                    event.getChannel().sendMessage("Multiple results found, please enter the users full name or alternatively tag them");
                    return false;
                }
                user = users.get(0);
            }
        } else {
            user = event.getAuthor();
        }
        if(user.isBot()) {
            event.getChannel().sendMessage("Bots aren't able to be Lyoko Warriors, choose a user instead");
            return false;
        }
        if(!ObjectManager.isLyokoWarrior(user)) {
            event.getChannel().sendMessage("This player doesn't have a registered Lyoko Warrior and so has no information to show!");
            return false;
        }
        LyokoWarrior lyokoWarrior = ObjectManager.getLyokoWarrior(user);
        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName(user.getName()+"'s LyokoWarrior Information");
        builder.withColor(0,255,0);
        builder.appendField("Class",lyokoWarrior.getLWClass().getName(),true);
        builder.appendField("Level - XP",lyokoWarrior.getLevel()+" ("+lyokoWarrior.getXP()+"/"+(lyokoWarrior.getLevel()*100/2)+")",true);
        builder.appendField("Primary Attack Points",""+lyokoWarrior.getPrimaryAttack(),true);
        builder.appendField("Secondary Attack Points",""+lyokoWarrior.getSecondaryAttack(),true);
        builder.appendField("Defense Points",""+lyokoWarrior.getDefense(),true);
        builder.appendField("Luck Points",""+lyokoWarrior.getLuck(),false);
        if(lyokoWarrior.getVirtualized()) {
            builder.appendField("Virtualized on",lyokoWarrior.getVirtualizedGuild().getGuild().getName(),true);
            builder.appendField("Current Lifepoints",""+lyokoWarrior.getHP(),true);
        }
        builder.withAuthorIcon(lyokoWarrior.getUser().getAvatarURL());
        event.getChannel().sendMessage(builder.build());
        return true;
    }
}
