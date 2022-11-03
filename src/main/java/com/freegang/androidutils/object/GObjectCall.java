package com.freegang.androidutils.object;

import org.jetbrains.annotations.NotNull;

/**
 * 配合 Lambda 表达式食用效果极佳
 */
public interface GObjectCall {
    // 对象回调接口
    interface CallIt<T> {
        void call(@NotNull T it);
    }

    // 回调接口
    interface CallElse {
        void call();
    }

    /**
     * 如果某个对象不为空, 则回调 CallIt<T>.call()
     *
     * @param it
     * @param callIt
     * @param <T>
     */
    default <T> void call(T it, CallIt<T> callIt) {
        if (isNotNull(it)) callIt.call(it);
    }

    /**
     * 如果某个对象不为空, 则回调 CallIt<T>.call(), 否则回调 Call.call()
     *
     * @param it
     * @param callIt
     * @param call
     * @param <T>
     */
    default <T> void call(T it, CallIt<T> callIt, CallElse call) {
        if (isNull(it)) {
            call.call();
        } else {
            callIt.call(it);
        }
    }

    /**
     * 如果某个对象不为空, 则回调 CallIt<T>.call() 返回它本身; 否则, 返回给定的默认值
     *
     * @param it
     * @param callIt
     * @param defaultIt
     * @param <T>
     */
    default <T> void call(T it, @NotNull T defaultIt, CallIt<T> callIt) {
        if (isNull(it)) {
            callIt.call(defaultIt);
        } else {
            callIt.call(it);
        }
    }

    /**
     * 判断某个对象是否不为空
     *
     * @param o
     * @return
     */
    default boolean isNotNull(Object o) {
        return o != null;
    }

    /**
     * 判断某个对象是否为空
     *
     * @param o
     * @return
     */
    default boolean isNull(Object o) {
        return o == null;
    }

}
