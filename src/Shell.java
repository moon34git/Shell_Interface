import java.io.*;
import java.util.ArrayList;

public class Shell {
    static String absolutePath = System.getProperty("user.home");   //A user home directory
    static String path = System.getProperty("user.dir");        //A present working directory
    static ArrayList<String> history;       //To store history
    static boolean hisCheck = true;         //To execute history
    static String hisCode = "";             //Code that will be executed

    static String cd_f(ArrayList<String> list, String tpath) {
        String path = "";
        if ((list.size() == 1) || (list.get(1).equals("/home"))) {      //Show user home directory
            return absolutePath;
        }
        String[] array = list.get(1).split("/");
        ArrayList<String> cdList = new ArrayList<String>();
        for (int i = 0; i < array.length; i++) {
            cdList.add(array[i]);
        }
        if (list.get(1).charAt(0) == '/' && list.size() == 2) {         //Change directory
            for (int i = 1; i < cdList.size(); i++) {
                path = path + File.separator + cdList.get(i);
            }
        } else if (list.get(1).equals("..") && list.size() == 2) {      //Go to upper directory
            String[] ar = tpath.split("/");
            ArrayList<String> uplist = new ArrayList<String>();
            for (int i = 0; i < ar.length; i++) {
                uplist.add(ar[i]);
            }
            for (int i = 1; i < uplist.size() - 1; i++) {
                path = path + File.separator + uplist.get(i);
            }
        } else if(list.size() == 2){                                    //Change directory
            path = tpath;
            for (int i = 0; i < cdList.size(); i++) {
                path = path + File.separator + cdList.get(i);
            }
        }else{}

		File file = new File(path);                                     //Check whether file exists or not
		if (file.exists()) {
			return path;
		} else{
            System.out.println("There is no such directory");
            return tpath;
        }
    }

    static void history_f(ArrayList<String> list){
        if(list.size() == 1){                                           //Show history record
            for(int i = 0; i < history.size(); i++){
                System.out.println(i + " " + history.get(i));
            }
        }
        else if(list.get(1).equals("!!") && list.size() == 2){          //Execute previous command
            if(history.size() == 1) System.out.println("There is no previous command");
            String his = history.get(history.size() - 2);
            hisCode = his;
            history.set(history.size() - 1, his);
            hisCheck = false;
        }
        else if(list.get(1).charAt(0) == '!' && list.size() == 2) {     //Execute a specific command
            try {
                String integer = list.get(1).substring(1);
                int n = Integer.parseInt(integer);
                if (n >= 0 && n < history.size()) {
                    String his = history.get(n);
                    hisCode = his;
                    history.set(history.size() - 1, his);
                    hisCheck = false;
                } else {
                    System.out.println("Enter a valid integer.");
                }
            }catch (NumberFormatException e){
                System.out.println(e);
            }
        }
        else {
            System.out.println("Invalid command");
        }
    }

    public static void main(String[] args) throws java.io.IOException {
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        history = new ArrayList<>();

        while (true) {
            if(hisCheck){                               //To execute history command
                System.out.print("jsh>");
                commandLine = console.readLine();
            }else{
                commandLine = hisCode;
                hisCheck = true;
            }

            ArrayList<String> dList = new ArrayList<>();        //Save a present command
            history.add(commandLine);                           //Insert the present command into history arraylist
            String[] commandLineSplit = commandLine.split(" ");
            for (int i = 0; i < commandLineSplit.length; i++) {
                dList.add(commandLineSplit[i]);
            }
            if ((commandLine.equals("exit")) || (commandLine.equals("quit"))) {
                System.out.println("Goodbye.");
                System.exit(0);
            }
            else if(dList.get(0).equals("cd")){                 //Execute "cd" command
                String newPath = cd_f(dList, path);
                path = newPath;
                continue;
            }
            else if(dList.get(0).equals("history")){            //Execute "history" command
                history_f(dList);
                continue;
            }
            else if(commandLine.equals("")) continue;

            try {
                ProcessBuilder pb = new ProcessBuilder(dList);
                pb.directory(new File(path));
                Process process = pb.start();
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
                br.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
