package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket server;
    private Socket connection;

    public Server() {
        super("Messenger test 1.. 2.. 3.. !");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                e -> {
                    sendMessage(e.getActionCommand());
                    userText.setText("");
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300, 150);
        setVisible(true);
    }

    public void startRunning() {
        try {
            server = new ServerSocket(1234, 100);
            while (true) {
                try {
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                } catch (EOFException eofException) {
                    showMessage("\n Server ended the connection!");
                } finally {
                    closeCrap();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException {
        showMessage("Waiting for someone to connect ...\n");
        connection = server.accept();
        showMessage(" Now connected to " + connection.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\n  Streams are now setup! ");
    }

    private void whileChatting() throws IOException {
        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);
        do {

            try {
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException classNotFoundException) {
                showMessage(" \n whaaaaaaaaaat?");
            }

        } while (!message.equals("CLIENT - END "));
    }

    private void closeCrap() {
        showMessage(" \n Closing connection ... \n");
        ableToType(false);
        try{
            outputStream.close();
            inputStream.close();
            connection.close();
        }catch (IOException ioException){
            ioException.printStackTrace();;
        }
    }

    private void sendMessage(String message){
        try{
            outputStream.writeObject("Server -" +  message);
            outputStream.flush();
            showMessage("\n Server - "+ message);
        }catch (IOException ioException){
            chatWindow.append("\n ERROR: ");
        }
    }

    private void showMessage(final String text) {
        SwingUtilities.invokeLater(
                () -> chatWindow.append(text)
        );
    }

    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                () -> userText.setEditable(tof)
        );
    }
}
