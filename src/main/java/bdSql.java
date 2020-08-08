import java.io.*;
import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class bdSql {
    private static final Logger logger = Logger.getLogger(bdSql.class.getName());
    private Connection connect;
    private final String resultFileName = "Result.txt";
    private final String wordsFileName = "Words.txt";

    public void all() throws IOException {

        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("DHPbd.log");
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        logger.addHandler(fileHandler);

        bdSql sqlProg = new bdSql();
        sqlProg.connectBD();
        sqlProg.dropTable();
        sqlProg.createTable();
        sqlProg.inputFile();
        sqlProg.selectWords();
        sqlProg.disconnectBD();

        fileHandler.close();
    }


    //Подключение к БД
    private void connectBD(){
        try {
            Class.forName("org.sqlite.JDBC");
            connect = DriverManager.getConnection("JDBC:sqlite:SearchWords.db");
            logger.info("Connected BD.");
        }
        catch (Exception e){
            logger.warning(e.getMessage());
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
            logger.warning(e.getMessage());
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
           logger.warning(e.getMessage());
        }
    }

    //Вывод данных таблицы в консоль + запись в текстовый файл Result.txt
    private void selectWords(){
        String querySelectTable = "SELECT id, sword, quantity " +
                                  "FROM words ; ";
        try (BufferedWriter writeFile = new BufferedWriter(new FileWriter(resultFileName));){
            Statement statement = connect.createStatement();
            ResultSet rs = statement.executeQuery(querySelectTable);
            while (rs.next()){
                int id = rs.getInt("id");
                String word = rs.getString("sword");
                int quantity = rs.getInt("quantity");
                System.out.println(id + "\t" + word + "\t - " + quantity);

                writeFile.write(id + "\t" + word + "\t - " + quantity);
                writeFile.newLine();
            }

            logger.info("BD.Select words.");
            logger.info("File create Result.txt.");
        }
        catch (IOException | SQLException ie){
            logger.warning(ie.getMessage());
        }
    }

    //Отключение БД
    private void disconnectBD(){
        try {
            connect.close();
            logger.info("BD.Disconnect.");
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }

    //Чтение файла со словами. Запись результата в БД.
    private void inputFile() {
        try (BufferedReader readFile = new BufferedReader(new FileReader(wordsFileName));){
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
                        String queryInsert = "INSERT INTO words (sword) " +
                                "VALUES ('" + word + "') " +
                                "ON CONFLICT (sword) DO UPDATE SET quantity = quantity+1 ";
                        statement.executeUpdate(queryInsert);
                    }
                    statement.execute("COMMIT;");
            }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            logger.info("File Word.txt read. Words insert in BD.");
        }
        catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

}
