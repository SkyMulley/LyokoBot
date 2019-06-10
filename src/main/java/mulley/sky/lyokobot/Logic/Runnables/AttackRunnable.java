package mulley.sky.lyokobot.Logic.Runnables;

import com.vdurmont.emoji.EmojiManager;
import mulley.sky.lyokobot.Logic.Enums.MONSTERS;
import mulley.sky.lyokobot.Logic.Objects.LyokoWarrior;
import mulley.sky.lyokobot.Logic.Objects.Monster;
import mulley.sky.lyokobot.Main;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AttackRunnable {
    private LyokoWarrior lyokoWarrior;
    private List<Monster> monsters = new ArrayList<>();
    private int amountMonsters = 0;
    private int xpGained = 0;
    private IMessage message;
    private boolean checking = false;
    private ScheduledFuture future;
    public AttackRunnable(LyokoWarrior warrior) {
        this.lyokoWarrior = warrior;
    }

    public void start() {
        try {
            int numberMonsters = new Random().nextInt(2) + 1;
            for (int i = 0; i < numberMonsters; i++) {
                MONSTERS monsters1 = MONSTERS.getRandomMonster(lyokoWarrior.getLevel());
                if(monsters1==null) {
                    i = i - 1;
                } else {
                    Monster monster2 = new Monster(monsters1,100);
                    monsters.add(monster2);
                }
            }
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(0, 0, 0);
            builder.withAuthorName(lyokoWarrior.getUser().getName() + "'s Battle");
            builder.withAuthorIcon(lyokoWarrior.getUser().getAvatarURL());
            String monsterType = "\n";
            int monsterNum = 0;
            for (Monster monster1 : monsters) {
                monsterNum = monsterNum + 1;
                monsterType = monsterType + "\n**" + monsterNum + ".** " + monster1.getName() + " | " + monster1.getHealth() + "/100";
            }
            lyokoWarrior.getVirtualizedChannel().sendMessage(lyokoWarrior.getUser().mention());
            builder.withDescription("You are faced with " + numberMonsters + " of XANA's monsters" + monsterType);
            builder.appendField("Your turn", "Health: " + lyokoWarrior.getHP() + "/100", false);
            builder.withFooterText("Tip: Reply with `primary 1` to use a primary attack");
            message = lyokoWarrior.getVirtualizedChannel().sendMessage(builder.build());
            lyokoWarrior.getVirtualizedChannel().getClient().getDispatcher().registerListener(this);
            startChecking();
        }catch (Exception e) {e.printStackTrace();lyokoWarrior.stopAttacking();}
    }

    @EventSubscriber
    public void onMessageReceieved(MessageReceivedEvent event) {
        if(event.getAuthor().equals(lyokoWarrior.getUser())) {
            if(event.getChannel().equals(lyokoWarrior.getVirtualizedChannel())) {
                String[] argArray = event.getMessage().getContent().split(" ");
                EmbedBuilder builder = new EmbedBuilder();
                builder.withColor(0,0,0);
                builder.withAuthorName(lyokoWarrior.getUser().getName()+"'s Battle");
                builder.withAuthorIcon(lyokoWarrior.getUser().getAvatarURL());
                if(argArray[0].equalsIgnoreCase("primary")) {
                    if (argArray.length!=2 || argArray[1].equals("1") || argArray[1].equals("2") || argArray[1].equals("3")) {
                        int monsterVar = Integer.parseInt(argArray[1]) - 1;
                        String recent = "";
                        if(monsters.size()>=monsterVar) {
                            try {
                                Thread.sleep(500);
                                event.getMessage().delete();
                            } catch (Exception e) {}
                            if((new Random().nextInt(3)+1 != 2)) {
                                int damage = (20 + (lyokoWarrior.getPrimaryAttack()*2)) - monsters.get(monsterVar).getDefensePoints()*3;
                                recent = lyokoWarrior.getUser().getName()+" used "+lyokoWarrior.getLWClass().getPrimaryAttack()+" and did "+damage+" damage to "+monsters.get(monsterVar).getName();
                                monsters.get(monsterVar).setHealth(monsters.get(monsterVar).getHealth()-damage);
                                if(monsters.get(monsterVar).getHealth()<=0) {
                                    recent = recent + "\n" +monsters.get(monsterVar).getName()+" was devirtualized";
                                    xpGained = (monsters.get(monsterVar).getAttackPoints() * 10) + xpGained;
                                    monsters.remove(monsterVar);
                                    amountMonsters = amountMonsters + 1;
                                }
                            } else {
                                recent = lyokoWarrior.getUser().getName()+" used "+lyokoWarrior.getLWClass().getPrimaryAttack()+" on "+monsters.get(monsterVar).getName() + " but missed!";
                            }
                        } else {
                            event.getMessage().addReaction(EmojiManager.getForAlias("interrobang"));
                            return;
                        }

                        for(Monster monster : monsters) {
                            if ((new Random().nextInt(3) + 1) == 2) {
                                if (lyokoWarrior.loseHP((monster.getAttackPoints() * 10) - lyokoWarrior.getDefense()*2)) {
                                    recent = recent + "\n" + monster.getName() + " hit you and dealt " +((monster.getAttackPoints() * 10) - lyokoWarrior.getDefense()*2)+" damage!";
                                    checking = true;
                                    stopAttacking(false);
                                    builder.withDescription(recent+"\n\n**You have lost all your life points and have been devirtualized...**");
                                    String monsterType = "\n";
                                    int monsterNum = 0;
                                    for (Monster monster1 : monsters) {
                                        monsterNum = monsterNum + 1;
                                        monsterType = monsterType + "\n**" + monsterNum + ".** " + monster.getName() + " | " + monster.getHealth() + "/100";
                                    }
                                    builder.appendField("Final Monster Stats",monsterType,false);
                                    builder.appendField("Monsters Defeated",""+amountMonsters,false);
                                    builder.appendField("XP Gained",""+xpGained,false);
                                    builder.withColor(255,0,0);
                                    if(lyokoWarrior.addXP(xpGained)) {
                                        lyokoWarrior.getVirtualizedChannel().sendMessage(lyokoWarrior.getUser().mention()+" **Level Up!** "+(lyokoWarrior.getLevel()-1)+" >> "+lyokoWarrior.getLevel()+"\nYou earned a skill point! Use it to increase your stats!");
                                    }
                                    message.edit(builder.build());
                                    return;
                                }
                                recent = recent + "\n" + monster.getName() + " hit you and dealt " +monster.getAttackPoints()*10+" damage!";
                            } else {
                                recent = recent + "\n" + monster.getName() + " shot at you but missed.";
                            }
                        }
                        if(monsters.size()!=0) {
                            getEmbed(recent);
                        } else {
                            builder.appendField("Battle Over","You won!",false);
                            builder.appendField("Monsters Defeated",""+amountMonsters,true);
                            builder.appendField("XP Gained",""+xpGained,true);
                            builder.withColor(0,255,0);
                            message.edit(builder.build());
                            if(lyokoWarrior.addXP(xpGained)) {
                                lyokoWarrior.getVirtualizedChannel().sendMessage(lyokoWarrior.getUser().mention()+" **Level Up!** "+(lyokoWarrior.getLevel()-1)+" >> "+lyokoWarrior.getLevel()+"\nYou earned a skill point! Use it to increase your stats!");
                            }
                            stopAttacking(false);
                        }
                    } else {
                        event.getMessage().addReaction(EmojiManager.getForAlias("interrobang"));
                        return;
                    }
                }
                if(argArray[0].equalsIgnoreCase("secondary")) {
                    String recent = "";
                    try {
                        Thread.sleep(500);
                        event.getMessage().delete();
                    }catch (Exception e){}
                    for(Monster monster : monsters) {
                        if ((new Random().nextInt(3) + 1 != 2)) {
                            int damage = (15 + (lyokoWarrior.getSecondaryAttack()*2) - monster.getDefensePoints()*3);
                            recent = lyokoWarrior.getUser().getName()+" used "+lyokoWarrior.getLWClass().getSecondaryAttack()+" and did "+damage+" damage to "+monster.getName();
                            monster.setHealth(monster.getHealth()-damage);
                            if(monster.getHealth()<=0) {
                                recent = recent + "\n" +monster.getName()+" was devirtualized";
                                xpGained  = (monster.getAttackPoints()*10) + xpGained;
                                monsters.remove(monster);
                                amountMonsters = amountMonsters + 1;
                            }
                        } else {
                            recent = lyokoWarrior.getUser().getName()+" used "+lyokoWarrior.getLWClass().getSecondaryAttack()+" on "+monster.getName()+" but missed!";
                        }
                    }
                    for(Monster monster : monsters) {
                        if ((new Random().nextInt(3) + 1) == 2) {
                            if (lyokoWarrior.loseHP((monster.getAttackPoints() * 10) - lyokoWarrior.getDefense()*2)) {
                                recent = recent + "\n" + monster.getName() + " hit you and dealt " +((monster.getAttackPoints() * 10) - lyokoWarrior.getDefense()*2)+" damage!";
                                checking = true;
                                stopAttacking(false);
                                builder.withDescription(recent+"\n\n**You have lost all your life points and have been devirtualized...**");
                                String monsterType = "\n";
                                int monsterNum = 0;
                                for (Monster monster1 : monsters) {
                                    monsterNum = monsterNum + 1;
                                    monsterType = monsterType + "\n**" + monsterNum + ".** " + monster.getName() + " | " + monster.getHealth() + "/100";
                                }
                                builder.appendField("Final Monster Stats",monsterType,false);
                                builder.appendField("Monsters Defeated",""+amountMonsters,false);
                                builder.appendField("XP Gained",""+xpGained,false);
                                builder.withColor(255,0,0);
                                if(lyokoWarrior.addXP(xpGained)) {
                                    lyokoWarrior.getVirtualizedChannel().sendMessage(lyokoWarrior.getUser().mention()+" **Level Up!** "+(lyokoWarrior.getLevel()-1)+" >> "+lyokoWarrior.getLevel()+"\nYou earned a skill point! Use it to increase your stats!");
                                }
                                message.edit(builder.build());
                                return;
                            }
                            recent = recent + "\n" + monster.getName() + " hit you and dealt " +monster.getAttackPoints()*10+" damage!";
                        } else {
                            recent = recent + "\n" + monster.getName() + " shot at you but missed.";
                        }
                    }
                    if(monsters.size()!=0) {
                        getEmbed(recent);
                    } else {
                        builder.appendField("Battle Over","You won!",false);
                        builder.appendField("Monsters Defeated",""+amountMonsters,true);
                        builder.appendField("XP Gained",""+xpGained,true);
                        builder.withColor(0,255,0);
                        message.edit(builder.build());
                        if(lyokoWarrior.addXP(xpGained)) {
                            lyokoWarrior.getVirtualizedChannel().sendMessage(lyokoWarrior.getUser().mention()+" **Level Up!** "+(lyokoWarrior.getLevel()-1)+" >> "+lyokoWarrior.getLevel()+"\nYou earned a skill point! Use it to increase your stats!");
                        }
                        stopAttacking(false);
                    }
                }
            }
        }
    }

    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    private void startChecking() {
        future = service.scheduleAtFixedRate(() -> {
            String recent = "";
            for(Monster monster : monsters) {
                if((new Random().nextInt(3)+1) == 2) {
                    if(lyokoWarrior.loseHP((monster.getAttackPoints()*10) - lyokoWarrior.getDefense()*2)) {
                        recent = recent + "\n" + monster.getName() + " hit you and dealt " +((monster.getAttackPoints()*10) - lyokoWarrior.getDefense()*2)+" damage!";
                        checking = true;
                        stopAttacking(false);
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.withAuthorName(lyokoWarrior.getUser().getName() + "'s Battle");
                        builder.withAuthorIcon(lyokoWarrior.getUser().getAvatarURL());
                        builder.withColor(255,0,0);
                        builder.withDescription(recent+"\n\n**You have lost all your life points and have been devirtualized...**");
                        String monsterType = "\n";
                        int monsterNum = 0;
                        for (Monster monster1 : monsters) {
                            monsterNum = monsterNum + 1;
                            monsterType = monsterType + "\n**" + monsterNum + ".** " + monster.getName() + " | " + monster.getHealth() + "/100";
                        }
                        builder.appendField("Final Monster Stats",monsterType,false);
                        builder.appendField("Monsters Defeated",""+amountMonsters,true);
                        builder.appendField("XP Gained",""+xpGained,true);
                        message.edit(builder.build());
                        if(lyokoWarrior.addXP(xpGained)) {
                            lyokoWarrior.getVirtualizedChannel().sendMessage(lyokoWarrior.getUser().mention()+" **Level Up!** "+(lyokoWarrior.getLevel()-1)+" >> "+lyokoWarrior.getLevel()+"\nYou earned a skill point! Use it to increase your stats!");
                        }
                        return;
                    }
                    recent = recent + "\n" + monster.getName() + " hit you and dealt " +monster.getAttackPoints()*10+" damage!";
                } else {
                    recent = recent + "\n" + monster.getName() + " shot at you but missed.";
                }
            }
            getEmbed(recent);
        },30,30, TimeUnit.SECONDS);
    }

    public LyokoWarrior getLyokoWarrior() {return lyokoWarrior;}

    public void stopAttacking(boolean doEmbed) {
        lyokoWarrior.getVirtualizedChannel().getClient().getDispatcher().unregisterListener(this);
        lyokoWarrior.softStopAttacking();
        future.cancel(false);
        if(doEmbed && !checking) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.withAuthorName(lyokoWarrior.getUser().getName() + "'s Battle");
                builder.withAuthorIcon(lyokoWarrior.getUser().getAvatarURL());
                builder.withColor(255, 0, 0);
                builder.withDescription("You were devirtualized");
                String monsterType = "\n";
                int monsterNum = 0;
                for (Monster monster1 : monsters) {
                    monsterNum = monsterNum + 1;
                    monsterType = monsterType + "\n**" + monsterNum + ".** " + monster1.getName() + " | " + monster1.getHealth() + "/100";
                }
                builder.appendField("Final Monster Stats", monsterType, false);builder.appendField("Remaining Lifepoints",""+lyokoWarrior.getHP(),true);
            builder.appendField("Monsters Defeated",""+amountMonsters,true);
            builder.appendField("XP Gained",""+xpGained,true);
                message.edit(builder.build());
            if(lyokoWarrior.addXP(xpGained)) {
                lyokoWarrior.getVirtualizedChannel().sendMessage(lyokoWarrior.getUser().mention()+" **Level Up!** "+(lyokoWarrior.getLevel()-1)+">>"+lyokoWarrior.getLevel()+"\nYou earned a skill point! Use it to increase your stats!");
            }
        }
    }

    public void getEmbed(String recent) {
        String monsterType = "\n";
        int monsterNum = 0;
        for (Monster monster : monsters) {
            monsterNum = monsterNum + 1;
            monsterType = monsterType + "\n**" + monsterNum + ".** " + monster.getName() + " | " + monster.getHealth() + "/100";
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName(lyokoWarrior.getUser().getName() + "'s Battle");
        builder.withAuthorIcon(lyokoWarrior.getUser().getAvatarURL());
        builder.withDescription(recent + monsterType);
        builder.appendField("Your turn", "Health: " + lyokoWarrior.getHP() + "/100", false);
        builder.withFooterText("Tip: Reply with `primary 1` to use a primary attack");
        message.edit(builder.build());
    }
}
