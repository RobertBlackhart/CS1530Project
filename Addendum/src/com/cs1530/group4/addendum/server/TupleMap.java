package com.cs1530.group4.addendum.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cs1530.group4.addendum.server.TupleMap.Pair;

public class TupleMap<K, V1, V2> implements Iterable<Map.Entry<K, Pair<V1,V2>>>
{
	private HashMap<K, Pair<V1, V2>> map;

	public TupleMap(int initialSize)
	{
		map = new HashMap<K, Pair<V1, V2>>(initialSize);
	}

	public TupleMap()
	{
		map = new HashMap<K, Pair<V1, V2>>();
	}

	public void put(K key, V1 value1, V2 value2)
	{
		Pair<V1, V2> pair = new Pair<V1, V2>(value1, value2);
		map.put(key, pair);
	}

	public Pair<V1, V2> get(K key)
	{
		Pair<V1, V2> pair = map.get(key);

		return pair;
	}
	
	@Override
	public Iterator<Map.Entry<K, Pair<V1, V2>>> iterator()
	{
		return map.entrySet().iterator();
	}

	static class Pair<L, R>
	{

		private final L left;
		private final R right;

		public Pair(L left, R right)
		{
			this.left = left;
			this.right = right;
		}

		public L getLeft()
		{
			return left;
		}

		public R getRight()
		{
			return right;
		}

		@Override
		public int hashCode()
		{
			return left.hashCode() ^ right.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if(o == null)
				return false;
			if(!(o instanceof Pair))
				return false;
			Pair pairo = (Pair) o;
			return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
		}

	}
}
