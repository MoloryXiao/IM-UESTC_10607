package Core;


import java.util.ArrayList;
import java.util.Hashtable;

import network.commonClass.Envelope;

public class GroupEnvelopeRepertory {

	// 每个ID号提供一个信封盒子(ArrayList类型)
	private static Hashtable<String,ArrayList<Envelope>> hashTable_groupEnvelope;
	
	public GroupEnvelopeRepertory() {
		hashTable_groupEnvelope = new Hashtable<String,ArrayList<Envelope>>();
	}
	
	/**
	 * 根据ID号将信封存储到其对应的信封盒子中
	 * @param ID
	 * @param evp
	 */
	public static void addToBox(String ID,Envelope evp) {
		ArrayList<Envelope> arr_envelope = new ArrayList<Envelope>();
		
		if(hashTable_groupEnvelope.containsKey(ID)) {	// 若已存在该群组的未读消息 则先取出再附加
			arr_envelope = hashTable_groupEnvelope.get(ID);
		}
		arr_envelope.add(evp);
		
		hashTable_groupEnvelope.put(ID, arr_envelope);	// 重新装回
	}
	
	/**
	 * 根据ID号 从其对应的信封盒子中取出所有的信封
	 * 前提：检查信封仓库中
	 * @param ID
	 * @return arrylist类型的信封盒子
	 */
	public static ArrayList<Envelope> getFromBox(String ID) {
		ArrayList<Envelope> arr_envelope = new ArrayList<Envelope>();
		arr_envelope = hashTable_groupEnvelope.get(ID);
		
		hashTable_groupEnvelope.remove(ID);	// 取出后将该群组移除
		
		return arr_envelope;
	}
	
	/**
	 * 信封仓库中是否包含索引值为ID的信封盒子
	 * @param ID
	 * @return 搜索结果
	 */
	public static boolean isContainsKey(String ID) {
		return hashTable_groupEnvelope.containsKey(ID);
	}
	
	/**
	 * 索引值为ID的信封盒子中信封的数量
	 * @param ID
	 * @return 信封数量
	 */
	public static int getUserNotReadMessNum(String ID) {
		int number = 0;
		if(hashTable_groupEnvelope.containsKey(ID)) {
			number = hashTable_groupEnvelope.get(ID).size();
		}
		return number;
	}
}
