import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class bdSql {
    private static final Logger logger = Logger.getLogger(bdSql.class.getName());
    private Connection connect;
    private final String resultFileName = "Result.txt";
    private final String wordsFileName = "Words.txt";

    public void all(String url) {

        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("DHPbd.log");
        } catch (IOException e) {
            logger.log(Level.WARNING,"Error: ", e);
        }
        logger.addHandler(fileHandler);

        bdSql sqlProg = new bdSql();
        sqlProg.connectBD();
        sqlProg.dropTable();
        sqlProg.createTable();
        sqlProg.inputFile();
        sqlProg.selectWords(url);
        sqlProg.disconnectBD();
    }


    //Подключение к БД
    private void connectBD(){
        try {
            Class.forName("org.sqlite.JDBC");
            connect = DriverManager.getConnection("JDBC:sqlite:SearchWords.db");
            logger.info("Connected BD.");
        }
        catch (Exception e){
            logger.log(Level.WARNING,"Error: ", e);
        }
    }

    //Удаление старой таблицы
    private void dropTable() {
        String queryDropTable = "DROP TABLE IF EXISTS words; ";
        try {
            Statement statement = connect.createStatement();
            statement.executeUpdate(queryDropTable);
            statement.execute("VACUUM;");
            logger.info("BD.Drop table.");
        }
        catch (SQLException e){
            logger.log(Level.WARNING,"Error: ", e);
        }

    }

    //Создание новой таблицы
    private void createTable() {
        String queryCreateTable = "CREATE TABLE words( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sword VARCHAR(100) UNIQUE, " +
                "quantity INT DEFAULT 1);";
        try {
            Statement statement = connect.createStatement();
            statement.executeUpdate(queryCreateTable);
            logger.info("BD.Create table.");
        }
        catch (SQLException e){
            logger.log(Level.WARNING,"Error: ", e);
        }
    }

    //запись в текстовый файл Result.txt
    private void selectWords(String url){
        String querySelectTable = "SELECT id, sword, quantity " +
                "FROM words ; ";
        try (BufferedWriter writeFile = new BufferedWriter(new FileWriter(resultFileName, true))){
            Statement statement = connect.createStatement();
            ResultSet rs = statement.executeQuery(querySelectTable);
            Date date = new Date();

            writeFile.write("------------------------------------------------------------");
            writeFile.newLine();
            writeFile.write("Link: " + url);
            writeFile.newLine();
            writeFile.write("Date: " + date.toString());
            writeFile.newLine();
            writeFile.write("------------------------------------------------------------");
            writeFile.newLine();

            while (rs.next()){
                int id = rs.getInt("id");
                String word = rs.getString("sword");
                int quantity = rs.getInt("quantity");
                writeFile.write(id + "\t" + word + "\t - " + quantity);
                writeFile.newLine();
            }

            logger.info("BD.Select words.");
            logger.info("File create Result.txt.");
        }
        catch (IOException | SQLException ie){
            logger.log(Level.WARNING,"Error: ", ie);
        }
    }

    //Отключение БД
    private void disconnectBD(){
        try {
            connect.close();
            logger.info("BD.Disconnect.");
        } catch (SQLException e) {
            logger.log(Level.WARNING,"Error: ", e);
        }
    }

    //Чтение файла со словами. Запись результата в БД.
    private void inputFile() {
        try (BufferedReader readFile = new BufferedReader(new FileReader(wordsFileName))){
            String word;
            boolean end = false;
            long i = 0;
            try {
                Statement statement = connect.createStatement();
                while (!end) {
                    statement.execute("BEGIN TRANSACTION;");
                    while (i < 100000) {
                        if ((word = readFile.readLine()) == null) {
                            end = true;
                            logger.info("File ended.");
                            break;
                        }
                        else {
                            i++;
                            String queryInsert = "INSERT INTO words (sword) " +
                                    "VALUES ('" + word + "') " +
                                    "ON CONFLICT (sword) DO UPDATE SET quantity = quantity+1 ";
                            statement.executeUpdate(queryInsert);
                        }
                    }
                    statement.execute("COMMIT;");
                    i = 0;
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING,"Error: ", e);
            }
            logger.info("File Word.txt read. Words insert in BD.");
        }
        catch (IOException e) {
            logger.log(Level.WARNING,"Error: ", e);
        }
    }

}
