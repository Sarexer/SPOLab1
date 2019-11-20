import analyzer.Lexer;
import analyzer.tokens.Token;
import ui.controller.MainFormController;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        MainFormController controller  = new MainFormController();
        controller.show();
        /*Scanner scanner = new Scanner(System.in);

        FileReader reader = checkForFile(scanner);

        try {
            StringReader reader1 = new StringReader()
            Lexer lexer = new Lexer(reader);

            ArrayList<Token> tokens = lexer.lex();
            //printTokens(analyzer.tokens);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private static FileReader checkForFile(Scanner scanner) {
        try {
            /*System.out.println("Введите имя файла");
            String filename = scanner.next();*/
            String filename = "C:\\Users\\shaka\\IdeaProjects\\SPOLab1\\src\\TestMain.java";
            return new FileReader(filename);
        } catch (Exception e) {
            System.out.println("File not found");
            return checkForFile(scanner);
        }
    }

    public static void printTokens(ArrayList<Token> tokens){
        for (Token token : tokens) {
            //if(token.getValue() != null){

                System.out.println(token.getType());
            //}
        }
    }
}
