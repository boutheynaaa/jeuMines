package mines;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Classe principale pour le jeu de démineur avec mode multijoueur
 */
public class MultiplayerMines extends JFrame {
    private static final long serialVersionUID = 4772165125287256838L;
    
    private final int WIDTH = 250;
    private final int HEIGHT = 290;
    
    private JLabel statusbar;
    
    public MultiplayerMines() {
        // Afficher l'écran de sélection du mode
        showModeSelection();
    }
    
    /**
     * Affiche l'écran de sélection du mode de jeu
     */
    private void showModeSelection() {
        JFrame selectionFrame = new JFrame("Sélection du Mode");
        selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectionFrame.setSize(300, 200);
        selectionFrame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        
        JLabel label = new JLabel("Choisissez le mode de jeu:", JLabel.CENTER);
        panel.add(label);
        
        JButton soloButton = new JButton("Mode Solo");
        soloButton.addActionListener(e -> {
            selectionFrame.dispose();
            startSoloGame();
        });
        panel.add(soloButton);
        
        JPanel multiPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JLabel playersLabel = new JLabel("Joueurs:");
        String[] playerOptions = {"2", "3", "4"};
        JComboBox<String> playersCombo = new JComboBox<>(playerOptions);
        multiPanel.add(playersLabel);
        multiPanel.add(playersCombo);
        panel.add(multiPanel);
        
        JButton multiButton = new JButton("Mode Multijoueur");
        multiButton.addActionListener(e -> {
            int numPlayers = Integer.parseInt((String) playersCombo.getSelectedItem());
            selectionFrame.dispose();
            startMultiplayerGame(numPlayers);
        });
        panel.add(multiButton);
        
        selectionFrame.add(panel);
        selectionFrame.setVisible(true);
    }
    
    /**
     * Démarre une partie en mode solo
     */
    private void startSoloGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Minesweeper - Mode Solo");
        
        statusbar = new JLabel("");
        add(statusbar, BorderLayout.SOUTH);
        
        add(new Board(statusbar));
        
        setResizable(false);
        setVisible(true);
    }
    
    /**
     * Démarre une partie en mode multijoueur
     */
    private void startMultiplayerGame(int numPlayers) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT + 20); // Un peu plus haut pour les infos
        setLocationRelativeTo(null);
        setTitle("Minesweeper - Mode Multijoueur (" + numPlayers + " joueurs)");
        
        statusbar = new JLabel("");
        add(statusbar, BorderLayout.SOUTH);
        
        add(new MultiplayerBoard(statusbar, true, numPlayers));
        
        setResizable(false);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new MultiplayerMines();
    }
}
