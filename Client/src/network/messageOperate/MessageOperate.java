package network.messageOperate;

import network.Builder.GroupBuilder;
import network.Builder.AccountBuilder;
import network.NetworkForClient.ConvertTypeTool;
import network.commonClass.*;

import java.util.ArrayList;

import javax.sound.midi.Soundbank;

import com.sun.java_cup.internal.runtime.Scanner;


/**
 * 标准通信协议处理类
 * @author ZiQin
 * @version V1.2.1
 */
public class MessageOperate {

    /**
     * 符号常量
     */
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
    public static final int ADD_GROUP = 14;         // 创建组
    public static final int GET_GROUP_LIST = 15;    // 获取群组列表
    public static final int CHANGE_GROUP = 16;      // 更改群组信息
    public static final int UPDATE_GROUP = 17;      // 更新群信息
    public static final int ADD_GROUP_BACK = 18;    // 对添加群的反馈
    public static final int USER_ADD_GROUP = 19;    // 用户添加群请求
    public static final int SEARCH_GROUP = 20;      // 搜索群
    public static final int DEL_GROUP = 21;         // 删除群
    public static final String OK = "ok";
    public static final String FALSE = "false";

    /**
     * 检查远端服务器将要发来的协议的类型
     * @param msg 远端服务器发来的信息
     * @return 返回检查类型的结果
     */
    public static int getMsgType(Message msg) {
        if (msg == null || msg.getText().length() == 0) {
            return ERROR;
        }
        switch (msg.getText().charAt(0)) {
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

    /**
     * 解析反馈消息，获取服务器发来的信息
     * 协议格式：Mmsg
     * @param msg 标准通信协议
     * @return 反馈内容
     */
    public static String unpackFeedbackMsg(Message msg) {
        String text = msg.getText();
        return text.substring(1);
    }

    /**
     * 该方法功能与unpackIsFinish方法相同，区别在与使用该方法之前需要先调用
     * unpackFeedbackMsg方法对数据报进行解析。
     * 调用者可以依个人喜好选择不同的方法
     * 协议格式：ok
     * @param msg
     * @return
     */
    public static boolean isFinish(String msg) {
        if (isOk(msg)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 解析反馈报，检查服务器处理结果是否ok。功能与isFinish方法相同，区别仅是该方法有解析数据报的功能。
     * 注意：该方法仅仅处理服务器是否同意之类的反馈（packageFinishMsg），对于其他反馈则不能进行有效处理
     * 协议格式：Lok
     * @param msg 标准通信协议
     * @return 服务同意请求与否
     */
    public static boolean unpackIsFinish(Message msg) {
        String text = msg.getText();
        if (isOk(text.substring(1))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 打包用户登录信息
     * 协议格式：LId Password
     * @param Id 要登录的用户ID
     * @param passwd 要登录的用户密码
     * @return 标准通信协议
     */
    public static Message packageLoginMsg(String Id, String passwd) {
        String text = new String("L" + Id + "\f" + passwd);
        return new Message(text, null);
    }

    /**
     * 打包用户登录信息
     * 协议格式：LId Password
     * @param accountBase 要登录的用户ID
     * @param passwd 要登录的用户密码
     * @return 标准通信协议
     */
    public static Message packageLoginMsg(AccountBase accountBase, String passwd) {
        String text = new String("L" + accountBase.getId() + "\f" + passwd);
        return new Message(text, null);
    }

    /**
     * 请求服务器发送自己的个人信息
     * 协议格式：I
     * @return 标准通信协议
     */
    public static Message packageAskMyselfInfoMsg() {
        return new Message(new String("I"), null);
    }

    /**
     * 接受服务器发来的个人信息
     * 协议格式：IID\fnickname\ftrue\signature
     * @return Account对象，保存个人基本信息
     */
    public static Account unpackMyselfInfoMsg(Message msg) {
        String myselfInfo = msg.getText().substring(1);
        String[] item = myselfInfo.split("\f");
        return new Account(item[0], item[1], true, item[3]);
    }

    /**
     * 打包详细的个人信息给服务器
     * @return 标准通信数据包
     */
    public static Message packageAskUserDetail() {
        String text = new String("I");
        return new Message(text, null);
    }

    public static Message packageUserDetail(Account account) {
        String text = new String("U");
        text += account.getId() + '\f' + account.getNikeName() + '\f' + account.getSignature() + '\f' +
                account.getMobliePhone() + '\f' + account.getMail() + '\f' + account.getStage() + '\f' +
                account.getOld() + '\f' + account.isMale() + '\f' + account.getHome() + '\f' + account.getOnline();
        byte[] stream = account.getPicture().getPictureBytes();
        return new Message(text, stream);
    }

    /**
     * 打包用户详细信息请求数据报
     * @param targetId 希望获取的用户ID
     * @return 标准通信数据报
     */
    public static Message packageAskOtherUserDetail(String targetId, String sourceId) {
        return new Message("G" + targetId + '\f' + sourceId, null);
    }

    /**
     * 解析用户个人信息的数据包
     * @param msg 标准通信数据包
     * @return Account对象
     */
    public static Account unpackageUserDetail(Message msg) {
        String text = msg.getText().substring(1);
        String[] item = text.split("\f");
        byte[] stream = msg.getStream();
        Picture picture = new Picture(stream);
        byte stage1 = (byte) Integer.parseInt(item[5]);
        int old1 = Integer.parseInt(item[6]);
        boolean male = false;
        if (item[7].equals("true"))
            male = true;
        boolean online = false;
        if (item[9].equals("true"))
            online = true;
        Account account = new AccountBuilder(item[0], item[1], item[2])
                .mobilePhone(item[3]).mail(item[4]).stage(stage1).old(old1).sex(male)
                .home(item[8]).picture(picture).online(online).createAccount();
        return account;
    }

    /**
     * 打包请求好友列表信息
     * @return 标准通信协议
     */
    public static Message packageAskFriendListMsg() {
        return new Message(new String("F"), null);
    }

    /**
     * 解析收到的好友列表
     * @param msg
     * @return 好友列表
     */
    public static ArrayList<Account> unpackFriendListMsg(Message msg) {
        String text = msg.getText();
        byte[] stream = msg.getStream();
        int i;
        String numberString = new String();
        for (i = 1; i < text.length() && text.charAt(i) != '\f'; i++) {
            numberString += text.charAt(i);
        }
        int number = Integer.valueOf(numberString);
        ArrayList<Account> list = new ArrayList<Account>();
        if (number == 0) {
        	return list;
        }
        String friendListMsg = text.substring(++i);
        String[] friendMsg = friendListMsg.split("\f");
        for (i = 0; i < number; i++) {
            boolean isOnline = false;
            String[] friendInfo = friendMsg[i].split("\n");
            if (friendInfo[2].equals("true")) {
                isOnline = true;
            }
//            Picture picture = getOnePicture(stream);	// for future
            Picture picture = null;						// for now
            Account account = new Account(friendInfo[0], friendInfo[1], isOnline, friendInfo[3], picture);
            list.add(account);
        }
        return list;
    }

    /**
     * 打包信封
     * 协议格式：CtargetID sourceID text
     * @param targetID 收件人
     * @param sourceID 发件人
     * @param msg 信封内容
     * @return 标准通信协议
     */
    public static Message packageEnvelope(String targetID, String sourceID, String msg) {
        String text = new String("C" + targetID + " " + sourceID + " " + msg);
        return new Message(text, null);
    }

    /**
     * 打包信封
     * 协议格式：CtargetId sourceId text
     * @param envelope 信封
     * @return 标准通信协议
     */
    public static Message packageEnvelope(Envelope envelope) {
        String text = new String("C" + envelope.getTargetAccountId() + " " + envelope.getSourceAccountId() + " " + envelope.getText());
        return new Message(text, null);
    }

    /**
     * 解析信封协议
     * 协议格式：CtargetId sourceId text
     * @param msg 标准通信协议（包含信封）
     * @return 信封
     */
    public static Envelope unpackEnvelope(Message msg) {
        String text = msg.getText();
        String friendId = new String();
        String me = new String();
        String message = new String();
        int k = 1;
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) == ' ' && k != 3) {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    me += text.charAt(i);
                    break;
                case 2:
                    friendId += text.charAt(i);
                    break;
                case 3:
                    message += text.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return new Envelope(me, friendId, message);
    }

    /**
     * 打包添加好友信息
     * 协议格式：AtargetID sourceID
     * @param targetID 目标ID
     * @param sourceID 发起ID
     * @return 标准通信协议
     */
    public static Message packageAddFriendMsg(String targetID, String sourceID) {
        String text = new String("A" + targetID + " " + sourceID);
        return new Message(text, null);
    }
    
    /**
     * 解包添加好友请求
     * 协议格式：AtargetID sourceID
     * @param msg 标准通信协议
     * @return 发起人ID 
     */
    public static String unpackAddFriendMsg(Message msg) {
		String text = msg.getText();
        String sourceId = new String();
		int k = 1;
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    break;
                case 2:
                	sourceId += text.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return sourceId;
	}

    /**
     * 打包添加好友结果信息
     * 协议格式：BtargetId sourceId ok
     * @param envelope 双方ID
     * @param ok 添加结果
     * @return 标准通信协议
     */
    public static Message packageAddFriendResultMsg(Envelope envelope, boolean ok) {
        String text = new String("B" + envelope.getTargetAccountId() + " " +  envelope.getSourceAccountId() + " " + ok);
        return new Message(text, null);
    }

    /**
     * 解析添加好友结果协议
     * 协议格式：BtargetId sourceId ok
     * @param msg 标准通信协议
     * @return 添加结果
     */
    public static boolean unpackAddFriendResultMsg(Message msg) {
        String text = msg.getText();
        String result = new String();
        int k = 1;
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    result += text.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return isOk(result);
    }

    /**
     * 打包查找好友信息
     * 协议格式：StargetId sourceId
     * @param accountID 查找目标ID
     * @param myselfID 发起人ID
     * @return 标准通信协议
     */
    public static Message packageSearchFriendMsg(String accountID, String myselfID) {
        String text = new String("S" + accountID + " " + myselfID);
        return new Message(text, null);
    }

    /**
     * 解析查找好友结果的协议
     * 协议格式：StargetId\fnickname\fisOnline\fsignature
     * @param msg 标准通信协议
     * @return 查找的用户，Account对象,若服务器未找到，则返回null
     */
    public static Account unpackSearchResultMsg(Message msg) {
        String text = msg.getText();
        if (text.equals("Snull")) {
            return null;
        }
        else {
            String accountInfo = text.substring(1);
            String[] item = accountInfo.split("\f");
            boolean online = false;
            if (item[2].equals("true")) {
                online = true;
            }
            return new Account(item[0], item[1], online, item[3]);
        }
    }

    /**
     * 打包删除好友的信息
     * 协议格式：DtargetId sourceId
     * @param targetId 目标ID
     * @param sourceID 发起人ID
     * @return 标准通信协议
     */
    public static Message packageDelFriendMsg(String targetId, String sourceID) {
        String text = new String("D" + targetId + " " + sourceID);
        return new Message(text, null);
    }

    /**
     * 解析删除好友协议
     * 协议格式：DtargetId sourceId result
     * @param msg
     * @return 信封（包含删除结果）
     */
    public static Envelope unpackDelFriendMsg(Message msg) {
        String text = msg.getText();
        String target = new String();
        String result = new String();
        int k = 1;
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                ++k;
                continue;
            }
            switch (k) {
                case 1:
                    target += text.charAt(i);
                    break;
                case 2:
                    result += text.charAt(i);
                    break;
                default:
                    break;
            }
        }
        return new Envelope(target, "", result);
    }

    /**
     * 发送创建群组请求
     * @param name 群名字
     * @param description 群描述
     * @return 标准通信协议包
     */
    public static Message packageAskCreateGroup(String sourceId, String name, String description) {
        String text = new String("Q" + sourceId + '\f' + name + '\f' + description);
        return new Message(text, null);
    }

    /**
     * 解析服务器响应建群的数据报
     * @param msg 标准通信协议数据报
     * @return 群组对象
     */
    public static Group unpackageRequestCreateGroup(Message msg) {
        String[] item = msg.getText().substring(1).split("\f");
        return new GroupBuilder().id(item[0]).name(item[1]).
                description(item[2]).message(item[3]).createGroup();
    }

    /**
     * 打包向服务器请求群组列表
     * @return 标准通信协议
     */
    public static Message packageAskGetGroupList() {
        return new Message("P", null);
    }

    /**
     * 解析服务器发送过来的群组列表数据报
     * @param msg 标准通信协议
     * @return 群组列表
     */
    public static ArrayList<Group> unpackageGroupList(Message msg) {
        String[] items = msg.getText().substring(1).split("\f");
        int number = Integer.parseInt(items[0]);
        ArrayList<Group> groups = new ArrayList<Group>();
        if (groups == null) 
        	return groups;
        for (int i = 0; i < number; i++) {
        	String[] item = items[i + 1].split("\n");
            Group group = new GroupBuilder().id(item[0]).name(item[1]).createGroup();
            groups.add(group);
        }
        return groups;
    }

    /**
     * 打包修改群信息
     * @param id 群ID
     * @param name 更改的群名（不更改使用“”）
     * @param description 更改的群描述（不更改使用“”）
     * @return 标准通信协议包
     */
    public static  Message packageChangeGroupMsg(String id, String name, String description) {
        String text = new String("E" + id + '\f' + name + '\f' + description);
        return new Message(text, null);
    }

    /**
     * 解析更改群消息结果消息报
     * @param msg 标准通信协议报
     * @return 群组对象
     */
    public static Group unpackageRequestChangeGroup(Message msg) {
        String[] item = msg.getText().substring(1).split("\f");
        return new GroupBuilder().id(item[0]).name(item[1])
                .description(item[2]).message(item[3]).createGroup();
    }

    /**
     * 打包更新某个群列表成员的消息
     * @param id 群ID
     * @return 标准通信协议数据报
     */
    public static Message packageUpdateGroup(String id) {
    	System.out.println("iii");
        return new Message("Y" + id, null);
    }

    /**
     * 解析更新某个群的数据报
     * @param msg 标准通信协议数据报
     * @return 群组对象
     */
    public static Group unpackageUpdateGroup(Message msg) {
        String[] items = msg.getText().substring(1).split("\f");
        int number = Integer.parseInt(items[0]);
        String id = items[1];
        String name = items[2];
        String description = items[3];
        ArrayList<Account> member = new ArrayList<Account>();
        for (int i = 0; i < number; i++) {
            String[] item = items[i + 4].split("\n");
            Account account = new AccountBuilder().id(item[0]).name(item[1]).createAccount();
            member.add(account);
        }
        return new GroupBuilder().id(id).name(name).description(description).member(member).createGroup();
    }

    /**
     * 打包搜索群的请求
     * @param gid 群组Id
     * @return 标准通信协议包
     */
    public static Message packageAskSearchGroup(String gid) {
        return new Message(new String("Z" + gid), null);
    }

    /**
     * 解析群搜索结果
     * @param msg 标准通信协议包
     * @return 群基本信息（仅包含ID、群名、群描述）
     */
    public static Group unpackageSearchGroupRes(Message msg) {
        String[] item = msg.getText().substring(1).split("\f");
        return new GroupBuilder().id(item[0]).name(item[1]).description(item[2]).createGroup();
    }

    /**
     * 打包添加群请求
     * @param gid 目标群id
     * @return 标准通信协议包
     */
    public static Message packageAskAddGroup(String gid) {
        return new Message(new String("J" + gid), null);
    }

    /**
     * 解包user请求群主添加群的消息
     * @param msg 标准通信协议包
     * @return 群id和user的信息（仅包含id和name）
     */
    public static AddGroupRequest unpackageAskGroupOwnerUserAddGroup(Message msg) {
        String[] item = msg.getText().substring(1).split("\f");
        Account account = new AccountBuilder().id(item[1]).name(item[2]).createAccount();
        AddGroupRequest addGroupRequest = new AddGroupRequest(item[0], account);
        return addGroupRequest;
    }

    /**
     * 打包是否允许user添加组的消息
     * @param gid 群组id
     * @param uid 被允许与否的用户
     * @param feedback 同意与否
     * @return 标准通信协议包
     */
    public static Message packageAddGroupRes(String gid, String uid, boolean feedback) {
        String text = "H";
        text += gid + "\f" + uid + "\f" + feedback;
        return new Message(text, null);
    }

    /**
     * 解包群主的意见（同意与否）
     * @param msg 标准通信协议
     * @return 群主的意见（仅群id和群主意见（同意与否））
     */
    public static AddGroupResult unpackageAddGroupResFromOwner(Message msg) {
        String[] item = msg.getText().substring(1).split("\f");     
        boolean ac = item[1].equals("true") ? true : false;
        return new AddGroupResult(item[0], null, ac);
    }

    /**
     * 打包删除群请求
     * @param gid 群id
     * @return 标准通信协议
     */
    public static Message pacakgeAskDelGroup(String gid) {
        return new Message(new String("V" + gid), null);
    }

    /**
     * 解包删除群消息结果
     * @param msg 标准通信协议
     * @return 删除群消息结果
     */
    public static DelGroupResult unpackageDelGroupRes(Message msg) {
        String[] item = msg.getText().substring(1).split("\f");
        return new DelGroupResult(item[0], item[1].equals("true") ? true : false);
    }

    /**
     * 判断结果是否可行
     * @param ok 结果
     * @return 判断结果
     */
    private static boolean isOk(String ok) {
        if (ok.equals(OK) || ok.equals("true")) {
            return true;
        }
        else  {
            return false;
        }
    }

    private static byte[] addPictureToList(byte[] pictureListStream, Picture picture) {
        byte[] picSize = ConvertTypeTool.intToByteArray(picture.getPictureSize());
        byte[] picStream = picture.getPictureBytes();
        byte[] stream = byteMerger(picSize, picStream, picStream.length);
        pictureListStream = byteMerger(pictureListStream, stream, stream.length);
        return pictureListStream;
    }

    private static Picture getOnePicture(byte[] pictureList) {
        // 获取图片在字节流中占的字节大小
        byte[] pictureSizeBytes = new byte[4];
        System.arraycopy(pictureList, 0, pictureSizeBytes, 0, 4);
        int pictureSize = ConvertTypeTool.byteArrayToInt(pictureSizeBytes);
        // 根据大小读取图片字节流
        byte[] picture = new byte[pictureSize];
        System.arraycopy(pictureList, 4, picture, 0, pictureSize);
        // 掐掉已经读取的图片字节流
        System.arraycopy(pictureList, 0, pictureList, 0, pictureSize + 4);
        return new Picture(picture);
    }

    /**
     * 字节数组合并
     * @param bt1 字节数组1
     * @param bt2 字节数组2
     * @return 字节数组1和字节数组2合并后的结果
     */
    private static byte[] byteMerger(byte[] bt1, byte[] bt2, int bt2Size){
        byte[] bt3 = new byte[bt1.length + bt2Size];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2Size);
        return bt3;
    }
}
