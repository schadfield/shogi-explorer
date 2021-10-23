package com.chadfield.shogiexplorer;

import com.chadfield.shogiexplorer.main.AnalysisManager;
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
import com.chadfield.shogiexplorer.objects.ImageCache;
import com.chadfield.shogiexplorer.objects.Position;
import com.chadfield.shogiexplorer.utils.ImageUtils;
import com.chadfield.shogiexplorer.utils.URLUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitResponse;
import java.awt.event.KeyEvent;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.KeyStroke;
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
    transient ImageCache imageCache;
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
    boolean classic;
    boolean shiftFile;
    boolean shiftURL;
    boolean saveAnalysis;
    transient List<Engine> engineList = new ArrayList<>();
    transient FileNameExtensionFilter kifFileFilter;
    File newEngineFile;
    static JFrame mainFrame;
    boolean inSelectionChange = false;
    static final String PREF_SAVE_ANALYSIS = "saveAnalysis";
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
    static final String PREF_FILE_OPEN_DIR = "fileOpenDir";
    static final String PREF_LANGUAGE = "language";
    static final String PREF_LANGUAGE_ENGLISH = "english";
    static final String PREF_LANGUAGE_JAPANESE = "japanese";
    static final String PREF_CLASSIC = "classic";
    static final String PREF_SHIFT_FILE = "shiftFile";
    static final String PREF_SHIFT_URL = "shiftURL";
    DefaultIntervalXYDataset plotDataset;
    JFreeChart chart;
    ChartPanel chartPanel;
    transient Thread analysisThread;
    File kifFile;
    transient AnalysisParameter analysisParam;
    XYPlot plot;
    String clipboardStr;
    long rotateTime;
    static final String PREF_HEIGHT = "height";
    int mainHeight;
    static final String PREF_WIDTH = "width";
    int mainWidth;
    static final String PREF_DIVIDER_LOCATION = "dividerLocation";
    int dividerLocation;
    String URLStr;
    static final String PREF_AUTO_REFRESH = "autoRefresh";
    boolean autoRefresh;
    javax.swing.Timer refreshTimer = null;

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

        mainHeight = prefs.getInt(PREF_HEIGHT, 650);
        mainWidth = prefs.getInt(PREF_WIDTH, 1000);
        dividerLocation = prefs.getInt(PREF_DIVIDER_LOCATION, 86);

        ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
        this.kifFileFilter = new FileNameExtensionFilter(bundle.getString("label_kif_files"), "kif");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        initComponents();

        setIconImage(ImageUtils.loadIconImageFromResources("logo"));
        jEngineManagerDialog.setIconImage(ImageUtils.loadIconImageFromResources("logo"));

        if (language.contentEquals(PREF_LANGUAGE_JAPANESE)) {
            jRadioButtonMenuItem6.setSelected(true);
        }

        jTabbedPane1.setForeground(Color.BLACK);

        imageCache = new ImageCache();

        board = SFENParser.parse("lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1");

        if (prefs.getBoolean(PREF_SAVE_ANALYSIS, false)) {
            jCheckBox1.setSelected(true);
            saveAnalysis = true;
        } else {
            saveAnalysis = false;
        }

        if (prefs.getBoolean(PREF_AUTO_REFRESH, false)) {
            autoRefreshCheckBoxMenuItem.setSelected(true);
            autoRefresh = true;
        } else {
            autoRefresh = false;
        }

        if (prefs.getBoolean(PREF_CLASSIC, true)) {
            classic = true;
            jRadioButtonMenuItem10.setSelected(true);
            jRadioButtonMenuItem11.setSelected(false);
        } else {
            classic = false;
            jRadioButtonMenuItem10.setSelected(false);
            jRadioButtonMenuItem11.setSelected(true);
        }

        if (prefs.getBoolean(PREF_SHIFT_FILE, false)) {
            shiftFile = true;
            jRadioButtonMenuItem1.setSelected(false);
            jRadioButtonMenuItem7.setSelected(true);
        } else {
            shiftFile = false;
            jRadioButtonMenuItem1.setSelected(true);
            jRadioButtonMenuItem7.setSelected(false);
        }

        if (prefs.getBoolean(PREF_SHIFT_URL, false)) {
            shiftURL = true;
            jRadioButtonMenuItem8.setSelected(false);
            jRadioButtonMenuItem9.setSelected(true);
        } else {
            shiftFile = false;
            jRadioButtonMenuItem8.setSelected(true);
            jRadioButtonMenuItem9.setSelected(false);
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

        if (IS_LINUX) {
            moveList.setFixedCellHeight(analysisTable.getRowHeight());
        }

        Desktop desktop = Desktop.getDesktop();

        if (IS_MAC) {
            jMenuItem6.setVisible(false);
            desktop.setAboutHandler(e
                    -> {
                try {
                    JOptionPane.showMessageDialog(mainFrame, getAboutMessage(), null, JOptionPane.INFORMATION_MESSAGE,
                            new ImageIcon(ImageIO.read(ClassLoader.getSystemClassLoader().getResource(LOGO_NAME))));
                } catch (IOException ex) {
                    Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            );
            desktop.setQuitHandler((QuitEvent evt, QuitResponse res) -> {
                try {
                    prefs.putInt(PREF_HEIGHT, mainFrame.getHeight());
                    prefs.putInt(PREF_WIDTH, mainFrame.getWidth());
                    prefs.putInt(PREF_DIVIDER_LOCATION, jSplitPane1.getDividerLocation());
                    prefs.flush();
                } catch (BackingStoreException ex) {
                    Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            });
        }

        UIManager.put("TabbedPane.selectedForeground", Color.BLACK);
        rotateTime = System.currentTimeMillis();
        initializeChart(true);
        RenderBoard.loadBoard(board, imageCache, boardPanel, rotatedView, classic);
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
        jEngineManagerPanel = new javax.swing.JPanel();
        addEngineButton = new javax.swing.JButton();
        configureEngineButton = new javax.swing.JButton();
        deleteEngineButton = new javax.swing.JButton();
        closeEngineManagerButton = new javax.swing.JButton();
        jEngineConfDialog = new javax.swing.JDialog(jEngineManagerDialog);
        jEngineConfPanel = new javax.swing.JPanel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        JRadioButton graph1000Button = new JRadioButton("1000");
        buttonGroup1.add(graph1000Button);
        JRadioButton graph2000Button = new JRadioButton("2000");
        buttonGroup1.add(graph2000Button);
        JRadioButton graph3000Button = new JRadioButton("3000");
        buttonGroup1.add(graph3000Button);
        buttonGroup2 = new javax.swing.ButtonGroup();
        jAnalysisDialog = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        analysisEngineComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        analysisTimePerMoveSpinner = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        jCheckBoxMenuItem3 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem4 = new javax.swing.JCheckBoxMenuItem();
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
        boardPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        analysisTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        moveListScrollPane = new javax.swing.JScrollPane();
        moveList = new javax.swing.JList<>();
        commentScrollPane = new javax.swing.JScrollPane();
        commentTextArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        gameTextArea = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem7 = new javax.swing.JRadioButtonMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        importURLMenuItem = new javax.swing.JMenuItem();
        refreshMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        autoRefreshCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jRadioButtonMenuItem8 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem9 = new javax.swing.JRadioButtonMenuItem();
        gameMenu = new javax.swing.JMenu();
        analyseGameMenuItem = new javax.swing.JMenuItem();
        resumeAnalysisMenuItem = new javax.swing.JMenuItem();
        stopAnalysisMenuItem = new javax.swing.JMenuItem();
        enginesMenu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jRadioButtonMenuItem10 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem11 = new javax.swing.JRadioButtonMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
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

        deleteEngineButton.setText(bundle.getString("ShogiExplorer.deleteEngineButton.text")); // NOI18N
        deleteEngineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEngineButtonActionPerformed(evt);
            }
        });

        closeEngineManagerButton.setText(bundle.getString("ShogiExplorer.closeEngineManagerButton.text")); // NOI18N
        closeEngineManagerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeEngineManagerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jEngineManagerPanelLayout = new javax.swing.GroupLayout(jEngineManagerPanel);
        jEngineManagerPanel.setLayout(jEngineManagerPanelLayout);
        jEngineManagerPanelLayout.setHorizontalGroup(
            jEngineManagerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(addEngineButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jEngineManagerPanelLayout.createSequentialGroup()
                .addGroup(jEngineManagerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(configureEngineButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeEngineManagerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(deleteEngineButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jEngineManagerPanelLayout.setVerticalGroup(
            jEngineManagerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jEngineManagerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addEngineButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteEngineButton)
                .addGap(8, 8, 8)
                .addComponent(configureEngineButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeEngineManagerButton))
        );

        javax.swing.GroupLayout jEngineManagerDialogLayout = new javax.swing.GroupLayout(jEngineManagerDialog.getContentPane());
        jEngineManagerDialog.getContentPane().setLayout(jEngineManagerDialogLayout);
        jEngineManagerDialogLayout.setHorizontalGroup(
            jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEngineManagerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jEngineManagerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jEngineManagerDialogLayout.setVerticalGroup(
            jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jEngineManagerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                    .addComponent(jEngineManagerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jEngineConfDialog.setAlwaysOnTop(true);
        jEngineConfDialog.setModal(true);
        jEngineConfDialog.setResizable(false);
        jEngineConfDialog.setSize(new java.awt.Dimension(100, 100));
        jEngineConfDialog.getContentPane().setLayout(new java.awt.FlowLayout());

        jEngineConfPanel.setLayout(new java.awt.GridLayout(0, 4, 20, 4));
        jEngineConfDialog.getContentPane().add(jEngineConfPanel);

        jEngineConfDialog.getAccessibleContext().setAccessibleParent(null);

        jAnalysisDialog.setResizable(false);
        jAnalysisDialog.getContentPane().setLayout(new java.awt.FlowLayout());

        jPanel2.setLayout(new java.awt.GridLayout(0, 2, 20, 4));

        jLabel1.setText(bundle.getString("ShogiExplorer.jLabel1.text")); // NOI18N
        jLabel1.setMinimumSize(new java.awt.Dimension(200, 16));
        jPanel2.add(jLabel1);
        jPanel2.add(analysisEngineComboBox);

        jLabel3.setText(bundle.getString("ShogiExplorer.jLabel3.text")); // NOI18N
        jPanel2.add(jLabel3);
        jPanel2.add(analysisTimePerMoveSpinner);

        jLabel2.setText(bundle.getString("ShogiExplorer.jLabel2.text")); // NOI18N
        jPanel2.add(jLabel2);

        jCheckBox1.setText(bundle.getString("ShogiExplorer.jCheckBox1.text")); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(jCheckBox1);

        jButton1.setText(bundle.getString("ShogiExplorer.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jButton2.setText(bundle.getString("ShogiExplorer.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);

        jAnalysisDialog.getContentPane().add(jPanel2);

        jCheckBoxMenuItem2.setSelected(true);
        jCheckBoxMenuItem2.setText(bundle.getString("ShogiExplorer.jCheckBoxMenuItem2.text")); // NOI18N

        jCheckBoxMenuItem3.setSelected(true);
        jCheckBoxMenuItem3.setText(bundle.getString("ShogiExplorer.jCheckBoxMenuItem3.text")); // NOI18N

        jCheckBoxMenuItem4.setSelected(true);
        jCheckBoxMenuItem4.setText(bundle.getString("ShogiExplorer.jCheckBoxMenuItem4.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("ShogiExplorer.title_1")); // NOI18N
        setBounds(new java.awt.Rectangle(58, 25, 1000, 650));
        setMinimumSize(new java.awt.Dimension(1000, 650));
        setPreferredSize(new java.awt.Dimension(mainWidth, mainHeight));
        setSize(new java.awt.Dimension(0, 0));
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

        boardPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        boardPanel.setMaximumSize(new java.awt.Dimension(603, 482));
        boardPanel.setMinimumSize(new java.awt.Dimension(603, 482));
        boardPanel.setPreferredSize(new java.awt.Dimension(603, 482));
        boardPanel.setRequestFocusEnabled(false);

        javax.swing.GroupLayout boardPanelLayout = new javax.swing.GroupLayout(boardPanel);
        boardPanel.setLayout(boardPanelLayout);
        boardPanelLayout.setHorizontalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 601, Short.MAX_VALUE)
        );
        boardPanelLayout.setVerticalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
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
            .addGap(0, 1004, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 53, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(bundle.getString("ShogiExplorer.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jRadioButton1.setText(bundle.getString("ShogiExplorer.jRadioButton1.text")); // NOI18N

        jSplitPane1.setDividerLocation(dividerLocation);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        moveList.setModel(moveListModel);
        moveList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        moveList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                moveListValueChanged(evt);
            }
        });
        moveListScrollPane.setViewportView(moveList);

        commentScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        commentScrollPane.setPreferredSize(new java.awt.Dimension(230, 84));

        commentTextArea.setEditable(false);
        commentTextArea.setColumns(20);
        commentTextArea.setLineWrap(true);
        commentTextArea.setRows(5);
        commentTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        commentTextArea.setEnabled(false);
        commentTextArea.setFocusable(false);
        commentScrollPane.setViewportView(commentTextArea);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(moveListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(moveListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .addComponent(commentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel3);

        gameTextArea.setEditable(false);
        gameTextArea.setColumns(20);
        gameTextArea.setRows(5);
        gameTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        gameTextArea.setEnabled(false);
        gameTextArea.setFocusable(false);
        gameTextArea.setPreferredSize(new java.awt.Dimension(100, 80));
        gameTextArea.setRequestFocusEnabled(false);
        jScrollPane3.setViewportView(gameTextArea);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setTopComponent(jPanel4);

        fileMenu.setText(bundle.getString("ShogiExplorer.fileMenu.text_1")); // NOI18N

        jMenuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        jMenuItem1.setText(bundle.getString("ShogiExplorer.jMenuItem1.text_1")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);
        fileMenu.add(jSeparator4);

        buttonGroup3.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem1.text")); // NOI18N
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jRadioButtonMenuItem1);

        buttonGroup3.add(jRadioButtonMenuItem7);
        jRadioButtonMenuItem7.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem7.text")); // NOI18N
        jRadioButtonMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem7ActionPerformed(evt);
            }
        });
        fileMenu.add(jRadioButtonMenuItem7);
        fileMenu.add(jSeparator5);

        jMenuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        jMenuItem3.setText(bundle.getString("ShogiExplorer.jMenuItem3.text_1")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem3);

        jMenuItem6.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        jMenuItem6.setText(bundle.getString("ShogiExplorer.jMenuItem6.text")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem6);

        jMenuBar1.add(fileMenu);

        jMenu2.setText(bundle.getString("ShogiExplorer.jMenu2.text")); // NOI18N

        importURLMenuItem.setText(bundle.getString("ShogiExplorer.importURLMenuItem.text_1")); // NOI18N
        importURLMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importURLMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(importURLMenuItem);

        refreshMenuItem.setText(bundle.getString("ShogiExplorer.refreshMenuItem.text")); // NOI18N
        refreshMenuItem.setEnabled(false);
        refreshMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(refreshMenuItem);
        jMenu2.add(jSeparator7);

        autoRefreshCheckBoxMenuItem.setText(bundle.getString("ShogiExplorer.autoRefreshCheckBoxMenuItem.text")); // NOI18N
        autoRefreshCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoRefreshCheckBoxMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(autoRefreshCheckBoxMenuItem);
        jMenu2.add(jSeparator6);

        buttonGroup4.add(jRadioButtonMenuItem8);
        jRadioButtonMenuItem8.setSelected(true);
        jRadioButtonMenuItem8.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem8.text")); // NOI18N
        jRadioButtonMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jRadioButtonMenuItem8);

        buttonGroup4.add(jRadioButtonMenuItem9);
        jRadioButtonMenuItem9.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem9.text")); // NOI18N
        jRadioButtonMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jRadioButtonMenuItem9);

        jMenuBar1.add(jMenu2);

        gameMenu.setText(bundle.getString("ShogiExplorer.gameMenu.text_1")); // NOI18N

        analyseGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        analyseGameMenuItem.setText(bundle.getString("ShogiExplorer.analyseGameMenuItem.text")); // NOI18N
        analyseGameMenuItem.setEnabled(false);
        analyseGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAnalyseGameDialog(evt);
            }
        });
        gameMenu.add(analyseGameMenuItem);

        resumeAnalysisMenuItem.setText(bundle.getString("ShogiExplorer.resumeAnalysisMenuItem.text")); // NOI18N
        resumeAnalysisMenuItem.setEnabled(false);
        resumeAnalysisMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeAnalysisMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(resumeAnalysisMenuItem);

        stopAnalysisMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        stopAnalysisMenuItem.setText(bundle.getString("ShogiExplorer.stopAnalysisMenuItem.text_1")); // NOI18N
        stopAnalysisMenuItem.setEnabled(false);
        stopAnalysisMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopAnalysisMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(stopAnalysisMenuItem);

        jMenuBar1.add(gameMenu);

        enginesMenu.setText(bundle.getString("ShogiExplorer.enginesMenu.text_1")); // NOI18N

        jMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        jMenuItem2.setText(bundle.getString("ShogiExplorer.jMenuItem2.text_1")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        enginesMenu.add(jMenuItem2);

        jMenuBar1.add(enginesMenu);

        viewMenu.setLabel(bundle.getString("ShogiExplorer.viewMenu.label_1")); // NOI18N

        jCheckBoxMenuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        jCheckBoxMenuItem1.setText(bundle.getString("ShogiExplorer.jCheckBoxMenuItem1.text")); // NOI18N
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
        viewMenu.add(jCheckBoxMenuItem1);
        viewMenu.add(jSeparator1);

        buttonGroup5.add(jRadioButtonMenuItem10);
        jRadioButtonMenuItem10.setSelected(true);
        jRadioButtonMenuItem10.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem10.text")); // NOI18N
        jRadioButtonMenuItem10.setToolTipText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem10.toolTipText")); // NOI18N
        jRadioButtonMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem10ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem10);

        buttonGroup5.add(jRadioButtonMenuItem11);
        jRadioButtonMenuItem11.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem11.text")); // NOI18N
        jRadioButtonMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem11ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem11);
        viewMenu.add(jSeparator3);

        jRadioButtonMenuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        buttonGroup1.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setSelected(true);
        jRadioButtonMenuItem3.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem3.text")); // NOI18N
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem3);

        jRadioButtonMenuItem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        buttonGroup1.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem4.setText(bundle.getString("ShogiExplorer.jRadioButtonMenuItem4.text")); // NOI18N
        jRadioButtonMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem4ActionPerformed(evt);
            }
        });
        viewMenu.add(jRadioButtonMenuItem4);

        jRadioButtonMenuItem5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
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
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(mainToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSplitPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(boardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                    .addComponent(jSplitPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO: "Save window size here?"
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
            refreshTimer = null;
        }
        refreshMenuItem.setEnabled(false);
        clipboardStr = null;
        jCheckBox1.setEnabled(true);
        File dirFile = new File(prefs.get(PREF_FILE_OPEN_DIR, System.getProperty("user.home")));
        if (IS_MAC) {
            FileDialog fileDialog = new FileDialog(mainFrame);
            fileDialog.setDirectory(dirFile.getPath());
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setTitle("Select KIF fileFile");
            fileDialog.setVisible(true);
            String name = fileDialog.getFile();
            String dir = fileDialog.getDirectory();
            if (name == null || dir == null) {
                return;
            }
            kifFile = new File(fileDialog.getDirectory(), fileDialog.getFile());
        } else {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(dirFile);
            fileChooser.showOpenDialog(mainFrame);
            kifFile = fileChooser.getSelectedFile();
            if (kifFile == null) {
                return;
            }
        }

        prefs.put(PREF_FILE_OPEN_DIR, kifFile.getParent());
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }

        int dotPos = kifFile.getPath().lastIndexOf(".");
        if (kifFile.getPath().substring(dotPos + 1).contentEquals("kaf")) {
            String newPath = kifFile.getPath().substring(0, dotPos) + ".kif";
            kifFile = new File(newPath);
        }
        parseKifu(false);
        analyseGameMenuItem.setEnabled(true);
        resumeAnalysisMenuItem.setEnabled(false);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void parseKifu(boolean refresh) {
        List<List<Position>> analysisPositionList;
        if (!refresh) {
            DefaultTableModel analysisTableModel = (DefaultTableModel) analysisTable.getModel();
            analysisTableModel.getDataVector().clear();
            jTabbedPane1.setComponentAt(1, new JPanel());
            analysisPositionList = new ArrayList<>();
        } else {
            analysisPositionList = game.getAnalysisPositionList();
        }

        try {
            game = com.chadfield.shogiexplorer.main.KifParser.parseKif(moveListModel, kifFile, clipboardStr, shiftFile, analysisPositionList);
        } catch (IOException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
        gameTextArea.setText(null);
        gameTextArea.append(bundle.getString("label_sente") + ": " + game.getSente() + "\n");
        gameTextArea.append(bundle.getString("label_gote") + ": " + game.getGote() + "\n");
        gameTextArea.append(bundle.getString("label_place") + ": " + game.getPlace() + "\n");
        gameTextArea.append(bundle.getString("label_handicap") + ": " + game.getHandicap() + "\n");
        gameTextArea.append(bundle.getString("label_date") + ": " + game.getDate() + "\n");
        gameTextArea.append(bundle.getString("label_time_limit") + ": " + game.getTimeLimit() + "\n");
        moveNumber = 0;
        if (!refresh) {
            initializeAnalysisParams(true);
            initializeChart(false);
            if (clipboardStr == null) {
                AnalysisManager.load(kifFile, game, analysisTable, analysisParam, plot);
            }
            moveList.setSelectedIndex(0);
        } else {
            initializeAnalysisParams(false);
        }
    }

    private void initializeAnalysisParams(boolean initChart) {
        if (initChart) {
            analysisParam = new AnalysisParameter();
        }
        analysisParam.setAnalysisTimePerMove(analysisTimePerMove);
        analysisParam.setGraphView1(jRadioButtonMenuItem3);
        analysisParam.setGraphView2(jRadioButtonMenuItem4);
        analysisParam.setGraphView3(jRadioButtonMenuItem5);
        analysisParam.setHaltAnalysisButton(jButton4);
        analysisParam.setAnalyseGameMenuItem(analyseGameMenuItem);
        analysisParam.setStopAnalysisMenuItem(stopAnalysisMenuItem);
        analysisParam.setResumeAnalysisMenuItem(resumeAnalysisMenuItem);
        analysisParam.setKifFile(kifFile);
        if (initChart) {
            analysisParam.setX1Start(new double[]{});
            analysisParam.setX1(new double[]{});
            analysisParam.setX1End(new double[]{});
            analysisParam.setY1Start(new double[]{});
            analysisParam.setY1(new double[]{});
            analysisParam.setY1End(new double[]{});
            analysisParam.setX2Start(new double[]{});
            analysisParam.setX2(new double[]{});
            analysisParam.setX2End(new double[]{});
            analysisParam.setY2Start(new double[]{});
            analysisParam.setY2(new double[]{});
            analysisParam.setY2End(new double[]{});
        }
    }

    private void initializeChart(boolean anal) {
        plotDataset = new DefaultIntervalXYDataset();
        chart = ChartFactory.createXYBarChart("", "", false, "", plotDataset);

        plot = chart.getXYPlot();
        plot.setRenderer(0, new XYBarRenderer());
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setShadowVisible(false);
        if (game != null) {
            if (anal) {
                if (game.isHandicap()) {
                    renderer.setSeriesPaint(0, Color.WHITE);
                    renderer.setSeriesPaint(1, Color.RED);
                    renderer.setSeriesPaint(2, Color.BLACK);
                } else {
                    renderer.setSeriesPaint(0, Color.BLACK);
                    renderer.setSeriesPaint(1, Color.RED);
                    renderer.setSeriesPaint(2, Color.WHITE);
                }
            } else {
                if (game.isHandicap()) {
                    renderer.setSeriesPaint(0, Color.WHITE);
                    renderer.setSeriesPaint(1, Color.BLACK);
                    renderer.setSeriesPaint(2, Color.RED);
                } else {
                    renderer.setSeriesPaint(0, Color.BLACK);
                    renderer.setSeriesPaint(1, Color.WHITE);
                    renderer.setSeriesPaint(2, Color.RED);
                }
            }
        }
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
                ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
                Rectangle2D dataArea = info.getPlotInfo().getDataArea();
                ValueAxis domainAxis = plot.getDomainAxis();
                RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
                double chartX = domainAxis.java2DToValue(p.getX(), dataArea,
                        domainAxisEdge);
                moveList.setSelectedIndex((int) Math.round(chartX + 0.5));
            }
        });
    }

    private void mediaForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaForwardActionPerformed
        if (!play && game != null && !analysing.get() && moveNumber < game.getPositionList().size() + 1) {
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
                inSelectionChange = false;
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
                if (moveNumber == 0) {
                    x3Start[0] = moveNumber;
                    x3[0] = 0;
                    x3End[0] = moveNumber + 0.04;
                    x3Start[1] = moveNumber;
                    x3[1] = 0;
                    x3End[1] = moveNumber + 0.04;
                } else {
                    x3Start[0] = moveNumber - 0.02;
                    x3[0] = 0;
                    x3End[0] = moveNumber + 0.02;
                    x3Start[1] = moveNumber - 0.02;
                    x3[1] = 0;
                    x3End[1] = moveNumber + 0.02;
                }
                y3Start[0] = 0;
                y3[0] = 3000;
                y3End[0] = 0;
                y3Start[1] = 0;
                y3[1] = -3000;
                y3End[1] = 0;
                double[][] data3 = new double[][]{x3, x3Start, x3End, y3, y3Start, y3End};
                plotDataset.addSeries("M", data3);
            }
            analysisTable.repaint();
            RenderBoard.loadBoard(board, imageCache, boardPanel, rotatedView, classic);
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
        if (IS_MAC) {
            FileDialog fileChooser = new FileDialog(mainFrame);
            fileChooser.setDirectory(dirFile.getPath());
            fileChooser.setMode(FileDialog.LOAD);
            fileChooser.setTitle("Select engine executable");
            fileChooser.setVisible(true);
            String name = fileChooser.getFile();
            String dir = fileChooser.getDirectory();
            if (name == null || dir == null) {
                return;
            }
            newEngineFile = new File(fileChooser.getDirectory(), fileChooser.getFile());
        } else {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(dirFile);
            fileChooser.showOpenDialog(jEngineManagerDialog);
            newEngineFile = fileChooser.getSelectedFile();
            if (newEngineFile == null) {
                return;
            }
        }

        prefs.put("engineOpenDir", newEngineFile.getParent());

        EngineManager.addNewEngine(newEngineFile, engineListModel, jEngineList, engineList);
        EngineManager.saveEngines(engineList);
    }//GEN-LAST:event_addEngineButtonActionPerformed

    private void configureEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configureEngineButtonActionPerformed
        if (!engineList.isEmpty()) {
            java.awt.EventQueue.invokeLater(()
                    -> ConfigurationManager.configureEngine(engineList, engineList.get(jEngineList.getSelectedIndex()), jEngineConfDialog, jEngineManagerDialog, jEngineConfPanel));
        }
    }//GEN-LAST:event_configureEngineButtonActionPerformed

    private void openAnalyseGameDialog(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAnalyseGameDialog
        analysisEngineComboBox.removeAllItems();
        for (Engine engine : engineList) {
            analysisEngineComboBox.addItem(engine.getName());
            if (engine.getName().contentEquals(analysisEngineName)) {
                analysisEngineComboBox.setSelectedItem(analysisEngineName);
            }
        }
        analysisTimePerMoveSpinner.setModel(new SpinnerNumberModel(
                analysisTimePerMove,
                3,
                60,
                1
        ));
        jAnalysisDialog.pack();
        jAnalysisDialog.setLocationRelativeTo(mainFrame);
        jAnalysisDialog.setVisible(true);
        jButton1.requestFocus();
    }//GEN-LAST:event_openAnalyseGameDialog

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jAnalysisDialog.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jAnalysisDialog.setVisible(false);
        if (analysing.get()) {
            return;
        }
        if (game == null || game.getPositionList().size() < 2) {
            return;
        }
        browse = false;
        analysisEngineName = (String) analysisEngineComboBox.getSelectedItem();
        analysisTimePerMove = (int) analysisTimePerMoveSpinner.getValue();
        prefs.put(PREF_ANALYSIS_ENGINE_NAME, analysisEngineName);
        prefs.putInt(PREF_ANALYSIS_TIME_PER_MOVE, analysisTimePerMove);
        initializeAnalysisParams(true);
        initializeChart(true);
        jButton4.setEnabled(true);
        stopAnalysisMenuItem.setEnabled(true);
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
                    new GameAnalyser().analyse(game, engine, moveList, analysisTable, analysisParam, analysing, plot, saveAnalysis, false);
                } catch (IOException ex) {
                    Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        analyseGameMenuItem.setEnabled(false);
        resumeAnalysisMenuItem.setEnabled(false);
        analysisThread.start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void closeEngineManagerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeEngineManagerButtonActionPerformed
        jEngineManagerDialog.setVisible(false);
    }//GEN-LAST:event_closeEngineManagerButtonActionPerformed

    private void analysisTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_analysisTableKeyReleased
        if (analysing.get()) {
            return;
        }
        int keyCode = evt.getKeyCode();
        switch (keyCode) {
            case 37 ->
                leftButtonAnalysis();
            case 39 ->
                rightButtonAnalysis();
            default ->
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
                RenderBoard.loadBoard(board, imageCache, boardPanel, rotatedView, classic);
                return;
            }
            position = game.getAnalysisPositionList().get(moveNumber - 1).get(browsePos);
            board = SFENParser.parse(position.getGameSFEN());
            board.setSource(position.getSource());
            board.setDestination(position.getDestination());
            commentTextArea.setText(null);
            analysisTable.repaint();
            RenderBoard.loadBoard(board, imageCache, boardPanel, rotatedView, classic);
        }
    }

    private void rightButtonAnalysis() {
        if (browse) {
            if (browsePos < game.getAnalysisPositionList().get(moveNumber - 1).size() - 1) {
                browsePos++;
            }
        } else {
            browse = true;
            browsePos = 0;
        }

        Position position = game.getAnalysisPositionList().get(moveNumber - 1).get(browsePos);
        board = SFENParser.parse(position.getGameSFEN());
        board.setSource(position.getSource());
        board.setDestination(position.getDestination());
        commentTextArea.setText(null);
        analysisTable.repaint();
        RenderBoard.loadBoard(board, imageCache, boardPanel, rotatedView, classic);
    }

    transient TableCellRenderer analysisMoveRenderer = new TableCellRenderer() {
        JLabel cellLabel = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable jTable, Object cellContents, boolean arg2, boolean arg3, int row, int arg5) {
            Color selBG = analysisTable.getSelectionBackground();
            String hexCol = String.format("#%06x", selBG.getRGB() & 0x00FFFFFF);
            if (cellContents != null) {
                if (browse && row == moveNumber - 1) {
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
                    for (int i = 0; i < cellContents.toString().length(); i++) {
                        if (browsePos > 6 && i == 0) {
                            cellStrBld.append("…");
                        }
                        if (!foundEnd && cellContents.toString().charAt(i) == '\u3000') {
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
                                    if (browsePos < 7 || spaceCount > browsePos - 6) {
                                        cellStrBld.append("\u3000");
                                    }
                                }
                            }
                        } else {
                            // Just a regular char.
                            if (browsePos < 7 || spaceCount > browsePos - 7) {
                                cellStrBld.append(cellContents.toString().charAt(i));
                            }
                        }
                    }
                    if (foundStart && !foundEnd) {
                        cellStrBld.append("</span>");
                    }
                    cellStrBld.append("</html>");
                    cellLabel.setText(cellStrBld.toString());
                } else {
                    // Either we are not browsing or we are rendering PV for a non-active line.
                    cellLabel.setText(cellContents.toString());
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
        stopAnalysisMenuItem.setEnabled(false);
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
            JOptionPane.showMessageDialog(mainFrame, getAboutMessage(), null, JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(ImageIO.read(ClassLoader.getSystemClassLoader().getResource(LOGO_NAME))));
        } catch (IOException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        prefs.putInt(PREF_HEIGHT, mainFrame.getHeight());
        prefs.putInt(PREF_WIDTH, mainFrame.getWidth());
        prefs.putInt(PREF_DIVIDER_LOCATION, jSplitPane1.getDividerLocation());
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        saveAnalysis = !saveAnalysis;
        prefs.putBoolean(PREF_SAVE_ANALYSIS, saveAnalysis);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
            refreshTimer = null;
        }
        refreshMenuItem.setEnabled(false);
        Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable transferable = clipBoard.getContents(null);

        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                clipboardStr = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                jCheckBox1.setSelected(false);
                jCheckBox1.setEnabled(false);
                saveAnalysis = false;
                prefs.putBoolean(PREF_SAVE_ANALYSIS, saveAnalysis);
                prefs.flush();
                parseKifu(false);
                analyseGameMenuItem.setEnabled(true);
                resumeAnalysisMenuItem.setEnabled(false);
            } catch (UnsupportedFlavorException | IOException | BackingStoreException ex) {
                Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void stopAnalysisMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopAnalysisMenuItemActionPerformed
        jButton4.setEnabled(false);
        stopAnalysisMenuItem.setEnabled(false);
        analysisThread.interrupt();
    }//GEN-LAST:event_stopAnalysisMenuItemActionPerformed

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        long now = System.currentTimeMillis();
        if (now - rotateTime > 500) {
            rotatedView = !rotatedView;
            RenderBoard.loadBoard(board, imageCache, boardPanel, rotatedView, classic);
            rotateTime = now;
        }
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    private void importURLMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importURLMenuItemActionPerformed
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
            refreshTimer = null;
        }
        if (autoRefresh) {
            refreshMenuItem.setEnabled(false);
        } else {
            refreshMenuItem.setEnabled(true);
        }
        Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable transferable = clipBoard.getContents(null);
        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                URLStr = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                String URLGameStr = URLUtils.readGameURL(URLStr, shiftURL);
                clipboardStr = URLGameStr;
                jCheckBox1.setSelected(false);
                jCheckBox1.setEnabled(false);
                saveAnalysis = false;
                prefs.putBoolean(PREF_SAVE_ANALYSIS, saveAnalysis);
                try {
                    prefs.flush();
                } catch (BackingStoreException ex) {
                    Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
                parseKifu(false);
                analyseGameMenuItem.setEnabled(true);
                resumeAnalysisMenuItem.setEnabled(false);
                java.awt.event.ActionListener taskPerformer = (java.awt.event.ActionEvent evt1) -> {
                    refreshMenuItemActionPerformed(evt1);
                };
                refreshTimer = new javax.swing.Timer(30000, taskPerformer);
                refreshTimer.setRepeats(true);
                if (autoRefresh) {
                    refreshTimer.start();
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_importURLMenuItemActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        shiftFile = false;
        prefs.putBoolean(PREF_SHIFT_FILE, shiftFile);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem7ActionPerformed
        shiftFile = true;
        prefs.putBoolean(PREF_SHIFT_FILE, shiftFile);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem7ActionPerformed

    private void jRadioButtonMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem8ActionPerformed
        shiftURL = false;
        prefs.putBoolean(PREF_SHIFT_URL, shiftURL);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem8ActionPerformed

    private void jRadioButtonMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem9ActionPerformed
        shiftURL = true;
        prefs.putBoolean(PREF_SHIFT_URL, shiftURL);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem9ActionPerformed

    private void jRadioButtonMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem10ActionPerformed
        classic = true;
        RenderBoard.loadBoard(board, imageCache, boardPanel, rotatedView, classic);
        prefs.putBoolean(PREF_CLASSIC, classic);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem10ActionPerformed

    private void jRadioButtonMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem11ActionPerformed
        classic = false;
        RenderBoard.loadBoard(board, imageCache, boardPanel, rotatedView, classic);
        prefs.putBoolean(PREF_CLASSIC, classic);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jRadioButtonMenuItem11ActionPerformed

    private void resumeAnalysisMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeAnalysisMenuItemActionPerformed
        if (analysing.get()) {
            return;
        }
        if (game == null || game.getPositionList().size() < 2) {
            return;
        }
        browse = false;
        analysisEngineName = (String) analysisEngineComboBox.getSelectedItem();
        analysisTimePerMove = (int) analysisTimePerMoveSpinner.getValue();
        prefs.put(PREF_ANALYSIS_ENGINE_NAME, analysisEngineName);
        prefs.putInt(PREF_ANALYSIS_TIME_PER_MOVE, analysisTimePerMove);
        initializeAnalysisParams(false);
        jButton4.setEnabled(true);
        stopAnalysisMenuItem.setEnabled(true);
        analyseGameMenuItem.setEnabled(false);
        resumeAnalysisMenuItem.setEnabled(false);
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
                try {
                    new GameAnalyser().analyse(game, engine, moveList, analysisTable, analysisParam, analysing, plot, saveAnalysis, true);
                } catch (IOException ex) {
                    Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        analysisThread.start();
    }//GEN-LAST:event_resumeAnalysisMenuItemActionPerformed

    private void refreshMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshMenuItemActionPerformed
        String URLGameStr = URLUtils.readGameURL(URLStr, shiftURL);
        clipboardStr = URLGameStr;
        jCheckBox1.setSelected(false);
        jCheckBox1.setEnabled(false);
        saveAnalysis = false;
        prefs.putBoolean(PREF_SAVE_ANALYSIS, saveAnalysis);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        parseKifu(true);
        analyseGameMenuItem.setEnabled(true);
        if (analysisTable.getRowCount() < game.getPositionList().size() - 1) {
            resumeAnalysisMenuItem.setEnabled(true);
        } else {
            resumeAnalysisMenuItem.setEnabled(false);
        }
    }//GEN-LAST:event_refreshMenuItemActionPerformed

    private void autoRefreshCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoRefreshCheckBoxMenuItemActionPerformed
        autoRefresh = autoRefreshCheckBoxMenuItem.getState();
        prefs.putBoolean(PREF_AUTO_REFRESH, autoRefresh);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (autoRefresh) {
            refreshMenuItem.setEnabled(false);
        } else {
            refreshMenuItem.setEnabled(true);
        }

        if (autoRefresh) {
            if (refreshTimer != null && !refreshTimer.isRunning()) {
                refreshTimer.start();
            }
        } else {
            if (refreshTimer != null && refreshTimer.isRunning()) {
                refreshTimer.stop();
            }
        }

    }//GEN-LAST:event_autoRefreshCheckBoxMenuItemActionPerformed

    private String getAboutMessage() {
        String aboutMessage;
        try ( InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("Project.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            aboutMessage = "Shogi Explorer\n\nVersion " + prop.getProperty("project.version")
                    + "\n\nCopyright © 2021 Stephen R Chadfield\nAll rights reserved."
                    + "\n\nPlay more Shogi!";
        } catch (IOException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
        return aboutMessage;
    }

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
                    Object value = UIManager.get(key);
                    if (value instanceof javax.swing.plaf.FontUIResource) {
                        UIManager.put(key, new javax.swing.plaf.FontUIResource("Meiryo", Font.PLAIN, 12));
                    }
                }
            }            //</editor-fold>
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            mainFrame = new ShogiExplorer();
            mainFrame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEngineButton;
    private javax.swing.JMenuItem analyseGameMenuItem;
    private javax.swing.JComboBox<String> analysisEngineComboBox;
    private javax.swing.JTable analysisTable;
    private javax.swing.JSpinner analysisTimePerMoveSpinner;
    private javax.swing.JCheckBoxMenuItem autoRefreshCheckBoxMenuItem;
    private javax.swing.JPanel boardPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JButton closeEngineManagerButton;
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
    private javax.swing.JTextArea gameTextArea;
    private javax.swing.JMenuItem importURLMenuItem;
    private javax.swing.JDialog jAnalysisDialog;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem3;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem4;
    private javax.swing.JDialog jEngineConfDialog;
    private javax.swing.JPanel jEngineConfPanel;
    private javax.swing.JList<String> jEngineList;
    private javax.swing.JDialog jEngineManagerDialog;
    private javax.swing.JPanel jEngineManagerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem10;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem11;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem6;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem7;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem8;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JSplitPane jSplitPane1;
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
    private javax.swing.JMenuItem refreshMenuItem;
    private javax.swing.JMenuItem resumeAnalysisMenuItem;
    private javax.swing.JMenuItem stopAnalysisMenuItem;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
}
