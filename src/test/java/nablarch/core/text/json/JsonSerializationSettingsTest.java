package nablarch.core.text.json;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThrows;

/**
 * {@link JsonSerializationSettings}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class JsonSerializationSettingsTest {

    @Test
    public void 設定の取得ができること() throws Exception {
        Map<String,String> map = new HashMap<String, String>();
        map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);

        assertThat(settings.getProp("datePattern"), is("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        assertThat(settings.getRequiredProp("datePattern"), is("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    @Test
    public void getPropで存在しない設定のときnullを返すこと() throws Exception {
        Map<String,String> map = new HashMap<String, String>();
        map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);

        assertThat(settings.getProp("beefPattern"), is(nullValue()));
    }

    @Test
    public void getRequiredPropで存在しない設定のとき例外がスローされること() throws Exception {

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Map<String,String> map = new HashMap<String, String>();
                map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                JsonSerializationSettings settings = new JsonSerializationSettings(map);

                settings.getRequiredProp("beefPattern");
            }
        });

        assertThat(e.getMessage(), is("'beefPattern' was not specified."));

    }

    @Test
    public void 読み込み済みの設定で初期化できること() throws Exception {
        Map<String,String> map = new HashMap<String, String>();
        map.put("oooFormatter.datePattern", "miss");
        map.put("xxxFormatter.datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map, "xxxFormatter.", "filePath");

        assertThat(settings.getProp("datePattern"), is("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        assertThat(settings.getRequiredProp("datePattern"), is("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    @Test
    public void 読み込み済みの設定で抽出エラー() throws Exception {

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Map<String,String> map = new HashMap<String, String>();
                map.put("xxxFormatter.datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                JsonSerializationSettings settings = new JsonSerializationSettings(map, "xxxFormatter.", "filePath");

                settings.getRequiredProp("beefPattern");
            }
        });

        assertThat(e.getMessage(), is("'xxxFormatter.beefPattern' was not specified. file path = [filePath]"));
    }

    @Test
    public void 読み込み済みの設定で値が空によるエラー() throws Exception {

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Map<String,String> map = new HashMap<String, String>();
                map.put("xxxFormatter.datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                map.put("xxxFormatter.porkPattern", "");
                JsonSerializationSettings settings = new JsonSerializationSettings(map, "xxxFormatter.", null);

                settings.getRequiredProp("porkPattern");
            }
        });

        assertThat(e.getMessage(), is("'xxxFormatter.porkPattern' was not specified."));
    }
}
