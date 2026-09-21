import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Queue;

class FileDetails {
     //Initialization of scanner for tthis class
     Scanner input = new Scanner(System.in);

     //Added new class to separate the getting of file details...
     String fileName;
     String fileType;
     int filePages;

     public FileDetails(){
          System.out.print("Enter the file name: ");
          String name = input.nextLine();
          this.fileName = name;

          System.out.print("Enter the file type: ");
          String type = input.nextLine();

          //erase the leading . so no duplicate
          while(type.startsWith(".")){
               type = type.substring(1);
          }
          this.fileType = type;

          System.out.print("How many pages does it have: ");
          int pages = input.nextInt();
          input.nextLine();
          this.filePages = pages;
     }

     // Mergg the details
     public void getFullFileName(){
          System.out.println(fileName + "." + fileType);
     }

     public int getFilePages(){
          return filePages;
     }
}

class Printer {
     Scanner input = new Scanner(System.in);
     Queue<FileDetails> printingQueue = new java.util.LinkedList<>();
     boolean isPrinting = false;
     volatile boolean stopPrinting = false;

     //for clearing the screen
     public void clearScreen(){
          try {
               new ProcessBuilder("clear").inheritIO().start().waitFor();
          } catch (Exception e) {
               System.out.print("\033[2J\033[H");
               System.out.flush();
          }
     }


     public void addPrintJob(){
          try {
               FileDetails newFile = new FileDetails();
               printingQueue.add(newFile);
               System.out.print("\n\nThe file ");
               newFile.getFullFileName();
               System.out.println("Print job successfully added!");
          } catch (Exception e) {
               System.out.println("\n\nInvalid input! Please try again.");
               input.nextLine();
          }
     }


     public void removePrintJob(){
          if(printingQueue.isEmpty()){
               System.out.println("Queue is empty. No job to remove.");
          } else {
               FileDetails removed = printingQueue.poll();
               System.out.print("Removed job: ");
               removed.getFullFileName();
          }
     }


     public void startPrintJob(){
          if(printingQueue.isEmpty()){
               System.out.println("No print jobs in the queue.");
          } else {
               FileDetails currentJob = printingQueue.peek();
               System.out.print("Printing: ");
               currentJob.getFullFileName();
               System.out.println("Total pages: " + currentJob.getFilePages());
               System.out.println("(Press Enter to stop printing)");

               stopPrinting = false;

               Thread keyListener = new Thread(() -> {
                    try {
                         java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
                         reader.readLine();
                         stopPrinting = true;
                    } catch (Exception e) {
                         stopPrinting = true;
                    }
               });
               keyListener.setDaemon(true);
               keyListener.start();

               try {
                    for(int i = 1; i <= currentJob.getFilePages(); i++){
                         if(stopPrinting) break;
                         System.out.print("\rPrinted page " + i + "/" + currentJob.getFilePages() + "   ");
                         System.out.flush();
                         TimeUnit.MILLISECONDS.sleep(500);
                    }

                    System.out.print("\r\033[2K");
                    System.out.flush();

                    printingQueue.poll();

                    if(!stopPrinting){
                         System.out.println("Print job completed successfully!");
                    } else {
                         System.out.println("Print was abruptly stopped by user.");
                    }

                    try {
                         if(System.in.available() > 0){
                              System.in.skip(System.in.available());
                         }
                    } catch (Exception e) {}

               } catch (InterruptedException e) {
                    System.out.println("Printing was interrupted!");
               }
          }
     }


     public void viewFullQueue(){
          if(printingQueue.isEmpty()){
               System.out.println("\n\nThe printing queue is empty.");
          } else {
               System.out.println("\n--- Full Printing Queue ---");
               int count = 1;
               for(FileDetails job : printingQueue){
                    System.out.print(count + ". ");
                    job.getFullFileName();
                    count++;
               }
          }
     }


     public void quickQueueView(){
          System.out.println("\nQueueing " + printingQueue.size() + " of files. Ready to print");
     }
}

public class PrintingQueueSimulation {
    public static void main (String[] args) {

     //    Printer Booting Up Animation Like (!!!)
          System.out.print("\n\n\nPrinter Starting Please Wait");

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

          Printer job = new Printer();
          job.clearScreen();

          System.out.println("------Cabilao & Aldave Printer Shop------");

          boolean start = true;

          // To looping of the queue printing
          while(start){
               // Choices
               System.out.println("\n[1] Add Print Job");
               System.out.println("[2] Remove Print Job");
               System.out.println("[3] Start Print Job");
               System.out.println("[4] View Full Queue");
               System.out.println("[5] Quick Queue View");
               System.out.println("[6] Exit");
               System.out.print("\nChoose an option: ");

               try {
                    int choice = job.input.nextInt();
                    job.input.nextLine();

                    job.clearScreen();

                    switch(choice){
                         case 1:
                              job.addPrintJob();
                              break;
                         case 2:
                              job.removePrintJob();
                              break;
                         case 3:
                              job.startPrintJob();
                              break;
                         case 4:
                              job.viewFullQueue();
                              break;
                         case 5:
                              job.quickQueueView();
                              break;
                         case 6:
                              start = false;
                              System.out.println("Thank you for using Cabilao & Aldave Printer Shop!");
                              break;
                         default:
                              System.out.println("Invalid choice! Please choose between 1-6.");
                    }

                    if(start){
                         Thread.sleep(800);
                         job.clearScreen();
                         System.out.println("------Cabilao & Aldave Printer Shop------");
                    }

               } catch (Exception e) {
                    System.out.println("Invalid input type! Please enter a number.");
                    job.input.nextLine();
               }
          }
     }
}
