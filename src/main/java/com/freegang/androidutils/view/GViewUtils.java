package com.freegang.androidutils.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
}