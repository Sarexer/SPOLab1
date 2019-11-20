import java.util.Scanner;

public class TestMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int x = scanner.nextInt();
        int y = scanner.nextInt();
        int variable = scanner.nextByte();


        if(x == 0 && y == 0){
            System.out.println("1");
        } else if(x == 0 || y == 0){
            if(x == 0){
                System.out.println("2");
            }
            if(y == 0){
                System.out.println("3");
            }
        } else {
            System.out.println("0");
        }
    }
    static void print(){

    }
}
