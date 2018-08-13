package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassMessage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

class WrenchTest {

    @Test
    void scan() throws IOException {
//        ClassMessage message = ClassMessage.of(Collections.class);
//        message.getMethodMessages().stream().map(MethodMessage::getMethodDescription).forEach(System.out::println);
        Result scan = Wrench.wrench()
                            .ignoreClassVisibility()
                            .includePackages("java")
                            .scan();
        Map<String, List<ClassMessage>> stringListMap = scan.groupingByPackageName();
//        scan.getClassMessages()
//            .values()
//            .stream()
//            .map(ClassMessage::getMethodMessages)
//            .flatMap(List::stream)
//            .map(MethodMessage::getMethodDescription)
//            .forEach(System.out::println);
        System.out.println();
    }

}

class A extends B implements Serializable, List<Date> {

    static {
        new ArrayList<>();
    }

    {
        new ArrayList<>();
    }

    A(){

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<Date> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(Date date) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Date> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Date> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public Date get(int index) {
        return null;
    }

    @Override
    public Date set(int index, Date element) {
        return null;
    }

    @Override
    public void add(int index, Date element) {

    }

    @Override
    public Date remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<Date> listIterator() {
        return null;
    }

    @Override
    public ListIterator<Date> listIterator(int index) {
        return null;
    }

    @Override
    public List<Date> subList(int fromIndex, int toIndex) {
        return null;
    }
}

class B implements C {

}

interface C extends Serializable {

}