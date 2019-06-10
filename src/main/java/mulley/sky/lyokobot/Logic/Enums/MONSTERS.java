package mulley.sky.lyokobot.Logic.Enums;

import java.util.Random;

public enum MONSTERS {
    KANKRELATS("Kankrelat",1,1,1,100),
    BLOKS("Blok",2,2,2,100),
    HORNETS("Hornet",3,3,4,100),
    KRABS("Krab",4,2,3,100),
    MEGATANKS("Megatank",5,5,3,100),
    MANTAS("Manta",6,3,4,100),
    TARANTULAS("Tarantula",7,4,3,100);

    final String name;
    final int requiredLevel;
    final int attackPoints;
    final int defensePoints;
    int health;

    MONSTERS(String name, int requiredLevel, int attackPoints, int defensePoints, int health) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.attackPoints = attackPoints;
        this.defensePoints = defensePoints;
        this.health = health;
    }

    public int getRequiredLevel() {return requiredLevel;}
    public int getAttackPoints() {return attackPoints;}
    public int getDefensePoints() {return defensePoints;}
    public int getHealth() {return health;}
    public String getName() {return name;}

    public void setHealth(int hp) {health = hp;}

    public static MONSTERS getRandomMonster(int level) {
        for(MONSTERS monsters : MONSTERS.values()) {
            MONSTERS randomMonster = MONSTERS.values()[new Random().nextInt(MONSTERS.values().length)];
            if(randomMonster.requiredLevel<=level) { return randomMonster;}
        }
        return null;
    }
}
