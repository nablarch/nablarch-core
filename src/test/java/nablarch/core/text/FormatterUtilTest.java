package nablarch.core.text;

import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * {@link FormatterUtil}のテストクラス
 *
 * @author Ryota Yoshinouchi
 */
public class FormatterUtilTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @After
    public void tearDown() {
        SystemRepository.clear();
    }

    @Test
    public void デフォルトのパターンでフォーマットできること() throws Exception {
        // デフォルトパターンはyyyy/MM/dd
        assertThat(FormatterUtil.format("dateTime", new SimpleDateFormat("yyyy/MM/dd").parse("2018/01/01")), is("2018/01/01"));
        // デフォルトパターンは#,###,##0.000
        assertThat(FormatterUtil.format("number", BigDecimal.valueOf(123456789.123)), is("123,456,789.123"));
    }

    @Test
    public void 指定したパターンでフォーマットできること() throws Exception {
        assertThat(FormatterUtil.format("dateTime", new SimpleDateFormat("yyyy/MM/dd").parse("2018/01/01"), "yyyy年MM月dd日"), is("2018年01月01日"));
        assertThat(FormatterUtil.format("number", BigDecimal.valueOf(1234567890), "#,###,###,### 円"), is("1,234,567,890 円"));
    }

    @Test
    public void システムリポジトリにフォーマッタコンフィグを登録した場合登録したフォーマッタを使用できること() throws Exception {
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
                dateTimeFormatter.setFormatterName("customDateTime");
                dateTimeFormatter.setDefaultPattern("yyyy年MM月dd日");

                NumberFormatter numberFormatter = new NumberFormatter();
                numberFormatter.setFormatterName("customNumber");
                numberFormatter.setDefaultPattern("#,###,###,### 円");

                List<Formatter<?>> list = new ArrayList<Formatter<?>>();
                list.add(dateTimeFormatter);
                list.add(numberFormatter);

                FormatterConfig formatterConfig = new FormatterConfig();
                formatterConfig.setFormatters(list);

                final Map<String, Object> result = new HashMap<String, Object>();
                result.put("formatter-config", formatterConfig);
                return result;
            }
        });

        assertThat(FormatterUtil.format("customDateTime", new SimpleDateFormat("yyyy/MM/dd").parse("2018/01/01")), is("2018年01月01日"));
        assertThat(FormatterUtil.format("customNumber", BigDecimal.valueOf(1234567890)), is("1,234,567,890 円"));
    }

    @Test
    public void システムリポジトリに登録していないフォーマッタを指定した場合エラーが送出されること() {
        expectedException.expect(IllegalArgumentException.class);
        FormatterUtil.format("invalidFormatter", "");
    }
}
