import javax.swing.*;
import java.awt.*;

public class View extends JFrame
{
    JPanel cards;
    final static String MENUPANEL = "Main menu card";
    final static String GAMEPANEL = "Game card";
    final static String HIGHSCOREPANEL = "Highscore card";
    final static String EDITPANEL = "Edit car card";
    final static String CHOOSEMAPPANEL = "CHoose map card";

    private JLabel playerLabel;
    JTextField nameTextField;

    JTextPane scoresTextPane = new JTextPane();
    JTextPane namesTextPane = new JTextPane();
    JComboBox cbHigscore;
    JButton playAgainButton;

    private Color buttonColor = new Color(0, 140, 198);
    private Color backgroundColor = new Color(45, 58, 87);

    private JPanel menuCard = new JPanel();
    private JPanel highscoreCard = new JPanel();
    private JPanel editCard = new JPanel();
    private JPanel chooseMapCard = new JPanel();

    private Canvas gameCanvas;
    private int width = 1280;
    private int height = 720;

    public View(Controller controller)
    {
        this.setSize(width,height);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);


        gameCanvas = new Canvas();
        gameCanvas.setPreferredSize(new Dimension(width, height));
        gameCanvas.setMaximumSize(new Dimension(width, height));
        gameCanvas.setMinimumSize(new Dimension(width, height));


        mainMenu(controller);
        editCarMenu(controller);
        highScoreMenu(controller);
        chooseMapMenu(controller);

        //Legger til keylistener på gameCanvas
        gameCanvas.addKeyListener(controller);

        //Legger de til i cards
        cards = new JPanel(new CardLayout());
        cards.add(menuCard, MENUPANEL);
        cards.add(gameCanvas, GAMEPANEL);
        cards.add(highscoreCard, HIGHSCOREPANEL);
        cards.add(editCard, EDITPANEL);
        cards.add(chooseMapCard, CHOOSEMAPPANEL);
        getContentPane().add(cards);

