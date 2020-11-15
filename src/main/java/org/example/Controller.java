package org.example;

import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Controller {
    public TextField customInput;
    public TextArea textBox;
    public Label pathDisplay;
    public RadioButton customRd;
    public ProgressBar progress;

    public String path;
    public String chapterReg;// default: ".*(chapter|Chapter|ch.|Ch.)\\w*\\d+[a-z|A-Z]?"
    public String chapterRepReg;//default: ([_\-\s]|)(chapter|Chapter|ch.|Ch.)\w*\d+[a-z|A-Z]?
    public double tasks;
    DirectoryChooser fileBrowser = new DirectoryChooser();
    Stage stage;


    public void customOff(){
        customInput.setDisable(true);
        System.out.println("custom input has been disabled");
    }

    public void customOn(){
        customInput.setDisable(false);
        chapterReg =".*(chapter|Chapter|ch.|Ch.)\\w*\\d+[a-z|A-Z]?";
        System.out.println("custom input has been enabled");
    }

    public void browse(){
        // get the file selected
        File file = fileBrowser.showDialog(stage);

        if (file != null) {

            pathDisplay.setText(file.getAbsolutePath()+"\\");
            path = file.getAbsolutePath()+"\\";
            System.out.println(path);
        }
    }

    public void start(){

        textBox.clear();
        progress.setProgress(0);

        if (customRd.isSelected()){
            String customChapId = customInput.getText().trim().replaceAll("(\\s*,\\s*)|(\\s*\\|\\s*)|( \\s*)","|");
            chapterReg =".*("+ customChapId +")\\w*\\d+[a-z|A-Z]?";
            chapterRepReg = "([_\\-\\s]|)("+ customChapId +")\\w*\\d+[a-z|A-Z]?";

            System.out.println("your custom ID's are:[" + customChapId +"]");
            textBox.appendText("your custom ID's are: [" + customChapId +"]\n");

        }else {
            chapterReg =".*(chapter|Chapter|ch.|Ch.)\\w*\\d+[a-z|A-Z]?";
            chapterRepReg = "([_\\-\\s]|)(chapter|Chapter|ch.|Ch.)\\w*\\d+[a-z|A-Z]?";
        }

        String parentPath;
        parentPath = path;

        try {
            File parentDirectory = new File(parentPath);
            File[] listOfFiles = parentDirectory.listFiles();
            ArrayList<String> fileNames = new ArrayList<>();

            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.getName().endsWith(".zip")) {
                        fileNames.add(file.getName());
                    }
                }


            }

            System.out.println(fileNames.toString().replaceAll("(?<=(.zip)),","\n"));
            textBox.appendText(fileNames.toString().replaceAll("(?<=(.zip)),","\n") + "\n");

            tasks = fileNames.size();
            double taskNr = 1.00;
            for (String fileName1 : fileNames) {
                String folderName = fileName1.replace(".zip", "");
                if (folderName.matches(chapterReg)) {
                    folderName = folderName.replaceAll(chapterRepReg, "");
                }


                File folder = new File(parentPath + folderName);
                if (folder.mkdir()) {
                    System.out.println("Folder created: " + folder.getName());
                    textBox.appendText("Folder created: " + folder.getName() + "\n");
                } else {
                    System.out.println("Folder:" + folderName + " already exists.");
                    textBox.appendText("Folder:" + folderName + " already exists.\n");
                }

                String filePath = parentPath + fileName1;
                String newFilePath = parentPath + folderName + "\\" + fileName1;

                try {
                    Path temp = Files.move(Paths.get(filePath), Paths.get(newFilePath));
                    textBox.appendText("Files were successfully moved  to folder\n");
                } catch (IOException x) {
                    System.out.println("an error has occurred trying to move the files to folder.");
                    textBox.appendText("an error has occurred trying to move the files to folder.\n");
                }
                progress.setProgress((taskNr/tasks));
                taskNr++;
            }
        }catch (NullPointerException ex){
            System.out.println("No matching files found!");
            textBox.appendText("No matching files found!\n");
        }
    }

    public void conScroll(){
        textBox.setScrollTop(Double.MAX_VALUE);
    }
}
