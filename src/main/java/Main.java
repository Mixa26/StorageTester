import customExceptions.*;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        if (args[0].equals("drive"))
        {
            try {
                Class.forName("DriveStorage");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else if (args[0].equals("local"))
        {
            try {
                Class.forName("");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        String path = args[0];
        Storage ls = StorageManager.getStorage(path);

        //DriveStorage ls = DriveStorage.getInstance();
        Scanner s = new Scanner(System.in);

        System.out.print("$ ");

        String input = s.nextLine();
        boolean firstDollar = false;

        while(!input.equals("exit")){

            String[] inputSplit = input.split(" ");

            switch(inputSplit[0]){
                case "help":
                    printMenu();
                    break;
                case "createRoot": {
                    try {
                        if (inputSplit.length == 3) {
                            if (inputSplit[1].equals("null") && inputSplit[2].equals("null")) {
                                ls.createRoot("", null);
                            } else if (inputSplit[2].equals("null")) {
                                ls.createRoot(inputSplit[1], null);
                            }
                        } else if (inputSplit.length >= 5) {
                            int bytes = Integer.parseInt(inputSplit[2]);
                            int fileCnt = Integer.parseInt(inputSplit[3]);
                            ArrayList<String> extensions;
                            if (inputSplit[5].equals("null"))
                                extensions = new ArrayList<>();
                            else extensions = new ArrayList<>(Arrays.asList(inputSplit).subList(4, inputSplit.length));
                            System.out.println("bytes => " + bytes);
                            if (inputSplit[1].equals("null")) {
                                ls.createRoot("", new Configuration(
                                        String.join("/", "", "configuration.json"),
                                        inputSplit[1],
                                        bytes,
                                        fileCnt,
                                        extensions));
                            } else
                                ls.createRoot(inputSplit[1], new Configuration(
                                        String.join("/", inputSplit[1], "configuration.json"),
                                        inputSplit[1],
                                        bytes,
                                        fileCnt,
                                        extensions));
                        }
                        else System.err.println("Too few arguments!");
                    }
                    catch (NoConfigException e)
                    {
                        System.out.println("No configuration found in the storage you're trying to connect to.");
                        System.out.println("Connection to the storage failed.");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad folder path, check your path and try again.");
                    }
                    break;
                }
                case "createDir": {
                    try {
                        if (inputSplit.length == 4) {
                            if (inputSplit[1].equals("null"))
                                ls.createDir("", inputSplit[2], null);
                            else ls.createDir(inputSplit[1], inputSplit[2], null);
                        } else if (inputSplit.length >= 6) {
                            ArrayList<String> extensions;
                            if (inputSplit[5].equals("null"))
                                extensions = new ArrayList<>();
                            else extensions = new ArrayList<>(Arrays.asList(inputSplit).subList(5, inputSplit.length));

                            Configuration conDir = new Configuration(
                                    String.join("/", inputSplit[1], inputSplit[2], "configuration.json"),
                                    inputSplit[2],
                                    Long.parseLong(inputSplit[3]),
                                    Integer.parseInt(inputSplit[4]),
                                    extensions
                            );
                            if (inputSplit[1].equals("null"))
                                ls.createDir("", inputSplit[2], conDir);
                            else ls.createDir(inputSplit[1], inputSplit[2], conDir);
                        } else System.err.println("Too few arguments!");
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                        System.out.println("Directory creation failed");
                    }
                    catch (NameExistsException e)
                    {
                        System.out.println("Provided directory name already exists!");
                        System.out.println("Directory creation failed");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad folder path, check your path and try again.");
                        System.out.println("Directory creation failed");
                    }
                    break;
                }
                case "createFiles": {
                    try {
                        if (!checkArgs(inputSplit.length, 3)) {
                            break;
                        }
                        String[] names = new String[inputSplit.length - 2];
                        System.arraycopy(inputSplit, 2, names, 0, inputSplit.length - 2);

                        if (inputSplit[1].equals("null"))
                            ls.createFiles("", names);
                        else ls.createFiles(inputSplit[1], names);
                    }
                    catch (FileCreationException e)
                    {
                        System.out.println("Something went wrong with creating the file on the local machine.");
                    }
                    catch (NoSpaceException e)
                    {
                        System.out.println("You have exited the maximum amount of bytes/files available for this directory.");
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (BadExtensionException e)
                    {
                        System.out.println("Files with provided extension are forbidden for this directory.");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad folder/file path, check your path and try again.");
                    }
                    break;
                }
                case "delete": {
                    try {
                        if (!checkArgs(inputSplit.length, 2)) {
                            break;
                        }
                        String[] paths = new String[inputSplit.length - 1];
                        System.arraycopy(inputSplit, 1, paths, 0, inputSplit.length - 1);

                        ls.delete(paths);
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad folder/file path, check your path and try again.");
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    break;
                }
                case "relocateFiles": {
                    try {
                        if (!checkArgs(inputSplit.length, 3)) {
                            break;
                        }
                        String[] paths = new String[inputSplit.length - 2];
                        System.arraycopy(inputSplit, 2, paths, 0, inputSplit.length - 2);

                        for (String st : paths) {
                            System.out.println(st);
                        }
                        if (inputSplit[1].equals("null"))
                            ls.relocateFiles(paths, "");
                        else ls.relocateFiles(paths, inputSplit[1]);
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad folder/file path, check your path and try again.");
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (NoSpaceException e)
                    {
                        System.out.println("You have exited the maximum amount of bytes/files available for this directory.");
                    }
                    break;
                }
                case "download": {
                    try {
                        if (!checkArgs(inputSplit.length, 3)) {
                            break;
                        }
                        ls.download(inputSplit[1], inputSplit[2]);
                    }
                    catch (UnsupportedOperationException e)
                    {
                        System.out.println("Folder download isn't available for Google Drive storage.");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad file path, check your path and try again.");
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    break;
                }
                case "rename":{
                    try {
                        if (!checkArgs(inputSplit.length, 3)) {
                            break;
                        }
                        ls.rename(inputSplit[1], inputSplit[2]);
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (NameExistsException e)
                    {
                        System.out.println("Provided directory name already exists!");
                        System.out.println("Directory rename failed");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad file path, check your path and try again.");
                    }
                    break;
                }
                case "searchAllFilesInDir":{
                    try {
                        if (!checkArgs(inputSplit.length, 2)) {
                            break;
                        }
                        ArrayList<Object> md;
                        if (inputSplit[1].equals("null"))
                            md = ls.searchAllFilesInDir("");
                        else md = ls.searchAllFilesInDir(inputSplit[1]);

                        ls.printRes(md);
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad file path, check your path and try again.");
                    }
                    break;
                }
                case "searchAllDirsInDir":{
                    try {
                        if (!checkArgs(inputSplit.length, 2)) {
                            break;
                        }
                        ArrayList<Object> allDirs;
                        if (inputSplit[1].equals("null"))
                            allDirs = ls.searchAllDirsInDir("");
                        else allDirs = ls.searchAllDirsInDir(inputSplit[1]);
                        ls.printRes(allDirs);
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad file path, check your path and try again.");
                    }
                    break;
                }
                case "searchAllFilesInDirs":{
                    try {
                        if (!checkArgs(inputSplit.length, 2)) {
                            break;
                        }
                        ArrayList<Object> allFilesInDirs;
                        if (inputSplit[1].equals("null"))
                            allFilesInDirs = ls.searchAllFilesInDirs("");
                        else allFilesInDirs = ls.searchAllFilesInDirs(inputSplit[1]);
                        ls.printRes(allFilesInDirs);
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad file path, check your path and try again.");
                    }
                    break;
                }
                case "searchFilesByExt": {
                    try{
                        if (!checkArgs(inputSplit.length, 3)) {
                            break;
                        }
                        ArrayList<Object> allFilesExt;
                        if (inputSplit[1].equals("null"))
                            allFilesExt = ls.searchFilesByExt("", inputSplit[2]);
                        else allFilesExt = ls.searchFilesByExt(inputSplit[1], inputSplit[2]);
                        ls.printRes(allFilesExt);
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad file path, check your path and try again.");
                    }
                    break;
                }
                case "searchFileBySub":{
                    try {
                        if (!checkArgs(inputSplit.length, 2)) {
                            break;
                        }
                        ArrayList<Object> allFilesSub = ls.searchFileBySub(inputSplit[1]);
                        ls.printRes(allFilesSub);
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    break;
                }
                case "dirContainsFiles":{
                    try {
                        if (!checkArgs(inputSplit.length, 3)) {
                            break;
                        }
                        String[] names = new String[inputSplit.length - 2];
                        System.arraycopy(inputSplit, 2, names, 0, inputSplit.length - 2);

                        if (inputSplit[1].equals("null"))
                            System.out.println(ls.dirContainsFiles("", names));
                        else System.out.println(ls.dirContainsFiles(inputSplit[1], names));
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (BadPathException e)
                    {
                        System.out.println("Bad file path, check your path and try again.");
                    }
                    break;
                }
                case "folderContainingFile":{
                    try {
                        if (!checkArgs(inputSplit.length, 2)) {
                            break;
                        }
                        String file = ls.folderContainingFile(inputSplit[1]);
                        if (file != null) {
                            System.out.println(file);
                        }
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("No such file found.");
                    }
                    break;
                }
                case "sort":{
                    try {
                        if (!checkArgs(inputSplit.length, 3)) {
                            break;
                        }

                        ls.sort(SortParamsEnum.valueOf(inputSplit[1]), Boolean.parseBoolean(inputSplit[2]));
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    break;
                }
                case "filesCreatedModifiedOnDate":{
                    try {
                        if (!checkArgs(inputSplit.length, 3)) {
                            break;
                        }
                        Date dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(inputSplit[1]);
                        Date dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(inputSplit[2]);

                        ArrayList<Object> res = ls.filesCreatedModifiedOnDate(dateFrom, dateTo);
                        ls.printRes(res);
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    catch (ParseException e)
                    {
                        System.out.println("Couldn't read date.");
                    }
                    break;
                }
                case "filterSearchResult":{
                    try {
                        if (!checkArgs(inputSplit.length, 5)) {
                            break;
                        }

                        ls.filterSearchResult(
                                Boolean.parseBoolean(inputSplit[1]),
                                Boolean.parseBoolean(inputSplit[2]),
                                Boolean.parseBoolean(inputSplit[3]),
                                Boolean.parseBoolean(inputSplit[4])
                        );
                    }
                    catch (NoRootException e)
                    {
                        System.out.println("There is no root!");
                        System.out.println("You haven't created or connected to a storage.");
                    }
                    break;
                }
                case "exit":
                    break;
                default:
                    System.out.println("Provided command doesn't exist.");
            }
            System.out.print("$ ");
            input = s.nextLine();
        }
    }

    public static void printMenu(){
        System.out.println("List of available commands: ");
        System.out.println("createRoot path bytes fileCnt extensions...");
        System.out.println("createDir path dirName bytes fileCnt extensions...");
        System.out.println("createFiles path fileNames...");
        System.out.println("delete paths...");
        System.out.println("relocateFiles pathTo pathsFrom...");
        System.out.println("download pathFrom pathTo");
        System.out.println("rename path name");

        System.out.println("searchAllFilesInDirs dirPath");
        System.out.println("searchAllDirsInDir dirPath");
        System.out.println("searchAllFilesInDirs dirPath");
        System.out.println("searchFilesByExt path ext");
        System.out.println("searchFileBySub substring");
        System.out.println("dirContainsFiles path names...");
        System.out.println("folderContainingFile name");
        System.out.println("sort (NAME || DATE_OF_CREATION || DATE_OF_MODIFICATION) ascending");
        System.out.println("filesCreatedModifiedOnDate date(YYYY-MM-DD)");
        System.out.println("filterSearchResult fullPath showSize showDateOfCreation showDateOfModification");
        System.out.println("Type exit to exit");
        System.out.println("If you need help, type help");
    }

    public static boolean checkArgs(int given, int min){
        if(given < min){
            System.err.println("Too few arguments!");
            return false;
        }
        return true;
    }
}