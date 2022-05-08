package com.example.musicplayerapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyItemTouchHelper extends ItemTouchHelper.Callback {
    //private final ItemTouchHelperAdapter mAdapter;
    private SongAdapter mAdapter;
    private ColorDrawable swipeBackground = new ColorDrawable(Color.parseColor("#FFEF5350"));
    private Drawable deleteIcon;

    public MyItemTouchHelper(SongAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //when we stop drag
        //if we have a color to return
         viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(),R.color.white));
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags =ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        final int swipeFlags =ItemTouchHelper.END|ItemTouchHelper.START;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @
            NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        deleteIcon= ContextCompat.getDrawable(viewHolder.itemView.getContext(),R.drawable.ic_delete);
        int iconMargin = (viewHolder.itemView.getHeight() - deleteIcon.getIntrinsicHeight())/2;
        if(dX>0){
            swipeBackground.setBounds(viewHolder.itemView.getLeft(),viewHolder.itemView.getTop(),(int)dX,viewHolder.itemView.getBottom());
            deleteIcon.setBounds(viewHolder.itemView.getLeft()+iconMargin,
                    viewHolder.itemView.getTop()+iconMargin,
                    viewHolder.itemView.getLeft()+iconMargin+deleteIcon.getIntrinsicWidth(),
                    viewHolder.itemView.getBottom()-iconMargin);
        }else{
            swipeBackground.setBounds(viewHolder.itemView.getRight()+(int)dX,viewHolder.itemView.getTop(),viewHolder.itemView.getRight(),viewHolder.itemView.getBottom());
            deleteIcon.setBounds(viewHolder.itemView.getRight()-iconMargin-deleteIcon.getIntrinsicWidth(),
                    viewHolder.itemView.getTop()+iconMargin,
                    viewHolder.itemView.getRight()-iconMargin,
                    viewHolder.itemView.getBottom()-iconMargin);
        }

        c.save();
        swipeBackground.draw(c);
        if(dX>0)
            c.clipRect(viewHolder.itemView.getLeft(),viewHolder.itemView.getTop(),(int)dX,viewHolder.itemView.getBottom());
        else
            c.clipRect(viewHolder.itemView.getRight()+(int)dX,viewHolder.itemView.getTop(),viewHolder.itemView.getRight(),viewHolder.itemView.getBottom());
        deleteIcon.draw(c);
        c.restore();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
