import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mojo(name = "parser", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MedicalProcedure extends AbstractMojo {

    @Parameter(defaultValue ="src/main/resources/insert-medical-procedure.sql", required = true, readonly = true)
    private String outputFile;

    @Parameter(defaultValue ="src/main/resources/data/medical-procedure.xlsx", required = true, readonly = true)
    private String inputFile;

    public MedicalProcedure() {
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        parse(inputFile);
    }

    public void parse(String file) {
        List<List<String>> medicalServices = read(file, 2);
        String sqlQuery = generateSqlQuery(medicalServices);
        generateFile(sqlQuery);
    }

    private List<List<String>> read(String file, int rowSkip) {
        List<List<String>> medicalServicesLists = new LinkedList<>();

        try (
                FileInputStream fileInputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)
        ) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rowEnd = sheet.getLastRowNum();

            for (int rowNum = rowSkip; rowNum <= rowEnd; rowNum++) {
                Row row = sheet.getRow(rowNum);
                Iterator<Cell> cellIterator = row.cellIterator();

                List<String> columns = new LinkedList<>();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    columns.add(cell.toString());
                }
                medicalServicesLists.add(columns);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return medicalServicesLists;
    }

    private String generateSqlQuery(List<List<String>> data) {

        String query = "INSERT INTO MEDICAL_PROCEDURE (CHAPTER_ID, CHAPTER_TITLE, SUB_CHAPTER_ID, " +
                "SUB_CHAPTER_TITLE, MAIN_CATEGORY_ID, MAIN_CATEGORY_TITLE, SUB_CATEGORY_ID, SUB_CATEGORY_TITLE) VALUES";

        StringBuilder insertSqlBuilder = new StringBuilder(query);

        for (List<String> x : data) {
            x.replaceAll(s -> s.replace("'", "''"));
            insertSqlBuilder.append("\n('").append(String.join("', '", x)).append("'),");
        }
        insertSqlBuilder.setCharAt(insertSqlBuilder.length() - 1, ';');

        return insertSqlBuilder.toString();
    }

    private void generateFile(String data) {
        try (
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, false))
        ) {
            writer.append(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


