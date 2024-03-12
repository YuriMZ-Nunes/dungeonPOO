import java.util.ArrayList;
import java.util.Random;


public class Team {
    private ArrayList<Character> teamFormed;
    public Random randon = new Random();

    public Team() {
        this.teamFormed = new ArrayList<>();
    }

    public void insertTeamMate(Character character) {
        teamFormed.add(character);
    }

    public ArrayList<Character> getFullTeam() {
        return teamFormed;
    }

    public Character getCharacter(int i) {
        return teamFormed.get(i);
    }

    public float totalLife() {
        float total = 0;
        for (Character teamMate : teamFormed)
            total += teamMate.getLife();
        return total;
    }

    public int totalTeamMates() {
        return teamFormed.size();
    }

    public Character nextToPlay() {

        ArrayList<Character> allMinimum = new ArrayList<>();
        
        int minimumCooldown = teamFormed.get(0).getCooldown();

        for (Character teamMate : teamFormed) 
            if (teamMate.getCooldown() < minimumCooldown)
                minimumCooldown = teamMate.getCooldown();
        
        for (Character teamMate : teamFormed)
            if (teamMate.getCooldown() == minimumCooldown)
                allMinimum.add(teamMate);
        
        
        return allMinimum.get(randon.nextInt(allMinimum.size()));

    }

    public void decreaseAllCooldown() {
        for (Character teamMate : teamFormed)
            teamMate.decreaseCooldown();
    }

    public int getOtherTeamMate(Character current){
        if (teamFormed.get(0).getID() == current.getID())
                return 0; 
        return 1;
    }

    public void removeTeamMate(Character charToRemove) {
        teamFormed.remove(charToRemove);
    }

}
