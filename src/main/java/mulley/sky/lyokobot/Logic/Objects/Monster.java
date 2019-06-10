package mulley.sky.lyokobot.Logic.Objects;

import mulley.sky.lyokobot.Logic.Enums.MONSTERS;

public class Monster {
    private MONSTERS monster;
    private int health;

    public Monster(MONSTERS monster, int health) {
        this.monster = monster;
        this.health = health;
    }

    public void setHealth(int hp) {health = hp;}
    public int getHealth() {return health;}
    public String getName() {return monster.getName();}
    public int getAttackPoints() {return monster.getAttackPoints();}
    public int getDefensePoints() {return monster.getDefensePoints();}
}
