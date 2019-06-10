package mulley.sky.lyokobot;

import mulley.sky.lyokobot.Logic.Objects.Guild;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Listener {

    @EventSubscriber
    public void onGuildStart(GuildCreateEvent event) {
        for(Guild guild : Main.getRegisteredGuilds()) {
            if(guild.getGuild().equals(event.getGuild())) {return;}
        }
        System.out.println("Bot was invited into Guild "+event.getGuild().getName()+"("+event.getGuild().getLongID()+")");
        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName("LyokoBot");
        builder.withColor(0,255,0);
        builder.withTitle("Hey There!");
        builder.withDescription("Let's get started, the default prefix to use me is `lb!`, so that's `lb!help` to find the rest of my commands.\n You can change this prefix using the Admin command reserved for super Guild Owners! `lb!admin`, along with other stuff like blacklisting users from using me on your server.\nMore information on actually using the bot can be found using `lb!tutorial`, feedback can be given directly to the bot owner using `lb!feedback`\nSee you on Lyoko!");
        builder.withFooterText("Made by Sky#2134");
        builder.withAuthorIcon(event.getClient().getOurUser().getAvatarURL());
        event.getGuild().getOwner().getOrCreatePMChannel().sendMessage(builder.build());
        Main.getDbManager().createGuild(event.getGuild().getLongID());
        Main.getRegisteredGuilds().add(new Guild(event.getGuild(),"lb!",event.getClient(),""));
        System.out.println("Guild successfully registered into system");
    }

    @EventSubscriber
    public void onGuildLeave(GuildLeaveEvent event) {
        System.out.println("Bot was kicked out of Guild "+event.getGuild().getName()+"("+event.getGuild().getLongID()+")");
        Main.getDbManager().deleteGuild(event.getGuild().getLongID());
        Guild delGuild = new Guild();
        for(Guild guild : Main.getRegisteredGuilds()) {
            if(guild.getGuild().equals(event.getGuild())) {
                delGuild = guild;
            }
        }
        Main.getRegisteredGuilds().remove(delGuild);
        System.out.println("Guild successfully removed from system");
    }
}
