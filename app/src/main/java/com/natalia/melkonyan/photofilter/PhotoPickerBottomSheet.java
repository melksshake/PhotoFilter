package com.natalia.melkonyan.photofilter;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import base.BaseBottomSheetFragment;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.view.FocusView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.LensPositionSelectorsKt.front;
import static io.fotoapparat.selector.PreviewFpsRangeSelectorsKt.highestFps;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;
import static io.fotoapparat.selector.SensorSensitivitySelectorsKt.highestSensorSensitivity;

@RuntimePermissions
public class PhotoPickerBottomSheet extends BaseBottomSheetFragment {
  private Fotoapparat fotoapparat;
  private FocusView focusView;

  @Override public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return null;
  }

  @Override @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AppCompatDialog(getContext(), getTheme()) {
      @Override public void onBackPressed() {
        if (expanded()) {
          collapseCamera();
        } else {
          super.onBackPressed();
        }
      }
    };
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    //binding.root.cameraView().setOnClickListener(cameraView -> viewModel.onCameraClicked());
    //binding.ivCameraLocation.setOnClickListener(icCameraLocation -> initCameraWithPermissionCheck(this));
    //binding.root.setCallback(this::collapseCamera);
    initBottomSheet();
    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        initCameraView();
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
    focusView = new FocusView(getContext());
    focusView.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    binding.ivCameraFlash.setImageResource(flashTag);
    binding.ivCameraFlash.setVisibility(backCamera ? View.VISIBLE : View.INVISIBLE);
    if (!PermissionUtil.neverAsk(getActivity(), Manifest.permission.CAMERA)) initCameraWithPermissionCheck(this);
  }

  private CameraConfiguration.Builder cameraConfigurationBuilder = CameraConfiguration.builder()
      .photoResolution(standardRatio(highestResolution()))
      .previewFpsRange(highestFps())
      .sensorSensitivity(highestSensorSensitivity());

  @NeedsPermission({ Manifest.permission.CAMERA })
  void initCamera() {
    fotoapparat = new Fotoapparat(getContext(), binding.root.cameraView(), focusView, backCamera ? back() : front(), ImageView.ScaleType.CenterCrop);
    cameraConfigurationBuilder.flash(flash(flashTag));
    fotoapparat.updateConfiguration(cameraConfigurationBuilder.build());
    binding.root.init();
    fotoapparat.start();
  }
}
