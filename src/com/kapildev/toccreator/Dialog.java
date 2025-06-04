package com.kapildev.toccreator;

import javax.swing.JOptionPane;

public class Dialog {
  public static void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public static void showInfo(String message) {
    JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
  }
}
