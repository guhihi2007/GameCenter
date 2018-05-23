package cn.lt.game.lib.netdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Administrator on 2015/11/10.
 */
public class BaseBeanList<E> extends BaseBean implements List<E> {

    private List<E> dateList;

    public BaseBeanList(){
        dateList = new ArrayList<>();
    }

    @Override
    public void add(int location, E object) {
        dateList.add(location,object);
    }

    @Override
    public boolean add(E object) {
        return dateList.add(object);
    }


    @Override
    public boolean addAll(int location, Collection<? extends E> collection) {
        return dateList.addAll(location,collection);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return dateList.addAll(collection);
    }

    @Override
    public void clear() {
        dateList.clear();
    }

    @Override
    public boolean contains(Object object) {
        return dateList.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return dateList.containsAll(collection);
    }

    @Override
    public E get(int location) {
        return dateList.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return dateList.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return dateList.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return dateList.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return dateList.lastIndexOf(object);
    }

    @Override
    public ListIterator<E> listIterator() {
        return dateList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int location) {
        return dateList.listIterator(location);
    }

    @Override
    public E remove(int location) {
        return dateList.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return dateList.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return dateList.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return dateList.retainAll(collection);
    }

    @Override
    public E set(int location, E object) {
        return dateList.set(location,object);
    }

    @Override
    public int size() {
        return dateList.size();
    }

    @Override
    public List<E> subList(int start, int end) {
        return dateList.subList(start,end);
    }

    @Override
    public Object[] toArray() {
        return dateList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return dateList.toArray(array);
    }
}
