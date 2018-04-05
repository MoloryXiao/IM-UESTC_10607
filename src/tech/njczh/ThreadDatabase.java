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

	public static Map<String, SendThread> sendThreadDb = new HashMap<String, SendThread>();

	public static Map<String, ServerThread> serverThreadDb = new HashMap<String, ServerThread>();

}
