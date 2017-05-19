package com.randal.aviana.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This is a Data driven singleton class, To use it, you need:
 *
 * 1, Define a custom data type;
 * 2, Place
 *         DataHolderBuilder.getSingleton(MyData.class).notifyObjectChanged(this.hashCode(), 1);
 *    where the data is modified
 * 3, RegisterListener by
 *         registerObjectChangedListener()/registerObjectListChangedListener()
 *    where you want to display when the data changes
 * 4, You should ensure every object you save here has DIFFERENT hashcode.
 *
 * That's All, Just enjoy that!
 */

public class DataHolderBuilder {
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
        void OnObjectChanged(int hashCode, int usrDef);
    }

    public interface OnObjectListChangedListener {
        void OnObjectAdded(int hashCode);
        void OnObjectRemoved(int hashCode);
        void OnObjectListChanged();
    }

    /*
     * Singleton Data Hold Class
     */
    public static class DataHolder<T> {
        private List<T> objectList;
        private List<OnObjectChangedListener> oCallbacks;
        private List<OnObjectListChangedListener> olCallbacks;

        private DataHolder(){
            objectList = new ArrayList<>();
            oCallbacks = new ArrayList<>();
            olCallbacks = new ArrayList<>();
        }

        public void notifyObjectChanged(int hashCode, int usrDef) {
            for (OnObjectChangedListener l : oCallbacks) {
                l.OnObjectChanged(hashCode, usrDef);
            }
        }

        public void addObject(T o) {
            if (o == null) {
                return;
            }

            objectList.add(o);
            for (OnObjectListChangedListener l : olCallbacks) {
                l.OnObjectAdded(o.hashCode());
            }
        }

        public void removeObject(T o) {
            if (o == null || !objectList.contains(o)) {
                return;
            }

            objectList.remove(o);
            for (OnObjectListChangedListener l : olCallbacks) {
                l.OnObjectRemoved(o.hashCode());
            }
        }

        public T getObjectByHashCode(int hash) {
            for (T o : objectList) {
                if (o.hashCode() == hash) {
                    return o;
                }
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
            objectList = null;
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
    }
}
