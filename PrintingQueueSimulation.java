import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class Printer {
     private String jobName;

     public Printer() {
          Scanner input = new Scanner(System.in);

          System.out.print("\nEnter the document name to print: ");
          String name = input.nextLine();

          this.jobName = name;
          input.close();
     }

     public void getFileName() {
          System.out.println("Printing file: " + jobName);
     }

}

public class PrintingQueueSimulation {
     public static void main (String[] args) {

          // Printer Booting Up
          System.out.print("\n\nPrinter Starting Please Wait");

          int t = 1;
          for (int i = 0 ; i < 3 ; i++){
               System.out.print("!");

               try {
                    TimeUnit.SECONDS.sleep(t);
                    t++;
               }catch(InterruptedException e){
                    System.out.println("\nPlease Try Again");

               }
          }
          System.out.println("\n------Cabilao Printer Shop------");

          Printer job = new Printer();
          job.getFileName();

     
     }
}
