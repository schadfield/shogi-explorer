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
import objects.Board;
import main.RenderBoard;
import main.SFENParser;
import objects.Game;
import objects.Position;

public class ShogiExplorer extends javax.swing.JFrame {

    Board board;
    Preferences prefs;
    Game game;
    int moveNumber;
    boolean play;
    DefaultListModel moveListModel = new DefaultListModel();
    DefaultListModel engineListModel = new DefaultListModel();
    boolean rotatedView;
    FileNameExtensionFilter kifFileFilter;

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
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        kifFileChooser = new javax.swing.JFileChooser();
        jDialog1 = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
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

        kifFileChooser.setFileFilter(kifFileFilter);

        jDialog1.setAlwaysOnTop(true);
        jDialog1.setModal(true);
        jDialog1.setSize(new java.awt.Dimension(400, 300));

        jList1.setModel(engineListModel);
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle"); // NOI18N
        setTitle(bundle.getString("ShogiExplorer.title_1")); // NOI18N
        setAlwaysOnTop(true);
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
            .addGap(0, 105, Short.MAX_VALUE)
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
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(moveListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(commentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE))
                            .addComponent(gameScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mainToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gameScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(144, 144, 144)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(commentScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(moveListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
        kifFileChooser.setCurrentDirectory(dirFile);
        kifFileChooser.showOpenDialog(mainToolBar);
        File kifFile = kifFileChooser.getSelectedFile();
        if (kifFile == null) {
            return;
        }
        prefs.put("fileOpenDir", kifFile.getParent());
        try {
            game = main.KifParser.parseKif(moveListModel, kifFile);
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
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void mediaForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaForwardActionPerformed
        if (!play && game != null && moveNumber < game.getPositionList().size() + 1) {
            moveList.setSelectedIndex(moveNumber +1 );
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
                        if (moveNumber < game.getPositionList().size()-1) {
                            moveList.setSelectedIndex(moveNumber+1);
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
                            moveList.setSelectedIndex(moveNumber - 1);
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
        jDialog1.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

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
    private javax.swing.JPanel boardPanel;
    private javax.swing.JScrollPane commentScrollPane;
    private javax.swing.JTextArea commentTextArea;
    private javax.swing.JScrollPane gameScrollPanel;
    private javax.swing.JTextArea gameTextArea;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFileChooser kifFileChooser;
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
    // End of variables declaration//GEN-END:variables
}
