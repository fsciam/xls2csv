package fsciamdev;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.*;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;




public class ConverterCallable implements Callable<Result>
{
    private File fileToBeConverted;
    private boolean folder;
    private String directoryPath;

    public ConverterCallable(File fileToBeConverted, boolean folder, String directoryPath) {
        this.fileToBeConverted = fileToBeConverted;
        this.folder = folder;
        this.directoryPath = directoryPath;
    }

    @Override
    public Result call() {

        FileInputStream fileInStream;
        try {
            fileInStream = new FileInputStream(fileToBeConverted);

        File convertedFile;
            int times = -1;
            do {
                times++;
                if (!folder)
                    convertedFile = new File(directoryPath + File.separator + "Converted-" + fileToBeConverted.getName().split(Pattern.quote("."))[0] + (times > 0 ? "(" + times + ")" : "") + ".csv");
                else
                    convertedFile = new File(directoryPath + File.separator + "Converted files" + File.separator + "Converted-" + fileToBeConverted.getName().split(Pattern.quote("."))[0] + (times > 0 ? "(" + times + ")" : "") + ".csv");

            } while (convertedFile.exists());

            Workbook workBook;
            System.out.println("Processing:\t" + fileToBeConverted.getName());
            System.out.println("Create:\t" + convertedFile.getName());
            // Open the xlsx and get the requested sheet from the workbook
            if (fileToBeConverted.getName().endsWith(".xls"))
                workBook = new HSSFWorkbook(fileInStream);
            else
                workBook = new XSSFWorkbook(fileInStream);

            Sheet selSheet = workBook.getSheetAt(0);

            // Iterate through all the rows in the selected sheet
            Iterator<Row> rowIterator = selSheet.iterator();
            StringBuilder sb = new StringBuilder();
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();

                // Iterate through all the columns in the row and build ","
                // separated string
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (sb.length() != 0) {
                        sb.append(",");
                    }


                    switch (cell.getCellType()) {
                        case STRING:
                            sb.append(cell.getStringCellValue());
                            break;
                        case BOOLEAN:
                            sb.append(cell.getBooleanCellValue());
                            break;
                        default:
                            sb.append(cell.getNumericCellValue());
                    }
                }

            }
            workBook.close();
            fileInStream.close();
            PrintWriter writer = new PrintWriter(convertedFile);
            writer.println(sb.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            return new Result(fileToBeConverted.getName(),false,e.getMessage());
        }
        return new Result(fileToBeConverted.getName(),true);
    }
}

