package hu.saborsoft.blockchain.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LedgerList<T> implements Serializable {

    private static final long serialVersionUID = 3022665999968465754L;

    private List<T> list;

    public LedgerList() {
        list = new ArrayList<>();
    }

    public int size() {
        return list.size();
    }

    public T getLast() {
        return list.get(size() - 1);
    }

    public T getFirst() {
        return list.get(0);
    }

    public boolean add(T b) {
        return list.add(b);
    }

    public T findByIndex(int index) {
        return list.get(index);
    }

}
