package mulley.sky.lyokobot.Commands.Commands.Administration;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import mulley.sky.lyokobot.Commands.CommandCore;
import mulley.sky.lyokobot.Logic.Objects.Guild;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Query extends CommandCore {
    public Query () {
        commandName = "Query";
        helpMessage = "Run a query on the SQL database";
        Usage = "query <string>";
        adminOnly = true;
        helpViewable = false;
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event, String[] argArray) {
        try {
            Guild guild = ObjectManager.getGuild(event.getGuild().getLongID());
            ResultSet rs = Main.getDbManager().runQuery(event.getMessage().getContent().substring(5 + guild.getPrefix().length()));
            event.getMessage().addReaction(EmojiManager.getForAlias("ok"));
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            String embed = "";
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) embed = embed + (",  ");
                    String columnValue = rs.getString(i);
                    embed = embed + (columnValue + " " + rsmd.getColumnName(i) + "");
                }
                embed = embed + "\n\n";
            }
            event.getChannel().sendMessage("```"+embed+"```");
            return true;
        }catch (Exception e) {
            return true;
        }
    }
}
