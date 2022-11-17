// //Returns 0 for error 
// //Returns 1 if completed


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
public class program2Java {
    public static void isWordinParagraphs(String paragraph){
        String[] parts = paragraph.toLowerCase().split("\\.",0);
        boolean found = false;
        for (int i = 0;i<6;i++){
            System.out.println(parts[i]);
        }
        Scanner myObj = new Scanner(System.in);
        String word = ""+myObj.nextLine();
        for(int i = 0;i<6 &&!found;i++){
            parts[i].replace(".","");
            String[] wNumber = parts[i].split(" ",0);
            int add = 0;
            if(!wNumber[0].equals("")){
                add = 1;
            }
            for(int j = 0;j<wNumber.length&&!found;j++){
                if(wNumber[j].equals(word)){
                    System.out.println("Word: "+ wNumber[j]);
                    System.out.println("Sentence Number: "+ (i+1));
                    System.out.println("Word Number in the Sentence: "+ (j+add));
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        File file = new File("program2-text.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String paragraph = br.readLine();
        isWordinParagraphs(paragraph);
        
    }
}
