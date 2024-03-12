public class Skill {
    private String name;
    private WeightAttributes damageWeight;
    private WeightAttributes manaWeight;
    private int time;
    private boolean affectsAll;
    private boolean affectsFriends;
    private static int ID_counter = 0;
    private int ID;

    public Skill(String name, WeightAttributes damageWeight, WeightAttributes manaWeight, int time, boolean affectsAll, boolean affectsFriends) {
        this.name = name;
        this.damageWeight = damageWeight;
        this.manaWeight = manaWeight;
        this.time = time;
        this.affectsAll = affectsAll;
        this.affectsFriends = affectsFriends;
        this.ID = ++ID_counter;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public boolean getAffectsAll() {
        return affectsAll;
    }

    public boolean getAffectsFriends() {
        return affectsFriends;
    }
    public WeightAttributes getManaWeight() {
        return manaWeight;
    }

    public WeightAttributes getDamageWeight() {
        return damageWeight;
    }

    public int[] damageWeightBattle() {
        return damageWeight.getAllWeight();
    }

    public int[] manaWeightBattle() {
        return manaWeight.getAllWeight();
    }
}
