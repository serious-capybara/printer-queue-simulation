import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Queue;

class FileDetails {
     //Initialization of scanner for tthis class
     Scanner input = new Scanner(System.in);

     //Added new class to separate the getting of file details...
     String fileName;
     String fileType;
     int filePages = -1;

     private static final String INVALID_CHARS = "%$.,^+=*!";

     private static boolean hasInvalidChar(String name) {
          for (int i = 0; i < name.length(); i++) {
               if (INVALID_CHARS.indexOf(name.charAt(i)) >= 0) {
                    return true;
               }
          }
          return false;
     }

     public FileDetails(){
          String name;
          while (true) {
               System.out.print("\nEnter the file name: ");
               name = input.nextLine().trim();

               if (name.isEmpty()) {
                    System.out.println("\n*Invalid input! File name cannot be empty. Returning to the menu...*");
                    return;
               }

               if (hasInvalidChar(name)) {
                    System.out.println("\n*Invalid input! File name must not contain any of these characters: % $ . , ^ + = * !*");
                    Printer.pause(1000);
                    Printer.clearScreenStatic();
                    continue;
               }

               break;
          }

          this.fileName = name;

          this.fileType = Printer.selectFileTypeStatic(fileName);

          // Keep asking for the page count until a valid whole number is typed
          while (true) {
               Printer.clearScreenStatic();
               System.out.println("\nFile name: " + getFullFileName());
               System.out.print("\nHow many pages does it have: ");

               String line = input.nextLine().trim();
               try {
                    this.filePages = Integer.parseInt(line);
                    break;
               } catch (NumberFormatException e) {
                    System.out.println("\n*Invalid input! Please enter a whole number for the page count.*");
                    Printer.pause(1000);
               }
          }
     }

     // Mergg the details
     public String getFullFileName(){
          return fileName + "." + fileType;
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
     java.util.List<FileDetails> printedFiles = new java.util.ArrayList<>();

     //for clearing the screen
     public void clearScreen() {
          clearScreenStatic();
     }

     public static void clearScreenStatic() {
          System.out.print("\033[H\033[2J");
          System.out.flush();
     }

     // time pause
     public static void pause(int ms) {
          try {
               Thread.sleep(ms);
          } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
          }
     }
     
     private static final Scanner numberInput = new Scanner(System.in);
     
     // for re print sa terminal. used when the action selection is wrong it will print the same look before keep the interaction clean
     private static int readNumber(int max, Runnable redraw) {
          while (true) {
               System.out.print("\nEnter the number of your choice: ");
               String line = numberInput.nextLine().trim();

               if (line.isEmpty()) {
                    System.out.println("\n*You didn't enter anything! Please enter a number from 1 to " + max + ".*");
               } else {
                    try {
                         int n = Integer.parseInt(line);
                         if (n >= 1 && n <= max) {
                              return n;
                         }
                         System.out.println("\n*Invalid choice! Please enter a number from 1 to " + max + ".*");
                    } catch (NumberFormatException e) {
                         System.out.println("\n*Invalid choice! Please enter a number from 1 to " + max + ".*");
                    }
               }

               pause(800);
               redraw.run();
          }
     }


     //before selecting a file type is as easy by just using up and down button but it was later change for the reason for compatability with window os
     public static String selectFileTypeStatic(String fileName) {
          java.util.List<String> allowedTypes = java.util.List.of("txt", "png", "jpeg", "gif", "docx", "md");

          Runnable draw = () -> {
               clearScreenStatic();
               System.out.println("\nFile name: " + fileName);
               System.out.println("\n*Type the number of the file type, then press Enter.*\n");
               for (int i = 0; i < allowedTypes.size(); i++) {
                    System.out.println("     " + (i + 1) + ". " + allowedTypes.get(i));
               }
          };

          // for drawing again the terminal after incorrect action selected
          draw.run();
          return allowedTypes.get(readNumber(allowedTypes.size(), draw) - 1);
     }

