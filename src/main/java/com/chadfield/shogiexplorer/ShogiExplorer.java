package com.chadfield.shogiexplorer;

import com.chadfield.shogiexplorer.main.ConfigurationManager;
import com.chadfield.shogiexplorer.main.EngineManager;
import com.chadfield.shogiexplorer.objectclasses.GameAnalyser;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.DefaultListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.chadfield.shogiexplorer.objects.Board;
import com.chadfield.shogiexplorer.main.RenderBoard;
import com.chadfield.shogiexplorer.main.SFENParser;
import com.chadfield.shogiexplorer.objects.AnalysisParameter;
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.Game;
import com.chadfield.shogiexplorer.objects.Position;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.ui.RectangleEdge;

public class ShogiExplorer extends javax.swing.JFrame {

    transient Board board;
    transient Preferences prefs;
    transient Game game;
    int moveNumber;
    boolean play;
    boolean browse;
    int browsePos;
    AtomicBoolean analysing = new AtomicBoolean(false);
    DefaultListModel<String> moveListModel = new DefaultListModel<>();
    DefaultListModel<String> engineListModel = new DefaultListModel<>();
    boolean rotatedView;
    transient List<Engine> engineList = new ArrayList<>();
    transient FileNameExtensionFilter kifFileFilter;
    File newEngineFile;
    static JFrame mainFrame;
    boolean inSelectionChange = false;
    static final String PREF_ROTATED = "rotated";
    static final String PREF_ANALYSIS_ENGINE_NAME = "analysisEngineName";
    String analysisEngineName;
    static final String PREF_ANALYSIS_TIME_PER_MOVE = "analysisTimePerMove";
    int analysisTimePerMove;
    static final String PREF_ANALYSIS_MISTAKE_THRESHOLD = "analysisMistakeThreshold";
    int analysisMistakeThreshold;
    static final String PREF_ANALYSIS_BLUNDER_THRESHOLD = "analysisBlunderThreshold";
    int analysisBlunderThreshold;
    static final String PREF_ANALYSIS_IGNORE_THRESHOLD = "analysisLosingThreshold";
    int analysisIgnoreThreshold;
    static final String PREF_LANGUAGE = "language";
    static final String PREF_LANGUAGE_ENGLISH = "english";
    static final String PREF_LANGUAGE_JAPANESE = "japanese";
    DefaultIntervalXYDataset plotDataset;
    JFreeChart chart;
    ChartPanel chartPanel;
    transient Thread analysisThread;
    static  String aboutMessage; 
    
    static final String LOGO_NAME = "logo.png";
    
    private static final String OS = System.getProperty("os.name").toLowerCase();
    public static final boolean IS_WINDOWS = (OS.contains("win"));
    public static final boolean IS_MAC = (OS.contains("mac"));
    public static final boolean IS_LINUX = (OS.contains("nux"));

