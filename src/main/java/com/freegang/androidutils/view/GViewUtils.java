package com.freegang.androidutils.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/// View 工具类
public class GViewUtils {

    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
        ///
    }

    private GViewUtils() {
        ///
    }

    /**
     * 对某个 ViewGroup 下的所有子视图进行 Visibility 设置
     *
     * @param viewGroup
     * @param visibility
     */
    public static void setVisibilityAll(ViewGroup viewGroup, @Visibility int visibility) {
        if (viewGroup.getChildCount() == 0) {
            viewGroup.setVisibility(visibility);
            return;
        }

        // 先递归遍历设置所有子视图的 Visibility
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                setVisibilityAll((ViewGroup) childAt, visibility);
            } else {
                childAt.setVisibility(visibility);
            }
        }

        // 在设置当前视图的 Visibility
        viewGroup.setVisibility(visibility);
    }

    public static void setVisibleAll(ViewGroup viewGroup) {
        setVisibilityAll(viewGroup, VISIBLE);
    }

    public static void setGoneAll(ViewGroup viewGroup) {
        setVisibilityAll(viewGroup, GONE);
    }

    public static void setInvisibleAll(ViewGroup viewGroup) {
        setVisibilityAll(viewGroup, INVISIBLE);
    }

    /**
     * 判断某个视图是否可见
     *
     * @param view
     * @return
     */
    public static boolean isVisible(View view) {
        return view.getVisibility() == VISIBLE;
    }

    /**
     * 判断某个视图是否隐藏
     *
     * @param view
     * @return
     */
    public static boolean isGon(View view) {
        return !isVisible(view);
    }

    /**
     * 判断某个视图是否不可见, 但是仍然占位
     *
     * @param view
     * @return
     */
    public static boolean isInvisible(View view) {
        return view.getVisibility() == INVISIBLE;
    }

    /**
     * 某个父视图下的所有视图是否可见
     *
     * @param viewGroup
     * @return
     */
    public static boolean isVisibleAll(ViewGroup viewGroup) {
        List<Integer> resultList = new ArrayList<>();
        _traverseVisibilityAll(viewGroup, resultList);
        return !resultList.contains(GONE); //不包含 GONE
    }

    /**
     * 某个父视图下的所有视图是否隐藏
     *
     * @param viewGroup
     * @return
     */
    public static boolean isGonAll(ViewGroup viewGroup) {
        List<Integer> resultList = new ArrayList<>();
        _traverseVisibilityAll(viewGroup, resultList);
        return !resultList.contains(VISIBLE);  //不包含 VISIBLE
    }

    /**
     * 某个父视图下的所有视图是否不可见, 但仍然占位
     *
     * @param viewGroup
     * @return
     */
    public static boolean isInvisibleAll(ViewGroup viewGroup) {
        List<Integer> resultList = new ArrayList<>();
        _traverseVisibilityAll(viewGroup, resultList);
        return !(resultList.contains(GONE) || resultList.contains(VISIBLE));  //不包含 GONE 或 VISIBLE
    }

    /**
     * 递归遍历某个 ViewGroup 的 Visibility
     *
     * @param viewGroup
     * @param resultList
     */
    private static void _traverseVisibilityAll(ViewGroup viewGroup, List<Integer> resultList) {
        if (viewGroup.getChildCount() == 0) {
            resultList.add(viewGroup.getVisibility());
            return;
        }

        // 先递归获取所有子 View、ViewGroup 的 Visibility
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                _traverseVisibilityAll((ViewGroup) childAt, resultList);
            } else {
                resultList.add(childAt.getVisibility());
            }
        }

        // 再获取当前 ViewGroup 的 Visibility
        resultList.add(viewGroup.getVisibility());
    }

    /**
     * 某个视图是否可用
     *
     * @param view
     * @return
     */
    public static boolean isEnabled(View view) {
        if (view == null) return false;
        return view.isEnabled();
    }

    /**
     * 某个父视图下的所有视图是否可用
     *
     * @param viewGroup
     * @return
     */
    public static boolean isEnabledAll(ViewGroup viewGroup) {
        List<Boolean> resultList = new ArrayList<>();
        _traverseEnabledAll(viewGroup, resultList);
        return !resultList.contains(false); //不包含 false
    }

    /**
     * 递归遍历某个 ViewGroup 的 isEnabled
     *
     * @param viewGroup
     * @param resultList
     */
    private static void _traverseEnabledAll(ViewGroup viewGroup, List<Boolean> resultList) {
        if (viewGroup.getChildCount() == 0) {
            resultList.add(viewGroup.isEnabled());
            return;
        }

        // 先递归获取所有子 View、ViewGroup
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                _traverseEnabledAll((ViewGroup) childAt, resultList);
            } else {
                resultList.add(childAt.isEnabled());
            }
        }

        // 再获取当前 ViewGroup
        resultList.add(viewGroup.isEnabled());
    }

    /**
     * 遍历ViewGroup 获取所有子视图, 该方法会遍历xml节点树, 将指定对象获取到线性数组中(层级将会彻底打乱)
     *
     * @param viewGroup
     * @return
     */
    public static List<View> deepViewGroup(ViewGroup viewGroup) {
        List<View> list = new ArrayList<>();
        _recursionViewGroup(viewGroup, list);
        return list;
    }

    private static void _recursionViewGroup(ViewGroup viewGroup, List<View> list) {
        int childCount = viewGroup.getChildCount();
        if (childCount == 0) return;
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                _recursionViewGroup((ViewGroup) childAt, list);
            } else {
                list.add(childAt);
            }
        }
    }

    /**
     * 遍历ViewGroup, 获取指定类型的View, 该方法会遍历xml节点树, 将指定对象获取到线性数组中(层级将会彻底打乱)
     *
     * @param viewGroup
     * @param targetType
     * @param <T>
     * @return
     */
    public static <T extends View> List<T> findViews(ViewGroup viewGroup, Class<T> targetType) {
        List<T> list = new ArrayList<>();
        _recursionViewGroup(viewGroup, list, targetType);
        return list;
    }

    private static <T extends View> void _recursionViewGroup(ViewGroup viewGroup, List<T> list, Class<T> targetType) {
        int childCount = viewGroup.getChildCount();
        if (childCount == 0) return;
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                if (targetType.isInstance(childAt)) {
                    list.add(targetType.cast(childAt));
                }
                _recursionViewGroup((ViewGroup) childAt, list, targetType);
            } else {
                if (targetType.isInstance(childAt)) {
                    list.add(targetType.cast(childAt));
                }
            }
        }
    }

    /**
     * 遍历ViewGroup, 获取指定类型的View, 并且当contains(contentDescription)文本时, 将指定对象获取到线性数组中(层级将会彻底打乱)
     *
     * @param viewGroup
     * @param targetType
     * @param containsContentDescription
     * @param <T>
     * @return
     */
    public static <T extends View> List<T> findViews(ViewGroup viewGroup, Class<T> targetType, String containsContentDescription) {
        List<T> list = new ArrayList<>();
        _recursionViewGroup(viewGroup, list, targetType, containsContentDescription);
        return list;
    }

    private static <T extends View> void _recursionViewGroup(ViewGroup viewGroup, List<T> list, Class<T> targetType, String containsContentDescription) {
        if (containsContentDescription == null) return;
        int childCount = viewGroup.getChildCount();
        if (childCount == 0) return;
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            String contentDescription = childAt.getContentDescription().toString();
            if (childAt instanceof ViewGroup) {
                if (targetType.isInstance(childAt)) {
                    if (contentDescription.contains(containsContentDescription)) {
                        list.add(targetType.cast(childAt));
                    }
                }
                _recursionViewGroup((ViewGroup) childAt, list, targetType, containsContentDescription);
            } else {
                if (targetType.isInstance(childAt)) {
                    if (contentDescription.contains(containsContentDescription)) {
                        list.add(targetType.cast(childAt));
                    }
                }
            }
        }
    }

    // 反射获取事件监听器 View$ListenerInfo 内部类
    private static <T extends View> Object getListenerInfo(T view) {
        try {
            Field mListenerInfoField = findFieldRecursiveImpl(view.getClass(), "mListenerInfo");
            mListenerInfoField.setAccessible(true);
            return mListenerInfoField.get(view);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取点击事件, 如果有, 否则返回NULL
     *
     * @param view 需要获取点击事件的视图
     * @return 返回该点击事件的具体实现, 需要响应点击, 请手动调用 onClick 方法.
     */
    public static <T extends View> View.OnClickListener getOnClickListener(T view) {
        Object listenerInfo = getListenerInfo(view);
        if (listenerInfo == null) return null;
        try {
            Field mOnClickListenerField = listenerInfo.getClass().getDeclaredField("mOnClickListener");
            mOnClickListenerField.setAccessible(true);
            Object mOnClickListener = mOnClickListenerField.get(listenerInfo);
            if (mOnClickListener instanceof View.OnClickListener) {
                return (View.OnClickListener) mOnClickListener;
            }
            return null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取点击事件, 如果有, 否则返回NULL
     *
     * @param view 需要获取点击事件的视图
     * @return 返回该点击事件的具体实现, 需要响应长按, 请手动调用 onLongClick 方法.
     */
    public static <T extends View> View.OnLongClickListener getOnLongClickListener(T view) {
        Object listenerInfo = getListenerInfo(view);
        if (listenerInfo == null) return null;
        try {
            Field mOnLongClickListenerField = listenerInfo.getClass().getDeclaredField("mOnLongClickListener");
            mOnLongClickListenerField.setAccessible(true);
            Object mOnLongClickListener = mOnLongClickListenerField.get(listenerInfo);
            if (mOnLongClickListener instanceof View.OnLongClickListener) {
                return (View.OnLongClickListener) mOnLongClickListener;
            }
            return null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 参照: XposedHelpers#findFieldRecursiveImpl
     * see at: GReflectUtils.java
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    private static Field findFieldRecursiveImpl(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            while (true) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz.equals(Object.class))
                    break;

                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                }
            }
            throw e;
        }
    }
}
