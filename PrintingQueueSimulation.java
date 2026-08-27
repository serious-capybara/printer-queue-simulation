import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Queue;

class FileDetails {
     //Initialization of scanner
     Scanner input = new Scanner(System.in);

     //Added new class to separate the getting of file details....
     String fileName;
     String fileType;
     int filePages;

     public FileDetails(){
          System.out.println("Enter the file name: ");
          String name = input.nextLine();
          this.fileName = name;

          System.out.println("Enter the file type: ");
          String type = input.nextLine();
          this.fileName = type;

          System.out.println("How many pages does it have: ");
          int pages = input.nextInt();
          this.filePages = pages;
     }

     // Mergg the details
     public void getFullFileName(){
          System.out.println(fileName + "." + fileType);
     }

     public getFilePages(){
          return filePages;
     }
}

class Printer {
     Scanner input = new Scanner(System.in);
     Queue<String> printingQueue = new Queue();

     public void addPrintJob(){

     }

     public void removePrintJob(){

     }

     public void startPrintJob(){

     }

     public void viewFullQueue(){

     }

     public quickQueueView(){
          //like Queueing ? of files. Ready to print
     }

     
}

public class PrintingQueueSimulation {
    public static void main (String[] args) {

     //    Printer Booting Up Animation Like
          System.out.print("\n\nPrinter Starting Please Wait");

          int t = 1;
          for (int i = 0 ; i < 3 ; i++){
               System.out.print("!");

               try {
               TimeUnit.SECONDS.sleep(t);
               t++;
               } catch(InterruptedException e){
                    System.out.println("\nFailed to start!");
                    System.out.println("\nPlease try again!");
               }
          }

          System.out.println("\n------Cabilao & Aldave Printer Shop------");

          Printer job = new Printer();
          job.getFileName();
     }
}