     //no more up and down also 
     public static int selectMenuOption(String[] options) {
          Runnable draw = () -> {
               clearScreenStatic();
               System.out.println("-----------Printing Simulation-----------");
               System.out.println("\n*Type the number of your choice, then press Enter.*\n");
               for (int i = 0; i < options.length; i++) {
                    System.out.println("     " + (i + 1) + ". " + options[i]);
               }
          };

          // same here it draw again if action is incorrect
          draw.run();
          return readNumber(options.length, draw);
     }


     public void addPrintJob(){
          FileDetails newFile = new FileDetails();

          // connected sa checking input text... ug checking name is empti
          if (newFile.fileName == null || newFile.fileName.isEmpty() || newFile.getFilePages() == -1) {
               return;
          }

          //added a limit para dili usahon ang >500 pages
          if (newFile.getFilePages() <= 0 || newFile.getFilePages() > 500) {
               System.out.println("\n*Invalid page count! A print job cannot have 0 or more than 500 pages. Please try again.*");
               return;
          }

          printingQueue.add(newFile);
          System.out.print("\n*The file '" + newFile.getFullFileName());
          System.out.println("' with " + newFile.getFilePages() + " pages has been successfully added!*");
     }


     //same here no more use of up and down
     public int selectJobToRemove(){
          java.util.List<FileDetails> jobs = new java.util.ArrayList<>(printingQueue);
          int totalOptions = jobs.size() + 1;

          Runnable draw = () -> {
               clearScreenStatic();
               System.out.println("-----------Remove Print Job-----------");
               System.out.println("\n*Type the number of the file to remove, then press Enter.*\n");
               for (int i = 0; i < jobs.size(); i++) {
                    System.out.println("     " + (i + 1) + ". " + jobs.get(i).getFullFileName() + " | Pages: " + jobs.get(i).getFilePages());
               }
               System.out.println("\n     " + totalOptions + ". Back to Menu");
          };

          // draw if incorrect
          draw.run();
          int n = readNumber(totalOptions, draw);
          if (n == totalOptions) {
               return -1;
          }
          return n - 1;
     }


     public void removePrintJob(){
          if(printingQueue.isEmpty()){
               System.out.println("\n*Queue is empty. No job to remove.*");
          } else {
               int index = selectJobToRemove();
               clearScreen();

               if(index == -1){
                    System.out.println("\n*Remove cancelled. Returning to the menu...*");
                    return;
               }

               FileDetails removed = new java.util.ArrayList<>(printingQueue).get(index);
               printingQueue.remove(removed);
               System.out.println("\n*Removed job: " + removed.getFullFileName() + "*");
          }
     }

     private boolean enterPressed() {
          try {
               if (System.in.available() > 0) {
                    while (System.in.available() > 0) {
                         System.in.read();
                    }
                    return true;
               }
          } catch (Exception e) {
          }
          return false;
     }

     // for asking if to continue with the next queue to print
     private boolean askStartNext(FileDetails next){
          while(true){
               System.out.print("\n*The next print \"" + next.getFullFileName() + "\" will be started (y/n)?* ");
               String answer = input.nextLine().trim();

               if(answer.equalsIgnoreCase("y")){
                    return true;
               }
               if(answer.equalsIgnoreCase("n")){
                    return false;
               }
               System.out.println("\n*Invalid input! Please enter y or n.*");
               pause(1000);
               clearScreen();
               System.out.println("\n*Print job completed successfully!*");
          }
     }


