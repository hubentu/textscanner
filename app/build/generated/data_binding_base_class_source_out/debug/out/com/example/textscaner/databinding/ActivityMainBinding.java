// Generated by view binder compiler. Do not edit!
package com.example.textscaner.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.textscaner.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final LinearLayout buttonContainer;

  @NonNull
  public final Button captureButton;

  @NonNull
  public final TextView capturedText;

  @NonNull
  public final Button copyButton;

  @NonNull
  public final View targetRegion;

  @NonNull
  public final PreviewView viewFinder;

  private ActivityMainBinding(@NonNull ConstraintLayout rootView,
      @NonNull LinearLayout buttonContainer, @NonNull Button captureButton,
      @NonNull TextView capturedText, @NonNull Button copyButton, @NonNull View targetRegion,
      @NonNull PreviewView viewFinder) {
    this.rootView = rootView;
    this.buttonContainer = buttonContainer;
    this.captureButton = captureButton;
    this.capturedText = capturedText;
    this.copyButton = copyButton;
    this.targetRegion = targetRegion;
    this.viewFinder = viewFinder;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttonContainer;
      LinearLayout buttonContainer = ViewBindings.findChildViewById(rootView, id);
      if (buttonContainer == null) {
        break missingId;
      }

      id = R.id.captureButton;
      Button captureButton = ViewBindings.findChildViewById(rootView, id);
      if (captureButton == null) {
        break missingId;
      }

      id = R.id.capturedText;
      TextView capturedText = ViewBindings.findChildViewById(rootView, id);
      if (capturedText == null) {
        break missingId;
      }

      id = R.id.copyButton;
      Button copyButton = ViewBindings.findChildViewById(rootView, id);
      if (copyButton == null) {
        break missingId;
      }

      id = R.id.targetRegion;
      View targetRegion = ViewBindings.findChildViewById(rootView, id);
      if (targetRegion == null) {
        break missingId;
      }

      id = R.id.viewFinder;
      PreviewView viewFinder = ViewBindings.findChildViewById(rootView, id);
      if (viewFinder == null) {
        break missingId;
      }

      return new ActivityMainBinding((ConstraintLayout) rootView, buttonContainer, captureButton,
          capturedText, copyButton, targetRegion, viewFinder);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}