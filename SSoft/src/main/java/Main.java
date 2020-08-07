import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        while (true) {
            DownloadHtmlPage DHtmlPage = new DownloadHtmlPage();    //Скачивание страницы HTML
            ParserHtmlPage tagNo = new ParserHtmlPage();            //Парсинг тегов HTML
            bdSql dataBase = new bdSql();                           //Запись результатов в БД и текстовый файл Result.txt


            Scanner scUrl = new Scanner(System.in);
            System.out.print("Input URL(https:\\....) (Enter '+' to exit)>>> ");
            String url = scUrl.nextLine();

            if ("+".equals(url)){
                break;
            }

            String fileName = DHtmlPage.downLoad(url);

            if (fileName != null) {
                tagNo.all();
                dataBase.all();
            }
        }
    }
}
