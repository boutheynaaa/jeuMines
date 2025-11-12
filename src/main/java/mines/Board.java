package mines;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Board extends JPanel {
    private static final long serialVersionUID = 6195235521361212179L;
    
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


    public Board(JLabel statusbar) {
        this.statusbar = statusbar;
        img = new Image[NUM_IMAGES];

        // Chargement sécurisé des images avec plusieurs chemins possibles
        for (int i = 0; i < NUM_IMAGES; i++) {
            img[i] = loadImage(i);
        }

        setDoubleBuffered(true);
        addMouseListener(new MinesAdapter());
        newGame();
    }

    /**
     * Charge une image avec plusieurs chemins de recherche
     */
    private Image loadImage(int index) {
        String[] paths = {
            "/" + index + ".gif",              // Racine du classpath
            "/images/" + index + ".gif",       // Dans dossier images
            "images/" + index + ".gif",        // Relatif
            index + ".gif"                     // Direct
        };
        
        for (String path : paths) {
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    return (new ImageIcon(imgURL)).getImage();
                }
            } catch (Exception e) {
                // Continuer avec le prochain chemin
            }
        }
        
        // Si aucune image trouvée, créer un placeholder
        System.err.println("ATTENTION: Image " + index + ".gif non trouvée dans le classpath");
        return createPlaceholderImage(index);
    }

    /**
     * Crée une image de remplacement en cas d'échec de chargement
     */
    private Image createPlaceholderImage(int index) {
        BufferedImage placeholder = new BufferedImage(CELL_SIZE, CELL_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics g = placeholder.getGraphics();
        
        // Couleur de fond selon le type
        if (index == DRAW_MINE) {
            g.setColor(java.awt.Color.RED);
            g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            g.setColor(java.awt.Color.BLACK);
            g.drawString("*", 5, 12);
        } else if (index == DRAW_COVER) {
            g.setColor(java.awt.Color.LIGHT_GRAY);
            g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            g.setColor(java.awt.Color.GRAY);
            g.drawRect(0, 0, CELL_SIZE - 1, CELL_SIZE - 1);
        } else if (index == DRAW_MARK) {
            g.setColor(java.awt.Color.YELLOW);
            g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            g.setColor(java.awt.Color.RED);
            g.drawString("F", 4, 12);
        } else if (index == DRAW_WRONG_MARK) {
            g.setColor(java.awt.Color.ORANGE);
            g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            g.setColor(java.awt.Color.BLACK);
            g.drawString("X", 4, 12);
        } else {
            // Cases numérotées (0-8)
            g.setColor(java.awt.Color.WHITE);
            g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            g.setColor(java.awt.Color.BLACK);
            g.drawRect(0, 0, CELL_SIZE - 1, CELL_SIZE - 1);
            if (index > 0 && index < 9) {
                // Couleurs différentes selon le chiffre
                java.awt.Color[] colors = {
                    java.awt.Color.BLACK,  // 0
                    java.awt.Color.BLUE,   // 1
                    java.awt.Color.GREEN,  // 2
                    java.awt.Color.RED,    // 3
                    new java.awt.Color(0, 0, 128),    // 4 - Bleu foncé
                    new java.awt.Color(128, 0, 0),    // 5 - Rouge foncé
                    java.awt.Color.CYAN,   // 6
                    java.awt.Color.BLACK,  // 7
                    java.awt.Color.GRAY    // 8
                };
                g.setColor(colors[index]);
                g.drawString(String.valueOf(index), 4, 12);
            }
        }
        
        g.dispose();
        return placeholder;
    }


    public void newGame() {
        Random random;
        int current_col;
        int i = 0;
        int position = 0;
        int cell = 0;

        random = new Random();
        inGame = true;
        mines_left = mines;

        all_cells = rows * cols;
        field = new int[all_cells];
        
        for (i = 0; i < all_cells; i++)
            field[i] = COVER_FOR_CELL;

        statusbar.setText(Integer.toString(mines_left));

        i = 0;
        while (i < mines) {
            position = (int) (all_cells * random.nextDouble());

            if ((position < all_cells) &&
                (field[position] != COVERED_MINE_CELL)) {

                current_col = position % cols;
                field[position] = COVERED_MINE_CELL;
                i++;

                if (current_col > 0) { 
                    cell = position - 1 - cols;
                    if (cell >= 0)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                    cell = position - 1;
                    if (cell >= 0)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;

                    cell = position + cols - 1;
                    if (cell < all_cells)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                }

                cell = position - cols;
                if (cell >= 0)
                    if (field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;
                cell = position + cols;
                if (cell < all_cells)
                    if (field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;

                if (current_col < (cols - 1)) {
                    cell = position - cols + 1;
                    if (cell >= 0)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                    cell = position + cols + 1;
                    if (cell < all_cells)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                    cell = position + 1;
                    if (cell < all_cells)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                }
            }
        }
    }


    public void find_empty_cells(int j) {
        int current_col = j % cols;
        int cell;

        if (current_col > 0) { 
            cell = j - cols - 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j - 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + cols - 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }
        }

        cell = j - cols;
        if (cell >= 0)
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

        cell = j + cols;
        if (cell < all_cells)
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

        if (current_col < (cols - 1)) {
            cell = j - cols + 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + cols + 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int cell = 0;
        int uncover = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cell = field[(i * cols) + j];

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
            }
        }

        if (uncover == mines && inGame) {
            inGame = false;
            statusbar.setText("Game won");
        } else if (!inGame)
            statusbar.setText("Game lost");
    }


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
            }

            if ((x < cols * CELL_SIZE) && (y < rows * CELL_SIZE)) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (field[(cRow * cols) + cCol] > MINE_CELL) {
                        rep = true;

                        if (field[(cRow * cols) + cCol] <= COVERED_MINE_CELL) {
                            if (mines_left > 0) {
                                field[(cRow * cols) + cCol] += MARK_FOR_CELL;
                                mines_left--;
                                statusbar.setText(Integer.toString(mines_left));
                            } else
                                statusbar.setText("No marks left");
                        } else {
                            field[(cRow * cols) + cCol] -= MARK_FOR_CELL;
                            mines_left++;
                            statusbar.setText(Integer.toString(mines_left));
                        }
                    }
                } else {
                    if (field[(cRow * cols) + cCol] > COVERED_MINE_CELL) {
                        return;
                    }

                    if ((field[(cRow * cols) + cCol] > MINE_CELL) &&
                        (field[(cRow * cols) + cCol] < MARKED_MINE_CELL)) {

                        field[(cRow * cols) + cCol] -= COVER_FOR_CELL;
                        rep = true;

                        if (field[(cRow * cols) + cCol] == MINE_CELL)
                            inGame = false;
                        if (field[(cRow * cols) + cCol] == EMPTY_CELL)
                            find_empty_cells((cRow * cols) + cCol);
                    }
                }

                if (rep)
                    repaint();
            }
        }
    }
}