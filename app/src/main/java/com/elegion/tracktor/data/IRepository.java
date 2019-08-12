package com.elegion.tracktor.data;

import java.util.List;

/**
 * @author Azret Magometov
 */
public interface IRepository<T> {

    int SORT_ORDER_ASC = 1;
    int SORT_ORDER_DESC = 2;
    int SORT_BY_START_DATE = 1;
    int SORT_BY_DURATION = 2;
    int SORT_BY_DISTANCE = 3;

    T getItem(long id);

    List<T> getAll();

    long insertItem(T t);

    boolean deleteItem(long id);

    void updateItem(T t);

    List<T> getAll(int sortOrder, int sortBy);


}
