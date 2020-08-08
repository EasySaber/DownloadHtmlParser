import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {

        Handler fileHandler = null;
        try {
           fileHandler = new FileHandler("DHPmain.log");
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        logger.addHandler(fileHandler);

        DownloadHtmlPage DHtmlPage = new DownloadHtmlPage();    //Скачивание страницы HTML
        ParserHtmlPage tagNo = new ParserHtmlPage();            //Парсинг тегов HTML
        bdSql dataBase = new bdSql();                           //Запись результатов в БД и текстовый файл Result.txt



        while (true) {
            logger.info("New query");
            Scanner scUrl = new Scanner(System.in);
            System.out.println("Input URL(https:\\....) (Enter '+' to exit)>>> ");
            String url = scUrl.nextLine();

            if ("+".equals(url)){
                logger.info("Enter '+' -> Program exit.");
                break;
            }

            String fileName = DHtmlPage.downLoad(url);

            if (fileName != null) {
                tagNo.all();
                dataBase.all();
            }
        }
        fileHandler.close();
    }
}
