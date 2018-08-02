package base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.natalia.melkonyan.photofilter.R;

/**
 * Modal bottom sheet. This is a version of {@link AppCompatDialogFragment} that shows a bottom sheet
 * using {@link BottomSheetDialog} instead of a floating dialog.
 */
public abstract class BaseBottomSheetFragment extends AppCompatDialogFragment {
  @NonNull @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new BottomSheetDialog(getContext(), getTheme());
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Manually expand bottom sheet (more info https://issuetracker.google.com/issues/37132390).
    View layout = layout(inflater, container, savedInstanceState);
    layout.getViewTreeObserver().addOnGlobalLayoutListener(this::expand);
    return layout;
  }

  public abstract View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

  private void expand() {
    FrameLayout bottomSheet = ((BottomSheetDialog) getDialog()).findViewById(android.support.design.R.id.design_bottom_sheet);
    BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    behavior.setPeekHeight(0);

    behavior.setHideable(true);

    behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
          dismiss();
        }
      }

      @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
      }
    });
  }

  protected void configureRecyclerView(RecyclerView recyclerView, @Nullable SwipeRefreshLayout swipeRefreshLayout,
      RecyclerView.LayoutManager layoutManager, RecyclerView.Adapter adapter, boolean addDivider, RecyclerView.ItemDecoration decoration) {
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(layoutManager);
    if (addDivider) recyclerView.addItemDecoration(decoration);
    recyclerView.setAdapter(adapter);
    if (swipeRefreshLayout != null) {
      swipeRefreshLayout.setColorSchemeResources(
          R.color.swipe_refresh_layout_1,
          R.color.swipe_refresh_layout_2,
          R.color.swipe_refresh_layout_3,
          R.color.swipe_refresh_layout_4
      );
    }
  }
}

