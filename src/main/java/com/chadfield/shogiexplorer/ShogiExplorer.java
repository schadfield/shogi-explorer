package com.chadfield.shogiexplorer;

import com.chadfield.shogiexplorer.main.ConfigurationManager;
import com.chadfield.shogiexplorer.main.EngineManager;
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
import com.chadfield.shogiexplorer.objects.Engine;
import com.chadfield.shogiexplorer.objects.Game;
import com.chadfield.shogiexplorer.objects.Position;
import java.util.ArrayList;
import java.util.List;

public class ShogiExplorer extends javax.swing.JFrame {

    Board board;
    Preferences prefs;
    Game game;
    int moveNumber;
    boolean play;
    DefaultListModel moveListModel = new DefaultListModel();
    DefaultListModel engineListModel = new DefaultListModel();
    boolean rotatedView;
    List<Engine> engineList = new ArrayList<>();
    FileNameExtensionFilter kifFileFilter;
    File newEngineFile;

    /**
     * Creates new form NewJFrame
     */
    public ShogiExplorer() {

        ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
        this.kifFileFilter = new FileNameExtensionFilter(bundle.getString("label_kif_files"), "kif");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        initComponents();
        board = SFENParser.parse(new Board(), "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1");
        prefs = Preferences.userNodeForPackage(ShogiExplorer.class);
        String rotated = (prefs.get("rotated", "false"));
        if (rotated.compareTo("true") == 0) {
            jRadioButtonMenuItem1.doClick();
        }
        engineList = EngineManager.loadEngines(engineListModel);
        if (engineList.size() > 0) {
            jEngineList.setSelectedIndex(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jKifFileChooser = new javax.swing.JFileChooser();
        jEngineManagerDialog = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEngineList = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        configureEngineButton = new javax.swing.JButton();
        deleteEngineButton = new javax.swing.JButton();
        addEngineButton = new javax.swing.JButton();
        renameEngineButton = new javax.swing.JButton();
        jEngineFileChooser1 = new javax.swing.JFileChooser();
        jEngineConfDialog = new javax.swing.JDialog(jEngineManagerDialog);
        jEngineConfPanel = new javax.swing.JPanel();
        mainToolBar = new javax.swing.JToolBar();
        mediaStart = new javax.swing.JButton();
        mediaReverse = new javax.swing.JButton();
        mediaBack = new javax.swing.JButton();
        mediaStop = new javax.swing.JButton();
        mediaForward = new javax.swing.JButton();
        mediaPlay = new javax.swing.JButton();
        mediaEnd = new javax.swing.JButton();
        gameScrollPanel = new javax.swing.JScrollPane();
        gameTextArea = new javax.swing.JTextArea();
        commentScrollPane = new javax.swing.JScrollPane();
        commentTextArea = new javax.swing.JTextArea();
        moveListScrollPane = new javax.swing.JScrollPane();
        moveList = new javax.swing.JList<>();
        boardPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        jKifFileChooser.setFileFilter(kifFileFilter);

        jEngineManagerDialog.setAlwaysOnTop(true);
        jEngineManagerDialog.setModal(true);
        jEngineManagerDialog.setSize(new java.awt.Dimension(400, 300));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jEngineList.setModel(engineListModel);
        jEngineList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jEngineList);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle"); // NOI18N
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

        addEngineButton.setText(bundle.getString("ShogiExplorer.addEngineButton.text")); // NOI18N
        addEngineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEngineButtonActionPerformed(evt);
            }
        });

        renameEngineButton.setText(bundle.getString("ShogiExplorer.renameEngineButton.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(configureEngineButton)
                    .addComponent(deleteEngineButton)
                    .addComponent(addEngineButton)
                    .addComponent(renameEngineButton))
                .addGap(0, 8, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(136, Short.MAX_VALUE)
                .addComponent(addEngineButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteEngineButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(renameEngineButton)
                .addGap(2, 2, 2)
                .addComponent(configureEngineButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout jEngineManagerDialogLayout = new javax.swing.GroupLayout(jEngineManagerDialog.getContentPane());
        jEngineManagerDialog.getContentPane().setLayout(jEngineManagerDialogLayout);
        jEngineManagerDialogLayout.setHorizontalGroup(
            jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jEngineManagerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jEngineManagerDialogLayout.setVerticalGroup(
            jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jEngineManagerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jEngineManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        jEngineConfDialog.setAlwaysOnTop(true);
        jEngineConfDialog.setModal(true);
        jEngineConfDialog.setResizable(false);
        jEngineConfDialog.setSize(new java.awt.Dimension(100, 100));
        jEngineConfDialog.getContentPane().setLayout(new java.awt.FlowLayout());

        jEngineConfPanel.setLayout(new java.awt.GridLayout(0, 4, 20, 0));
        jEngineConfDialog.getContentPane().add(jEngineConfPanel);

        jEngineConfDialog.getAccessibleContext().setAccessibleParent(null);

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

        mediaStop.setFocusable(false);
        mediaStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mediaStop.setLabel(bundle.getString("ShogiExplorer.mediaStop.label_1")); // NOI18N
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 168, Short.MAX_VALUE)
        );

        jMenu1.setText(bundle.getString("ShogiExplorer.jMenu1.text_1")); // NOI18N

        jMenuItem1.setText(bundle.getString("ShogiExplorer.jMenuItem1.text_1")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(bundle.getString("ShogiExplorer.jMenu2.text_1")); // NOI18N
        jMenuBar1.add(jMenu2);

        jMenu3.setLabel(bundle.getString("ShogiExplorer.jMenu3.label_1")); // NOI18N

        jRadioButtonMenuItem1.setLabel(bundle.getString("ShogiExplorer.jRadioButtonMenuItem1.label_1")); // NOI18N
        jRadioButtonMenuItem1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonMenuItem1ItemStateChanged(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem1);

        jMenuBar1.add(jMenu3);

        jMenu4.setText(bundle.getString("ShogiExplorer.jMenu4.text_1")); // NOI18N

        jMenuItem2.setText(bundle.getString("ShogiExplorer.jMenuItem2.text_1")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(moveListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(commentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonMenuItem1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ItemStateChanged
        rotatedView = !rotatedView;
        if (rotatedView) {
            prefs.put("rotated", "true");
        } else {
            prefs.put("rotated", "false");
        }
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        RenderBoard.loadBoard(board, boardPanel, rotatedView);
    }//GEN-LAST:event_jRadioButtonMenuItem1ItemStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // System.out.println("Save window size here?");
    }//GEN-LAST:event_formWindowClosing

    private void boardPanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_boardPanelComponentResized
        RenderBoard.loadBoard(board, boardPanel, rotatedView);
    }//GEN-LAST:event_boardPanelComponentResized

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        File dirFile = new File(prefs.get("fileOpenDir", System.getProperty("user.home")));
        jKifFileChooser.setCurrentDirectory(dirFile);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                jKifFileChooser.showOpenDialog(null);
                File kifFile = jKifFileChooser.getSelectedFile();
                if (kifFile == null) {
                    return;
                }
                prefs.put("fileOpenDir", kifFile.getParent());
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
            }
        });
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void mediaForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaForwardActionPerformed
        if (!play && game != null && moveNumber < game.getPositionList().size() + 1) {
            moveList.setSelectedIndex(moveNumber + 1);
        }
    }//GEN-LAST:event_mediaForwardActionPerformed

    private void mediaBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaBackActionPerformed
        if (!play && moveNumber > 0) {
            moveList.setSelectedIndex(moveNumber - 1);
        }
    }//GEN-LAST:event_mediaBackActionPerformed

    private void mediaStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaStartActionPerformed
        if (!play) {
            moveList.setSelectedIndex(0);
        }
    }//GEN-LAST:event_mediaStartActionPerformed

    private void mediaEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaEndActionPerformed
        if (!play && game != null) {
            moveList.setSelectedIndex(game.getPositionList().size() - 1);
        }
    }//GEN-LAST:event_mediaEndActionPerformed

    private void mediaStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaStopActionPerformed
        play = false;
    }//GEN-LAST:event_mediaStopActionPerformed

    private void mediaPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaPlayActionPerformed
        if (!play && game != null) {
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
                            }
                        } else {
                            play = false;
                        }
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_mediaPlayActionPerformed

    private void moveListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_moveListValueChanged
        if (!evt.getValueIsAdjusting()) {
            moveNumber = moveList.getSelectedIndex();
            if (moveNumber < 0) {
                return;
            }
            moveList.ensureIndexIsVisible(moveNumber);
            Position position = game.getPositionList().get(moveNumber);
            board = SFENParser.parse(new Board(), position.game);
            board.setSource(position.source);
            board.setDestination(position.destination);
            commentTextArea.setText(null);
            commentTextArea.append(position.comment);
            RenderBoard.loadBoard(board, boardPanel, rotatedView);
        }
    }//GEN-LAST:event_moveListValueChanged

    private void mediaReverseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaReverseActionPerformed
        if (!play) {
            new Thread() {
                @Override
                public void run() {
                    play = true;
                    while (play) {
                        if (moveNumber > 0) {
                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    moveList.setSelectedIndex(moveNumber - 1);
                                }
                            });
                            try {
                                Thread.sleep(500L);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
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
        // TODO add your handling code here:
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                jEngineManagerDialog.pack();
                jEngineManagerDialog.setLocationRelativeTo(boardPanel);
                jEngineManagerDialog.setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void deleteEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEngineButtonActionPerformed
        engineList = EngineManager.deleteSelectedEngine(engineListModel, jEngineList, engineList);
    }//GEN-LAST:event_deleteEngineButtonActionPerformed

    private void addEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEngineButtonActionPerformed
        File dirFile = new File(prefs.get("engineOpenDir", System.getProperty("user.home")));
        jEngineFileChooser1.setCurrentDirectory(dirFile);
        jEngineFileChooser1.showOpenDialog(jEngineManagerDialog);
        newEngineFile = jEngineFileChooser1.getSelectedFile();

        if (newEngineFile == null) {
            return;
        }
        prefs.put("engineOpenDir", newEngineFile.getParent());
        EngineManager.addNewEngine(newEngineFile, engineListModel, jEngineList, engineList);
        EngineManager.SaveEngines(engineList);
    }//GEN-LAST:event_addEngineButtonActionPerformed

    private void configureEngineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configureEngineButtonActionPerformed
        if (engineList.size() > 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ConfigurationManager.configureEngine(engineList, engineList.get(jEngineList.getSelectedIndex()), jEngineConfDialog, jEngineManagerDialog, jEngineConfPanel);
                }
            });
        }
    }//GEN-LAST:event_configureEngineButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
            /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
            * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
             */
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //</editor-fold>
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException ex) {
            Logger.getLogger(ShogiExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ShogiExplorer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEngineButton;
    private javax.swing.JPanel boardPanel;
    private javax.swing.JScrollPane commentScrollPane;
    private javax.swing.JTextArea commentTextArea;
    private javax.swing.JButton configureEngineButton;
    private javax.swing.JButton deleteEngineButton;
    private javax.swing.JScrollPane gameScrollPanel;
    private javax.swing.JTextArea gameTextArea;
    private javax.swing.JDialog jEngineConfDialog;
    private javax.swing.JPanel jEngineConfPanel;
    private javax.swing.JFileChooser jEngineFileChooser1;
    private javax.swing.JList<String> jEngineList;
    private javax.swing.JDialog jEngineManagerDialog;
    private javax.swing.JFileChooser jKifFileChooser;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JButton renameEngineButton;
    // End of variables declaration//GEN-END:variables
}
