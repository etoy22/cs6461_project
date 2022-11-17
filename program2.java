//Returns 0 for error 
//Returns 1 if completed
public class program2 {
    public int isWordinParagraphs(String paragraph,String word){
        String[] parts = paragraph.toLowerCase().split("\\.",0);
        boolean found = false;
        if(parts.length!=6){
            return 0;
        }
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
        return 1;
    }
}