    /**
     * Creates new form NewJFrame
     */
    public ShogiExplorer() {
        prefs = Preferences.userNodeForPackage(ShogiExplorer.class);
        String language = prefs.get(PREF_LANGUAGE, PREF_LANGUAGE_ENGLISH);
        if (language.contentEquals(PREF_LANGUAGE_JAPANESE)) {
            Locale.setDefault(Locale.JAPAN);
        } else {
            Locale.setDefault(Locale.ENGLISH);
        }

        ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
        this.kifFileFilter = new FileNameExtensionFilter(bundle.getString("label_kif_files"), "kif");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        initComponents();
        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemClassLoader().getResource(LOGO_NAME)));
        } catch (IOException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        if (language.contentEquals(PREF_LANGUAGE_JAPANESE)) {
            jRadioButtonMenuItem6.setSelected(true);
        } 

        jTabbedPane1.setForeground(Color.BLACK);

        board = SFENParser.parse("lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1");
        
        String rotated = prefs.get(PREF_ROTATED, "false");
        if (rotated.compareTo("true") == 0) {
            jRadioButtonMenuItem1.doClick();
        }
        analysisEngineName = prefs.get(PREF_ANALYSIS_ENGINE_NAME, "");
        analysisTimePerMove = prefs.getInt(PREF_ANALYSIS_TIME_PER_MOVE, 3);
        analysisMistakeThreshold = prefs.getInt(PREF_ANALYSIS_MISTAKE_THRESHOLD, 250);
        analysisBlunderThreshold = prefs.getInt(PREF_ANALYSIS_BLUNDER_THRESHOLD, 500);
        analysisIgnoreThreshold = prefs.getInt(PREF_ANALYSIS_IGNORE_THRESHOLD, 2000);
        engineList = EngineManager.loadEngines(engineListModel);
        if (engineList == null) {
            engineList = new ArrayList<>();
        }
        if (!engineList.isEmpty()) {
            jEngineList.setSelectedIndex(0);
        }
        analysisTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        analysisTable.getColumnModel().getColumn(0).setMinWidth(130);
        analysisTable.getColumnModel().getColumn(1).setMinWidth(35);
        analysisTable.getColumnModel().getColumn(2).setMinWidth(70);
        analysisTable.getColumnModel().getColumn(3).setMinWidth(35);
        analysisTable.getColumnModel().getColumn(4).setMinWidth(1000);
        analysisTable.getSelectionModel().addListSelectionListener(new AnalysisTableListener());
        analysisTable.getColumnModel().getColumn(4).setCellRenderer(analysisMoveRenderer);
        analysisTable.setShowHorizontalLines(false);
        analysisTable.setShowVerticalLines(false);
        analysisTable.setDefaultEditor(Object.class, null);
        
        try (InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("Project.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            aboutMessage = "Shogi Explorer\n\nVersion " + prop.getProperty("project.version") + 
                    "\n\nCopyright © 2021 Stephen R Chadfield\nAll rights reserved."
                    + "\n\nPlay more Shogi!";
        } catch (IOException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (IS_MAC) {
            jMenu1.setVisible(false);
        }

        UIManager.put("TabbedPane.selectedForeground", Color.BLACK);
    }
    

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jEngineManagerDialog = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEngineList = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        deleteEngineButton = new javax.swing.JButton();
        addEngineButton = new javax.swing.JButton();
        configureEngineButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jEngineConfDialog = new javax.swing.JDialog(jEngineManagerDialog);
        jEngineConfPanel = new javax.swing.JPanel();
        jMenuItem3 = new javax.swing.JMenuItem();
        jAnalysisDialog = new javax.swing.JDialog();
        jAnalysisStartPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        analysisEngineComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        analysisTimePerMoveSpinner = new javax.swing.JSpinner();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        JRadioButton graph1000Button = new JRadioButton("1000");
        buttonGroup1.add(graph1000Button);
        JRadioButton graph2000Button = new JRadioButton("2000");
        buttonGroup1.add(graph2000Button);
        JRadioButton graph3000Button = new JRadioButton("3000");
        buttonGroup1.add(graph3000Button);
        buttonGroup2 = new javax.swing.ButtonGroup();
        jMenuItem5 = new javax.swing.JMenuItem();
        mainToolBar = new javax.swing.JToolBar();
        mediaStart = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        mediaReverse = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        mediaBack = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        mediaStop = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        mediaForward = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        mediaPlay = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        mediaEnd = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0));
        jButton4 = new javax.swing.JButton();
        gameScrollPanel = new javax.swing.JScrollPane();
        gameTextArea = new javax.swing.JTextArea();
        commentScrollPane = new javax.swing.JScrollPane();
        commentTextArea = new javax.swing.JTextArea();
        moveListScrollPane = new javax.swing.JScrollPane();
        moveList = new javax.swing.JList<>();
        boardPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        analysisTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        gameMenu = new javax.swing.JMenu();
        analyseGame = new javax.swing.JMenuItem();
        enginesMenu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem5 = new javax.swing.JRadioButtonMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem6 = new javax.swing.JRadioButtonMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        jEngineManagerDialog.setAlwaysOnTop(true);
        jEngineManagerDialog.setModal(true);
        jEngineManagerDialog.setSize(new java.awt.Dimension(400, 300));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jEngineList.setModel(engineListModel);
        jEngineList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jEngineList);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle"); // NOI18N
        deleteEngineButton.setText(bundle.getString("ShogiExplorer.deleteEngineButton.text")); // NOI18N
        deleteEngineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEngineButtonActionPerformed(evt);
            }
        });

        addEngineButton.setText(bundle.getString("ShogiExplorer.addEngineButton.text")); // NOI18N
        addEngineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEngineButtonActionPerformed(evt);
            }
        });

        configureEngineButton.setText(bundle.getString("ShogiExplorer.configureEngineButton.text")); // NOI18N
        configureEngineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configureEngineButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addEngineButton)
                    .addComponent(deleteEngineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(configureEngineButton))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addEngineButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteEngineButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(configureEngineButton))
        );

        jButton3.setText(bundle.getString("ShogiExplorer.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jEngineManagerDialogLayout = new javax.swing.GroupLayout(jEngineManagerDialog.getContentPane());
        jEngineManagerDialog.getContentPane().setLayout(jEngineManagerDialogLayout);
        jEngineManagerDialogLayout.setHorizontalGroup(
            jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEngineManagerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addContainerGap())
        );
        jEngineManagerDialogLayout.setVerticalGroup(
            jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jEngineManagerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jEngineManagerDialogLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
                .addContainerGap())
        );

        jEngineConfDialog.setAlwaysOnTop(true);
        jEngineConfDialog.setModal(true);
        jEngineConfDialog.setResizable(false);
        jEngineConfDialog.setSize(new java.awt.Dimension(100, 100));
        jEngineConfDialog.getContentPane().setLayout(new java.awt.FlowLayout());

        jEngineConfPanel.setLayout(new java.awt.GridLayout(0, 4, 20, 2));
        jEngineConfDialog.getContentPane().add(jEngineConfPanel);

        jEngineConfDialog.getAccessibleContext().setAccessibleParent(null);

        jMenuItem3.setText(bundle.getString("ShogiExplorer.jMenuItem3.text")); // NOI18N

        jAnalysisDialog.setAlwaysOnTop(true);
        jAnalysisDialog.setModal(true);
        jAnalysisDialog.setResizable(false);
        jAnalysisDialog.setSize(new java.awt.Dimension(480, 130));
        jAnalysisDialog.getContentPane().setLayout(new java.awt.FlowLayout());

        jAnalysisStartPanel.setLayout(new java.awt.GridLayout(0, 2, 20, 2));

        jLabel1.setText(bundle.getString("ShogiExplorer.jLabel1.text")); // NOI18N
        jLabel1.setMinimumSize(new java.awt.Dimension(200, 16));
        jAnalysisStartPanel.add(jLabel1);
        jAnalysisStartPanel.add(analysisEngineComboBox);

        jLabel3.setText(bundle.getString("ShogiExplorer.jLabel3.text")); // NOI18N
        jAnalysisStartPanel.add(jLabel3);
        jAnalysisStartPanel.add(analysisTimePerMoveSpinner);

        jButton1.setText(bundle.getString("ShogiExplorer.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jAnalysisStartPanel.add(jButton1);

        jButton2.setText(bundle.getString("ShogiExplorer.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jAnalysisStartPanel.add(jButton2);

        jAnalysisDialog.getContentPane().add(jAnalysisStartPanel);

        jMenuItem5.setText(bundle.getString("ShogiExplorer.jMenuItem5.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("ShogiExplorer.title_1")); // NOI18N
        setBounds(new java.awt.Rectangle(58, 25, 1000, 650));
        setMinimumSize(new java.awt.Dimension(1000, 650));
        setSize(new java.awt.Dimension(1000, 650));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);

        mediaStart.setText(bundle.getString("ShogiExplorer.mediaStart.text_1")); // NOI18N
        mediaStart.setFocusable(false);
        mediaStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mediaStart.setMaximumSize(new java.awt.Dimension(40, 24));
        mediaStart.setMinimumSize(new java.awt.Dimension(40, 24));
        mediaStart.setPreferredSize(new java.awt.Dimension(40, 24));
        mediaStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mediaStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediaStartActionPerformed(evt);
            }
        });
        mainToolBar.add(mediaStart);
        mainToolBar.add(filler1);

        mediaReverse.setFocusable(false);
        mediaReverse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mediaReverse.setLabel(bundle.getString("ShogiExplorer.mediaReverse.label_1")); // NOI18N
        mediaReverse.setMaximumSize(new java.awt.Dimension(40, 24));
        mediaReverse.setMinimumSize(new java.awt.Dimension(40, 24));
        mediaReverse.setPreferredSize(new java.awt.Dimension(40, 24));
        mediaReverse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mediaReverse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediaReverseActionPerformed(evt);
            }
        });
        mainToolBar.add(mediaReverse);
        mainToolBar.add(filler2);

        mediaBack.setFocusable(false);
        mediaBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mediaBack.setLabel(bundle.getString("ShogiExplorer.mediaBack.label_1")); // NOI18N
        mediaBack.setMaximumSize(new java.awt.Dimension(24, 24));
        mediaBack.setMinimumSize(new java.awt.Dimension(24, 24));
        mediaBack.setPreferredSize(new java.awt.Dimension(40, 24));
        mediaBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mediaBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediaBackActionPerformed(evt);
            }
        });
        mainToolBar.add(mediaBack);
        mainToolBar.add(filler3);

        mediaStop.setText(bundle.getString("ShogiExplorer.mediaStop.text")); // NOI18N
        mediaStop.setToolTipText(bundle.getString("ShogiExplorer.mediaStop.toolTipText")); // NOI18N
        mediaStop.setFocusable(false);
        mediaStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mediaStop.setMaximumSize(new java.awt.Dimension(24, 24));
        mediaStop.setMinimumSize(new java.awt.Dimension(24, 24));
        mediaStop.setPreferredSize(new java.awt.Dimension(40, 24));
        mediaStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mediaStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediaStopActionPerformed(evt);
            }
        });
        mainToolBar.add(mediaStop);
        mainToolBar.add(filler4);

        mediaForward.setFocusable(false);
        mediaForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mediaForward.setLabel(bundle.getString("ShogiExplorer.mediaForward.label_1")); // NOI18N
        mediaForward.setMaximumSize(new java.awt.Dimension(24, 24));
        mediaForward.setMinimumSize(new java.awt.Dimension(24, 24));
        mediaForward.setPreferredSize(new java.awt.Dimension(40, 24));
        mediaForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mediaForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediaForwardActionPerformed(evt);
            }
        });
        mainToolBar.add(mediaForward);
        mainToolBar.add(filler5);

        mediaPlay.setFocusable(false);
        mediaPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mediaPlay.setLabel(bundle.getString("ShogiExplorer.mediaPlay.label_1")); // NOI18N
        mediaPlay.setMaximumSize(new java.awt.Dimension(40, 24));
        mediaPlay.setMinimumSize(new java.awt.Dimension(40, 24));
        mediaPlay.setPreferredSize(new java.awt.Dimension(40, 24));
        mediaPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mediaPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediaPlayActionPerformed(evt);
            }
        });
        mainToolBar.add(mediaPlay);
        mainToolBar.add(filler6);

        mediaEnd.setFocusable(false);
        mediaEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mediaEnd.setLabel(bundle.getString("ShogiExplorer.mediaEnd.label_1")); // NOI18N
        mediaEnd.setMaximumSize(new java.awt.Dimension(40, 24));
        mediaEnd.setMinimumSize(new java.awt.Dimension(40, 24));
        mediaEnd.setPreferredSize(new java.awt.Dimension(40, 24));
        mediaEnd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mediaEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediaEndActionPerformed(evt);
            }
        });
        mainToolBar.add(mediaEnd);
        mainToolBar.add(filler7);

        jButton4.setEnabled(false);
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setLabel(bundle.getString("ShogiExplorer.jButton4.label")); // NOI18N
        jButton4.setMaximumSize(new java.awt.Dimension(100, 24));
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        mainToolBar.add(jButton4);

        gameScrollPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gameScrollPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        gameScrollPanel.setPreferredSize(new java.awt.Dimension(100, 84));

        gameTextArea.setEditable(false);
        gameTextArea.setColumns(20);
        gameTextArea.setRows(5);
        gameTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        gameTextArea.setEnabled(false);
        gameTextArea.setFocusable(false);
        gameTextArea.setPreferredSize(new java.awt.Dimension(100, 80));
        gameTextArea.setRequestFocusEnabled(false);
        gameScrollPanel.setViewportView(gameTextArea);

        commentScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        commentScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        commentScrollPane.setPreferredSize(new java.awt.Dimension(230, 84));

        commentTextArea.setEditable(false);
        commentTextArea.setColumns(20);
        commentTextArea.setRows(5);
        commentTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        commentTextArea.setEnabled(false);
        commentTextArea.setFocusable(false);
        commentScrollPane.setViewportView(commentTextArea);

        moveList.setModel(moveListModel);
        moveList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        moveList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                moveListValueChanged(evt);
            }
        });
        moveListScrollPane.setViewportView(moveList);

        boardPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        boardPanel.setMaximumSize(new java.awt.Dimension(603, 482));
        boardPanel.setMinimumSize(new java.awt.Dimension(603, 482));
        boardPanel.setPreferredSize(new java.awt.Dimension(603, 482));
        boardPanel.setRequestFocusEnabled(false);
        boardPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                boardPanelComponentResized(evt);
            }
        });

        javax.swing.GroupLayout boardPanelLayout = new javax.swing.GroupLayout(boardPanel);
        boardPanel.setLayout(boardPanelLayout);
        boardPanelLayout.setHorizontalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 601, Short.MAX_VALUE)
        );
        boardPanelLayout.setVerticalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        jTabbedPane1.setName(""); // NOI18N

        analysisTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Move", "?!", "Score", "+-", "Principal Variation"
            }
        ));
        analysisTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        analysisTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        analysisTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        analysisTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                analysisTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(analysisTable);

        jTabbedPane1.addTab(bundle.getString("ShogiExplorer.jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 982, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 117, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(bundle.getString("ShogiExplorer.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jRadioButton1.setText(bundle.getString("ShogiExplorer.jRadioButton1.text")); // NOI18N

        fileMenu.setText(bundle.getString("ShogiExplorer.fileMenu.text_1")); // NOI18N

        jMenuItem1.setText(bundle.getString("ShogiExplorer.jMenuItem1.text_1")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        jMenuItem6.setText(bundle.getString("ShogiExplorer.jMenuItem6.text")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem6);

        jMenuBar1.add(fileMenu);

        gameMenu.setText(bundle.getString("ShogiExplorer.gameMenu.text_1")); // NOI18N

        analyseGame.setText(bundle.getString("ShogiExplorer.analyseGame.text")); // NOI18N
        analyseGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAnalyseGameDialog(evt);
            }
        });
        gameMenu.add(analyseGame);

        jMenuBar1.add(gameMenu);

        enginesMenu.setText(bundle.getString("ShogiExplorer.enginesMenu.text_1")); // NOI18N

        jMenuItem2.setText(bundle.getString("ShogiExplorer.jMenuItem2.text_1")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        enginesMenu.add(jMenuItem2);

        jMenuBar1.add(enginesMenu);

        viewMenu.setLabel(bundle.getString("ShogiExplorer.viewMenu.label_1")); // NOI18N

        jRadioButtonMenuItem1.setLabel(bundle.getString("ShogiExplorer.jRadioButtonMenuItem1.label_1")); // NOI18N
        jRadioButtonMenuItem1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonMenuItem1ItemStateChanged(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem1);
        viewMenu.add(jSeparator1);

        buttonGroup1.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setSelected(true);
        jRadioButtonMenuItem3.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem3.text")); // NOI18N
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem3);

        buttonGroup1.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem4.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem4.text")); // NOI18N
        jRadioButtonMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem4ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem4);

        buttonGroup1.add(jRadioButtonMenuItem5);
        jRadioButtonMenuItem5.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem5.text")); // NOI18N
        jRadioButtonMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem5ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem5);
        viewMenu.add(jSeparator2);

        buttonGroup2.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setSelected(true);
        jRadioButtonMenuItem2.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem2.text_1")); // NOI18N
        jRadioButtonMenuItem2.setToolTipText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem2.toolTipText")); // NOI18N
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem2);

        buttonGroup2.add(jRadioButtonMenuItem6);
        jRadioButtonMenuItem6.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem6.text")); // NOI18N
        jRadioButtonMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem6ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem6);

        jMenuBar1.add(viewMenu);

        jMenu1.setText(bundle.getString("ShogiExplorer.jMenu1.text")); // NOI18N

        jMenuItem4.setText(bundle.getString("ShogiExplorer.jMenuItem4.text")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addComponent(mainToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(moveListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(commentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(gameScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(gameScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(moveListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                            .addComponent(commentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonMenuItem1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ItemStateChanged
        rotatedView = !rotatedView;
        if (rotatedView) {
            prefs.put(PREF_ROTATED, "true");
        } else {
            prefs.put(PREF_ROTATED, "false");
        }
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        RenderBoard.loadBoard(board, boardPanel, rotatedView);
    }//GEN-LAST:event_jRadioButtonMenuItem1ItemStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO: "Save window size here?"
    }//GEN-LAST:event_formWindowClosing

    private void boardPanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_boardPanelComponentResized
        analysisTable.repaint();
        RenderBoard.loadBoard(board, boardPanel, rotatedView);
    }//GEN-LAST:event_boardPanelComponentResized

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        File dirFile = new File(prefs.get("fileOpenDir", System.getProperty("user.home")));    
        FileDialog fileChooser = new FileDialog(mainFrame);
        fileChooser.setDirectory(dirFile.getPath());
        fileChooser.setMode(FileDialog.LOAD);
        fileChooser.setTitle("Select KIF fileFile");
        
        java.awt.EventQueue.invokeLater(() -> {
            fileChooser.setVisible(true);
            String name = fileChooser.getFile();
            String dir = fileChooser.getDirectory();
            if (name == null || dir == null) {
                return;
            }
            File kifFile = new File(fileChooser.getDirectory(), fileChooser.getFile());
            
            prefs.put("fileOpenDir", kifFile.getParent());
            DefaultTableModel analysisTableModel = (DefaultTableModel) analysisTable.getModel();
            analysisTableModel.getDataVector().clear();
            jTabbedPane1.setComponentAt(1, new JPanel());

            try {
                game = com.chadfield.shogiexplorer.main.KifParser.parseKif(moveListModel, kifFile);
            } catch (IOException ex) {
                Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
            }
            ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
            gameTextArea.setText(null);
            gameTextArea.append(bundle.getString("label_sente") + ": " + game.getSente() + "\n");
            gameTextArea.append(bundle.getString("label_gote") + ": " + game.getGote() + "\n");
            gameTextArea.append(bundle.getString("label_place") + ": " + game.getPlace() + "\n");
            gameTextArea.append(bundle.getString("label_date") + ": " + game.getDate() + "\n");
            gameTextArea.append(bundle.getString("label_time_limit") + ": " + game.getTimeLimit() + "\n");
            moveNumber = 0;
            moveList.setSelectedIndex(0);
        });
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void mediaForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaForwardActionPerformed
        if (!play && game != null  && !analysing.get() && moveNumber < game.getPositionList().size() + 1) {
            moveList.setSelectedIndex(moveNumber + 1);
        }
    }//GEN-LAST:event_mediaForwardActionPerformed

    private void mediaBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaBackActionPerformed
        if (!play && moveNumber > 0 && !analysing.get()) {
            moveList.setSelectedIndex(moveNumber - 1);
        }
    }//GEN-LAST:event_mediaBackActionPerformed

    private void mediaStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaStartActionPerformed
        if (!play && !analysing.get()) {
            moveList.setSelectedIndex(0);
        }
    }//GEN-LAST:event_mediaStartActionPerformed

    private void mediaEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaEndActionPerformed
        if (!play && game != null && !analysing.get()) {
            moveList.setSelectedIndex(game.getPositionList().size() - 1);
        }
    }//GEN-LAST:event_mediaEndActionPerformed

    private void mediaStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaStopActionPerformed
        play = false;
    }//GEN-LAST:event_mediaStopActionPerformed

    private void mediaPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaPlayActionPerformed
        if (!play && game != null && !analysing.get()) {
            new Thread() {
                @Override
                public void run() {
                    play = true;
                    while (play) {
                        if (moveNumber < game.getPositionList().size() - 1) {
                            moveList.setSelectedIndex(moveNumber + 1);
                            try {
                                Thread.sleep(500L);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                                Thread.currentThread().interrupt();
                            }
                        } else {
                            play = false;
                        }
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_mediaPlayActionPerformed

    private class AnalysisTableListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent evt) {
            if (!evt.getValueIsAdjusting() && !inSelectionChange && !analysing.get()) {
                browse = false;
                if (evt.getFirstIndex() + 1 == moveNumber) {
                    moveList.setSelectedIndex(evt.getLastIndex() + 1);
                } else {
                    moveList.setSelectedIndex(evt.getFirstIndex() + 1);
                }
            }
        }
    }

    private void moveListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_moveListValueChanged
        if (!evt.getValueIsAdjusting()) {
            inSelectionChange = true;
            browse = false;
            moveNumber = moveList.getSelectedIndex();
            if (moveNumber < 0) {
                return;
            }
            moveList.ensureIndexIsVisible(moveNumber);
            Position position = game.getPositionList().get(moveNumber);
            board = SFENParser.parse(position.getGameSFEN());
            board.setSource(position.getSource());
            board.setDestination(position.getDestination());
            commentTextArea.setText(null);
            commentTextArea.append(position.getComment());
            if (moveNumber > 0 && analysisTable.getRowCount() >= moveNumber) {
                analysisTable.setRowSelectionInterval(moveNumber - 1, moveNumber - 1);
                analysisTable.getSelectionModel().setSelectionInterval(moveNumber - 1, moveNumber - 1);
                analysisTable.scrollRectToVisible(new Rectangle(analysisTable.getCellRect(moveNumber - 1, 0, true)));
            }
            if (plotDataset != null && plotDataset.getSeriesCount() > 0) {
                double[] x3Start = new double[2];
                double[] x3 = new double[2];
                double[] x3End = new double[2];
                double[] y3Start = new double[2];
                double[] y3 = new double[2];
                double[] y3End = new double[2];
                x3Start[0] = moveNumber-0.02;
                x3[0] = 0;
                x3End[0] = moveNumber+0.02;
                y3Start[0] = 0;
                y3[0] = 3000;
                y3End[0] = 0;
                x3Start[1] = moveNumber-0.02;
                x3[1] = 0;
                x3End[1] = moveNumber+0.02;
                y3Start[1] = 0;
                y3[1] = -3000;
                y3End[1] = 0;
                double[][] data3 = new double[][] {x3, x3Start, x3End, y3, y3Start, y3End};
                plotDataset.addSeries("M", data3); 
            }
            analysisTable.repaint();
            RenderBoard.loadBoard(board, boardPanel, rotatedView);
            inSelectionChange = false;
        }
    }//GEN-LAST:event_moveListValueChanged

    private void mediaReverseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaReverseActionPerformed
        if (!play && !analysing.get()) {
            new Thread() {
                @Override
                public void run() {
                    play = true;
                    while (play) {
                        if (moveNumber > 0) {
                            java.awt.EventQueue.invokeLater(()
                                    -> moveList.setSelectedIndex(moveNumber - 1));
                            try {
                                Thread.sleep(500L);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                                Thread.currentThread().interrupt();
                            }
                        } else {
                            play = false;
                        }
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_mediaReverseActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            jEngineManagerDialog.pack();
            jEngineManagerDialog.setLocationRelativeTo(mainFrame);
            jEngineManagerDialog.setVisible(true);
        });
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void deleteEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEngineButtonActionPerformed
        engineList = EngineManager.deleteSelectedEngine(engineListModel, jEngineList, engineList);
    }//GEN-LAST:event_deleteEngineButtonActionPerformed

    private void addEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEngineButtonActionPerformed
        File dirFile = new File(prefs.get("engineOpenDir", System.getProperty("user.home"))); 
        FileDialog fileChooser = new FileDialog(mainFrame);
        fileChooser.setDirectory(dirFile.getPath());
        fileChooser.setMode(FileDialog.LOAD);
        fileChooser.setTitle("Select engine executable");
        if (IS_WINDOWS) {
            jEngineManagerDialog.setVisible(false);
        }
        fileChooser.setVisible(true);
        String name = fileChooser.getFile();
        String dir = fileChooser.getDirectory();
        if (name == null || dir == null) {
            return;
        }
        newEngineFile = new File(fileChooser.getDirectory(), fileChooser.getFile());

        prefs.put("engineOpenDir", newEngineFile.getParent());
        
        EngineManager.addNewEngine(newEngineFile, engineListModel, jEngineList, engineList);
        EngineManager.saveEngines(engineList);
        if (IS_WINDOWS) {
            jEngineManagerDialog.pack();
            jEngineManagerDialog.setLocationRelativeTo(mainFrame);
            jEngineManagerDialog.setVisible(true);
        }
    }//GEN-LAST:event_addEngineButtonActionPerformed

    private void configureEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configureEngineButtonActionPerformed
        if (!engineList.isEmpty()) {
            java.awt.EventQueue.invokeLater(()
                    -> ConfigurationManager.configureEngine(engineList, engineList.get(jEngineList.getSelectedIndex()), jEngineConfDialog, jEngineManagerDialog, jEngineConfPanel));
        }
    }//GEN-LAST:event_configureEngineButtonActionPerformed

    private void openAnalyseGameDialog(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAnalyseGameDialog
        Dimension comboSize = analysisEngineComboBox.getPreferredSize();
        analysisEngineComboBox.removeAllItems();
        for (Engine engine : engineList) {
            analysisEngineComboBox.addItem(engine.getName());
            if (engine.getName().contentEquals(analysisEngineName)) {
                analysisEngineComboBox.setSelectedItem(analysisEngineName);
            }
        }
        analysisEngineComboBox.setPreferredSize(new Dimension(220, comboSize.height));
        analysisTimePerMoveSpinner.setModel(new SpinnerNumberModel(
                analysisTimePerMove,
                3,
                60,
                1
        ));

        jAnalysisDialog.setLocationRelativeTo(mainFrame);
        jAnalysisDialog.setVisible(true);
    }//GEN-LAST:event_openAnalyseGameDialog

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jAnalysisDialog.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (analysing.get()) {
            return;
        }
        if (game == null) {
            return;
        }
        browse = false;
        analysisEngineName = (String) analysisEngineComboBox.getSelectedItem();
        analysisTimePerMove = (int) analysisTimePerMoveSpinner.getValue();
        prefs.put(PREF_ANALYSIS_ENGINE_NAME, analysisEngineName);
        prefs.putInt(PREF_ANALYSIS_TIME_PER_MOVE, analysisTimePerMove);
        AnalysisParameter analysisParam = new AnalysisParameter();
        analysisParam.setAnalysisTimePerMove(analysisTimePerMove);
        analysisParam.setGraphView1(jRadioButtonMenuItem3);
        analysisParam.setGraphView2(jRadioButtonMenuItem4);
        analysisParam.setGraphView3(jRadioButtonMenuItem5);
        analysisParam.setHaltAnalysisButton(jButton4);
        plotDataset = new DefaultIntervalXYDataset();  
        chart = ChartFactory.createXYBarChart("", "", false, "", plotDataset);
       
        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(0, new XYBarRenderer());
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, Color.BLACK);  
        renderer.setSeriesPaint(2, Color.WHITE); 
        renderer.setSeriesPaint(1, Color.RED); 
        renderer.setSeriesVisibleInLegend(0, false);
        renderer.setSeriesVisibleInLegend(1, false);
        renderer.setSeriesVisibleInLegend(2, false);
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setRange(-1000, 1000);
        chartPanel = new ChartPanel(chart); 
        chartPanel.setPopupMenu(null);
        chartPanel.setMouseZoomable(false);
        jTabbedPane1.setComponentAt(1, chartPanel);
        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseMoved(ChartMouseEvent evt) {
                // Required implementation.
            }
            @Override
            public void chartMouseClicked(ChartMouseEvent evt) {
                if (analysing.get()) {
                    return;
                }
                int mouseX = evt.getTrigger().getX();
                int mouseY = evt.getTrigger().getY();
                Point2D p = chartPanel.translateScreenToJava2D(
                        new Point(mouseX, mouseY));
                XYPlot plot = (XYPlot) chart.getPlot();
                ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
                Rectangle2D dataArea = info.getPlotInfo().getDataArea();
                ValueAxis domainAxis = plot.getDomainAxis();
                RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
                double chartX = domainAxis.java2DToValue(p.getX(), dataArea,
                        domainAxisEdge);
                moveList.setSelectedIndex((int) Math.round(chartX + 0.5));            
            }
        });
        jButton4.setEnabled(true);
        analysisThread = new Thread() {
            @Override
            public void run() {
                Engine engine = null;
                for (Engine thisEngine : engineList) {
                    if (thisEngine.getName().contentEquals(analysisEngineName)) {
                        engine = thisEngine;
                        break;
                    }
                }
                DefaultTableModel analysisTableModel = (DefaultTableModel) analysisTable.getModel();
                analysisTableModel.getDataVector().clear();
                try {
                    new GameAnalyser().analyse(game, engine, moveList, analysisTable, analysisParam, analysing, plot);
                } catch (IOException ex) {
                    Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        analysisThread.start();
        jAnalysisDialog.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jEngineManagerDialog.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void analysisTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_analysisTableKeyReleased
        if (analysing.get()) {
            return;
        }
        int keyCode = evt.getKeyCode();
        switch(keyCode) {
            case 37:
                leftButtonAnalysis();
                break;
            case 39:
                rightButtonAnalysis();
                break;
            default:
                browse = false;
        }
    }//GEN-LAST:event_analysisTableKeyReleased

    private void leftButtonAnalysis() {
        Position position;
        if (browse) {
            browsePos--;
            if (browsePos < 0) {
                browse = false;
                browsePos = 0;
                position = game.getPositionList().get(moveNumber);
                board = SFENParser.parse(position.getGameSFEN());
                board.setSource(position.getSource());
                board.setDestination(position.getDestination());
                commentTextArea.setText(null);
                commentTextArea.append(position.getComment());
                analysisTable.repaint();
                RenderBoard.loadBoard(board, boardPanel, rotatedView);
                return;
            }
            position = game.getAnalysisPositionList().get(moveNumber-1).get(browsePos);
            board = SFENParser.parse(position.getGameSFEN());
            board.setSource(position.getSource());
            board.setDestination(position.getDestination());
            commentTextArea.setText(null);
            analysisTable.repaint();
            RenderBoard.loadBoard(board, boardPanel, rotatedView);
        }
    }
    
    private void rightButtonAnalysis() {    
                if (browse) {
                    if (browsePos < game.getAnalysisPositionList().get(moveNumber-1).size()-1) {
                        browsePos++;
                    }
                } else {
                    browse = true;
                    browsePos = 0;
                }

                Position position = game.getAnalysisPositionList().get(moveNumber-1).get(browsePos);
                board = SFENParser.parse(position.getGameSFEN());
                board.setSource(position.getSource());
                board.setDestination(position.getDestination());
                commentTextArea.setText(null);
                analysisTable.repaint();
                RenderBoard.loadBoard(board, boardPanel, rotatedView);
    }
    
    transient TableCellRenderer analysisMoveRenderer = new TableCellRenderer() {
        JLabel cellLabel = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
            Color selBG = analysisTable.getSelectionBackground();
            String hexCol = String.format("#%06x", selBG.getRGB() & 0x00FFFFFF);
            if (arg1 != null) {
                if (browse && arg4 == moveNumber-1) {
                    // We are in browse mode and rendering the PV for the active line.
                    StringBuilder cellStrBld = new StringBuilder("<html>");
                    int spaceCount = 0;
                    boolean foundStart = false;
                    boolean foundEnd = false;
                    if (browsePos == 0) {
                        // In this case we insert at the begining.
                        cellStrBld.append("<span style=\"background:");
                        cellStrBld.append(hexCol);
                        cellStrBld.append(";color:white;\">");
                        foundStart = true;
                    }
                    for (int i = 0; i < arg1.toString().length(); i++) {
                        if (!foundEnd && arg1.toString().charAt(i) == '\u3000') {
                            spaceCount++;
                            if (foundStart) {
                                //　This is the end, my friend.
                                cellStrBld.append("</span>\u3000");
                                foundEnd = true;
                            } else {
                                // Is this the start?
                                if (spaceCount == browsePos) {
                                    cellStrBld.append("\u3000<span style=\"background:");
                                    cellStrBld.append(hexCol);
                                    cellStrBld.append(";color:white;\">");
                                    foundStart = true;
                                } else {
                                    // Keep looking.
                                    cellStrBld.append("\u3000");
                                }
                            }
                        } else {
                            // Just a regular char.
                            cellStrBld.append(arg1.toString().charAt(i));
                        }
                    }
                    if (foundStart && !foundEnd) {
                        cellStrBld.append("</span>");
                    }
                    cellStrBld.append("</html>");
                    cellLabel.setText(cellStrBld.toString());
                } else {
                    // Either we are not browsing or we are rendering PV for a non-active line.
                    cellLabel.setText(arg1.toString());
                }
            } else {
                cellLabel.setText("");
            }
            return cellLabel;
        }
    };
        
    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        if (chart != null) {
            chart.getXYPlot().getRangeAxis().setRange(-1000, 1000);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem4ActionPerformed
        if (chart != null) {
            chart.getXYPlot().getRangeAxis().setRange(-2000, 2000);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem4ActionPerformed

    private void jRadioButtonMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem5ActionPerformed
        if (chart != null) {
            chart.getXYPlot().getRangeAxis().setRange(-3000, 3000);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jButton4.setEnabled(false);
        analysisThread.interrupt();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        prefs.put(PREF_LANGUAGE, PREF_LANGUAGE_ENGLISH);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void jRadioButtonMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem6ActionPerformed
        prefs.put(PREF_LANGUAGE, PREF_LANGUAGE_JAPANESE);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }//GEN-LAST:event_jRadioButtonMenuItem6ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        try {
            JOptionPane.showMessageDialog(mainFrame, aboutMessage, null, JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(ImageIO.read(ClassLoader.getSystemClassLoader().getResource(LOGO_NAME))));
        } catch (IOException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
            /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
            * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
             */
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
            if (IS_WINDOWS) {
                java.util.Enumeration<?> keys = UIManager.getDefaults().keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = UIManager.get (key);
                    if (value instanceof javax.swing.plaf.FontUIResource) {
                        UIManager.put (key, new javax.swing.plaf.FontUIResource("Meiryo",Font.PLAIN,12));
                    }
                }
            }            //</editor-fold>
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }

        Desktop desktop = Desktop.getDesktop();
        
        if (IS_MAC) {
            desktop.setAboutHandler(e ->
                {
                try {
                    JOptionPane.showMessageDialog(mainFrame, aboutMessage, null, JOptionPane.INFORMATION_MESSAGE,
                            new ImageIcon(ImageIO.read(ClassLoader.getSystemClassLoader().getResource(LOGO_NAME))));
                } catch (IOException ex) {
                    Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            );
        }

        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            mainFrame = new ShogiExplorer();
            mainFrame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEngineButton;
    private javax.swing.JMenuItem analyseGame;
    private javax.swing.JComboBox<String> analysisEngineComboBox;
    private javax.swing.JTable analysisTable;
    private javax.swing.JSpinner analysisTimePerMoveSpinner;
    private javax.swing.JPanel boardPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JScrollPane commentScrollPane;
    private javax.swing.JTextArea commentTextArea;
    private javax.swing.JButton configureEngineButton;
    private javax.swing.JButton deleteEngineButton;
    private javax.swing.JMenu enginesMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JMenu gameMenu;
    private javax.swing.JScrollPane gameScrollPanel;
    private javax.swing.JTextArea gameTextArea;
    private javax.swing.JDialog jAnalysisDialog;
    private javax.swing.JPanel jAnalysisStartPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JDialog jEngineConfDialog;
    private javax.swing.JPanel jEngineConfPanel;
    private javax.swing.JList<String> jEngineList;
    private javax.swing.JDialog jEngineManagerDialog;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JButton mediaBack;
    private javax.swing.JButton mediaEnd;
    private javax.swing.JButton mediaForward;
    private javax.swing.JButton mediaPlay;
    private javax.swing.JButton mediaReverse;
    private javax.swing.JButton mediaStart;
    private javax.swing.JButton mediaStop;
    private javax.swing.JList<String> moveList;
    private javax.swing.JScrollPane moveListScrollPane;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
}
