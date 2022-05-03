package com.company;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	    Server funny = new Server();
        funny.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        funny.startRunning();
    }
}
