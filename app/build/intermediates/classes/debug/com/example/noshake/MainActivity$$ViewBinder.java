// Generated code from Butter Knife. Do not modify!
package com.example.noshake;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.example.noshake.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492944, "field 'mTextAccX'");
    target.mTextAccX = finder.castView(view, 2131492944, "field 'mTextAccX'");
    view = finder.findRequiredView(source, 2131492945, "field 'mTextAccY'");
    target.mTextAccY = finder.castView(view, 2131492945, "field 'mTextAccY'");
    view = finder.findRequiredView(source, 2131492946, "field 'mTextAccZ'");
    target.mTextAccZ = finder.castView(view, 2131492946, "field 'mTextAccZ'");
    view = finder.findRequiredView(source, 2131492943, "field 'mTargetView'");
    target.mTargetView = view;
  }

  @Override public void unbind(T target) {
    target.mTextAccX = null;
    target.mTextAccY = null;
    target.mTextAccZ = null;
    target.mTargetView = null;
  }
}
