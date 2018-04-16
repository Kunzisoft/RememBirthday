package com.kunzisoft.remembirthday.utility;

/**
 * Created by joker on 05/08/17.
 */

public class QueryTool<E> {

    /**
     * Format string for query with string of object (add '(' at start, ',' between elements, ' and ')' at end )
     * Use with QueryTool.getString((Object[]) array) for suppress warning
     * @param array
     * @return
     */
    public static String getString(Object[] array) {
        StringBuilder ids = new StringBuilder();
        ids.append("(");
        for(int i = 0; i < array.length; i++) {
            ids.append("'");
            ids.append(String.valueOf(array[i]));
            if (i < array.length - 1) {
                ids.append("',");
            }
        }
        ids.append("')");
        return ids.toString();
    }

    public static String getString(Object object) {
        Object[] objects = new Object[1];
        objects[0] = object;
        return getString(objects);
    }
}
