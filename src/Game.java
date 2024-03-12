import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

public class Game extends JFrame{
    public ArrayList<String> story = new ArrayList<>();
    public Random random = new Random();

    public Team mainTeam = new Team();
    public Team enemyTeam = new Team();

    public boolean firstTurn = true;

    public boolean click = false;

    public int selectedIndexSkill = 0;
    public int selectedIndexEnemy = 0;


    Scanner scan = new Scanner(System.in);

    public JLabel storyLabel = new JLabel();
    public JLabel playerInfos = new JLabel();
    public JLabel currentInfos = new JLabel();
    public JLabel enemyInfos = new JLabel();
    public ButtonGroup buttonGroupEnemy = new ButtonGroup();

    public JRadioButton[] skillsRButtons = new JRadioButton[3];

    public ImageIcon playerIconWarrior = new ImageIcon("lib/warrior.png");
    public ImageIcon playerIconWizard = new ImageIcon("lib/wizard.png");
    public ImageIcon playerIconArcher = new ImageIcon("lib/archer.png");
    public ImageIcon playerIconEmpty = new ImageIcon("lib/archer.png");
    public ImageIcon emptyIcon = new ImageIcon();
    

    public JPanel centerPanel = new JPanel();
    public JPanel bottonPanel = new JPanel();

    public JPanel playerPanel = new JPanel();
    public JPanel infosPanel = new JPanel();
    public JPanel panelButtons = new JPanel();

    public JRadioButton rbSkill1 = new JRadioButton();
    public JRadioButton rbSkill2 = new JRadioButton();
    public JRadioButton rbSkill3 = new JRadioButton();

    public ButtonGroup buttonGroup = new ButtonGroup();

    public JButton attackButton = new JButton("Atacar");

