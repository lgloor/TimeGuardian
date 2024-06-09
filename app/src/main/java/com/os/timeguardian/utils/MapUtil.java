package com.os.timeguardian.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortMapByValueDesc(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        return list;
    }
}
