package mulley.sky.lyokobot.Logic.Objects;

import mulley.sky.lyokobot.DBManager;
import mulley.sky.lyokobot.Logic.Enums.CLASS;
import mulley.sky.lyokobot.Logic.Runnables.AttackRunnable;
import mulley.sky.lyokobot.Main;
import mulley.sky.lyokobot.ObjectManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class LyokoWarrior {
    private IUser user;
    private CLASS classs;
    private int level;
    private int points;
    private int primaryAttack;
    private int secondaryAttack;
    private int defense;
    private int xp;
    private boolean virtualized;
    private IChannel virtualizedChannel;
    private Guild virtualizedGuild;
    private int hp = 100;
    private int xpgained = 0;
    private int monstersKilled = 0;
    private boolean recentlyAttacked = false;
    private AttackRunnable runnable;
    private int luck;

    public LyokoWarrior(IUser user,CLASS classs, int level, int points, int primaryAttack, int secondaryAttack, int defense,  int xp, int luck) {
        this.user = user;
        this.classs = classs;
        this.level = level;
        this.points = points;
        this.primaryAttack = primaryAttack;
        this.secondaryAttack = secondaryAttack;
        this.defense = defense;
        this.xp = xp;
        this.luck = luck;
    }

    public IUser getUser() {return this.user;}
    public CLASS getLWClass() {return this.classs;}
    public int getLevel() {return this.level; }
    public int getXP() {return this.xp;}
    public int getPrimaryAttack() {return this.primaryAttack;}
    public int getSecondaryAttack() {return this.secondaryAttack;}
    public int getDefense() {return this.defense;}
    public boolean getVirtualized() {return virtualized;}
    public IChannel getVirtualizedChannel() {return virtualizedChannel;}
    public Guild getVirtualizedGuild() {return virtualizedGuild;}
    public int getHP() {return hp;}
    public int getPoints() {return points;}
    public int getLuck() {return luck;}

    public int getMonstersKilled() {return monstersKilled;}
    public int getXpgained() {return xpgained;}

    public void setVirtualized(IChannel guild,boolean toggle) {
        virtualized = toggle;
        virtualizedChannel = guild;
        virtualizedGuild = ObjectManager.getGuild(guild.getGuild().getLongID());
        if (virtualized) {
            xpgained = 0;
            monstersKilled = 0;
            recentlyAttacked = false;
        } else {
            if(isAttacking()) {runnable.stopAttacking(true);}
            runnable = null;
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorName("Final Virtualization Figures");
            builder.withAuthorIcon(user.getAvatarURL());
            builder.appendField("Monsters Killed",monstersKilled+"",true);
            builder.appendField("XP Gained",xpgained+"",true);
            if(this.hp<0) {this.hp=0;}
            builder.appendField("Final Lifepoints",hp+"",true);
            builder.withColor(0,255,0);
            virtualizedChannel.sendMessage(builder.build());
            Main.getVirtualizedLyokoWarriors().remove(this);
            ObjectManager.getGuild(guild.getGuild().getLongID()).removeVirtualizedLW(this);
        }
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    public void addHP(int hp) {
        this.hp = this.hp + hp;
        if(this.hp > 100) {
            this.hp = 100;
        }
    }

    public boolean loseHP(int hp) {
        this.hp = this.hp - hp;
        if(this.hp<=0) {
            setVirtualized(getVirtualizedChannel(),false);
            this.hp = 0;
            return true;
        }
        return false;
    }

    public boolean addXP(int xps) {
        xp = xps + xp;
        xpgained = xpgained + xps;
        if(xp >= level*100/2) {
            level = level + 1;
            xp = 0;
            points = points + 1;
            Main.getDbManager().runQuery("UPDATE Players SET XP = 0 WHERE UserID = "+user.getLongID());
            Main.getDbManager().runQuery("UPDATE Players SET Level = "+level+" WHERE UserID = "+user.getLongID());
            Main.getDbManager().runQuery("UPDATE Players SET Points = "+points+" WHERE UserID = "+user.getLongID());
            return true;
        }
        Main.getDbManager().runQuery("UPDATE Players SET XP = "+xp+" WHERE UserID = "+user.getLongID());
        return false;
    }

    public void upgradePrimary() {
        points = points - 1;
        Main.getDbManager().runQuery("UPDATE Players SET Points = "+points+" WHERE UserID = "+user.getLongID());
        primaryAttack = primaryAttack + 1;
        Main.getDbManager().runQuery("UPDATE Players SET PrimaryAttack = "+primaryAttack+" WHERE UserID = "+user.getLongID());
    }

    public void upgradeSecondary() {
        points = points - 1;
        Main.getDbManager().runQuery("UPDATE Players SET Points = "+points+" WHERE UserID = "+user.getLongID());
        secondaryAttack = secondaryAttack + 1;
        Main.getDbManager().runQuery("UPDATE Players SET PrimaryAttack = "+secondaryAttack+" WHERE UserID = "+user.getLongID());
    }

    public void upgradeDefense() {
        points = points - 1;
        Main.getDbManager().runQuery("UPDATE Players SET Points = "+points+" WHERE UserID = "+user.getLongID());
        defense = defense + 1;
        Main.getDbManager().runQuery("UPDATE Players SET PrimaryAttack = "+defense+" WHERE UserID = "+user.getLongID());
    }

    public void upgradeLuck() {
        points = points - 1;
        Main.getDbManager().runQuery("UPDATE Players SET Points = "+points+" WHERE UserID = "+user.getLongID());
        luck = luck + 1;
        Main.getDbManager().runQuery("UPDATE Players SET PrimaryAttack = "+luck+" WHERE UserID = "+user.getLongID());
    }

    public void startAttacking(AttackRunnable runnable) {this.runnable = runnable;}
    public void stopAttacking() {runnable.stopAttacking(true);this.runnable=null;}
    public void softStopAttacking() {this.runnable=null;}
    public boolean isAttacking() {if(this.runnable!=null){return true;}else{return false;}}
    public void addMonsters(int monsters) {this.monstersKilled = this.monstersKilled + monsters;}

    public boolean recentlyAttackedToggle() {
        if(recentlyAttacked) {
            recentlyAttacked = false;
            return false;
        } else {
            recentlyAttacked = true;
            return true;
        }
    }
}
