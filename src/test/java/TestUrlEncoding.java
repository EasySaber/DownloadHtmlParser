import org.junit.Assert;
import org.junit.Test;

public class TestUrlEncoding {
    @Test
    public void urlTest() {
        DownloadHtmlPage url = new DownloadHtmlPage();
        Assert.assertEquals("%D1%82%D0%B5%D1%81%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5",
                           url.enCoding("тестирование"));
        Assert.assertEquals("https://yandex.ru/search/?text=%D1%82%D0%B5%D1%81%D1%82%D0%B8%D1%80%D0%BE" +
                            "%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5&lr=195&clid=9582",
                            url.enCoding("https://yandex.ru/search/?text=тестирование&lr=195&clid=9582"));
    }
}
