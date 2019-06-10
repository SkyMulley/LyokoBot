package mulley.sky.lyokobot.Logic.Objects;

import mulley.sky.lyokobot.Main;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

public class Guild {
    private IGuild guild;
    private String prefix;
    private List<IUser> users = new ArrayList<>();
    private IDiscordClient cli;
    private List<LyokoWarrior> virtualizedLWs = new ArrayList<>();

    public Guild(IGuild guild, String prefix, IDiscordClient cli, String blacklisted) {
        this.guild = guild;
        this.prefix = prefix;
        this.cli = cli;
        if(blacklisted!=null) {
            if (!blacklisted.equals("")) {
                String[] blacklist = blacklisted.split(",");
                for (String string : blacklist) {
                    users.add(cli.getUserByID(Long.parseLong(string)));
                }
            }
        }
    }
    public Guild() {}

    public String getPrefix() {
        return prefix;
    }
    public IGuild getGuild() {return guild;}
    public List<IUser> getBlacklisted() {return users;}

    public void setPrefix(String prefix) {
        Main.getDbManager().runQuery("UPDATE Guilds SET Prefix = '"+prefix+"' WHERE GuildID = "+guild.getLongID());
        this.prefix = prefix;
    }

    public void addBlacklist(IUser user) {
        users.add(user);
        if(users.size()==0) {
            Main.getDbManager().runQuery("UPDATE Guilds SET Blacklisted = '"+user.getLongID()+"' WHERE GuildID = "+guild.getLongID());
        } else {
            String blacklisted = "";
            int i = 0;
            for(IUser user1 : getBlacklisted()) {
                i = i + 1;
                if(i==getBlacklisted().size()) {
                    blacklisted = blacklisted + user1.getLongID();
                } else {
                    blacklisted = blacklisted + user1.getLongID() + ",";
                }
            }
            Main.getDbManager().runQuery("UPDATE Guilds SET Blacklisted = '"+blacklisted+"' WHERE GuildID = "+guild.getLongID());
        }
    }

    public void removeBlacklist(IUser user) {
        users.remove(user);
        String blacklisted = "";
        int i = 0;
        for(IUser user1 : getBlacklisted()) {
            i = i + 1;
            if(i==getBlacklisted().size()) {
                blacklisted = blacklisted + user1.getLongID();
            } else {
                blacklisted = blacklisted + user1.getLongID() + ",";
            }
        }
        Main.getDbManager().runQuery("UPDATE Guilds SET blacklisted '"+blacklisted+"' WHERE GuildID = "+guild.getLongID());
    }

    public void addVirtualizedLW(LyokoWarrior warrior) {
        virtualizedLWs.add(warrior);
    }

    public void removeVirtualizedLW(LyokoWarrior warrior) {
        virtualizedLWs.remove(warrior);
    }
}
