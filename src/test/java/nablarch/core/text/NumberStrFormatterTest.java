package nablarch.core.text;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class NumberStrFormatterTest {

    @Test
    public void フォーマッタの名前が取得できること() {
        NumberStrFormatter sut = new NumberStrFormatter();
        assertThat(sut.getFormatterName(), is("number"));
    }

    @Test
    public void パターンを指定しない場合デフォルトのパターンでフォーマットされること() {
        NumberStrFormatter sut = new NumberStrFormatter();
        assertThat(sut.format("123456789.123"), is("123,456,789.123"));
    }

    @Test
    public void 指数表現の文字列はそのまま返却されること() {
        NumberStrFormatter sut = new NumberStrFormatter();
        assertThat(sut.format("1.23E+3"), is("1.23E+3"));
    }

    @Test
    public void 指定されたパターンで数値文字列をフォーマットできること() {
        NumberStrFormatter sut = new NumberStrFormatter();
        String pattern = "00,000.000";

        assertThat(sut.format("123.123", pattern), is("00,123.123"));
    }

    @Test
    public void パターン文字列がnulの場合フォーマットされずに返却されること() {
        NumberStrFormatter sut = new NumberStrFormatter();
        assertThat(sut.format("123456789.123", null), is("123456789.123"));
    }

    @Test
    public void パターン文字列が不正な場合フォーマットされずに返却されること() {
        NumberStrFormatter sut = new NumberStrFormatter();
        String pattern = "#,###...000";

        assertThat(sut.format("123456789.123", pattern), is("123456789.123"));

    }

    @Test
    public void フォーマット対象がnullの場合nullが返却されること() {
        NumberStrFormatter sut = new NumberStrFormatter();
        String pattern = "00,000.000";

        assertThat(sut.format(null), is(nullValue()));
        assertThat(sut.format(null, pattern), is(nullValue()));
    }

}