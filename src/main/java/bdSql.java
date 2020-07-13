import java.io.*;
import java.sql.*;
import java.util.concurrent.TimeUnit;

public class bdSql {

    private Connection connect;
    private final String resultFileName = "Result.txt";
    private final String wordsFileName = "Words.txt";

    public void all() throws IOException {
        bdSql sqlProg = new bdSql();
        sqlProg.connectBD();
        sqlProg.dropTable();
        sqlProg.createTable();
        sqlProg.inputFile();
        sqlProg.selectWords();
        sqlProg.disconnectBD();
    }


    //Подключение к БД
    private void connectBD(){
        try {
            Class.forName("org.sqlite.JDBC");
            connect = DriverManager.getConnection("JDBC:sqlite:SearchWords.db");
            System.out.println(">>>Ok.Connected BD.");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    //Удаление старой таблицы
    private void dropTable() {
        String queryDropTable = "DROP TABLE words; ";

        try {
            Statement statement = connect.createStatement();
            statement.executeUpdate(queryDropTable);
            System.out.println(">>>Ok.BD.Drop table.");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    //Создание новой таблицы
    private void createTable() {
        String queryCreateTable = "CREATE TABLE words( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sword VARCHAR(100), " +
                "quantity INTEGER);";
        try {
            Statement statement = connect.createStatement();
            statement.executeUpdate(queryCreateTable);
            System.out.println(">>>Ok.BD.Create table.");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //Вывод данных таблицы в консоль + запись в текстовый файл Result.txt
    private void selectWords() throws IOException {
        long startTime = System.nanoTime();

        String querySelectTable = "SELECT id, sword, quantity " +
                "FROM words ; ";
        try {
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(resultFileName));

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
            writeFile.close();

            System.out.println(">>>Ok.BD.Select words.");
            System.out.println(">>>Ok.File create Result.txt.");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }


        long endTime = System.nanoTime();
        long msTimeRun = TimeUnit.MILLISECONDS.convert(endTime-startTime, TimeUnit.NANOSECONDS);
        System.out.println("Time run: " + msTimeRun + " ms");
    }

    //Отключение БД
    private void disconnectBD(){
        try {
            connect.close();
            System.out.println(">>>Ok.BD.Disconnect.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Чтение файла со словами. Запись результата в БД.
    private void inputFile() throws IOException {
        long startTime = System.nanoTime();

        try {
            BufferedReader readFile = new BufferedReader(new FileReader(wordsFileName));
            String word;
            System.out.println(">>>Please wait.....");
            while ((word = readFile.readLine()) != null) {
                String querySwordsTable = "SELECT COUNT(*) AS count " +
                        "FROM words " +
                        "WHERE sword = '" + word + "'";

                String queryUpdate = "UPDATE words " +
                        "SET quantity = quantity+1 " +
                        "WHERE sword = '" + word + "';";

                String queryInsert = "INSERT INTO words (sword, quantity) " +
                        "VALUES ('" + word + "', '1');";

                try {
                    Statement statement = connect.createStatement();
                    ResultSet rs = statement.executeQuery(querySwordsTable);
                    int count = rs.getInt("count");

                    if (count > 0) {
                        try {
                            statement.executeUpdate(queryUpdate);
                            //System.out.println(">>>Ok.BD.Update quantity for: "+word);
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        try {
                            statement.executeUpdate(queryInsert);
                            //System.out.println(">>>Ok.BD.Word insert: " + word);
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
            readFile.close();
            System.out.println(">>>Ok.File Word.txt read. Words insert in BD.");

            long endTime = System.nanoTime();
            long msTimeRun = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            System.out.println("Time run: " + msTimeRun + " ms");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }


    }

}
