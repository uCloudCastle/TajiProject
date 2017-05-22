package com.randal.aviana.data;

import android.util.Pair;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This is a Data driven singleton class, To use it, you need:
 *
 * 1, Define a custom data type;
 * 2, Place
 *         DataHolderBuilder.getSingleton(MyData.class).notifyObjectChanged(this, 1);
 *    where the data is modified
 * 3, RegisterListener by
 *         registerObjectChangedListener()/registerObjectListChangedListener()
 *    where you want to display when the data changes
 * 4, You should ensure every object you save here has DIFFERENT hashcode.
 *
 * That's All, Just enjoy that!
 */

public class DataHolderBuilder {
    public static final int DATA_HOLDER_ERROR_VALUE = -1;
    public static final int DATA_HOLDER_BITSET_SIZE = 64;

    @SuppressWarnings("rawtypes")
    private static final ConcurrentMap<Class, DataHolder> map = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> DataHolder<T> getSingleton(Class<T> type) {
        DataHolder<T> singleton = map.get(type);
        if (singleton == null) {
            synchronized (map) {
                if ((singleton = map.get(type)) == null) {
                    map.put(type, singleton = new DataHolder<>());
                }
            }
        }
        return singleton;
    }

    public interface OnObjectChangedListener {
        void OnObjectChanged(int oid, int usrDef);
    }

    public interface OnObjectListChangedListener {
        void OnObjectAdded(int oid);
        void OnObjectRemoved(int oid);
        void OnObjectListChanged();
    }

    /*
     * Singleton Data Hold Class
     */
    public static class DataHolder<T> {
        private List<T> objectList;
        private List<OnObjectChangedListener> oCallbacks;
        private List<OnObjectListChangedListener> olCallbacks;
        private List<Pair<Integer, Integer>> hash2IdList;
        private BitSet idSet;

        private DataHolder(){
            objectList = new ArrayList<>();
            oCallbacks = new ArrayList<>();
            olCallbacks = new ArrayList<>();
            hash2IdList = new ArrayList<>();
            idSet = new BitSet(DATA_HOLDER_BITSET_SIZE);
        }

        public void notifyObjectChanged(T object, int usrDef) {
            int oid = getIdByHashCode(object.hashCode());
            if (oid == DATA_HOLDER_ERROR_VALUE) {
                return;
            }

            for (OnObjectChangedListener l : oCallbacks) {
                l.OnObjectChanged(oid, usrDef);
            }
        }

        public int addObject(T o) {
            if (o == null) {
                return DATA_HOLDER_ERROR_VALUE;
            }

            objectList.add(o);
            int id = insertPair(o.hashCode());
            for (OnObjectListChangedListener l : olCallbacks) {
                l.OnObjectAdded(id);
            }
            return id;
        }

        public int removeObject(T o) {
            if (o == null || !objectList.contains(o)) {
                return DATA_HOLDER_ERROR_VALUE;
            }

            objectList.remove(o);
            int id = removePair(o.hashCode());
            for (OnObjectListChangedListener l : olCallbacks) {
                l.OnObjectRemoved(id);
            }
            return id;
        }

        public T getObjectById(int id) {
            int hash = getHashCodeById(id);
            if (hash != DATA_HOLDER_ERROR_VALUE) {
                return getObjectByHashCode(hash);
            }
            return null;
        }

        public List<T> getObjectList() {
            return objectList;
        }

        public void setObjectList(List<T> list) {
            objectList = list;
            for (OnObjectListChangedListener l : olCallbacks) {
                l.OnObjectListChanged();
            }
        }

        public void releaseData() {
            objectList.clear();
            hash2IdList.clear();
            idSet.clear();
            for (OnObjectListChangedListener l : olCallbacks) {
                l.OnObjectListChanged();
            }
        }

        public boolean registerObjectChangedListener(OnObjectChangedListener l) {
            return  !(l == null || oCallbacks.contains(l)) && oCallbacks.add(l);
        }

        public boolean unregisterObjectChangedListener(OnObjectChangedListener l) {
            return  !(l == null || !oCallbacks.contains(l)) && oCallbacks.remove(l);
        }

        public boolean registerObjectListChangedListener(OnObjectListChangedListener l) {
            return  !(l == null || olCallbacks.contains(l)) && olCallbacks.add(l);
        }

        public boolean unregisterObjectListChangedListener(OnObjectListChangedListener l) {
            return  !(l == null || !olCallbacks.contains(l)) && olCallbacks.remove(l);
        }

        private int insertPair(int hashCode) {
            int id = produceId();
            if (id == DATA_HOLDER_ERROR_VALUE) {
                return id;
            }

            hash2IdList.add(new Pair<>(hashCode, id));
            idSet.set(id);
            return id;
        }

        private int removePair(int hashCode) {
            for (Pair<Integer, Integer> pair : hash2IdList) {
                if (pair.first == hashCode) {
                    hash2IdList.remove(pair);
                    idSet.clear(pair.second);
                    return pair.second;
                }
            }
            return DATA_HOLDER_ERROR_VALUE;
        }

        private T getObjectByHashCode(int hash) {
            for (T o : objectList) {
                if (o.hashCode() == hash) {
                    return o;
                }
            }
            return null;
        }

        private int getIdByHashCode(int hashCode) {
            for (Pair<Integer, Integer> pair : hash2IdList) {
                if (pair.first == hashCode) {
                    return pair.second;
                }
            }
            return DATA_HOLDER_ERROR_VALUE;
        }

        private int getHashCodeById(int id) {
            for (Pair<Integer, Integer> pair : hash2IdList) {
                if (pair.second == id) {
                    return pair.first;
                }
            }
            return DATA_HOLDER_ERROR_VALUE;
        }

        private int produceId() {
            for (int i = 0; i < idSet.size(); ++ i) {
                if (!idSet.get(i)) {
                    return i;
                }
            }
            return DATA_HOLDER_ERROR_VALUE;
        }
    }
}