     public void startPrintJob(){
          if(printingQueue.isEmpty()){
               System.out.println("\n*No print jobs in the queue.*");
               return;
          }

          stopPrinting = false;
          System.out.println("\n*Printing Started! (Press Enter to stop printing)*");

          try {
               while(!printingQueue.isEmpty() && !stopPrinting){
                    FileDetails currentJob = printingQueue.peek();
                    int total = currentJob.getFilePages();

                    System.out.println("\nPrinting: " + currentJob.getFullFileName() + " with a total " + total + " pages.");

                    for(int i = 1; i <= total; i++){
                         if(enterPressed()){
                              stopPrinting = true;
                              break;
                         }
                         System.out.print("\rPrinted page " + i + "/" + total + "          ");
                         System.out.flush();
                         TimeUnit.MILLISECONDS.sleep(500);
                    }

                    System.out.println();
                    FileDetails finishedJob = printingQueue.poll();

                    if(stopPrinting){
                         System.out.println("\n*Print was abruptly stopped by user.*");
                         break;
                    }

                    printedFiles.add(finishedJob);
                    System.out.println("\n*Print job completed successfully!*");

                    if(!printingQueue.isEmpty()){
                         if(!askStartNext(printingQueue.peek())){
                              System.out.println("\n*Returning to the menu...*");
                              break;
                         }
                         System.out.println("\n*Moving to the next print job...*");
                         TimeUnit.MILLISECONDS.sleep(1000);
                         clearScreen();
                    }
               }
          } catch (InterruptedException e) {
               System.out.println("\n*Printing was interrupted!*");
               return;
          }

          if(printingQueue.isEmpty() && !stopPrinting){
               System.out.println("\n*All queued print jobs have been processed.*");
          }
     }


     public void viewFullQueue(){
          if(printingQueue.isEmpty()){
               System.out.println("\n*The printing queue is empty.*");
          } else {
               System.out.println("\n--- Full Printing Queue ---\n");
               int count = 1;
               for(FileDetails job : printingQueue){
                    System.out.println(count + ". " + job.getFullFileName() + " | Pages: " + job.getFilePages());
                    count++;
               }
          }
     }

     public void quickQueueView(){
          System.out.println("\n*Queueing " + printingQueue.size() + " of files. Ready to print.*");
     }


     //sorting 0 - z
     public void sortPrintedFiles(){
          for(int i = 0; i < printedFiles.size() - 1; i++){
               int minIndex = i;
               for(int j = i + 1; j < printedFiles.size(); j++){
                    if(printedFiles.get(j).getFullFileName().compareToIgnoreCase(printedFiles.get(minIndex).getFullFileName()) < 0){
                         minIndex = j;
                    }
               }
               FileDetails temp = printedFiles.get(i);
               printedFiles.set(i, printedFiles.get(minIndex));
               printedFiles.set(minIndex, temp);
          }
     }


     public void viewPrintedFiles(){
          if(printedFiles.isEmpty()){
               System.out.println("\n*No files have been printed yet.*");
          } else {
               sortPrintedFiles();
               System.out.println("\n--- Previously Printed Files (A-Z) ---\n");
               int count = 1;
               for(FileDetails job : printedFiles){
                    System.out.println(count + ". " + job.getFullFileName() + " | Pages: " + job.getFilePages());
                    count++;
               }
          }
     }
}





public class PrintingQueueSimulation {

     public static void clearScreen(){
          System.out.print("\033[2J\033[H");
          System.out.flush();
    }

    public static void main (String[] args) {

     //    Printer Booting Up Animation Like (!!!)
          clearScreen();
          System.out.print("\n\n\nPrinter Starting Please Wait");

          int t = 1;
          for (int i = 0 ; i < 3 ; i++){
               System.out.print("!");

               try {
               TimeUnit.SECONDS.sleep(t);
               t++;
               } catch(InterruptedException e){
                    System.out.println("\n*Failed to start!*");
                    System.out.println("\n*Please try again!*");
               }
          }

          Printer job = new Printer();
          job.clearScreen();

          boolean start = true;

          // The looping of the queue printing
          while(start){
               String[] menuOptions = {
                    "Add Print Job",
                    "Remove Print Job",
                    "Start Print Job",
                    "View Printed Files",
                    "View Full Queue",
                    "Quick Queue View",
                    "Exit"
               };

               int choice = Printer.selectMenuOption(menuOptions);
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
                         job.viewPrintedFiles();
                         break;
                    case 5:
                         job.viewFullQueue();
                         break;
                    case 6:
                         job.quickQueueView();
                         break;
                    case 7:
                         start = false;
                         System.out.println("\n*Thank you for using the Printing Simulation made by Aldave & Cabilao. :/*\n");
                         break;
                    default:
                         System.out.println("\n*Invalid choice! Please choose between 1-7.*");
               }

               if(start){
                    Printer.pause(3000);
               }

          }
     }
}
