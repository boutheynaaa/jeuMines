package mines;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Plateau de jeu pour le mode multijoueur
 * Extends JPanel pour une implémentation indépendante du mode solo
 */
public class MultiplayerBoard extends JPanel {
    private static final long serialVersionUID = 6195235521361212180L;
    
    private final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 15;

    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int mines_left;
    private Image[] img;
    private int mines = 40;
    private int rows = 16;
    private int cols = 16;
    private int all_cells;
    private JLabel statusbar;
    
    // Attributs multijoueur
    private Player[] players;
    private int currentPlayerIndex;
    private Map<Integer, Integer> cellOwners; // Position -> Index du joueur
    private boolean multiplayerMode;

    /**
     * Constructeur
     * @param statusbar Barre de statut pour afficher les informations
     * @param multiplayer True pour activer le mode multijoueur
     * @param numPlayers Nombre de joueurs (2-4)
     */
    public MultiplayerBoard(JLabel statusbar, boolean multiplayer, int numPlayers) {
        this.statusbar = statusbar;
        this.multiplayerMode = multiplayer;
        this.cellOwners = new HashMap<>();
        
        if (multiplayer && numPlayers >= 2 && numPlayers <= 4) {
            initializePlayers(numPlayers);
        } else {
            this.multiplayerMode = false;
        }
        
        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
            var imgURL = getClass().getClassLoader().getResource("images/" + i + ".gif");
            if (imgURL != null) {
                img[i] = (new ImageIcon(imgURL)).getImage();
            }
        }

