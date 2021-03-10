package nablarch.core.text.json;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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

    @Test(expected = IllegalArgumentException.class)
    public void getRequiredPropで存在しない設定のとき例外がスローされること() throws Exception {
        Map<String,String> map = new HashMap<String, String>();
        map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);

        settings.getRequiredProp("beefPattern");
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

    @Test(expected = IllegalArgumentException.class)
    public void 読み込み済みの設定で抽出エラー() throws Exception {
        Map<String,String> map = new HashMap<String, String>();
        map.put("xxxFormatter.datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map, "xxxFormatter.", "filePath");

        settings.getRequiredProp("beefPattern");

    }

    @Test(expected = IllegalArgumentException.class)
    public void 読み込み済みの設定で値が空によるエラー() throws Exception {
        Map<String,String> map = new HashMap<String, String>();
        map.put("xxxFormatter.datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        map.put("xxxFormatter.porkPattern", "");
        JsonSerializationSettings settings = new JsonSerializationSettings(map, "xxxFormatter.", null);

        settings.getRequiredProp("porkPattern");

    }


}
