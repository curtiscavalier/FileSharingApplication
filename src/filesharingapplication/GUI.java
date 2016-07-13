/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesharingapplication;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {

    //
    GUI t = this;
    static ArrayList<String> allOnlineIps;
    static Server server; // CONTAIN SERVER SOCKETS  /// RESPONSIBLE FOR SEND 
    static Client client;  // CONTAIN  CLIENT SOCKETS/// RESPONSIBLE FOR RECEIVING

    Thread serverThread;
    static Thread clientThread;

    //
    JLabel scaningForIps;
    JLabel specificLabel;
    static JLabel downloadSpeedLabel;

    JButton startClient;
    static JButton getList;
    JButton donwloadButton;
    static JButton mySharedPathButton;
    //static JButton connectDirectly;

    JTextField sharedPathField;
    JTextField specificIpField;
    JTextField downloadSpeed;

    JScrollPane scrollPane1;
    JScrollPane scrollPane2;

    JTable ipAddressTable;
    JTable sharedFilesTable;

    JPanel ipAddressPanel;
    JPanel mainPanel;
    JPanel sharedFilesPanel;

    JProgressBar progressBar;

    //-----------------------------------//
    public static void main(String[] args) {

        try {
            new GUI();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public GUI() throws IOException {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }

//------------------------------------------------------------------//
        // frame and its main panel
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //  setResizable(false);

        //this.setLayeredPane(new GridLayout(2,2)) ;
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // to Stop running threads of GUI closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (serverThread != null && serverThread.isAlive()) {
                    serverThread.interrupt();
                }
            }
        });
//--------------------------------------------------------------------------//
        //upper panel
        JPanel buttonPanels = new JPanel(new FlowLayout());

        this.scaningForIps = new JLabel("");
        scaningForIps.setForeground(Color.red);
        startClient = new JButton("Start Client");

        getList = new JButton("Get List");
        sharedPathField = new JTextField("", 45);
        mySharedPathButton = new JButton("My Shared path");

        //connectDirectly = new JButton("Connect Directly");
        sharedPathField = new JTextField("", 45);;

        buttonPanels.add(startClient);

        buttonPanels.add(getList);

        buttonPanels.add(sharedPathField);

        buttonPanels.add(mySharedPathButton);

        buttonPanels.add(scaningForIps);

//------------------------------------------------------------------------//
        //Connect direcly method
        JPanel specificFieldPane = new JPanel(new FlowLayout());
        JLabel l = new JLabel("Your IP Address is: " + InetAddress.getLocalHost().getHostAddress());
        l.setForeground(Color.red);
        this.specificLabel = new JLabel("Enter Specific address to connect to");
        specificIpField = new JTextField("", 45);
        specificFieldPane.add(specificLabel);
        specificFieldPane.add(specificIpField);
        specificFieldPane.add(l);
       ///////////////////////////////////////////////////////////////

        //
        //ip panel
        //------/
        ipAddressPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        ipAddressTable = new JTable();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ipAddressTable.setModel(
                new DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                            "Ip Address"
                        }
                ));

        scrollPane1.setViewportView(ipAddressTable);

        GroupLayout ipAddressPanelLayout = new GroupLayout(ipAddressPanel);

        ipAddressPanel.setLayout(ipAddressPanelLayout);

        ipAddressPanelLayout.setHorizontalGroup(
                ipAddressPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
        );
        ipAddressPanelLayout.setVerticalGroup(
                ipAddressPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(ipAddressPanelLayout.createSequentialGroup()
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());

        //getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(ipAddressPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(ipAddressPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
        );

        //------------------------------------------------------------------------//
        //shared files  panel
