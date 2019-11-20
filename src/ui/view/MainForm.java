package ui.view;

import javax.swing.*;

public class MainForm extends JFrame{
    private JTextArea txtArea1;
    private JPanel panel;
    private JTextArea txtArea2;
    private JButton btnAnalyze;
    private JTable table;
    private JComboBox comboBox;

    public MainForm() {
        setSize(1000,800);
        setContentPane(panel);
    }

    public JTextArea getTxtArea1() {
        return txtArea1;
    }

    public JTextArea getTxtArea2() {
        return txtArea2;
    }

    public JButton getBtnAnalyze() {
        return btnAnalyze;
    }

    public JTable getTable() {
        return table;
    }

    public JComboBox getComboBox() {
        return comboBox;
    }

    public void setTable(JTable table) {
        this.table = table;
    }
}
