package app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The user interface for this program.
 */
@SuppressWarnings("serial")
public class ProgramUI extends JFrame {

  /**
   * Constructor.
   */
  public ProgramUI() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationByPlatform(true);
    setTitle("Rabbit Data Converter");

    final JLabel label = new JLabel();
    label.setText("Converts Rabbit 1.0 data files to Rabbit 1.1 format");
    JPanel panel = new JPanel();
    add(panel, BorderLayout.NORTH);
    panel.add(label);

    final JButton button = new JButton();
    button.setText("Start");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        button.setText("Working...");
        // OK to run this in UI thread, workload is small
        Program.run();
        label.setText("All operations have finished.");
        button.setText("Exit");
        button.removeActionListener(this);
        button.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });
      }
    });
    panel = new JPanel();
    add(panel, BorderLayout.CENTER);
    panel.add(button);

    pack();
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException e) {
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    } catch (UnsupportedLookAndFeelException e) {
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        ProgramUI ui = new ProgramUI();
        ui.setVisible(true);
      }
    });
  }
}
