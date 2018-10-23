package nablarch.core.util;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;

import nablarch.core.util.annotation.Published;

/**
 * Base64エンコーディングを行うユーティリティクラス。
 * 
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public final class Base64Util {
    
    /** エンコードの変換表 */
    private static final char[] ENCODING = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
    
    /** デコードの変換表 */
    private static final byte[] DECODING = new byte[124];
    
    static {
        for (int i = 0; i < DECODING.length; i++) {
            DECODING[i] = 0x00;
        }
        for (int i = 0; i < ENCODING.length; i++) {
            DECODING[ENCODING[i]] = (byte) i;
        }
    }
    
    /** 隠蔽コンストラクタ */
    private Base64Util() {
    }
    
    /**
     * バイト配列をBase64でエンコードする。
     * <p/>
     * 引数にnullが渡された場合、nullを返す。<br/>
     * 引数の長さが0の場合、空文字を返す。
     * 
     * @param b バイト配列
     * @return エンコード結果の文字列
     */
    public static String encode(byte[] b) {
        
        if (b == null) {
            return null;
        }
        
        StringBuilder base64 = new StringBuilder();
        int i = 0;
        while (i < b.length) {
            byte b1 = getByte(b, i++);
            base64.append(ENCODING[(b1 >>> 2) & 0x3F]);
            byte b2 = getByte(b, i++);
            base64.append(ENCODING[(b1 & 0x03) << 4 | (b2 >>> 4) & 0xF]);
            if (i == b.length) {
                byte b3 = getByte(b, i++);
                base64.append(ENCODING[(b2 & 0x0F) << 2 | (b3 >>> 6) & 0x03]);
                base64.append('=');
            } else if (i < b.length) {
                byte b3 = getByte(b, i++);
                base64.append(ENCODING[(b2 & 0x0F) << 2 | (b3 >>> 6) & 0x03]);
                base64.append(ENCODING[b3 & 0x3F]);
            } else {
                base64.append("==");
            }
        }

        return base64.toString();
    }
     
    /**
    * 文字列をバイトに変換する。
    * @param b バイトに変換したい文字列
    * @param pos インデックス
    * @return バイト
    */
    private static byte getByte(byte[] b, int pos) {
        return pos < b.length ? b[pos] : 0x00;
    }
    
    /**
     * Base64でエンコードした文字列をデコードする。
     * <p/>
     * 引数にnullが渡された場合、nullを返す。<br/>
     * 引数の長さが0の場合、空のバイト配列を返す。
     *
     * @param base64 Base64でエンコードした文字列
     * @return デコード結果のバイト配列
     * @throws IllegalArgumentException デコードできなかった場合
     */
    public static byte[] decode(String base64) throws IllegalArgumentException {
        
        if (base64 == null) {
            return null;
        }
        if (base64.length() == 0) {
            return new byte[0];
        }
        
        int length = base64.length();
        if (length % 4 != 0) {
            throw new IllegalArgumentException(
                String.format("length of base64 was invalid. base64 = [%s], length = [%s]", base64, length));
        }
        
        if (base64.substring(0, length - 2).indexOf("=") != -1
                || ('=' == base64.charAt(length - 2) && '=' != base64.charAt(length - 1))) {
            throw new IllegalArgumentException(
                    String.format("position of '=' in base64 was invalid. base64 = [%s]", base64));
        }
        
        if (containsInvalidCharacter(base64)) {
            throw new IllegalArgumentException(
                    String.format("base64 contained invalid character. base64 = [%s]", base64));
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;
        while (i < base64.length()) {
            
            char c1 = base64.charAt(i++);
            char c2 = base64.charAt(i++);
            char c3 = base64.charAt(i++);
            char c4 = base64.charAt(i++);
            
            byte b1 = DECODING[c1];
            
            byte b2 = 0x00;
            b2 = DECODING[c2];
            baos.write((byte) ((b1 & 0x3F) << 2 | (b2 >>> 4) & 0x03));
            
            byte b3 = 0x00;
            if (c3 != '=') {
                b3 = DECODING[c3];
                baos.write((byte) ((b2 & 0x0F) << 4 | (b3 >>> 2) & 0x0F));
            } else {
                break;
            }
            
            if (c4 != '=') {
                byte b4 = DECODING[c4];
                baos.write((byte) ((b3 & 0x03) << 6 | b4 & 0x3F));
            }
        }
        return baos.toByteArray();
    }
    
    /**
     * 文字列にBase64エンコードに使用しない不正な文字が含まれているかを判定する。
     * @param base64 文字列
     * @return 不正な文字が含まれている場合はtrue
     */
    private static boolean containsInvalidCharacter(String base64) {
        StringTokenizer tokenizer
            = new StringTokenizer(base64.toLowerCase(), "0123456789abcdefghijklmnopqrstuvwxyz+/=", false);
        return tokenizer.hasMoreTokens();
    }
}
