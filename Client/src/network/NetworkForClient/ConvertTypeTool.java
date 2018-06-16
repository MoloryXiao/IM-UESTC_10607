package network.NetworkForClient;

import java.io.UnsupportedEncodingException;

/**
 * 类型转换工具类
 * @author 土豆
 * @version v1.0.1
 */
public class ConvertTypeTool {
    /**
     * 描述：将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToByteArray(int value)
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    /**
     * 描述：将字节数组转为int类型的值
     * @param b 一个字节数组
     * @return int型的值
     */
    public static int byteArrayToInt(byte[] b) {
        return b[0] & 0xFF |
                (b[1] & 0xFF) << 8 |
                (b[2] & 0xFF) << 16 |
                (b[3] & 0xFF) << 24;
    }

    /**
     * 描述：将string类型的值转为字节数组
     * @param str string类型值
     * @return 一个字节数组
     */
    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = new byte[0];
        try {
            byteArray = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("[ERROR] 不支持该类型转换!");
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * 描述：将字节数组转为string类型
     * @param byteArray 一个字节数组
     * @return 一个string类型的值
     */
    public static String byteArrayToStr(byte[] byteArray) {
        String str = new String();
        if (byteArray == null) {
            return str;
        }
        try {
            int strEndIndex;
            for (strEndIndex = 0; strEndIndex < 1024; strEndIndex++) {
                if (byteArray[strEndIndex] == 0) break;
            }
            str = new String(byteArray, 0, strEndIndex, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("[ERROR] 不支持该类型转换!");
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 描述：将long型数值转为字节数组
     * @param values long型的数值
     * @return 字节数组
     */
    public static byte[] longToByteArray(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    /**
     * 描述：将byte数组转化为long型
     * @param buffer 一个字节数组
     * @return long型的数值
     */
    public static long byteArrayToLong(byte[] buffer) {
        long  values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8; values|= (buffer[i] & 0xff);
        }
        return values;
    }
}
