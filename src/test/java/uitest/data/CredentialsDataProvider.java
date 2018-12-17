package uitest.data;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import org.testng.annotations.DataProvider;

public class CredentialsDataProvider {

    // Read data from csv file
    private static Object[][] readCsvData() {
        String csvFilePath = "src/test/java/uitest/data/data.csv";
        ArrayList<Object[]> dataList = new ArrayList<Object[]>();
        CSVReader reader = null;

        try {
            reader = new CSVReader(new FileReader(csvFilePath));
            String[] line;
            while ((line = reader.readNext()) != null) {
                Object[] record = { line[0], line[1], Boolean.parseBoolean(line[2]) };
                dataList.add(record);
            }
            reader.close();

            // Convert ArrayList<Object[]> to Object[][]
            return dataList.toArray(new Object[dataList.size()][]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Create the Data Provider
    @DataProvider(name = "Credentials")
    public static Object[][] credentials() {
        return readCsvData();
    }
}
