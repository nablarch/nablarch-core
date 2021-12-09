package nablarch.core.text.json;

import nablarch.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * シリアライザの初期化に使用する設定クラス。
 * @author Shuji Kitamura
 */
public class JsonSerializationSettings {

    /** 設定から取得する日時フォーマットのプロパティ名 */
    private static final String DATE_PATTERN_PROPERTY = "datePattern";

    /** デフォルトの日時フォーマット */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 設定内容 */
    private final Map<String, String> props;

    /** 設定取り込み対象とするプレフィックスとする文字列 */
    private final String prefix;

    /** 設定取り込み元のファイルパス */
    private final String filePath;

    /**
     * コンストラクタ。
     */
    public JsonSerializationSettings() {
        this(new HashMap<String, String>(),null,null);
    }

    /**
     * コンストラクタ。
     * @param settings 設定内容
     */
    public JsonSerializationSettings(Map<String, String> settings) {
        this(settings,null,null);
    }

    /**
     * 読み込み済みの設定から部分指定して設定を取り込むコンストラクタ。
     * 取り込み時はプレフィックスに指定した文字列を除いた部分がキーとして保持される。
     * @param settings 取り込み元の設定
     * @param prefix 取り込み対象のプレフィックスとする文字列
     * @param filePath 取り込み元のプロパティファイルのファイルパス
     */
    public JsonSerializationSettings(Map<String, String> settings, String prefix, String filePath) {
        this.filePath = filePath;
        this.prefix = prefix;
        if (StringUtil.isNullOrEmpty(prefix)) {
            props = settings;
        } else {
            props = getSettingsByPrefix(settings, prefix);
        }
    }

    /**
     * 全てのプロパティを取得する。
     * @return 全てのプロパティ
     */
    public Map<String, String> getProps() {
        return props;
    }

    /**
     * 指定されたプレフィックスにマッチする設定を取得する。
     * @param settings ログ出力の設定
     * @param prefix プレフィックス
     *
     * @return プレフィックスにマッチする設定
     */
    private Map<String, String> getSettingsByPrefix(Map<String, String> settings, String prefix) {
        int prefixLength = prefix.length();
        Map<String, String> settingsForPrefix = new HashMap<String, String>();
        for (Map.Entry<String, String> prop : settings.entrySet()) {
            if (prop.getKey().startsWith(prefix)) {
                settingsForPrefix.put(prop.getKey().substring(prefixLength), prop.getValue());
            }
        }
        return settingsForPrefix;
    }

    /**
     * 必須でないプロパティを取得する。
     * @param propName プロパティ名
     * @return プロパティに設定された値。プロパティが存在しない場合は<code>null</code>
     */
    public String getProp(String propName) {
        return getProps().get(propName);
    }

    /**
     * 必須プロパティを取得する。
     * @param propName プロパティ名
     * @return プロパティに設定された値
     * @throws IllegalArgumentException プロパティが存在しない場合
     */
    public String getRequiredProp(String propName) throws IllegalArgumentException {
        String propValue = getProps().get(propName);
        if (propValue == null || propValue.length() == 0) {
            throw new IllegalArgumentException(
                    "'" + (prefix != null ? prefix : "") + propName + "' was not specified."
                    + (!StringUtil.isNullOrEmpty(filePath) ? " file path = [" + filePath + "]" : ""));
        }
        return propValue;
    }

    /**
     * 日時フォーマットを取得する。<br>
     * 日時フォーマットのプロパティ名は"datePattern"。
     * プロパティの値が設定されていない、もしくはnull、空の文字列の場合、デフォルトの日時フォーマットとして、
     * "yyyy-MM-dd HH:mm:ss.SSS"を返す。
     * @return 日時フォーマット
     */
    public String getDatePattern() {
        String datePattern = getProp(DATE_PATTERN_PROPERTY);
        return !StringUtil.isNullOrEmpty(datePattern) ?  datePattern : DEFAULT_DATE_PATTERN;
    }

    /**
     * 日時フォーマットを設定する。
     * @param datePattern 日時フォーマット
     */
    public void setDatePattern(String datePattern) {
        getProps().put(DATE_PATTERN_PROPERTY, datePattern);
    }

}
