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

     public FileDetails(){
          System.out.print("\nEnter the file name: ");
          String name = input.nextLine();
          this.fileName = name;

          this.fileType = Printer.selectFileTypeStatic(fileName);

          System.out.print("\nHow many pages does it have: ");

          // checking for text input
          if (!input.hasNextInt()) {
               System.out.println("\nInvalid input! Please enter a whole number for the page count.");
               input.nextLine();
               return;
          }

          int pages = input.nextInt();
          input.nextLine();
          this.filePages = pages;
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

     //for clearing the screen
     public void clearScreen() {
          System.out.print("\033[H\033[2J");
          System.out.flush();
     }

     private static void enableRawInput() {
          try {
               String os = System.getProperty("os.name").toLowerCase();
               if (!os.contains("win")) {
                    Runtime.getRuntime().exec(new String[]{"sh", "-c", "stty -icanon -echo < /dev/tty"}).waitFor();
               }
          } catch (Exception e) {
          }
     }

     private static void restoreTerminal() {
          try {
               String os = System.getProperty("os.name").toLowerCase();
               if (!os.contains("win")) {
                    Runtime.getRuntime().exec(new String[]{"sh", "-c", "stty sane < /dev/tty"}).waitFor();
               }
          } catch (Exception e) {
          }
     }

     private static int readKey() {
          try {
               return System.in.read();
          } catch (Exception e) {
               return -1;
          }
     }


     // Selecting file tpe using up/down arrow instead of manualli tiping it
     public static String selectFileTypeStatic(String fileName) {
          java.util.List<String> allowedTypes = java.util.List.of("txt", "png", "jpeg", "gif", "docx", "md");
          int selectedIndex = 0;

          enableRawInput();

          try {
               while (true) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();

                    System.out.println("\nFile name: " + fileName);
                    System.out.println("\n[Select file type using Up/Down, then press Enter to proceed]\n");
                    for (int i = 0; i < allowedTypes.size(); i++) {
                         if (i == selectedIndex) {
                              System.out.println("   > " + allowedTypes.get(i));
                         } else {
                              System.out.println("     " + allowedTypes.get(i));
                         }
                    }

                    int c = readKey();

                    if (c == 27) {
                         int c2 = readKey();
                         int c3 = readKey();
                         if (c2 == 91) {
                              if (c3 == 65) {
                                   selectedIndex = (selectedIndex - 1 + allowedTypes.size()) % allowedTypes.size();
                              } else if (c3 == 66) {
                                   selectedIndex = (selectedIndex + 1) % allowedTypes.size();
                              }
                         }
                    } else if (c == 10 || c == 13) {
                         String chosen = allowedTypes.get(selectedIndex);
                         return chosen;
                    }
               }
          } finally {
               restoreTerminal();
          }
     }

     // same to the selecting in the file type
     public static int selectMenuOption(String[] options) {
          int selectedIndex = 0;

          enableRawInput();
          try {
               while (true) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();

                    System.out.println("-----------Printing Simulation-----------");

                    System.out.println("\n[Use Up/Down, then press Enter to choose.]\n");
                    for (int i = 0; i < options.length; i++) {
                         if (i == selectedIndex) {
                              System.out.println("   > " + options[i]);
                         } else {
                              System.out.println("     " + options[i]);
                         }
                    }

                    int c = readKey();

                    if (c == 27) {
                         int c2 = readKey();
                         int c3 = readKey();
                         if (c2 == 91) {
                              if (c3 == 65) {
                                   selectedIndex = (selectedIndex - 1 + options.length) % options.length;
                              } else if (c3 == 66) {
                                   selectedIndex = (selectedIndex + 1) % options.length;
                              }
                         }
                    } else if (c == 10 || c == 13) {
                         return selectedIndex + 1;
                    }
               }
          } finally {
               restoreTerminal();
          }
     }


     public void addPrintJob(){
          FileDetails newFile = new FileDetails();

          // connected sa checking input text...
          if (newFile.getFilePages() == -1) {
               return;
          }

          //added a limit para dili usahon ang >500 pages
          if (newFile.getFilePages() <= 0 || newFile.getFilePages() > 500) {
               System.out.println("\n\nInvalid page count! A print job cannot have 0 or more than 500 pages. Please try again.");
               return;
          }

          printingQueue.add(newFile);
          System.out.print("\n\nThe file '" + newFile.getFullFileName());
          System.out.println("' with " + newFile.getFilePages() + " pages has been successfully added!");
     }


     public void removePrintJob(){
          if(printingQueue.isEmpty()){
               System.out.println("\nQueue is empty. No job to remove.");
          } else {
               FileDetails removed = printingQueue.poll();
               System.out.print("\nRemoved job: " + removed.getFullFileName());
          }
     }


     public void startPrintJob(){
          if(printingQueue.isEmpty()){
               System.out.println("\nNo print jobs in the queue.");
          } else {

               System.out.println("\nPrinting Started!");
               while(!printingQueue.isEmpty() && !stopPrinting){
                    FileDetails currentJob = printingQueue.peek();

                    clearScreen();
                    System.out.println("\nPrinting: " + currentJob.getFullFileName() + " with a total " + currentJob.getFilePages() + " pages. | (Press Enter to stop printing)");

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
                              System.out.println("\nPrint job completed successfully!");
                         } else {
                              System.out.println("\nPrint was abruptly stopped by user.");
                              break;
                         }

                         if(!printingQueue.isEmpty() && !stopPrinting){
                              System.out.println("\nMoving to the next print job...");
                              TimeUnit.MILLISECONDS.sleep(700);
                         }

                    } catch (InterruptedException e) {
                         System.out.println("Printing was interrupted!");
                         break;
                    }
               }

               if(printingQueue.isEmpty() && !stopPrinting){
                    System.out.println("\nAll queued print jobs have been processed.");
               }
          }
     }


     public void viewFullQueue(){
          if(printingQueue.isEmpty()){
               System.out.println("\nThe printing queue is empty.");
          } else {
               System.out.println("\n--- Full Printing Queue ---");
               int count = 1;
               for(FileDetails job : printingQueue){
                    System.out.println(count + ". " + job.getFullFileName() + " | Pages: " + job.getFilePages()); //New added, naa also display sa Pages
                    count++;
               }
          }
     }


     public void quickQueueView(){
          System.out.println("\nQueueing " + printingQueue.size() + " of files. Ready to print.");
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
                    System.out.println("\nFailed to start!");
                    System.out.println("\nPlease try again!");
               }
          }

          Printer job = new Printer();
          job.clearScreen();

          System.out.println("------Printing Simulation------");

          boolean start = true;

          // The looping of the queue printing
          while(start){
               String[] menuOptions = {
                    "Add Print Job",
                    "Remove Print Job",
                    "Start Print Job",
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
                         job.viewFullQueue();
                         break;
                    case 5:
                         job.quickQueueView();
                         break;
                    case 6:
                         start = false;
                         System.out.println("Thank you for using the Printing Simulation made by Aldave & Cabilao. :/");
                         break;
                    default:
                         System.out.println("Invalid choice! Please choose between 1-6.");
               }

               if(start){
                    try {
                         Thread.sleep(3000);
                    } catch (InterruptedException e) {
                         Thread.currentThread().interrupt();
                    }
                    System.out.println("------Printing Simulation------");
               }

          }
     }
}
