import java.util.ArrayList;
import java.util.Random;

public class Character {
    private static int ID_COUNTER = 0;

    private int ID;
    private String name;
    private CharacterClass characterClass;
    private int level;
    private int EXP;
    private float life;
    private float mana;
    private int cooldown;
    private boolean stuuned;

    public Character(String name, CharacterClass characterClass, int level) {
        this.ID = ++ID_COUNTER;
        this.name = name;
        this.characterClass = characterClass;
        this.level = level;
        this.life = level * 10; // PVmax = nivel * 10
        this.mana = level * 5; // PMmax = nivel * 5
        this.cooldown = 0;
        this.stuuned = false;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return characterClass.getClass().getSimpleName();
    }
    
    public CharacterClass getCharClass() {
        return characterClass;
    }

    public ArrayList<Skill> getSkills() {
        return characterClass.getSkills();
    }

    public Skill getSkill(int i){
        return characterClass.getSkill(i);
    }

    public int getLevel() {
        return level;
    }

    public float getExp() {
        return EXP;
    }

    public float getLife() {
        return life;
    }

    public float getMaxLife() {
        return level * characterClass.getStrength() + (level * (characterClass.getAgility()));
    }

    public float getMana() {
        return mana;
    }

    public float getMaxMana() {
        return level * characterClass.getIntelligence() + (level * (characterClass.getAgility()));
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean getStuuned() {
        return stuuned;
    }

    //setters

    public void setLife() {
        float maxLife = getMaxLife();
        this.life = maxLife;
    }

    public void setMana() {
        float maxMana = getMaxLife();
        this.mana = maxMana;
    }

    public void setStuuned(boolean stuuned) {
        this.stuuned = stuuned;
    }

    // in battle

    public void setLifeHeal(float heal) {
        if ((life += heal) >= getMaxLife())
            life = getMaxLife();
        else
            life += heal; 
    }

    public void decreaseCooldown() {
        cooldown--;
    }

    public void restoreCooldown() {
        cooldown = 0;
    }

    // battle calc

    public float damage(Skill skill) {
        int[] damageWeights = skill.damageWeightBattle();
        return level * ((characterClass.getStrength() * ((float)damageWeights[0] / 10)) + (characterClass.getAgility() * ((float)damageWeights[1] / 10)) + (characterClass.getIntelligence() * ((float)damageWeights[2] / 10)));
    }

    public float mana(Skill skill) {
        int[] manaWeights = skill.manaWeightBattle();
        return level * ((characterClass.getStrength() * ((float)manaWeights[0] / 10)) + (characterClass.getAgility() * ((float)manaWeights[1] / 10)) + (characterClass.getIntelligence() * ((float)manaWeights[2] / 10)));
    }

    public boolean hasMana(float attackMana) {
        if (mana >= attackMana)
            return true;
        return false;
    }

    public void downMana(float attackMana) {
        mana -= attackMana;
    }

    public float hurtChar(float damage) {
        this.life -= damage;
        if (life < 0)
            this.life = 0;
        return life;
    }

    public void setCooldown(int time) {
        cooldown = time;
    }

    public void setExp(int levelDefeated) {
        EXP += levelDefeated * 5;
    }

    public boolean setLevelUp() {
        int expNeed = level * 25;
        int expRemain = expNeed - EXP;

        if (expRemain <= 0){
            EXP = (-1) * expRemain;
            level++;
            if (getClassName().equals("Wizard")){
                characterClass.setStrengthUp(1);
                characterClass.setAgilityUp(3);
                characterClass.setIntelligenceUp(4);
            } else if (getClassName().equals("Warrior")){
                characterClass.setStrengthUp(4);
                characterClass.setAgilityUp(1);
                characterClass.setIntelligenceUp(1);
            } else if (getClassName().equals("Archer")){
                characterClass.setStrengthUp(1);
                characterClass.setAgilityUp(2);
                characterClass.setIntelligenceUp(3);
            } else {
                characterClass.setStrengthUp(4);
                characterClass.setAgilityUp(1);
            }
            setLife();
            setMana();
            return true;
        } 
        return false;
    }

    public int[] getEnemyMove(Team mainTeam, Team enemyTeam) {
        float[] weights = new float[3];
        CharacterClass charClass = getCharClass();
        ArrayList<Skill> skills = charClass.getSkills(); 

        int i = 0;
        for (Skill skill : skills) {
            float cooldownWeight = 0;
            // pouca vida -> muita forca / atordoar
            float lifeWeight = (life / getMaxLife());
            if (lifeWeight <= 0.25){ 
                lifeWeight *= 2;
                if (getClassName().equals("Monster") && skill.getAffectsAll())
                    lifeWeight *= 2;
                else if (i == 2)
                    lifeWeight *= 2;
            } else if (i == 0){
                cooldownWeight += 0.2;
            }
            
            // inimigo com pouca vida ataque mais forte
            float enemyLifeWeight = (getLife() / getMaxLife());
                if (enemyLifeWeight <= 0.25)
                    enemyLifeWeight *= 2;
                    if (i == 2)
                        enemyLifeWeight *= 2;
                else if (i == 0)
                    cooldownWeight += 0.2;

            float healWeight = 0;
            boolean findToHeal = false;
            if(i == 2 && getClassName().equals("Wizard")){
                if (enemyTeam.totalLife() < mainTeam.totalLife()){
                    if(enemyTeam.getFullTeam().size() <= 2 && enemyTeam.getFullTeam().size() > 1){
                        ArrayList<Character> enemyTeamArray = enemyTeam.getFullTeam();
                        for (int e = 0; e < enemyTeam.getFullTeam().size(); e++){
                            if (enemyTeamArray.get(e).getID() != getID())
                                if(life / getMaxLife() > 0.5 && (enemyTeamArray.get(e).getLife() / enemyTeamArray.get(e).getMaxLife()) <= 0.3){
                                    
                                    findToHeal = true;
                                }
                        }
                    }
                }
            }

            if(findToHeal)
                healWeight += 1;
            else
                healWeight -= 2;

            weights[i] = lifeWeight + enemyLifeWeight + cooldownWeight + healWeight;
            i++;
        }

        float sumWeights = 0;

        for (float num : weights) {
            sumWeights += num;
        }

        Random randon = new Random();
        float randonNum = randon.nextFloat()* sumWeights;

        float accSum = 0;
        int[] index = new int[3];
        int back = 2;
        boolean findFirst = false;

        for (int j = 0; j < 3; j++) {
            accSum += weights[j];
            if (accSum >= randonNum && !findFirst) {
                index[0] = j;
                findFirst = true;
                continue;
            }
            index[back] = j;
            back--;
        }

        return index;
    }
}

    

    



































    

    

    


    

    

    

    

    

    

    

    

    

    

    

    


