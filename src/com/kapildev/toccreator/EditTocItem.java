package com.kapildev.toccreator;

import javax.swing.*;

public class EditTocItem {
   public static boolean showFor(TocItem tocItem) {
      JTextField labelInput = new JTextField(20);
      labelInput.setText(tocItem.label);

      JTextField pageNumInput = new JTextField(4);
      pageNumInput.setText("" + tocItem.pageNum);

      JPanel inputPanel = new JPanel();
      inputPanel.add(new JLabel("Label:"));
      inputPanel.add(labelInput);
      inputPanel.add(Box.createHorizontalStrut(15)); // a spacer
      inputPanel.add(new JLabel("Page Number:"));
      inputPanel.add(pageNumInput);

      int result;
      boolean loop = false;

      do {
        result = JOptionPane.showConfirmDialog(null, inputPanel,
               "Please enter details", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
          try {
            Integer.parseInt(pageNumInput.getText());
            loop = false;
          } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter integer for page number", "Error", JOptionPane.ERROR_MESSAGE);
            loop = true;
          }
        }
      } while(loop);

      if (result == JOptionPane.OK_OPTION) {
        tocItem.label = labelInput.getText();
        tocItem.pageNum = Integer.parseInt(pageNumInput.getText());

        return true;
      }

      return false;
   }
}
