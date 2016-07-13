/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesharingapplication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Server {

    ServerSocket serverSocket = null;
    String sharedDirectory;

    //  ArrayList<Socket> peersConnectedTo = new ArrayList<Socket>();
    int port;

    public Server(int port, String sharedDirectory) throws IOException {
        this.port = port;
        this.sharedDirectory = sharedDirectory;
        serverSocket = new ServerSocket(port);
    }

    public void openForRequests() throws IOException {
        Socket socket;
        String response = "";

        OutputStream os;
        BufferedReader br;

        while (true) {
            try {
                socket = serverSocket.accept();
                response = "";
 
                //communicate with it. until types FINISHED.
               
//                 while(!response.equals("FINISHED"))
//                 {
                    DataInputStream in
                            = new DataInputStream(socket.getInputStream());
                  response =in.readUTF();
                    System.out.println("client says " + response);

//                    response = br.readLine();
//                    System.out.println("response from client: " + response);
                    if (response.startsWith("$FILE$"))//inquire for a file
                    {
                       
                        File file = new File(  response.replace("$FILE$",""));
                        
                        DataOutputStream out
                                = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(file.length()+"");
                        
                        if (!sendFile(file , socket)) {
                            JOptionPane.showMessageDialog(null, "File failed to be sent!");
                        }

                    } else if (response.equals("SHARED")) {
                        ArrayList<String> sharedFiles = this.queryForSharedFiles( );
                        String allSharedFiles = makeStringOfSharedFiles(sharedFiles);

                         DataOutputStream out
                                = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(allSharedFiles);
 

                        //send paths of shared of shared files
                    }else if (response.startsWith("$SIZE$"))
                    {
                        File file = new File(  response.replace("$SIZE$",""));
                        
                        DataOutputStream out
                                = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(file.length()+"");
                        
                       
                    }
                    else if (response.equals("FINISHED")) {
                        break;
                    }
                // }
                    // response ;
                

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Exception in open for Request : " + e.getMessage());
            }
        }
    }

    public String makeStringOfSharedFiles(ArrayList<String> sharedFiles) {
        String stringOfSharedFiles = "";

        for (int i = 0; i < sharedFiles.size(); ++i) {
            stringOfSharedFiles += sharedFiles.get(i) + " <> ";
        }
          String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] x = stringOfSharedFiles.split( " <> " );
        return stringOfSharedFiles;
    }

    public ArrayList<String> queryForSharedFiles() {
        ArrayList<String> sharedFiles = new ArrayList<String>();

        File folder = new File(this.sharedDirectory);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                sharedFiles.add(listOfFiles[i].getPath());
            }
        }

        return sharedFiles;
    }

    public boolean sendFile(File file, Socket socket ) {
        if (!file.canRead() || !file.exists()) {
            JOptionPane.showMessageDialog(null, "File does not exist or can't read.");
            return false;
        }

        // Get the size of the file
        long length = file.length();
        try {

            byte[] bytes = new byte[ 100*1024];
            InputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
            socket.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }

        return true;
    }

}
