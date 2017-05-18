package com.springboottest.utils;

import java.util.Iterator;
import java.util.LinkedList;

/**

 */
public class MyList<E> extends LinkedList<E> {

    @Override
    public String toString() {
        Iterator<E> it = iterator();
        if (!it.hasNext())
            return "";

        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (!it.hasNext())
                return sb.toString();
            sb.append(',');
        }
    }
}
