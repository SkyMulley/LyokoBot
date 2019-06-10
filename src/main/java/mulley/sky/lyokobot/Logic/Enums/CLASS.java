package mulley.sky.lyokobot.Logic.Enums;

public enum CLASS {
    GUARDIAN("Guardian","Energy Field","Energy Smash","Flight"),
    FELINE("Feline","Lazer Arrow","Lazer Storm","Shield"),
    GEISHA("Geisha","Single Fan","Dual Fans","Telekenesis"),
    SAMURAI("Samurai","Sword Flash","Triplicate","Super Sprint"),
    WARRIOR("Warrior","Sword Smash","Spinning Top","Super Smoke");


    final String name;
    final String primaryAttack;
    final String secondaryAttack;
    final String defense;
    CLASS(String name, String primaryAttack, String secondaryAttack, String defense) {
        this.name = name;
        this.primaryAttack = primaryAttack;
        this.secondaryAttack = secondaryAttack;
        this.defense = defense;
    }

    public static CLASS getClass(String namo) {
        for(CLASS classs : CLASS.values()) {
            if(namo.equalsIgnoreCase(classs.getName())) {
                return classs;
            }
        }
        return null;
    }

    public String getName() { return name; }
    public String getPrimaryAttack() {return primaryAttack;}
    public String getSecondaryAttack() {return secondaryAttack;}
    public String getDefense() {return defense;}
}
