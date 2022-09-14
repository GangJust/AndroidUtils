package com.freegang.utils.object;

import org.jetbrains.annotations.NotNull;

public class GObjectUtils {
    private GObjectUtils() {

    }

    /**
     * 如果对象不为空, 则回调call方法
     *
     * @param object
     * @param objectCall
     * @param <T>
     */
    public static <T> void call(T object, ObjectCall<T> objectCall) {
        if (isNotNull(object)) {
            objectCall.call(object);
        }
    }

    /**
     * 判断某个对象是否为空
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> boolean isNull(T object) {
        return object == null;
    }

    /**
     * 判断某个对象是否非空
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> boolean isNotNull(T object) {
        return !isNull(object);
    }

    public interface ObjectCall<T> {
        void call(@NotNull T it);
    }

}
