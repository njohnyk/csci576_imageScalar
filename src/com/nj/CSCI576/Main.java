package com.nj.CSCI576;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        ImageDisplay imageDisplay = new ImageDisplay();
        SwingUtilities.invokeLater(() -> imageDisplay.showIms(args));
    }
}