package nablarch.core.util.map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.util.StringUtil;

import org.junit.Test;

/**
 * {@link MultipleKeyCaseMap}のテスト。
 * @author Kiyohito Itoh
 */
public class MultipleKeyCaseMapTest {

    /**
     * キーの大文字と小文字、アンダースコアの有無を区別しないマップであること。
     */
    @SuppressWarnings("serial")
    @Test
    public void test() {

        MultipleKeyCaseMap<Object> map;

        // デフォルトコンストラクタを使用する場合
        map = new MultipleKeyCaseMap<Object>();
        setUpTestData(map);
        assertMap(map);
        
        map = new MultipleKeyCaseMap<Object>();
        map.putAll(new HashMap<String, Object>() {{ setUpTestData(this); }}); // putAllの呼び出し
        assertMap(map);

        // マップのみ指定するコンストラクタを使用する場合
        map = new MultipleKeyCaseMap<Object>(new HashMap<String, Object>() {{ setUpTestData(this); }});
        assertMap(map);

        // マップとキー変換情報を指定するコンストラクタを使用する場合
        Map<String, Object> data = new HashMap<String, Object>() {{ setUpTestData(this); }};
        Map<String, String> keyNames = new HashMap<String, String>();
        for (String key : data.keySet()) {
            keyNames.put(StringUtil.lowerAndTrimUnderScore(key), key);
        }
        map = new MultipleKeyCaseMap<Object>(data, keyNames);
        assertMap(map);
    }

    private static void setUpTestData(Map<String, Object> map) { // putの呼び出し
        map.put("USER_ID", "test1"); // アンダースコア区切り
        map.put("USERNAME", "test2"); // すべて大文字
        map.put("userno", "test3"); // すべて小文字
        map.put("userAge", "test4"); // キャメルケース
        map.put("UserWeight", "test5"); // パスカルケース
        map.put("UserWas NOT found_inThe deparTment.", "test6"); // 込み込み
    }

    private static void assertMap(Map<String, Object> map) { // getとcontainsKeyの呼び出し

        // すべて大文字の場合
        assertThat(map.get("USERID").toString(), is("test1"));
        assertThat(map.get("USERNAME").toString(), is("test2"));
        assertThat(map.get("USERNO").toString(), is("test3"));
        assertThat(map.get("USERAGE").toString(), is("test4"));
        assertThat(map.get("USERWEIGHT").toString(), is("test5"));
        assertThat(map.get("USERWAS NOT FOUNDINTHE DEPARTMENT.").toString(), is("test6"));

        assertTrue(map.containsKey("USERID"));
        assertTrue(map.containsKey("USERNAME"));
        assertTrue(map.containsKey("USERNO"));
        assertTrue(map.containsKey("USERAGE"));
        assertTrue(map.containsKey("USERWEIGHT"));
        assertTrue(map.containsKey("USERWAS NOT FOUNDINTHE DEPARTMENT."));

        // すべて大文字＋アンスコ区切りの場合
        assertThat(map.get("USER_ID").toString(), is("test1"));
        assertThat(map.get("USER_NAME").toString(), is("test2"));
        assertThat(map.get("USER_NO").toString(), is("test3"));
        assertThat(map.get("USER_AGE").toString(), is("test4"));
        assertThat(map.get("USER_WEIGHT").toString(), is("test5"));
        assertThat(map.get("USER_WAS NOT FOUND_IN_THE DEPARTMENT.").toString(), is("test6"));

        assertTrue(map.containsKey("USER_ID"));
        assertTrue(map.containsKey("USER_NAME"));
        assertTrue(map.containsKey("USER_NO"));
        assertTrue(map.containsKey("USER_AGE"));
        assertTrue(map.containsKey("USER_WEIGHT"));
        assertTrue(map.containsKey("USER_WAS NOT FOUND_IN_THE DEPARTMENT."));

        // キャメルケースの場合
        assertThat(map.get("userId").toString(), is("test1"));
        assertThat(map.get("userName").toString(), is("test2"));
        assertThat(map.get("userNo").toString(), is("test3"));
        assertThat(map.get("userAge").toString(), is("test4"));
        assertThat(map.get("userWeight").toString(), is("test5"));
        assertThat(map.get("userWas not foundInThe department.").toString(), is("test6"));

        assertTrue(map.containsKey("userId"));
        assertTrue(map.containsKey("userName"));
        assertTrue(map.containsKey("userNo"));
        assertTrue(map.containsKey("userAge"));
        assertTrue(map.containsKey("userWeight"));
        assertTrue(map.containsKey("userWas not foundInThe department."));
    }
}
