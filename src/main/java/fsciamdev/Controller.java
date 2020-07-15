package fsciamdev;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class Controller {
    @FXML
    private Button DirectoryPickerButton;
    @FXML
    private Button ConvertButton;
    @FXML
    private TextField DirectoryPath;
    @FXML
    private Pane MainPane;
    @FXML
    private CheckBox folder;
    @FXML
    private ProgressBar progressBar;
    private File selectedDirectory;

    private  ExecutorService threadPool;

    @FXML
    public void initialize()
    {
         threadPool= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
    public void shutdown() {
        threadPool.shutdown();
    }

    public void chooseFile(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        selectedDirectory = directoryChooser.showDialog(MainPane.getScene().getWindow());

        if(selectedDirectory != null){

            if(!isThereAnyFile(selectedDirectory))
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("There are no xls/xlsx files in the selected folder");
                alert.showAndWait();

                ConvertButton.setDisable(true);
                DirectoryPath.setText(null);
                selectedDirectory=null;
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Result");
                alert.setHeaderText(null);
                String message=countSpreadsheet()+(countSpreadsheet()>1?" files":" file")+" found, ready for conversion";
                alert.setContentText(message);
                alert.showAndWait();
                ConvertButton.setDisable(false);
                DirectoryPath.setText(selectedDirectory.getAbsolutePath());
            }

        }
    }

    private int countSpreadsheet()
    {
        int count=0;
        for(File f : Objects.requireNonNull(selectedDirectory.listFiles()))
        {

            if(f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx"))
            {
                count++;
            }
        }
        return count;
    }
    private boolean isThereAnyFile(File selectedDirectory) {


        for(File f : Objects.requireNonNull(selectedDirectory.listFiles()))
        {

            if(f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx"))
            {
                return true;
            }
        }
        return false;
    }

    public void convertFile(MouseEvent mouseEvent) {

        DirectoryPickerButton.setDisable(true);
        DirectoryPath.setDisable(true);
        ConvertButton.setDisable(true);
        progressBar.setVisible(true);


        if (folder.isSelected())
        {
            File newFolder=new File(selectedDirectory.getPath()+File.separator+"Converted files");
            if (!newFolder.exists())
                newFolder.mkdir();
        }
        List<Callable<Result>> fileTasks=new ArrayList<>();
        for(File f : Objects.requireNonNull(selectedDirectory.listFiles()))
        {
            if(f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx"))
                fileTasks.add(new ConverterCallable(f,folder.isSelected(),selectedDirectory.getPath()));
        }



        int fileConverted=0;
        List<Pair<String,String>> errors=new ArrayList<>();

        try {
            for (final Future<Result> future : threadPool.invokeAll(fileTasks)) {
                Result res = future.get();

                if(res.isCompleted()){
                    System.out.println(res.getFileName()+" COMPLETED");
                    fileConverted++;}
                else {
                    System.out.println(res.getFileName()+" FAILED");
                    errors.add(new Pair<>(res.getFileName(), res.getExceptionMessage()));
                }
            }
        } catch (ExecutionException | InterruptedException ex) { ex.printStackTrace(); }

        for(Pair<String,String> error:errors)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("xls2csv");
            alert.setHeaderText(error.getKey()+" conversion failed!");
            alert.setContentText("Message: "+error.getValue());
            alert.showAndWait();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("xls2csv");
        alert.setHeaderText(null);
        alert.setContentText("Conversion completed!\n Converted "+fileConverted+" files out of "+fileTasks.size());
        alert.showAndWait();

        DirectoryPickerButton.setDisable(false);
        DirectoryPath.setDisable(false);
        ConvertButton.setDisable(false);
        progressBar.setVisible(false);

    }
}
