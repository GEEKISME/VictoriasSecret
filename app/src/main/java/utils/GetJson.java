package utils;

/**
 * Created by Lxh on 2017/10/19.
 */

public class GetJson {
    /**
     * URL 拼接
     *
     * @param source
     * @param regex
     * @param replacement
     * @return
     */
    public static String replace(String source, String regex, String replacement) {
        int index = -1;
        StringBuffer buffer = new StringBuffer();
        while ((index = source.indexOf(regex)) >= 0) {
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + regex.length());
        }
        buffer.append(source);
        return buffer.toString();
    }

}
