import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class DownloadHtmlPage {
    private String DownloadFileName="Download.html";

    String downLoad(String inUrl){
        long startTime = System.nanoTime();


        UrlEncoding urlRename = new UrlEncoding();              //Преобразование Кириллицы в URL


        try {
            URL url = new URL(urlRename.enCoding(inUrl));

            System.out.println(">>>Ok.URL address");

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new FileWriter(DownloadFileName));

            String line;

            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }

            reader.close();
            writer.close();
            System.out.println(">>>Ok.Html-file download. File name: Download.html");

        }
        catch (MalformedURLException mue) {
            System.out.println("Error: in the URL address");
            DownloadFileName = null;
        }
        catch (IOException ie) {
            System.out.println("Error: input-output. Possible: you entered http:\\url. Try: https:\\url.");
            DownloadFileName = null;
        }

        long endTime = System.nanoTime();
        long msTimeRun = TimeUnit.MILLISECONDS.convert(endTime-startTime, TimeUnit.NANOSECONDS);
        System.out.println("Time run: " + msTimeRun + " ms");

        return DownloadFileName;
    }
}