    public Game() {
        super("Dungeon POO");
        setLayout(new BorderLayout());

        storyLabel.setPreferredSize(new Dimension(200, 100));
        Border border = storyLabel.getBorder();
        Border margin = new EmptyBorder(10,10,10,10);
        storyLabel.setBorder(new CompoundBorder(border, margin));
        add(storyLabel, BorderLayout.NORTH);

        centerPanel.setLayout(new GridLayout(1, 3));
        
        playerPanel.add(playerInfos, BorderLayout.WEST);
        infosPanel.add(currentInfos, BorderLayout.CENTER);
        bottonPanel.setLayout(new FlowLayout());
        bottonPanel.add(attackButton);
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));
        
        add(centerPanel, BorderLayout.CENTER);
        add(bottonPanel, BorderLayout.SOUTH);
        
        centerPanel.add(playerPanel);
        centerPanel.add(infosPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 300);
        setVisible(true);
    }

    public void runGame(String file) {
        getStory();
        int currentLine = 0;
        
        while (currentLine < 3) {
            String[] characterLine = story.get(currentLine).split(" ");
            String name = characterLine[0];
            String charNameClass = characterLine[1];
            String level = characterLine[2];

            if(checkClassName(charNameClass))
                mainTeam.insertTeamMate(initializeAndExtractCharacterData(name, charNameClass, Integer.parseInt(level)));
            else
               currentInfos.setText("Class missing or wrong");
            currentLine++;
        }
        for (int i = currentLine; i < story.size(); i++) {
            String line = story.get(i);
            if(line.startsWith("Battle")) {
                String storyText = "<html>"+ line.substring(7) +"</html>";
                storyLabel.setText(storyText);
                setDelay(3);
                if (!runBattle()){
                    currentInfos.setText("Game Over");
                    setDelay(5);
                    System.exit(0);
                }
            } else if (!line.isEmpty()){ // get enemy data
                String[] lineSplitted = line.split(" ");
                String name = lineSplitted[0];
                String charNameClass = lineSplitted[1];
                int level = Integer.parseInt(lineSplitted[2]);

                if(checkClassName(charNameClass))
                    enemyTeam.insertTeamMate(initializeAndExtractCharacterData(name, charNameClass, level));
                else
                    currentInfos.setText("Class missing or wrong");
            }
        }
 
        currentInfos.setText("You win the game!");
        setDelay(5);
        System.exit(0);
    }

    public boolean runBattle() {
        int turn = 0;
        
        Character mainPlayer;
        Character monsterPlayer;

        ArrayList<Character> mainTeamArray = mainTeam.getFullTeam();

        boolean played = false;

        while (checkTeams()) {
            if (turn == 0) {
                if (random.nextBoolean()){
                    playerTurn(mainTeam.getCharacter(random.nextInt(mainTeam.totalTeamMates())));
                    turn++;
                    continue;
                }
                else{
                    enemyTurn(enemyTeam.getCharacter(random.nextInt(enemyTeam.totalTeamMates())));
                    turn++;
                    continue;
                }
            }
        
            played = false;
            while (!played) {
                mainPlayer = mainTeam.nextToPlay();
                monsterPlayer = enemyTeam.nextToPlay();

                if (mainPlayer.getStuuned()) {
                    if (monsterPlayer.getCooldown() == 0) {
                        enemyTurn(monsterPlayer);
                        turn++;
                        for (Character player : mainTeamArray)
                            player.setStuuned(false);
                        played = true;
                        break;
                    } else {
                        turn++;
                        for (Character player : mainTeamArray)
                            player.setStuuned(false);
                        played = true;
                        break;
                    }
                } else if (monsterPlayer.getCooldown() != 0 && mainPlayer.getCooldown() != 0) {
                    mainTeam.decreaseAllCooldown(); 
                    enemyTeam.decreaseAllCooldown();
                    continue;
                } else {
                    if (mainPlayer.getCooldown() == 0 && monsterPlayer.getCooldown() == 0 ){
                        if (random.nextBoolean()){
                            playerTurn(mainPlayer);
                            played = true;
                            turn++;
                            continue;
                        }
                        else {
                            enemyTurn(monsterPlayer);
                            played = true;
                            turn++;
                            continue;
                        }
                    } else if(mainPlayer.getCooldown() == 0) {
                        playerTurn(mainPlayer);
                        played = true;
                        turn++;
                        continue;
                    } else {
                        enemyTurn(monsterPlayer);
                        played = true;
                        turn++;
                        continue;
                    }
                }
            }
        }

        if (!enemyTeam.getFullTeam().isEmpty()){
            return false;
        }
        currentInfos.setText("You win the battle!");
        return true;
    }

    // turns
    public void playerTurn(Character currentPlayer) {
        currentInfos.setText("Player turn");
        setDelay(2);

        boolean finishTurn = false;
        
        while (!finishTurn){
            prepareUI();
            String playerText = "<html><p>"+ currentPlayer.getName() + " - " + currentPlayer.getClassName() + " - Level " + currentPlayer.getLevel() + "</p>";
            playerText += "<p>Life: " + currentPlayer.getLife() + " - Mana: " + currentPlayer.getMana() + "</p>";
            playerText += "\nExp: " + currentPlayer.getExp() + "</html>";
            playerInfos.setText(playerText); // mostrar no label da esquerda do meio
            setPlayerIcon(currentPlayer);
            currentInfos.setText("Choose a skill and a enemy to attack");// mostrar no label do meio do meio

            ArrayList<Skill> currentSkills = currentPlayer.getSkills();

            rbSkill1.setText(currentSkills.get(0).getName() + " - Cooldown: " + currentSkills.get(0).getTime());
            rbSkill2.setText(currentSkills.get(1).getName() + " - Cooldown: " + currentSkills.get(1).getTime());
            rbSkill3.setText(currentSkills.get(2).getName() + " - Cooldown: " + currentSkills.get(2).getTime());
            
            buttonGroup.add(rbSkill1);
            buttonGroup.add(rbSkill2);
            buttonGroup.add(rbSkill3);
            bottonPanel.add(rbSkill1);
            bottonPanel.add(rbSkill2);
            bottonPanel.add(rbSkill3);
            
            ArrayList<Character> monsterTeamArray = enemyTeam.getFullTeam();

            ArrayList<JRadioButton> enemySelect = new ArrayList<>();
            for(Character currentMonster : monsterTeamArray) {
                String enemyText = currentMonster.getName() + "- Class: " + currentMonster.getClassName() + " - Level " + currentMonster.getLevel() + "\n";
                enemyText += " Life: " + currentMonster.getLife() + "\n";
                enemyInfos.setText(enemyText);

                JRadioButton enemyRb = new JRadioButton(enemyText);
                enemySelect.add(enemyRb);
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    for (JRadioButton rButton : enemySelect) {
                        buttonGroupEnemy.add(rButton);
                        panelButtons.add(rButton);
                    }
        
                    centerPanel.add(panelButtons);
                    setVisible(true);
                    return;
                }
            });

            click = false;

            while(!click) {
                attackButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        centerPanel.requestFocusInWindow();
                        selectedIndexSkill = getSelectedIndexSkill();
                        selectedIndexEnemy = getSelectedIndexEnemy(buttonGroupEnemy);
                        if (selectedIndexSkill == -1 || selectedIndexEnemy == -1)
                            currentInfos.setText("Choose a skill and enemy");
                         else 
                            click = true;
                        
                    }
                });
            }

            click = false;

            Skill skillPicked = currentPlayer.getSkill(selectedIndexSkill);
            
            float damage = currentPlayer.damage(skillPicked);
            float mana = currentPlayer.mana(skillPicked);

            if(!currentPlayer.hasMana(mana)){
                currentInfos.setText("Has no mana, choose another attack");
                setDelay(1);
                continue;
            }

            Character enemyPicked = enemyTeam.getCharacter(selectedIndexEnemy);

            ArrayList<Character> mainTeamArray = mainTeam.getFullTeam();
            ArrayList<Character> playersToHeal = new ArrayList<>();

            // attack logic
            if (skillPicked.getName().equals("Heal")) {
                int k = 0;
                if (mainTeamArray.size() == 3) {
                    Object[] teamMates = new Object[2];
                    for(Character player : mainTeamArray) {
                        if(player.getID() == currentPlayer.getID())
                            continue;
                        playersToHeal.add(player);
                        teamMates[k] = player.getName() + " - Life: " + player.getLife();
                        k++;
                    }

                    int escolha = JOptionPane.showOptionDialog(
                        null,
                        "Choose a team mate to heal:",
                        "Heal",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        teamMates, 
                        teamMates[0]
                    );

                    if (escolha == 0)
                        playersToHeal.get(0).setLifeHeal(damage);
                    else
                        playersToHeal.get(1).setLifeHeal(damage);

                } else {
                    currentInfos.setText("Unable to heal");
                    setDelay(1);
                    continue;
                }
            } else {
                float targetLife = enemyPicked.hurtChar(damage);
                currentInfos.setText("The player " + enemyPicked.getName() + " lost " + damage + " life points");
                setDelay(2);
                if (targetLife == 0){
                    enemyTeam.removeTeamMate(enemyPicked);
                    currentInfos.setText("All player  gained " + (enemyPicked.getLevel() * 5) +" EXP");
                    setDelay(2);
                    for(Character player : mainTeamArray) {
                        player.setExp(enemyPicked.getLevel());
                        if (player.setLevelUp()){
                            currentInfos.setText("Level Up!");
                            setDelay(1);
                        }
                    }
                }
            }

            currentPlayer.downMana(mana);
            currentPlayer.setCooldown(skillPicked.getTime());

            panelButtons.removeAll();
            rechargeButtons(buttonGroup);
            rechargeButtons(buttonGroupEnemy);
            centerPanel.revalidate(); // Revalida o layout do panel
            centerPanel.repaint(); 
            setDelay(2);

            for (Character player : mainTeamArray) {
                player.setMana();
            }

            finishTurn = true;
        }
    }

    public void enemyTurn(Character currentPlayer) {
        currentInfos.setText("Enemy turn");
        setDelay(1);
        int[] enemySkillOrder = currentPlayer.getEnemyMove(mainTeam, enemyTeam);
        
        for (int i = 0; i < 3; i++){
            Skill enemySkill = currentPlayer.getCharClass().getSkill(enemySkillOrder[i]);
            float mana = currentPlayer.mana(enemySkill);
            float damage = currentPlayer.damage(enemySkill);

            if(!currentPlayer.hasMana(mana))
                continue;
                
            int targetIndex = findTarget();
            Character targetPlayer = mainTeam.getCharacter(targetIndex);
                
            if(targetPlayer.getStuuned() && enemySkill.getName().equals("Scream"))
                continue;
            
            ArrayList<Character> fullTargetTeam = mainTeam.getFullTeam();
            ArrayList<Character> fullMonsterTeam = enemyTeam.getFullTeam();

            // attack logic
            currentInfos.setText("The " + currentPlayer.getName() + " use " + enemySkill.getName());
            setDelay(2);
            int healPlayerTargetIndex = 0;
            if (enemySkill.getName().equals("Scream")) {
                for (Character target : fullTargetTeam) {
                    target.setStuuned(true);
                }
            } else if (enemySkill.getName().equals("Heal")) {
                float minLife = 9999;
                
                int j = 0;
                for (Character monster : fullMonsterTeam) {
                    if (monster.getID() == currentPlayer.getID()){
                        j++;
                        continue;
                    }
                    if (monster.getLife() < minLife){
                        minLife = monster.getLife();
                        healPlayerTargetIndex = j;
                    }
                    j++;
                }
                Character healPlayerTarget = enemyTeam.getCharacter(healPlayerTargetIndex);
                healPlayerTarget.setLifeHeal(damage);

            } else {
                float targetLife = targetPlayer.hurtChar(damage);
                currentInfos.setText("The player " + targetPlayer.getName() + " lost " + damage + " life points");
                setDelay(2);
                if (targetLife == 0){
                    mainTeam.removeTeamMate(targetPlayer);
                    currentInfos.setText("Player "+targetPlayer.getName()+" dead");
                    setDelay(2);
                }
            }

            currentPlayer.downMana(mana);
            currentPlayer.setCooldown(enemySkill.getTime());

            break;
        }
    }

    public int findTarget() {
        return random.nextInt(mainTeam.getFullTeam().size());
    }

    // extract class name

    public boolean checkClassName(String charClassName) {
        if (charClassName.equals("Wizard"))
            return true;
        else if (charClassName.equals("Warrior"))
            return true;
        else if (charClassName.equals("Archer"))
            return true;
        else if (charClassName.equals("Monster"))
            return true;

        return false;
    }

    public Character initializeAndExtractCharacterData(String name, String charClassName, int level) {
        if (charClassName.equals("Wizard")){
            Wizard newWizard = new Wizard();
            initializeSkills(newWizard, charClassName);
            Character newCharacter = new Character(name, newWizard, level);
            newCharacter.setLife();
            newCharacter.setMana();
            newWizard.setAllAttributes(newCharacter);
            return newCharacter;
        } else if (charClassName.equals("Warrior")){
            Warrior newWarrior = new Warrior();
            initializeSkills(newWarrior, charClassName);
            Character newCharacter = new Character(name, newWarrior, level);
            newCharacter.setLife();
            newCharacter.setMana();
            newWarrior.setAllAttributes(newCharacter);
            return newCharacter;
        } else if (charClassName.equals("Archer")){
            Archer newArcher = new Archer();
            initializeSkills(newArcher, charClassName);
            Character newCharacter = new Character(name, newArcher, level);
            newCharacter.setLife();
            newCharacter.setMana();
            newArcher.setAllAttributes(newCharacter);
            return newCharacter;
        }

        Monster newMonster = new Monster();
        initializeSkills(newMonster, charClassName);
        Character newCharacter = new Character(name, newMonster, level);
        newCharacter.setLife();
        newCharacter.setMana();
        newMonster.setAllAttributes(newCharacter);
        return newCharacter;
    }

    // extract TXT

    public void initializeSkills(CharacterClass charClass, String charClassName) {
        String[] allLines = new String[16];
        int iLine = 0;
        try {
            Scanner scannerInit = new Scanner(new File("lib/initialize.txt"));
            while (scannerInit.hasNextLine()) {
                allLines[iLine] = scannerInit.nextLine();
                iLine++;
            }
            int initLine = 9, finalLine = 11;
            if (charClassName.equals("Wizard")) {
                initLine = 0; 
                finalLine = 2;
            }
            else if (charClassName.equals("Warrior")) {
                initLine = 3;
                finalLine = 5;
            }
            else if (charClassName.equals("Archer")) {
                initLine = 6;
                finalLine = 8;
            }

            for (int i = initLine; i <= finalLine; i++) {
                String[] line = allLines[i].split(" ");
                String skillName = line[0];
                WeightAttributes damage = new WeightAttributes(Integer.parseInt(line[1]), Integer.parseInt(line[2]),Integer.parseInt(line[3]));
                WeightAttributes mana = new WeightAttributes(Integer.parseInt(line[4]), Integer.parseInt(line[5]),Integer.parseInt(line[6]));
                int cooldown = Integer.parseInt(line[7]);
                boolean affectsAll = Boolean.parseBoolean(line[8]);
                boolean affectsFriends = Boolean.parseBoolean(line[9]);

                Skill skill = new Skill(skillName, damage, mana, cooldown, affectsAll, affectsFriends);
                charClass.setSkill(skill);
            }
            scannerInit.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getStory() {
        try {
            Scanner scanner = new Scanner(new File("lib/test.txt"));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty())
                    continue;
                story.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public boolean checkTeams() {
        if(mainTeam.getFullTeam().isEmpty())
            return false;
        else if (enemyTeam.getFullTeam().isEmpty())
            return false;
        
        return true;
    }

    public int getSelectedIndexSkill() {
        int index = -1;
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        while (buttons.hasMoreElements()) {
            index++;
            if (buttons.nextElement().isSelected()) {
                return index;
            }
        }
        return -1;
    }

    public int getSelectedIndexEnemy(ButtonGroup buttonGroupEnemy) {
        int index = -1;
        Enumeration<AbstractButton> buttons = buttonGroupEnemy.getElements();
        while (buttons.hasMoreElements()) {
            index++;
            if (buttons.nextElement().isSelected()) {
                return index;
            }
        }
        return -1;
    }

    public void setDelay(int seconds) {
        try {
            Thread.sleep(seconds * 1000); // 2000 milissegundos = 2 segundos
        } catch (InterruptedException e) {
        }
    }

    public void rechargeButtons(ButtonGroup buttonGroup) {
        buttonGroup.clearSelection();
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        List<AbstractButton> buttonList = Collections.list(buttons);
        for (AbstractButton button : buttonList)
            buttonGroup.remove(button);
    }

    public void prepareUI() {
        panelButtons.removeAll();
        rechargeButtons(buttonGroup);
        rechargeButtons(buttonGroupEnemy);
        centerPanel.revalidate();
        centerPanel.repaint();

        playerInfos.setIcon(emptyIcon);
    }

    public void setPlayerIcon(Character player) {
        switch (player.getClassName()) {
            case "Wizard":
                playerIconWizard = new ImageIcon(playerIconWizard.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                playerInfos.setIcon(playerIconWizard);
                break;
            case "Warrior":
                playerIconWarrior = new ImageIcon(playerIconWarrior.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                playerInfos.setIcon(playerIconWarrior);
                break;
            case "Archer":
                playerIconArcher = new ImageIcon(playerIconArcher.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                playerInfos.setIcon(playerIconArcher);
                break;
        
            default:
                break;
        }
    }
}