//        frame.add(mainPanel);
        //------/
        sharedFilesPanel = new JPanel();
        scrollPane2 = new JScrollPane();
        sharedFilesTable = new JTable();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        sharedFilesTable.setModel(
                new javax.swing.table.DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                            "File Name", "Ip Address", "File size",}
                ));
        scrollPane2.setViewportView(sharedFilesTable);

        GroupLayout sharedFilesPanelLayout = new GroupLayout(sharedFilesPanel);

        sharedFilesPanel.setLayout(sharedFilesPanelLayout);

        sharedFilesPanelLayout.setHorizontalGroup(
                sharedFilesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
        );
        sharedFilesPanelLayout.setVerticalGroup(
                sharedFilesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(sharedFilesPanelLayout.createSequentialGroup()
                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE))
        );

        //---------------------------------------------------------------//
        //Progress bar
        //
        JPanel progressBarPanel = new JPanel(new FlowLayout());
        progressBar = new JProgressBar();

        progressBar.setMinimum(
                0);
        progressBar.setMaximum(
                100);
        progressBar.setPreferredSize(
                new Dimension(600, 20));
        donwloadButton = new JButton("Download");

        progressBarPanel.add(progressBar);

        progressBarPanel.add(donwloadButton);
        //--------------------------------------------------------------
        //download Speed per second
        JPanel bandwidthPanel = new JPanel(new FlowLayout());
        downloadSpeed = new JTextField();
        JLabel downLabel = new JLabel("Download speed: B/Second (MAX IS :6.5 Mbs/Sec)");
        downloadSpeedLabel = new JLabel("");
        downloadSpeedLabel.setForeground(Color.red);

        downloadSpeed.setPreferredSize(
                new Dimension(200, 20));
        bandwidthPanel.add(downLabel);

        bandwidthPanel.add(downloadSpeed);
        bandwidthPanel.add(downloadSpeedLabel);

        //-----------------------------------------------------------------
        //add all panels
        mainPanel.add(buttonPanels);
        mainPanel.add(specificFieldPane);
//        mainPanel.add(connectDirectlyPanels);
        mainPanel.add(ipAddressPanel);

        mainPanel.add(sharedFilesPanel);

        mainPanel.add(bandwidthPanel);

        mainPanel.add(progressBarPanel);

        JScrollPane mainScroll = new JScrollPane(mainPanel);

        add(mainScroll);

        setVisible(
                true);
        //---------------------------------------------------------------------------------------------------------------//
        getList.setEnabled(
                false);
        donwloadButton.setEnabled(
                false);

        startClient.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {

                        //startClient.setEnabled(false); 
                        if (serverThread != null && serverThread.isAlive()) {
                            serverThread.interrupt();
                        }
                        try {
                            server = new Server(8888, sharedPathField.getText());
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                            return;
                        }
                        getList.setEnabled(true);
                        donwloadButton.setEnabled(true);
                        serverThread = new ServerThread(t);
                        serverThread.start();
                    }
                }
        );
        getList.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        try {
                            getList.setEnabled(false);
                            mySharedPathButton.setEnabled(false);
                            String ip = specificIpField.getText();

//                            String myIp = InetAddress.getLocalHost().getHostAddress();
//                            String subnet = myIp.substring(0, myIp.lastIndexOf("."));
//
                            scanNetworkForUsers(ip, ipAddressTable, scaningForIps, sharedFilesTable);

                        } catch (Exception ee) {
                            JOptionPane.showMessageDialog(null, "error in get list button listener");

                        }
                    }
                }
        );
        mySharedPathButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setCurrentDirectory(new java.io.File("."));
                        chooser.setDialogTitle("Choose Output Directory");
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        chooser.setAcceptAllFileFilterUsed(false);

                        if (chooser.showOpenDialog(t) == JFileChooser.APPROVE_OPTION) {
                            sharedPathField.setText(chooser.getSelectedFile() + "");

                        }
                    }
                }
        );
        donwloadButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        try {
                            Integer.parseInt(downloadSpeed.getText());
                        } catch (Exception ee) {
                            JOptionPane.showMessageDialog(null, "Please specify download speed by bytes before downloading");
                            return;
                        }
                        int row = sharedFilesTable.getSelectedRow();
                        if (row == -1) {
                            JOptionPane.showMessageDialog(null, "Please select a file first");
                            return;
                        }
                        String ip = (String) sharedFilesTable.getValueAt(row, 1);
                        String path = filePathWithName(ip, (String) sharedFilesTable.getValueAt(row, 0));

                        Thread dLoad = new DownloadThread(ip, path.split("<>")[0], progressBar, Integer.parseInt(downloadSpeed.getText()));
                        dLoad.start();

                    }
                }
        );
    }

    public String filePathWithName(String ip, String fileName) {
        String res = "";
        String item;
        String itemName;
        Set<String> set = (Set<String>) client.filesSortedByServer.get(ip);
        Iterator<String> it = set.iterator();
        int i = 0;
        while (it.hasNext()) {
            res = it.next();
            item = res.split("<>")[0];
            itemName = Client.getFileName(item);
            if (itemName.equals(fileName)) {
                return res;
            }
        }

        return res;
    }

    public static void scanNetworkForUsers(String myIp, JTable ipTable, JLabel status, JTable sharedFilesTable) {
        allOnlineIps = new ArrayList<String>();
//        ipTable.setModel(new javax.swing.table.DefaultTableModel(
//                new Object[][]{},
//                new String[]{
//                    "Ip Address"
//                }
//        ));
        status.setText("Scanning for online IPs, This takes about 1 min");
        Thread scan = new IpScannerImplementer(myIp, ipTable, status, sharedFilesTable);
        scan.start();

    }

    public void fillSharedFilesTable() {
        client = new Client();

    }
