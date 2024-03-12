import java.util.ArrayList;

public abstract class CharacterClass {
    private int strength;
    private int agility;
    private int intelligence;
    private ArrayList<Skill> skills;

    public CharacterClass(int strength, int agility, int intelligence) {
        this.strength = strength;
        this.agility = agility;
        this.intelligence = intelligence;
        this.skills = new ArrayList<>();
    }

    public void setStrength(int strength, int level) {
        this.strength +=  strength * level;
    }

    public void setAgility(int agility, int level) {
        this.agility +=  agility * level;
    }

    public void setIntelligence(int intelligence, int level) {
        this.intelligence +=  intelligence * level;
    }

    public void setStrengthUp(int strength) {
        this.strength += strength;
    }

    public void setAgilityUp(int agility) {
        this.agility += agility;
    }

    public void setIntelligenceUp(int intelligence) {
        this.intelligence += intelligence;
    }

    public void setSkill(Skill skill) {
        skills.add(skill);
    }

    public void setAllAttributes(Character currentCharacter) {
        if (currentCharacter.getClassName().equals("Wizard")){
            currentCharacter.getCharClass().setStrength(1, currentCharacter.getLevel());
            currentCharacter.getCharClass().setAgility(3, currentCharacter.getLevel());
            currentCharacter.getCharClass().setIntelligence(4, currentCharacter.getLevel());
        } else if (currentCharacter.getClassName().equals("Warrior")){
            currentCharacter.getCharClass().setStrength(4, currentCharacter.getLevel());
            currentCharacter.getCharClass().setAgility(1, currentCharacter.getLevel());
            currentCharacter.getCharClass().setIntelligence(1, currentCharacter.getLevel());
        } else if (currentCharacter.getClassName().equals("Archer")){
            currentCharacter.getCharClass().setStrength(1, currentCharacter.getLevel());
            currentCharacter.getCharClass().setAgility(2, currentCharacter.getLevel());
            currentCharacter.getCharClass().setIntelligence(3, currentCharacter.getLevel());
        } else {
            currentCharacter.getCharClass().setStrength(4, currentCharacter.getLevel());
            currentCharacter.getCharClass().setAgility(1, currentCharacter.getLevel());
        }
    }

    public int getStrength(){
        return strength;
    }

    public int getAgility(){
        return agility;
    }

    public int getIntelligence(){
        return intelligence;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public Skill getSkill(int i) {
        return skills.get(i);
    }
}
