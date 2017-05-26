/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mattcarroll.hover.defaulthovermenu.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import io.mattcarroll.hover.BuildConfig;
import io.mattcarroll.hover.HoverMenu;
import io.mattcarroll.hover.HoverMenuAdapter;
import io.mattcarroll.hover.R;
import io.mattcarroll.hover.defaulthovermenu.HoverView;

/**
 * {@link HoverMenu} implementation that can be embedded in traditional view hierarchies.
 */
public class ViewHoverMenu extends FrameLayout implements HoverMenu {

    private static final String PREFS_FILE = "viewhovermenu";
    private static final String PREFS_KEY_ANCHOR_SIDE = "anchor_side";
    private static final String PREFS_KEY_ANCHOR_Y = "anchor_y";

    private HoverView mHoverView;
    private InViewGroupDragger mDragger;
    private HoverMenuAdapter mAdapter;
    private SharedPreferences mPrefs;
    private Set<OnExitListener> mOnExitListeners = new HashSet<>();

    public ViewHoverMenu(Context context) {
        this(context, null);
    }

    public ViewHoverMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPrefs = getContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        int touchDiameter = getResources().getDimensionPixelSize(R.dimen.exit_radius);
        mDragger = new InViewGroupDragger(this, touchDiameter, ViewConfiguration.get(getContext()).getScaledTouchSlop());
        mDragger.enableDebugMode(BuildConfig.DEBUG);
        mHoverView = new HoverView(getContext());
        addView(mHoverView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (null != mAdapter) {
            mHoverView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        saveAnchorState();
        removeView(mHoverView);
        mHoverView = null;
        mDragger.deactivate(); // TODO: should be called by HoverMenuView in some kind of release() method.
        super.onDetachedFromWindow();
    }

    @Override
    public String getVisualState() {
        // TODO: figure out saved state for hover view
//        PointF anchorState = mHoverView.getAnchorState();
//        return new VisualStateMemento((int) anchorState.x, anchorState.y).toJsonString();
        return "";
    }

    @Override
    public void restoreVisualState(@NonNull String savedVisualState) {
        // TODO: figure out saved state for hover view
//        try {
//            VisualStateMemento memento = VisualStateMemento.fromJsonString(savedVisualState);
//            mHoverView.setAnchorState(new PointF(memento.getAnchorSide(), memento.getNormalizedPositionY()));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void setAdapter(@Nullable HoverMenuAdapter adapter) {
        mAdapter = adapter;
        if (null != mAdapter && null != mHoverView) {
            mHoverView.setAdapter(adapter);
        }
    }

    @Override
    public void show() {
        // TODO:
    }

    @Override
    public void hide() {
        // TODO:
    }

    @Override
    public void expandMenu() {
        // TODO: figure out programmatic expansion/collapse for hover view
//        mHoverView.expand();
    }

    @Override
    public void collapseMenu() {
        // TODO: figure out programmatic expansion/collapse for hover view
//        mHoverView.collapse();
    }

    @Override
    public void addOnExitListener(@NonNull OnExitListener onExitListener) {
        mOnExitListeners.add(onExitListener);
    }

    @Override
    public void removeOnExitListener(@NonNull OnExitListener onExitListener) {
        mOnExitListeners.remove(onExitListener);
    }

    private void notifyOnExitListeners() {
        for (OnExitListener listener : mOnExitListeners) {
            listener.onExitByUserRequest();
        }
    }

    private void saveAnchorState() {
        // TODO: figure out saving state for hover view
//        PointF anchorState = mHoverView.getAnchorState();
//        mPrefs.edit()
//            .putFloat(PREFS_KEY_ANCHOR_SIDE, anchorState.x)
//            .putFloat(PREFS_KEY_ANCHOR_Y, anchorState.y)
//            .apply();
    }

    private PointF loadSavedAnchorState() {
        // TODO: figure out saving state for hover view
//        return new PointF(
//                mPrefs.getFloat(PREFS_KEY_ANCHOR_SIDE, 2),
//                mPrefs.getFloat(PREFS_KEY_ANCHOR_Y, 0.5f)
//        );
        return null;
    }

    private static class VisualStateMemento {

        private static final String JSON_KEY_ANCHOR_SIDE = "anchor_side";
        private static final String JSON_KEY_NORMALIZED_POSITION_Y = "normalized_position_y";

        public static VisualStateMemento fromJsonString(@NonNull String jsonString) throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonString);
            int anchorSide = jsonObject.getInt(JSON_KEY_ANCHOR_SIDE);
            float normalizedPositionY = (float) jsonObject.getDouble(JSON_KEY_NORMALIZED_POSITION_Y);
            return new VisualStateMemento(anchorSide, normalizedPositionY);
        }

        private int mAnchorSide;
        private float mNormalizedPositionY;

        public VisualStateMemento(int anchorSide, float normalizedPositionY) {
            mAnchorSide = anchorSide;
            mNormalizedPositionY = normalizedPositionY;
        }

        public int getAnchorSide() {
            return mAnchorSide;
        }

        public float getNormalizedPositionY() {
            return mNormalizedPositionY;
        }

        public String toJsonString() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JSON_KEY_ANCHOR_SIDE, mAnchorSide);
                jsonObject.put(JSON_KEY_NORMALIZED_POSITION_Y, mNormalizedPositionY);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
