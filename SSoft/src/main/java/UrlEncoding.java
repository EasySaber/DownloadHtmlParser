
//Преобразование кириллицы в адресе
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlEncoding {
    String enCoding(String url) throws UnsupportedEncodingException {


        Pattern patURL = Pattern.compile("[А-яЁё]");
        Matcher matcherURL = patURL.matcher(url);

        StringBuffer urlBuff = new StringBuffer();

        while (matcherURL.find()) {
            String t = Character.toString(url.charAt(matcherURL.start()));
            matcherURL.appendReplacement(urlBuff, URLEncoder.encode(t, "UTF-8"));
        }
        matcherURL.appendTail(urlBuff);
        return urlBuff.toString();
    }
}