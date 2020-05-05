package top.duwd.common.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {

    /*我的思路也比较简单,就是遍历加切块,
     *若toIndex大于list的size说明已越界,需要将toIndex设为list的size值
     */
    public static <T> List<List<T>> splitList(List<T> list, int pageSize) {
        List<List<T>> listArray = new ArrayList<List<T>>();
        for (int i = 0; i < list.size(); i+=pageSize) {
            int toIndex = i + pageSize>list.size()?list.size():i+pageSize;
            listArray.add(list.subList(i, toIndex));
        }
        return listArray;
    }
}