        setDoubleBuffered(true);
        addMouseListener(new MinesAdapter());
        newGame();
    }

    /**
     * Initialise les joueurs avec leurs couleurs
     */
    private void initializePlayers(int numPlayers) {
        players = new Player[numPlayers];
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE};
        String[] names = {"Rouge", "Bleu", "Vert", "Orange"};
        
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(names[i], colors[i]);
        }
        currentPlayerIndex = 0;
    }

    /**
     * Démarre une nouvelle partie
     */
    public void newGame() {
        Random random;
        int current_col;
        int i = 0;
        int position = 0;
        int cell = 0;

        random = new Random();
        inGame = true;
        mines_left = mines;
        cellOwners.clear();
        currentPlayerIndex = 0;

        if (multiplayerMode) {
            for (Player player : players) {
                player.resetScore();
            }
        }

        all_cells = rows * cols;
        field = new int[all_cells];
        
        for (i = 0; i < all_cells; i++)
            field[i] = COVER_FOR_CELL;

        updateStatusBar();

        i = 0;
        while (i < mines) {
            position = (int) (all_cells * random.nextDouble());

            if ((position < all_cells) &&
                (field[position] != COVERED_MINE_CELL)) {

                current_col = position % cols;
                field[position] = COVERED_MINE_CELL;
                i++;

                // Incrémenter les cases adjacentes
                if (current_col > 0) { 
                    cell = position - 1 - cols;
                    if (cell >= 0 && field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;
                    cell = position - 1;
                    if (cell >= 0 && field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;
                    cell = position + cols - 1;
                    if (cell < all_cells && field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;
                }

                cell = position - cols;
                if (cell >= 0 && field[cell] != COVERED_MINE_CELL)
                    field[cell] += 1;
                cell = position + cols;
                if (cell < all_cells && field[cell] != COVERED_MINE_CELL)
                    field[cell] += 1;

                if (current_col < (cols - 1)) {
                    cell = position - cols + 1;
                    if (cell >= 0 && field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;
                    cell = position + cols + 1;
                    if (cell < all_cells && field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;
                    cell = position + 1;
                    if (cell < all_cells && field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;
                }
            }
        }
    }

    /**
     * Découvre les cases vides adjacentes de manière récursive
     */
    public void find_empty_cells(int j) {
        int current_col = j % cols;
        int cell;

        if (current_col > 0) { 
            cell = j - cols - 1;
            if (cell >= 0 && field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (multiplayerMode && !cellOwners.containsKey(cell)) {
                    cellOwners.put(cell, currentPlayerIndex);
                    players[currentPlayerIndex].addPoints(field[cell] == EMPTY_CELL ? 1 : field[cell]);
                }
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

            cell = j - 1;
            if (cell >= 0 && field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (multiplayerMode && !cellOwners.containsKey(cell)) {
                    cellOwners.put(cell, currentPlayerIndex);
                    players[currentPlayerIndex].addPoints(field[cell] == EMPTY_CELL ? 1 : field[cell]);
                }
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

            cell = j + cols - 1;
            if (cell < all_cells && field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (multiplayerMode && !cellOwners.containsKey(cell)) {
                    cellOwners.put(cell, currentPlayerIndex);
                    players[currentPlayerIndex].addPoints(field[cell] == EMPTY_CELL ? 1 : field[cell]);
                }
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }
        }

        cell = j - cols;
        if (cell >= 0 && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (multiplayerMode && !cellOwners.containsKey(cell)) {
                cellOwners.put(cell, currentPlayerIndex);
                players[currentPlayerIndex].addPoints(field[cell] == EMPTY_CELL ? 1 : field[cell]);
            }
            if (field[cell] == EMPTY_CELL)
                find_empty_cells(cell);
        }

        cell = j + cols;
        if (cell < all_cells && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (multiplayerMode && !cellOwners.containsKey(cell)) {
                cellOwners.put(cell, currentPlayerIndex);
                players[currentPlayerIndex].addPoints(field[cell] == EMPTY_CELL ? 1 : field[cell]);
            }
            if (field[cell] == EMPTY_CELL)
                find_empty_cells(cell);
        }

        if (current_col < (cols - 1)) {
            cell = j - cols + 1;
            if (cell >= 0 && field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (multiplayerMode && !cellOwners.containsKey(cell)) {
                    cellOwners.put(cell, currentPlayerIndex);
                    players[currentPlayerIndex].addPoints(field[cell] == EMPTY_CELL ? 1 : field[cell]);
                }
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

            cell = j + cols + 1;
            if (cell < all_cells && field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (multiplayerMode && !cellOwners.containsKey(cell)) {
                    cellOwners.put(cell, currentPlayerIndex);
                    players[currentPlayerIndex].addPoints(field[cell] == EMPTY_CELL ? 1 : field[cell]);
                }
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

            cell = j + 1;
            if (cell < all_cells && field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (multiplayerMode && !cellOwners.containsKey(cell)) {
                    cellOwners.put(cell, currentPlayerIndex);
                    players[currentPlayerIndex].addPoints(field[cell] == EMPTY_CELL ? 1 : field[cell]);
                }
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }
        }
    }

    /**
     * Met à jour la barre de statut
     */
    private void updateStatusBar() {
        if (multiplayerMode) {
            StringBuilder sb = new StringBuilder();
            sb.append("Tour: ").append(players[currentPlayerIndex].getName()).append(" | ");
            for (Player p : players) {
                sb.append(p.getName()).append(": ").append(p.getScore()).append(" | ");
            }
            sb.append("Mines: ").append(mines_left);
            statusbar.setText(sb.toString());
        } else {
            statusbar.setText(Integer.toString(mines_left));
        }
    }

    /**
     * Passe au joueur suivant
     */
    private void nextPlayer() {
        if (multiplayerMode) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            updateStatusBar();
        }
    }

    /**
     * Détermine le gagnant
     */
    private Player getWinner() {
        Player winner = players[0];
        for (Player p : players) {
            if (p.getScore() > winner.getScore()) {
                winner = p;
            }
        }
        return winner;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int cell = 0;
        int uncover = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int pos = (i * cols) + j;
                cell = field[pos];

                if (inGame && cell == MINE_CELL)
                    inGame = false;

                if (!inGame) {
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }
                } else {
                    if (cell > COVERED_MINE_CELL)
                        cell = DRAW_MARK;
                    else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE), (i * CELL_SIZE), this);
                
                // Dessiner la bordure colorée pour les cases en mode multijoueur
                if (multiplayerMode && cellOwners.containsKey(pos) && field[pos] < COVER_FOR_CELL) {
                    int owner = cellOwners.get(pos);
                    g.setColor(players[owner].getColor());
                    g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
                }
            }
        }

        if (uncover == mines && inGame) {
            inGame = false;
            if (multiplayerMode) {
                Player winner = getWinner();
                statusbar.setText("Victoire de " + winner.getName() + " avec " + winner.getScore() + " points!");
            } else {
                statusbar.setText("Game won");
            }
        } else if (!inGame && !multiplayerMode) {
            statusbar.setText("Game lost");
        } else if (!inGame && multiplayerMode) {
            statusbar.setText("Partie terminée! Mine touchée!");
        }
    }

    /**
     * Gère les événements de la souris
     */
    class MinesAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean rep = false;

            if (!inGame) {
                newGame();
                repaint();
                return;
            }

            if ((x < cols * CELL_SIZE) && (y < rows * CELL_SIZE)) {
                int pos = (cRow * cols) + cCol;
                
                // Clic droit : marquer/démarquer
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (field[pos] > MINE_CELL) {
                        rep = true;

                        if (field[pos] <= COVERED_MINE_CELL) {
                            if (mines_left > 0) {
                                field[pos] += MARK_FOR_CELL;
                                mines_left--;
                                updateStatusBar();
                            } else {
                                statusbar.setText("No marks left");
                            }
                        } else {
                            field[pos] -= MARK_FOR_CELL;
                            mines_left++;
                            updateStatusBar();
                        }
                    }
                } 
                // Clic gauche : découvrir
                else {
                    if (field[pos] > COVERED_MINE_CELL) {
                        return;
                    }

                    if ((field[pos] > MINE_CELL) && (field[pos] < MARKED_MINE_CELL)) {
                        field[pos] -= COVER_FOR_CELL;
                        rep = true;

                        if (multiplayerMode && !cellOwners.containsKey(pos)) {
                            cellOwners.put(pos, currentPlayerIndex);
                            int points = field[pos] == EMPTY_CELL ? 1 : (field[pos] == MINE_CELL ? -10 : field[pos] + 1);
                            players[currentPlayerIndex].addPoints(points);
                        }

                        if (field[pos] == MINE_CELL) {
                            inGame = false;
                            if (multiplayerMode) {
                                players[currentPlayerIndex].addPoints(-10);
                            }
                        }
                        
                        if (field[pos] == EMPTY_CELL) {
                            find_empty_cells(pos);
                        }
                        
                        if (multiplayerMode && inGame) {
                            nextPlayer();
                        }
                    }
                }

                if (rep) {
                    updateStatusBar();
                    repaint();
                }
            }
        }
    }
    
    /**
     * Classe interne représentant un joueur
     */
    class Player {
        private String name;
        private int score;
        private Color color;
        
        public Player(String name, Color color) {
            this.name = name;
            this.color = color;
            this.score = 0;
        }
        
        public String getName() {
            return name;
        }
        
        public int getScore() {
            return score;
        }
        
        public Color getColor() {
            return color;
        }
        
        public void addPoints(int points) {
            score += points;
        }
        
        public void resetScore() {
            score = 0;
        }
    }
}
