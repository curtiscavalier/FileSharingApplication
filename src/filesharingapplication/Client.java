/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesharingapplication;

import static filesharingapplication.GUI.allOnlineIps;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;


public class Client {

    Socket socket;
    Map filesSortedByServer = new HashMap();

    BufferedReader in;
    PrintWriter out;
    final int PORT = 8888;
    String ip;
    int read = 0;
    JLabel downloadSpeedLabelGUI;

    public Client() {

    }

    public void connectTo(String ip, int port) throws IOException {
        this.ip = ip;
        socket = new Socket(ip, port);
    }

    public String sendMessage(String ip, String message) throws IOException {
        String response = "";

        connectTo(ip, PORT);

        OutputStream outToServer = socket.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        out.writeUTF(message);

        InputStream inFromServer = socket.getInputStream();
        DataInputStream in
                = new DataInputStream(inFromServer);

        response = in.readUTF();
        System.out.println(response);
        //System.out.println("Server says " + response);

        return response;
    }

    public void listDownloadableFiles(String ip) throws IOException {
        // ArrayList<String> sharedFiles = new ArrayList<String>();
        String response = "";

        response = sendMessage(ip, "SHARED"); // files paths concatenatd together with a separator

        if (((Set<String>) filesSortedByServer.get(ip)) == null) {
            filesSortedByServer.put(ip, new HashSet<String>());
        }

        String[] arrayOfFiles = parseString(response);

        Set<String> serverWithIpFiles = (Set<String>) filesSortedByServer.get(ip);
        // s
        for (int i = 0; i < arrayOfFiles.length; ++i) {
            String length = sendMessage(ip, "$SIZE$" + arrayOfFiles[i]);
            serverWithIpFiles.add(arrayOfFiles[i] + "<>" + length);
        }
        //sendMessage(ip,"FINISHED");
    }

    private void saveFile(String name, int fileSize, JProgressBar progressBar, int downloadSpeed) throws IOException, InterruptedException {
        //this.connectTo(ip, PORT);
         int i = 1000;
        if (downloadSpeed < 0) {
            i = 1;
            downloadSpeed *= -1;
        }
        
        read = 0;
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        FileOutputStream fos = new FileOutputStream(name);
        byte[] buffer = new byte[downloadSpeed];
        progressBar.setMaximum(fileSize);

        int totalRead = 0;
        int remaining = fileSize;
        long time1 = System.currentTimeMillis();
        int counter = 0;
       

        while ((read = dis.read(buffer, 0, buffer.length)) > 0) {
            if (i == 1) {

                Thread.sleep(1000);
            }
            if (counter++ == 0) {
                downloadSpeedLabelGUI.setText(i * read + " B/Second");
            }
            totalRead += read;
            remaining -= read;
//            System.out.println("read " + totalRead + " bytes.");
//            System.out.println("Read: " + read);
            fos.write(buffer, 0, read);
            // System.out.println("Time of transfer "+(System.currentTimeMillis()-time1) + " =---------------------");
            if ((System.currentTimeMillis() - time1) > 2000) {
                downloadSpeedLabelGUI.setText(i * read + " B/Second");
                time1 = System.currentTimeMillis();
            }

            progressBar.setValue(totalRead);
            if (read != -1) {
                counter = read;
            }
//            
        }
        downloadSpeedLabelGUI.setText(counter + " B/Second");
        read = 0;
        fos.close();
        dis.close();
    }

    public void downloadFile(String ip, String path, JProgressBar progressBar, int downloadSpeed, JLabel downLabel) throws IOException, InterruptedException {
//        String ip = socket.getRemoteSocketAddress().toString();
        //  connectTo(ip, PORT);
        this.downloadSpeedLabelGUI = downLabel;
        int fileSize = Integer.parseInt(this.sendMessage(ip, path));
        String fileName = getFileName(path);
        saveFile(fileName, fileSize, progressBar, downloadSpeed);

        File file = new File(fileName);

        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;

    }

    private String[] parseString(String s) {
        String[] splitted = s.split(" <> ");

        return splitted;
    }

    public static String getFileName(String path) {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] pathSeperated = path.split(pattern);

        return pathSeperated[pathSeperated.length - 1];
    }

    public void removeDuplicate(ArrayList<String> allOnlineIps) {

        for (int i = 0; i < filesSortedByServer.size(); ++i) {
            if (filesSortedByServer.get(allOnlineIps.get(i)) == null) {
                continue;
            }
            for (int j = 0; j < allOnlineIps.size(); ++j) {
                String key = allOnlineIps.get(j);
                if (filesSortedByServer.get(key) == null) {
                    continue;
                }
//                if (filesSortedByServer.get(key) == null) {
//                    System.out.println(filesSortedByServer.remove(filesSortedByServer.get(key)));
//                    continue;
//                }
                Object x = filesSortedByServer.get(allOnlineIps.get(i));
                Object y = filesSortedByServer.get(key);
                if (x == null || y == null) {
                    continue;
                }
                if (x.equals(y)) {
                    filesSortedByServer.remove(key);
                    i = -1;
                    break;
                    // j = -1;
                }
            }
        }

        for (int i = 0; i < allOnlineIps.size(); ++i) {
            if (filesSortedByServer.get(allOnlineIps.get(i)) == null) {
                allOnlineIps.remove(i);

                i = -1;
            }
        }
        System.out.println();
    }
}
