package com.bmsh.router.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CheckBox;

public class MutilRadioGroup extends LinearLayout {
	private int mCheckedId = -1;
	private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
	private boolean mProtectFromCheckedChange = false;
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private PassThroughHierarchyChangeListener mPassThroughListener;
	
	/**
	 * {@inheritDoc}
	 */
	public MutilRadioGroup(Context context) {
		super(context);
		setOrientation(VERTICAL);
		init();
	}

	/**
	 * {@inheritDoc}
	 */
	public MutilRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mChildOnCheckedChangeListener = new CheckedStateTracker();
		mPassThroughListener = new PassThroughHierarchyChangeListener();
		super.setOnHierarchyChangeListener(mPassThroughListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
		mPassThroughListener.mOnHierarchyChangeListener = listener;
	}

	/**
	 * 设置默认的选项
	 * 
	 * @param id
	 */
	public void setCheckWithoutNotif(int id) {
		if (id != -1 && (id == mCheckedId)) {
			return;
		}

		mProtectFromCheckedChange = true;
		if (mCheckedId != -1) {
			setCheckedStateForView(mCheckedId, false);
		}

		if (id != -1) {
			mProtectFromCheckedChange = true;
			setCheckedStateForView(id, true);
		}
		mProtectFromCheckedChange = false;
		mCheckedId = id;
		mProtectFromCheckedChange = false;
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		List<CheckBox> btns = getAllCheckBox(child);
		if (btns != null && btns.size() > 0) {
			for (CheckBox button : btns) {
				if (button.isChecked()) {
					mProtectFromCheckedChange = true;
					if (mCheckedId != -1) {
						setCheckedStateForView(mCheckedId, false);
					}
					mProtectFromCheckedChange = false;
					setCheckedId(button.getId());
				}
			}
		}
		super.addView(child, index, params);
	}

	/**
	 * 获取所有的CheckBox
	 * 
	 * @param child
	 * @return
	 */
	private List<CheckBox> getAllCheckBox(View child) {
		List<CheckBox> btns = new ArrayList<CheckBox>();
		if (child instanceof CheckBox) {
			btns.add((CheckBox) child);
		} else if (child instanceof ViewGroup) {
			int counts = ((ViewGroup) child).getChildCount();
			for (int i = 0; i < counts; i++) {
				btns.addAll(getAllCheckBox(((ViewGroup) child).getChildAt(i)));
			}
		}
		return btns;
	}

	/**
	 * 选择选项
	 * 
	 * @param id
	 */
	public void check(int id) {
		// don't even bother
		if (id != -1 && (id == mCheckedId)) {
			return;
		}

		// 将原来的选项设置false
		if (mCheckedId != -1) {
			setCheckedStateForView(mCheckedId, false);
		}

		// 修改当前选项为相应的值
		if (id != -1) {
			setCheckedStateForView(id, true);
		}

		setCheckedId(id);
	}

	private void setCheckedId(int id) {
		mCheckedId = id;
		if (mOnCheckedChangeListener != null) {
			mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
		}
	}

	/**
	 * 设置选择状态
	 * 
	 * @param viewId
	 * @param checked
	 */
	public void setCheckedStateForView(int viewId, boolean checked) {
		View checkedView = findViewById(viewId);
		if (checkedView != null && checkedView instanceof CheckBox) {
			((CheckBox) checkedView).setChecked(checked);
		}
	}

	public int getCheckedCheckBoxId() {
		return mCheckedId;
	}

	/**
	 * 清空选择
	 */
	public void clearCheck() {
		check(-1);
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MutilRadioGroup.LayoutParams(getContext(), attrs);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof MutilRadioGroup.LayoutParams;
	}

	@Override
	protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
		event.setClassName(MutilRadioGroup.class.getName());
	}

	@Override
	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
		super.onInitializeAccessibilityNodeInfo(info);
		info.setClassName(MutilRadioGroup.class.getName());
	}

	/**
	 * <p>
	 * This set of layout parameters defaults the width and the height of the
	 * children to {@link #WRAP_CONTENT} when they are not specified in the XML
	 * file. Otherwise, this class ussed the value read from the XML file.
	 * </p>
	 * 
	 * <p>
	 * See {@link android.R.styleable#LinearLayout_Layout LinearLayout
	 * Attributes} for a list of all child view attributes that this class
	 * supports.
	 * </p>
	 * 
	 */
	public static class LayoutParams extends LinearLayout.LayoutParams {
		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(int w, int h) {
			super(w, h);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(int w, int h, float initWeight) {
			super(w, h, initWeight);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(ViewGroup.LayoutParams p) {
			super(p);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(MarginLayoutParams source) {
			super(source);
		}

		/**
		 * <p>
		 * Fixes the child's width to
		 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and the
		 * child's height to
		 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} when not
		 * specified in the XML file.
		 * </p>
		 * 
		 * @param a
		 *            the styled attributes set
		 * @param widthAttr
		 *            the width attribute to fetch
		 * @param heightAttr
		 *            the height attribute to fetch
		 */
		@Override
		protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {

			if (a.hasValue(widthAttr)) {
				width = a.getLayoutDimension(widthAttr, "layout_width");
			} else {
				width = WRAP_CONTENT;
			}

			if (a.hasValue(heightAttr)) {
				height = a.getLayoutDimension(heightAttr, "layout_height");
			} else {
				height = WRAP_CONTENT;
			}
		}
	}

	/**
	 * CheckBox选择的回调接口
	 * 
	 * @author Morse
	 * @date 2016-2-4
	 * @Time 下午1:47:27
	 */
	public interface OnCheckedChangeListener {
		public void onCheckedChanged(MutilRadioGroup group, int checkedId);
	}

	private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (mProtectFromCheckedChange) {
				return;
			}

			mProtectFromCheckedChange = true;
			if (mCheckedId != -1) {
				setCheckedStateForView(mCheckedId, false);
			}
			mProtectFromCheckedChange = false;

			int id = buttonView.getId();

			setCheckedStateForView(id, isChecked);
			
			setCheckedId(id);
		}
	}

	/**
	 * <p>
	 * A pass-through listener acts upon the events and dispatches them to
	 * another listener. This allows the table layout to set its own internal
	 * hierarchy change listener without preventing the user to setup his.
	 * </p>
	 */
	private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
		private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

		/**
		 * {@inheritDoc}
		 */
		@SuppressLint("NewApi")
		public void onChildViewAdded(View parent, View child) {
			if (parent == MutilRadioGroup.this) {
				List<CheckBox> btns = getAllCheckBox(child);
				if (btns != null && btns.size() > 0) {
					for (CheckBox btn : btns) {
						int id = btn.getId();
						// 自动生成一个ID防止ID为空
						if (id == View.NO_ID && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
							id = View.generateViewId();
							btn.setId(id);
						}
						btn.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
					}
				}
			}

			if (mOnHierarchyChangeListener != null) {
				mOnHierarchyChangeListener.onChildViewAdded(parent, child);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void onChildViewRemoved(View parent, View child) {
			if (parent == MutilRadioGroup.this) {
				List<CheckBox> btns = getAllCheckBox(child);
				if (btns != null && btns.size() > 0) {
					for (CheckBox btn : btns) {
						btn.setOnCheckedChangeListener(null);
					}
				}
			}

			if (mOnHierarchyChangeListener != null) {
				mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
			}
		}
	}
}
