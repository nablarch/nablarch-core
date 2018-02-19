package nablarch.core.text;

import nablarch.core.repository.SystemRepository;
import nablarch.core.repository.di.DiContainer;
import nablarch.core.repository.di.config.xml.XmlComponentDefinitionLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

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

    @Before
    public void setUp() {
        XmlComponentDefinitionLoader loader = new XmlComponentDefinitionLoader("nablarch/core/text/formatter-test.xml");
        DiContainer container = new DiContainer(loader);
        SystemRepository.load(container);
    }

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
    public void システムリポジトリにフォーマッタコンフィグが登録されていない場合エラーが送出されること() {
        SystemRepository.clear();

        expectedException.expect(IllegalArgumentException.class);
        assertThat(FormatterUtil.format("number", BigDecimal.valueOf(1234567890), "#,###,###,### 円"), is("1,234,567,890 円"));
    }

    @Test
    public void システムリポジトリに登録していないフォーマッタを指定した場合エラーが送出されること() {
        expectedException.expect(IllegalArgumentException.class);
        FormatterUtil.format("invalidFormatter", "");
    }
}
