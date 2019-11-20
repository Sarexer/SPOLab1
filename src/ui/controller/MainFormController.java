package ui.controller;

import analyzer.Lexer;
import analyzer.exceptions.LexerException;
import javafx.util.Pair;
import ui.view.MainForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;

public class MainFormController {
    private MainForm mainForm;
    private JTextArea txtArea1;
    private JTextArea txtArea2;
    private JButton btnAnalyze;
    private JTable table;
    private JComboBox comboBox;
    private String[][][] arrTables;

    private Object[][] array = new String[][] {{ "Сахар" , "кг", "1.5" },
            { "Мука"  , "кг", "4.0" },
            { "Молоко", "л" , "2.2" }};
    // Заголовки столбцов
    private Object[] columnsHeader = new String[] {"Лексема", "Адресс",
            "Смещение"};
    public MainFormController() {
        mainForm = new MainForm();
        txtArea1 = mainForm.getTxtArea1();
        txtArea2 = mainForm.getTxtArea2();
        btnAnalyze = mainForm.getBtnAnalyze();
        table = mainForm.getTable();
        comboBox = mainForm.getComboBox();


        comboBox.addActionListener(actionEvent -> {
            if(actionEvent.getActionCommand().equals("comboBoxChanged")){

                table.setModel(new DefaultTableModel(arrTables[comboBox.getSelectedIndex()],columnsHeader));
            }
        });

        btnAnalyze.addActionListener(this::analyze);
    }

    public void show() {
        mainForm.setVisible(true);
    }

    private void analyze(ActionEvent actionEvent) {
        txtArea2.setText("");

        StringReader reader = new StringReader(txtArea1.getText());
        Lexer lexer = new Lexer(reader);
        try {
            ArrayList<Pair<Integer,Integer>> descript = lexer.lex();
            printPairs(descript);
            arrTables = tablesToArray(lexer.tables);
            System.out.println("");

        } catch (LexerException e) {
            e.printStackTrace();
        }
    }

    private String[][][] tablesToArray(ArrayList<ArrayList<ArrayList<String>>> tables){
        String[][][] arrays = new String[tables.size()][][];
        for (int i =0;i<tables.size();i++) {
            ArrayList<ArrayList<String>> table = tables.get(i);
            String[][] arrTable = new String[table.size()][];
            for (int j =0;j<table.size();j++) {
                ArrayList<String> row = table.get(j);
                if(row.size() == 2){
                    String[] arrRow = new String[]{row.get(0), j+"", row.get(1)};
                    arrTable[j] = arrRow;
                }else{
                    String[] arrRow = new String[]{row.get(0),j+"" ,""};
                    arrTable[j] = arrRow;
                }
            }
            arrays[i] = arrTable;
        }
        return arrays;
    }
    private void printPairs(ArrayList<Pair<Integer,Integer>> pairs){
        int counter = 0;
        for (Pair<Integer, Integer> pair : pairs) {
            if(counter++ == 10){
                txtArea2.append("\n");
                counter = 0;
            }
            txtArea2.append(String.format("(%d,%d)",pair.getKey(),pair.getValue()));

        }
    }
}
