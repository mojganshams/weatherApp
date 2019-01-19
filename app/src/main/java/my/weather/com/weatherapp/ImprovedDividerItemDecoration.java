package my.weather.com.weatherapp;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ImprovedDividerItemDecoration extends DividerItemDecoration {

    public ImprovedDividerItemDecoration(Context context, int orientation) {
        super(context, orientation);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        // hide the divider for the last child
        if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.setEmpty();
        } else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }

}