        this.setVisible(true);
    }

    public void mainMenu(Controller controller){
        menuCard.setLayout(new GridBagLayout());
        menuCard.setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.RELATIVE;

        gbc.insets = new Insets(0,0,20,0);
        menuCard.add(newLabel("RaceGame", 25), gbc);

        String[] text = {"1 Player", "2 Players", "Highscores"};

        for (int i = 0; i < 3; i++){
            gbc.gridx = 0;
            gbc.gridy = i+1; //starter på en pga text er 0
            menuCard.add(newSolidButton(controller, text[i], 16, 200, 50),gbc);
        }
    }

    public void editCarMenu(Controller controller){
        editCard.setLayout(new GridBagLayout());
        editCard.setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.RELATIVE;

        //Legger til menubutton
        gbc.insets = new Insets(0,0,20,0);
        editCard.add(newSolidButton(controller, "Main menu", 12, 120, 25),gbc);

        //Legger til player- og info-tekst
        playerLabel = newLabel("Player 1", 20);
        gbc.insets = new Insets(0,0,5,0);
        gbc.gridy = 1;
        editCard.add(playerLabel, gbc);

        gbc.gridy = 2;
        editCard.add(newLabel("Write your name and choose a car", 14), gbc);

        //Lager et inputPanel der navn og textField er
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(backgroundColor);
        inputPanel.add(newLabel("Name:", 14), gbc);

        nameTextField = new JTextField(10);
        inputPanel.add(nameTextField, gbc);

        gbc.gridy = 3;
        editCard.add(inputPanel, gbc);

        //Lager knapper med bilde av bilene, legges til på carsPanel()
        JPanel carsPanel = new JPanel();
        carsPanel.setBackground(backgroundColor);

        String[] paths = {"Assets/Cars/carImgGrey.png", "Assets/Cars/carImgYellow.png", "Assets/Cars/carImgBlue.png"};
        String[] names = {"grey", "yellow", "blue"};

        for (int  i = 0; i < 3; i++){
            carsPanel.add(newIconButton(controller, paths[i], names[i],75, 145));
        }

        gbc.gridx = 0;
        gbc.gridy = 5;
        editCard.add(carsPanel,gbc);
    }

    public void chooseMapMenu(Controller controller){
        chooseMapCard.setLayout(new GridBagLayout());
        chooseMapCard.setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.RELATIVE;

        gbc.insets = new Insets(0,0,20,0);
        chooseMapCard.add(newSolidButton(controller, "Main menu", 12, 120, 25),gbc);

        //Legger til Choose map teksten
        gbc.insets = new Insets(0,0,5,0);
        gbc.gridy = 1;
        chooseMapCard.add(newLabel("Choose map", 20), gbc);

        gbc.gridy = 2;
        chooseMapCard.add(newLabel("Click on a map to choose map and start game", 14), gbc);

        gbc.gridy = 4;
        chooseMapCard.add(newIconButton(controller, "Assets/Maps/Map1.jpg", "map1", 256, 144), gbc);

        gbc.gridy = 5;
        chooseMapCard.add(newIconButton(controller, "Assets/Maps/Map2.jpg", "map2",256, 144), gbc);
    }

    public void highScoreMenu(Controller controller){
        highscoreCard.setLayout(new GridBagLayout());
        highscoreCard.setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.RELATIVE;

        //Lager constraints for menuButton og legger til
        gbc.insets = new Insets(0,0,20,0);
        highscoreCard.add(newSolidButton(controller, "Main menu", 12, 120, 25), gbc);

        //Legger til highscores-teksten
        gbc.gridy = 1;
        gbc.insets = new Insets(0,0,5,0);
        highscoreCard.add(newLabel("Highscores", 20), gbc);

        //Legger til dropdown menu så man kan velge hvilken bane hs skal vises fra
        cbHigscore = new JComboBox<>(new String[] {"Map1", "Map2"});
        cbHigscore.addActionListener(controller);
        gbc.gridy = 2;
        highscoreCard.add(cbHigscore, gbc);

        //Lager to textpane der navn og score skal stå
        namesTextPane = newTextPane(140, 220);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        highscoreCard.add(namesTextPane, gbc);

        scoresTextPane = newTextPane(70, 220);
        gbc.gridx = 1;
        highscoreCard.add(scoresTextPane, gbc);


        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20,0, 0 ,0);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        playAgainButton = newSolidButton(controller, "Play again", 16, 200, 50);
        highscoreCard.add(playAgainButton, gbc);
        playAgainButton.setVisible(false);

    }

    public void blankEditPlayer(int i){
        playerLabel.setText("Player "+i);
        nameTextField.setText("");
    }

    public  Canvas getGameCanvas() {
        return gameCanvas;
    }

    public  int getHeight() {
        return this.height;
    }

    public int getWidth(){
        return this.width;
    }

    public ImageIcon resizeIcon(String path, int width, int height){

        ImageIcon imageIcon = new ImageIcon(path);
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(width,height, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(newImage);

        return resizedIcon;
    }

    public JButton newSolidButton(Controller controller, String text, int size, int width, int height){

        JButton  b = new JButton();
        b.setText(text);
        b.setFont(new Font("Sans-Serif", Font.PLAIN, size)); //12 og 16
        b.setBackground(buttonColor);
        b.setForeground(Color.white);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(width,height)); //200 50
        b.addActionListener(controller);
        b.setName(text);

        return b;
    }

    public JButton newIconButton(Controller controller, String path, String name, int width, int height){

        JButton b = new JButton();
        b.setIcon(resizeIcon(path, width, height));
        b.setBackground(backgroundColor);
        b.setBorderPainted(false);
        b.addActionListener(controller);
        b.setName(name);

        return b;
    }

    public JLabel newLabel(String text, int size){

        JLabel label = new JLabel(text);
        label.setFont(new Font("Sans-Serif", Font.PLAIN, size));
        label.setForeground(Color.white);

        return label;
    }

    public JTextPane newTextPane(int widht, int height){

        JTextPane p = new JTextPane();
        p.setPreferredSize(new Dimension(widht,height));
        p.setEditable(false);
        p.setFont(new Font("Sans-Serif", Font.PLAIN, 16));
        p.setForeground(Color.white);
        p.setOpaque(false);

        return p;
    }
}



