/*
   Парсинг скаченного HTML-файла
*/
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHtmlPage {
    private final String inFileName = "Download.html";
    private final String nostyleFileName = "DownloadNoStyle.html";
    private final String txtFileName = "DownloadNoTag.txt";
    private final String wordsFileName = "Words.txt";

    public void all() throws Exception {
        long startTime = System.nanoTime();

        ParserHtmlPage parser = new ParserHtmlPage();

        parser.noStyle();
        parser.noTagHtml();
        parser.parsingWords();

        long endTime = System.nanoTime();
        long msTimeRun = TimeUnit.MILLISECONDS.convert(endTime-startTime, TimeUnit.NANOSECONDS);
        System.out.println("Time run: " + msTimeRun + " ms");
    }

    //Удаление текста между тегами <style></style>
    //Парсер HTMLEditorKit.ParserCallback убирает только теги, но параметры оставляет в качестве видимого текста
    private void noStyle() throws IOException {
        try {
            BufferedReader readFile = new BufferedReader(new FileReader(inFileName));
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(nostyleFileName));

            String line;

            while ((line = readFile.readLine()) != null){
                Pattern pattern = Pattern.compile("<style.*?</style>");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                    writeFile.write(matcher.replaceAll(""));
                else writeFile.write(line);
            }
            writeFile.close();
            readFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //Парсер HTML-тегов
    //Использование стандартного парсера, без сторонней библиотеки
    private void noTagHtml() throws Exception{

        BufferedReader readFile = new BufferedReader(new FileReader(nostyleFileName));
        final BufferedWriter writeFile = new BufferedWriter(new FileWriter(txtFileName));

        HTMLDocument doc = new HTMLDocument() {
            public HTMLEditorKit.ParserCallback getReader(int pos) {
                return new HTMLEditorKit.ParserCallback() {
                    public void handleText(char[] data, int pos) {
                        try {
                            //Пропускаем пустые строки, где записан только 1 пробел
                            if(!String.valueOf(data).equals(" ")){
                                writeFile.write(data);
                                writeFile.newLine();
                            }
                        }
                        catch (IOException ie) {
                            System.out.println(ie.getMessage());
                        }
                    }
                };
            }

        };
        EditorKit kit = new HTMLEditorKit();
        kit.read(readFile, doc, 0);
        writeFile.close();

        File deleteFile = new File(nostyleFileName);
        deleteFile.deleteOnExit();

    }

    //Поиск слов в видимом тексте
    private void parsingWords() throws IOException {

        String search = "((?<=[\\s\\d.,! ?\":;\\[\\](){}+^|\\\\/*=])*|(^))" +
                "(([^|\\\\/\\s `^\\d.,!?\":;\\[\\](){}+=]+" +
                "[-][^|\\\\/\\n\\r\\t`  ^\\d.,!?\":;\\[\\](){}+=]+)|" +
                "([^|\\\\/\\s^'\\d., !?\":;\\[\\](){}+-=]+)|" +
                "([A-z]+[']+[A-z]+))(?=[\\s\\d.,! ?\":';\\[\\](){}+^|\\\\/*=-]|$)";

        try {
            BufferedReader readFile = new BufferedReader(new FileReader(txtFileName));
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(wordsFileName));

            String line;

            while ((line = readFile.readLine()) != null) {
                Pattern pattern = Pattern.compile(search);
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    String insertWord = line.substring(matcher.start(), matcher.end());

                    //Убираем специальные символы
                    Pattern patternSymbol = Pattern.compile("^[^A-zА-я@$&]$");
                    Matcher matcherSymbol = patternSymbol.matcher(insertWord);

                    if(!matcherSymbol.find()) {
                        writeFile.write(insertWord);
                        writeFile.newLine();
                    }
                }
            }
            readFile.close();
            writeFile.close();
            System.out.println(">>>Ok.Parsing Words. File name: Words.txt");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


}