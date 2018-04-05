/**
 * 
 */
package tech.njczh;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 97njczh
 *
 */
public class ThreadDatabase {

	public static Map<OnlineUser, ServerThread> sendThreadDb = new HashMap<OnlineUser, ServerThread>();

	public static Map<OnlineUser, ServerThread> recvThreadDb = new HashMap<OnlineUser, ServerThread>();

}
