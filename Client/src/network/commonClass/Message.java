package network.commonClass;

/**
 * 数据包
 * @author ZiQin
 * @version v1.0.1
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
    public static final int ADD_GROUP = 14;         // 创建组
    public static final int GET_GROUP_LIST = 15;    // 获取群组列表
    public static final int CHANGE_GROUP = 16;      // 更改群组信息
    public static final int UPDATE_GROUP = 17;      // 更新群信息
    public static final int ADD_GROUP_BACK = 18;    // 对添加群的反馈
    public static final int USER_ADD_GROUP = 19;    // 用户添加群请求
    public static final int SEARCH_GROUP = 20;      // 搜索群
    public static final int DEL_GROUP = 21;         // 删除群

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
        if (msg == null || msg.length() == 0) {
            return ERROR;
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
            case 'Q':
                return ADD_GROUP;
            case 'P':
                return GET_GROUP_LIST;
            case 'E':
                return CHANGE_GROUP;
            case 'Y':
                return UPDATE_GROUP;
            case 'H':
                return ADD_GROUP_BACK;
            case 'J':
                return USER_ADD_GROUP;
            case 'Z':
                return SEARCH_GROUP;
            case 'V':
                return DEL_GROUP;
            default:
                return ERROR;
        }
    }

}
