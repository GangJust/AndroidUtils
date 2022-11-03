package com.freegang.androidutils.object;

import org.jetbrains.annotations.NotNull;

public class GObjectUtils {

    private GObjectUtils() {
        ///
    }

    // 对象回调接口
    public interface CallIt<T> {
        void call(@NotNull T it);
    }

    // 回调接口
    public interface CallElse {
        void call();
    }

    /**
     * 如果某个对象不为空, 则回调 CallIt<T>.call()
     *
     * @param it
     * @param callIt
     * @param <T>
     */
    public static <T> void call(T it, CallIt<T> callIt) {
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
    public static <T> void call(T it, CallIt<T> callIt, CallElse call) {
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
    public static <T> void call(T it, @NotNull T defaultIt, CallIt<T> callIt) {
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
    public static boolean isNotNull(Object o) {
        return o != null;
    }

    /**
     * 判断某个对象是否为空
     *
     * @param o
     * @return
     */
    public static boolean isNull(Object o) {
        return o == null;
    }

    /**
     * 工厂模式, 灰度中
     */
    public static class Factory {
        private static final Factory factory = new Factory();

        // 操作轮换的对象
        private Object object;

        // 计数器
        private int count = 0;

        // 是否抛出空异常
        private boolean throwNullPointerException = false;

        public static Factory at(Object o) {
            factory.object = o;
            factory.count = 0;
            return factory;
        }

        public Factory throwNullPointerException(boolean b) {
            throwNullPointerException = b;
            return factory;
        }

        public <T> Factory next(CallNext<T> callNext) {

            if (object == null) { //打印异常提示
                try {
                    throw new NullPointerException("In the `" + (count++) + " ~ next(null)`.");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    if (throwNullPointerException) throw e;
                }
            }

            object = callNext.call((T) object);
            return factory;
        }
    }

    public interface CallNext<T> {
        Object call(T it);
    }
}
