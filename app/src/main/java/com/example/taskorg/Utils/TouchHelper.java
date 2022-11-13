package com.example.taskorg.Utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskorg.Adapters.ToDoAdapter;
import com.example.taskorg.R;
import com.example.taskorg.Vars.GlobalVar;


public class TouchHelper extends ItemTouchHelper.SimpleCallback {
    private ToDoAdapter adapter;

    public TouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.RIGHT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            if(GlobalVar.isArchiveShowing)
            builder.setMessage("Are You Sure?")
                    .setTitle("Delete Task")
                    .setPositiveButton("Yes", (dialog, which) -> adapter.deleteTask(position)).setNegativeButton("No", (dialog, which) -> adapter.notifyItemChanged(position));
            else
                builder.setMessage("Are You Sure?")
                        .setTitle("Archive task?")
                        .setPositiveButton("Yes", (dialog, which) -> adapter.deleteTask(position)).setNegativeButton("No", (dialog, which) -> adapter.notifyItemChanged(position));

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        } else {
            if(GlobalVar.isArchiveShowing)
                adapter.unarciveTask(position);
            else
                adapter.editTask(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) {
            if (GlobalVar.isArchiveShowing)
                icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete_24);
            else
                icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_archive_24);
            background = new ColorDrawable(Color.RED);
        } else {
            if (GlobalVar.isArchiveShowing)
                icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_unarchive_24);
            else
                icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit_24);
            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.colorPrimaryDark));
        }

        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }
}