public class WeightAttributes {
    private int strength;
    private int agility;
    private int intelligence;

    public WeightAttributes(int strength, int agility, int intelligence) {
        this.strength = strength;
        this.agility = agility;
        this.intelligence = intelligence;
    }

    public int getAgility() {
        return agility;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getStrength() {
        return strength;
    }

    public int[] getAllWeight() {
        int[] allWeight = new int[3];
        allWeight[0] = getStrength();
        allWeight[1] = getAgility();
        allWeight[2] = getIntelligence();

        return allWeight;
    }
}
