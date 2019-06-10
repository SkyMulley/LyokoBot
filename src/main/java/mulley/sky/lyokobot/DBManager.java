package mulley.sky.lyokobot;

import mulley.sky.lyokobot.Logic.Enums.CLASS;
import mulley.sky.lyokobot.Logic.Objects.Guild;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    Connection c = null;
    Statement stmt = null;
    IDiscordClient cli;
    DBManager(IDiscordClient client) {
        try {
            this.cli = client;
            c = DriverManager.getConnection("jdbc:sqlite:LyokoDB.db");
            stmt = c.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Guilds (GuildID bigint(20), Prefix VARCHAR (5),Blacklisted varchar(2000))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Players (UserID bigint(20), Class varchar(10),Level int, Points int, PrimaryAttack int, SecondaryAttack int, Defense int, XP int, Luck int)");
            System.out.println("Database Connection Complete");
        } catch (Exception e) {
            System.out.println("Something has gone wrong");
            e.printStackTrace();
        }
    }

    public void createLW(long id, String classs) {
        try {
            stmt = c.createStatement();
            stmt.execute("INSERT INTO Players (userid,class,level,points,primaryattack,secondaryattack,defense,xp,luck) VALUES ("+id+",'"+classs+"',1,0,0,0,0,0,0)");
        }catch (Exception e) {
            System.out.println("Something has gone wrong");
            e.printStackTrace();
        }
    }

    public void createGuild(long guildid) {
        try {
            stmt = c.createStatement();
            stmt.execute("INSERT INTO Guilds (guildid,prefix) VALUES ("+guildid+",'lb!')");
        } catch (Exception e) {
            System.out.println("Something has gone wrong");
            e.printStackTrace();
        }
    }

    public void deleteGuild(long guildid) {
        try {
            stmt = c.createStatement();
            stmt.execute("DELETE FROM Guilds WHERE GuildID = "+guildid);
        } catch (Exception e) {
            System.out.println("Something has gone wrong");
            e.printStackTrace();
        }
    }

    public void deleteLyokoWarrior(long userid) {
        try {
            stmt = c.createStatement();
            stmt.execute("DELETE FROM Players WHERE UserID = "+userid);
        }catch (Exception e) {
            System.out.println("Something has gone wrong");
            e.printStackTrace();
        }
    }

    public List<Guild> getGuilds() {
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Guilds");
            List<Guild> guilds = new ArrayList<>();
            while(rs.next()) {
                guilds.add(new Guild(cli.getGuildByID(rs.getLong("guildid")),rs.getString("prefix"),cli,rs.getString("blacklisted")));
            }
            return guilds;
        } catch (Exception e) {
            System.out.println("Something has gone wrong");
            e.printStackTrace();
            return null;
        }
    }

    public List<LyokoWarrior> getPlayers() {
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Players");
            List<LyokoWarrior> players = new ArrayList<>();
            while (rs.next()) {
                players.add(new LyokoWarrior(cli.getUserByID(rs.getLong("userid")), CLASS.getClass(rs.getString("class")), rs.getInt("level"), rs.getInt("points"), rs.getInt("primaryattack"), rs.getInt("secondaryattack"), rs.getInt("defense"), rs.getInt("xp"),rs.getInt("luck")));
            }
            return players;
        }catch (Exception e) {
            System.out.println("Something has gone wrong");
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet runQuery(String statement) {
        try {
            stmt = c.createStatement();
            System.out.println("Query: "+statement+" was just run");
            return stmt.executeQuery(statement);
        } catch (Exception e) {
            return null;
        }
    }
}