package nablarch.core.util.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Nablarchが後方互換性を維持するAPIであることを表すアノテーション。
 * <p/>
 * Nablarchが後方互換性を維持するAPI（メソッドやフィールドを含む）のことを公開APIと呼ぶ。<br/>
 * 本アノテーションを付けたAPIは、Javadocにより仕様が公開する。<br/>
 * クラスの全てのAPIを公開APIとする場合は、本アノテーションをクラス宣言に付与している。<br/>
 * 特定のAPIを公開APIとする場合は、本アノテーションを対象のAPI宣言に付与している。<br/>
 * また、利用者がオーバーライド可能なメソッドも公開APIとし、本アノテーションを付与している。<br/>
 * 公開APIのオーバーライドを行う場合は、Javadocに記述された仕様に則り実装を行うこと。<br/>
 *
 * @author Masayuki Fujikuma
 */
@Target({ElementType.TYPE,
         ElementType.FIELD,
         ElementType.CONSTRUCTOR,
         ElementType.METHOD,
         ElementType.ANNOTATION_TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Published {

    /**
     * 対象となる読者を識別する用途に使用するタグ（複数指定可）。
     * <p/>
     * 例えば、アーキテクト向けに公開したい場合は、
     * {@code @Published(tag = "architect")}というようにタグを付与する。<br/>
     * カスタムドックレットでAPIドキュメント生成時に、
     * 出力対象タグを明示的に指定することで、
     * そのタグを持つAPIのみを公開することができる。<br/>
     * この際、タグが指定されていないAPIも出力対象となる。
     * つまり、タグが指定されていない公開APIと"architect"タグが指定された公開APIが出力される。
     */
    String[] tag() default {"" };
}
