package dev.NiharikaRastogi.utils;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

/**
 * Created by Niharika Rastogi on 26-12-2015.
 */
public class GridAutofitLayoutManager extends GridLayoutManager {
    private int mcolumnWidthInDp;
    private boolean mcolumnWidthInDpChanged = true;

    public GridAutofitLayoutManager(Context context, int columnWidthInDp) {
        /* Initially set spanCount to 1, will be changed automatically later. */
        super(context, 1);
        setcolumnWidthInDp(checkedcolumnWidthInDp(context, columnWidthInDp));
    }

    public GridAutofitLayoutManager(Context context, int columnWidthInDp, int orientation, boolean reverseLayout) {
        /* Initially set spanCount to 1, will be changed automatically later. */
        super(context, 1, orientation, reverseLayout);
        setcolumnWidthInDp(checkedcolumnWidthInDp(context, columnWidthInDp));
    }

    private int checkedcolumnWidthInDp(Context context, int columnWidthInDp) {
        if (columnWidthInDp <= 0) {
            /* Set default columnWidthInDp value (48dp here). It is better to move this constant
            to static constant on top, but we need context to convert it to dp, so can't really
            do so. */
            columnWidthInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                    context.getResources().getDisplayMetrics());
        }
        columnWidthInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, columnWidthInDp,
                context.getResources().getDisplayMetrics());
        return columnWidthInDp;
    }

    public void setcolumnWidthInDp(int newcolumnWidthInDp) {
        if (newcolumnWidthInDp > 0 && newcolumnWidthInDp != mcolumnWidthInDp) {
            mcolumnWidthInDp = newcolumnWidthInDp;
            mcolumnWidthInDpChanged = true;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mcolumnWidthInDpChanged && mcolumnWidthInDp > 0) {
            int totalSpace;
            if (getOrientation() == VERTICAL) {
                totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, totalSpace / mcolumnWidthInDp);
            setSpanCount(spanCount);
            mcolumnWidthInDpChanged = false;
        }
        super.onLayoutChildren(recycler, state);
    }
}