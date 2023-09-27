package fr.iglee42.notenoughchest;

public class Deferred<T> {
    public T data;

    public T set(T val) {
        data = val;
        return val;
    }
}