package com.example.todolist;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.Adapters.ToDoAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private final ToDoAdapter adapter;
    public RecyclerItemTouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }
    @Override
    public boolean onMove( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }
    @Override
    public void onSwiped( RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            new AlertDialog.Builder(adapter.getContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Confirm", (dialog, which) -> adapter.deleteItem(position))
                    .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position))
                    .show();
        } else {
            adapter.editItem(position);
        }
    }
    @Override
    public void onChildDraw( Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int iconMargin = (itemView.getHeight() - 48) / 2;
        Drawable icon = ContextCompat.getDrawable(adapter.getContext(), dX > 0 ? R.drawable.baseline_edit_24 : R.drawable.baseline_delete_24);
        ColorDrawable background = new ColorDrawable(dX > 0 ? ContextCompat.getColor(adapter.getContext(), R.color.colorPrimaryDark) : Color.RED);
        if (icon == null) return;
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + 48;
        int iconLeft = dX > 0 ? itemView.getLeft() + iconMargin : itemView.getRight() - iconMargin - 48;
        int iconRight = iconLeft + 48;
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        background.setBounds(dX > 0 ? itemView.getLeft() : itemView.getRight() + ((int) dX),
                itemView.getTop(), dX > 0 ? itemView.getLeft() + ((int) dX) : itemView.getRight(), itemView.getBottom());
        background.draw(c);
        icon.draw(c);
    }
}
