package network.commonClass;

/**
 * 数据包
 * @author ZiQin
 * @version v1.0.0
 */
public class Message {

    /**
     * 符号常量
     */
    public static final int NULL = 0;
    public static final int LOGIN = 1;
    public static final int CHAT = 2;
    public static final int FRIENDLIST = 3;
    public static final int ERROR = 4;
    public static final int MSG = 5;
    public static final int ADDFRIEND = 6;
    public static final int DELETE = 7;
    public static final int SEARCH = 8;
    public static final int BACKADD = 9;
    public static final int REGISTOR = 10;
    public static final int MYSELF = 11;
	public static final int USER_DETAIL = 12;
	public static final int GET_OTHER_USER_DETAIL = 13;

    /**
     * 数据包类型
     */
    private int type;

    /**
     * 数据包中文本信息
     */
    private String text;

    /**
     * 数据包中字节流消息（包括图片、视频等）
     */
    private byte[] stream;

    /**
     * 默认构造函数
     */
    public Message() {
        this.text = null;
        this.stream = null;
        this.type = NULL;
    }

    /**
     * 构造函数
     * @param txt 数据包文本信息
     * @param bytes 数据包字节流消息
     */
    public Message(String txt, byte[] bytes) {
        this.text = (txt == null) ? "" : txt;
        this.stream = (bytes == null) ? new byte[0] : bytes;
        this.type = getMsgType(this.text);
    }

    /**
     * 获取数据包类型
     * @return 数据包类型
     */
    public int getType() {
        return type;
    }

    /**
     * 获取数据包文本
     * @return 数据包文本
     */
    public String getText() {
        return text;
    }

    /**
     * 获取数据包字节流
     * @return 数据包字节流
     */
    public byte[] getStream() {
        return stream;
    }

    /**
     * 检查远端服务器将要发来的协议的类型
     * @param msg 远端服务器发来的信息
     * @return 返回检查类型的结果
     */
    private static int getMsgType(String msg) {
        if (msg.length() == 0) {
            return NULL;
        }
        switch (msg.charAt(0)) {
            case 'C':
                return CHAT;
            case 'L':
                return LOGIN;
            case 'M':
                return MSG;
            case 'F':
                return FRIENDLIST;
            case 'A':
                return ADDFRIEND;
            case 'S':
                return SEARCH;
            case 'D':
                return DELETE;
            case 'B':
                return BACKADD;
            case 'R':
                return REGISTOR;
            case 'I':
                return MYSELF;
	        case 'U':
		        return USER_DETAIL;
	        case 'G':
		        return GET_OTHER_USER_DETAIL;
            default:
                return ERROR;
        }
    }

}