//-----------------------------------------------------------------------------------------------------------------//

    static class IpScannerImplementer extends Thread {

        ArrayList<String> list;
        String subnet;
        JTable ipAddressTable;
        JTable sharedFilesTable;
        JLabel status;

        public IpScannerImplementer(String subnet, JTable ipAddressTable, JLabel status, JTable sharedFilesTable) {
            this.list = list;
            this.subnet = subnet;
            this.ipAddressTable = ipAddressTable;
            this.sharedFilesTable = sharedFilesTable;
            this.status = status;
        }

        @Override
        public void run() {
            try {
                client = new Client();
                DefaultTableModel model = (DefaultTableModel) ipAddressTable.getModel();

                allOnlineIps.add(subnet);
                System.out.println(subnet);
                model.addRow(new Object[]{subnet});

                //    }
                //   }
                //System.out.println(System.currentTimeMillis() - time1);
                clientThread = new ClientThread(sharedFilesTable, status);

                clientThread.start();

                getList.setEnabled(true);
                mySharedPathButton.setEnabled(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }

    }

    static class ServerThread extends Thread {

        GUI gui;

        public ServerThread(GUI gui) {
            this.gui = gui;
        }

        @Override
        public void run() {
            try {
                server.openForRequests();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage() + " Server services has stopped");
            }
        }

    }

    static class ClientThread extends Thread {

        //GUI gui;
        JTable sharedFilesTable;
        JLabel status;

        public ClientThread(JTable sharedFilesTable, JLabel status) {
            this.sharedFilesTable = sharedFilesTable;
            this.status = status;

        }

        @Override
        public void run() {
            String response;
            status.setText("Filling shared files table");
            DefaultTableModel model = (DefaultTableModel) sharedFilesTable.getModel();
            for (int i = 0; i < allOnlineIps.size(); ++i) {
                try {
                    client.listDownloadableFiles(allOnlineIps.get(i));
                    String[] filesOfCurrentIp = setToStringArray((Set<String>) client.filesSortedByServer.get(allOnlineIps.get(i)));

                    for (int j = 0; j < filesOfCurrentIp.length; ++j) {
                        String[] x = filesOfCurrentIp[j].split("<>"); // separate file path from its size
                        model.addRow(new Object[]{Client.getFileName(x[0]), allOnlineIps.get(i), x[1] + " B"});
                    }
                } catch (IOException ex) {
                    // JOptionPane.showMessageDialog(null, "Failed to fill shared files table, Please re getList" + ex.getMessage());
                }
            }
            status.setText("Done");

        }

        public String[] setToStringArray(Set<String> set) {
            String[] result = new String[set.size()];
            Iterator<String> it = set.iterator();
            int i = 0;
            while (it.hasNext()) {
                result[i++] = it.next();
            }

            return result;
        }
    }

    static class DownloadThread extends Thread {

        String ip, path;
        JProgressBar progressBar;
        int downloadSpeed;

        public DownloadThread(String ip, String path, JProgressBar progressBar, int downloadSpeed) {
            this.ip = ip;
            this.path = path;
            this.progressBar = progressBar;
            this.downloadSpeed = downloadSpeed;
        }

        @Override
        public void run() {
            try {
//                if(downloadSpeed> 6500000) 
//                {
//                     JOptionPane.showMessageDialog(null, "Max Speed is 6500,000 B/Second");
//                }
                if (downloadSpeed >= 1000) {
                    client.downloadFile(ip, "$FILE$" + path.split("<>")[0], progressBar, downloadSpeed / 1000, downloadSpeedLabel);//separate file path from its size
                } else {
                    client.downloadFile(ip, "$FILE$" + path.split("<>")[0], progressBar, downloadSpeed*-1, downloadSpeedLabel);//separate file path from its size
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
            catch(InterruptedException ex)
            {
                  JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }

    }

}
