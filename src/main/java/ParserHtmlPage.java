/*
   Парсинг скаченного HTML-файла
*/
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHtmlPage {
    private static final Logger logger = Logger.getLogger(ParserHtmlPage.class.getName());
    private final String inFileName = "Download.html";
    private final String nostyleFileName = "DownloadNoStyle.html";
    private final String txtFileName = "DownloadNoTag.txt";
    private final String wordsFileName = "Words.txt";

    public void all() throws Exception {
        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("DHParser.log");
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        logger.addHandler(fileHandler);
        ParserHtmlPage parser = new ParserHtmlPage();
        parser.noStyle();
        parser.noTagHtml();
        parser.parsingWords();

        fileHandler.close();
    }

    //Удаление текста между тегами <style></style>
    private void noStyle(){
        try(BufferedReader readFile = new BufferedReader(new FileReader(inFileName));
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(nostyleFileName));) {
            String line = null;

            while (true) {
                try {
                    if ((line = readFile.readLine()) == null) break;
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                }
                Pattern pattern = Pattern.compile("<style.*?</style>");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    try {
                        writeFile.write(matcher.replaceAll(""));
                    } catch (IOException e) {
                        logger.warning(e.getMessage());
                    }
                } else {
                    try {
                        writeFile.write(line);
                    } catch (IOException e) {
                        logger.warning(e.getMessage());
                    }
                }
            }

        }
         catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

    //Парсер HTML-тегов
    //Использование стандартного парсера, без сторонней библиотеки
    private void noTagHtml() {
        try(BufferedReader readFile = new BufferedReader(new FileReader(nostyleFileName));
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(txtFileName));) {

            HTMLDocument doc = new HTMLDocument() {
                public HTMLEditorKit.ParserCallback getReader(int pos) {
                    return new HTMLEditorKit.ParserCallback() {
                        public void handleText(char[] data, int pos) {
                            try {
                                //Пропускаем пустые строки, где записан только 1 пробел
                                if (!String.valueOf(data).equals(" ")) {
                                    writeFile.write(data);
                                    writeFile.newLine();
                                }
                            } catch (IOException ie) {
                                logger.warning(ie.getMessage());
                            }
                        }
                    };
                }

            };
            EditorKit kit = new HTMLEditorKit();
            try {
                kit.read(readFile, doc, 0);
            } catch (BadLocationException e) {
                logger.warning(e.getMessage());
            }
        }
        catch (IOException e){
            logger.warning(e.getMessage());
        }

        File deleteFile = new File(nostyleFileName);
        deleteFile.deleteOnExit();

    }

    //Поиск слов в видимом тексте
    private void parsingWords() {
        String search = "((?<=[\\s\\d.,! ?\":;\\[\\](){}+^|\\\\/*=])*|(^))" +
                "(([^|\\\\/\\s `^\\d.,!?\":;\\[\\](){}+=]+" +
                "[-][^|\\\\/\\n\\r\\t`  ^\\d.,!?\":;\\[\\](){}+=]+)|" +
                "([^|\\\\/\\s^'\\d., !?\":;\\[\\](){}+-=]+)|" +
                "([A-z]+[']+[A-z]+))(?=[\\s\\d.,! ?\":';\\[\\](){}+^|\\\\/*=-]|$)";

        try (BufferedReader readFile = new BufferedReader(new FileReader(txtFileName));
             BufferedWriter writeFile = new BufferedWriter(new FileWriter(wordsFileName));){
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
            logger.info("Parsing Words. File name: Words.txt");

        } catch (IOException e) {
            logger.warning(e.getMessage());
        }


    }


}