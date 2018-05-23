package cn.lt.game.lib.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



public class MapVisitor <T extends Map<K, V>, K, V>{
	
	public void visitAll(T map, Visitor<Map.Entry<K, V>> v) {
		
		Iterator<Entry<K, V>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<K, V> e = it
					.next();
			v.visit(e);
		}
	}
}
