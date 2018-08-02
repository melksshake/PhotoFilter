package views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.natalia.melkonyan.photofilter.R;
import io.fotoapparat.view.CameraView;

public final class CamView extends CoordinatorLayout {
  private final CameraView camera;
  private final ViewDragHelper viewDragHelper;
  private final int cameraSize;
  @Nullable private PullCallback callback;
  private boolean cameraAnimation = false;

  public CamView(Context context) {
    this(context, null);
  }

  public CamView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CamView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    camera = new CameraView(context);
    cameraSize = getResources().getDimensionPixelSize(R.dimen.bottom_sheet_camera_preview);
    viewDragHelper = ViewDragHelper.create(this, 1f / 8f, new ViewDragCallback());
    int cameraPreview = getResources().getDimensionPixelOffset(R.dimen.bottom_sheet_camera_preview);
    camera.setLayoutParams(new CoordinatorLayout.LayoutParams(cameraPreview, cameraPreview));
  }

  public void init() {
    post(() -> addView(camera, 1));
  }

  public CameraView cameraView() {
    return camera;
  }

  public void setCallback(PullCallback callback) {
    this.callback = callback;
  }

  public void setCameraAnimation(boolean cameraAnimation) {
    this.cameraAnimation = cameraAnimation;
  }

  private boolean cameraCollapsed() {
    return camera.getHeight() == cameraSize;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (cameraAnimation) return false;
    if (cameraCollapsed() || camera.getParent() == null) return super.onInterceptTouchEvent(ev);
    return viewDragHelper.shouldInterceptTouchEvent(ev);
  }

  @SuppressLint("ClickableViewAccessibility") @Override
  public boolean onTouchEvent(@NonNull MotionEvent event) {
    if (cameraAnimation) return false;
    if (cameraCollapsed() || camera.getParent() == null) return super.onTouchEvent(event);
    viewDragHelper.processTouchEvent(event);
    return true;
  }

  @Override
  public void computeScroll() {
    if (cameraCollapsed() || camera.getParent() == null) {
      super.computeScroll();
      return;
    }
    if (viewDragHelper.continueSettling(true)) ViewCompat.postInvalidateOnAnimation(this);
  }

  public interface PullCallback {
    void onPullComplete();
  }

  private class ViewDragCallback extends ViewDragHelper.Callback {

    @Override
    public boolean tryCaptureView(View child, int pointerId) {
      return true;
    }

    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {
      return 0;
    }

    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
      return top;
    }

    @Override
    public int getViewHorizontalDragRange(View child) {
      return 0;
    }

    @Override
    public int getViewVerticalDragRange(View child) {
      return getHeight();
    }

    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
      if (Math.abs(releasedChild.getTop()) > getHeight() / 10 && callback != null) {
        callback.onPullComplete();
      } else {
        viewDragHelper.settleCapturedViewAt(0, 0);
        invalidate();
      }
    }
  }
}

