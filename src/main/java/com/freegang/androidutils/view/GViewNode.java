package com.freegang.androidutils.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//视图节点多叉树
public class GViewNode {
    //父视图
    public ViewGroup parent;
    //当前视图
    public View view;
    //子节点
    public List<GViewNode> children;
    //当前树的深度
    public int depth;

    //销毁节点树
    public void destroy() {
        destroyChildren(this);
    }

    private void destroyChildren(GViewNode viewNode) {
        viewNode.parent = null;
        viewNode.view = null;
        viewNode.depth = 0;
        if (!viewNode.children.isEmpty()) {
            for (GViewNode child : viewNode.children) {
                destroyChildren(child);
            }
        }
        viewNode.children.clear();
    }

    public String toSimpleString() {
        if (view == null) {
            return "ViewNode{parent=" + parent + ", view=null, depth=" + depth + ", hashCode=" + this.hashCode() + "}";
        }

        return "ViewNode{view=" + view.getClass().getSimpleName() +
                ", idHex=" + (view.getId() == -1 ? "-1" : "0x" + Integer.toHexString(view.getId())) +
                ", idName=" + (view.getId() == -1 ? "-1" : "@id/" + view.getContext().getResources().getResourceEntryName(view.getId())) +
                ", depth=" + depth +
                ", childrenSize=" + children.size() +
                ", hashCode=" + this.hashCode() +
                "}";
    }

    @NonNull
    @Override
    public String toString() {
        return "ViewNode{" +
                "parent=" + parent +
                ", view=" + view +
                ", children=" + children +
                ", depth=" + depth +
                '}';
    }
}