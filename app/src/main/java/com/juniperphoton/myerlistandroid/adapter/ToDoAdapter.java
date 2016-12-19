package com.juniperphoton.myerlistandroid.adapter;


import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juniperphoton.myerlistandroid.App;
import com.juniperphoton.myerlistandroid.R;
import com.juniperphoton.myerlistandroid.callback.OnItemOperationCompletedCallback;
import com.juniperphoton.myerlistandroid.model.ToDo;
import com.juniperphoton.myerlistandroid.model.ToDoCategory;
import com.juniperphoton.myerlistandroid.util.CustomItemTouchHelper;
import com.juniperphoton.myerlistandroid.widget.CircleView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ToDoAdapter extends BaseAdapter<ToDo, ToDoAdapter.ToDoViewHolder> {
    private final static String TAG = "ToDoAdapter";

    private OnItemOperationCompletedCallback mCallback;
    private boolean mCanDrag = true;

    private RecyclerView mRecyclerView;

    public ToDoAdapter(List<ToDo> data) {
        super(data);
    }

    public void setCallback(OnItemOperationCompletedCallback callback) {
        mCallback = callback;
    }

    public void setCanDrag(boolean canDrag) {
        mCanDrag = canDrag;
    }

    private CustomItemTouchHelper helper = new CustomItemTouchHelper(new CustomItemTouchHelper.Callback() {

        private final float SWIPE_THRESHOLD = 0.2f;

        private ToDoViewHolder getToDoViewHolder(RecyclerView.ViewHolder viewHolder) {
            return (ToDoViewHolder) viewHolder;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = CustomItemTouchHelper.UP | CustomItemTouchHelper.DOWN;
            int swipeFlags = CustomItemTouchHelper.LEFT | CustomItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Collections.swap(getData(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
            notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            realm.commitTransaction();
            mCallback.onArrangeCompleted();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d(TAG, "onSwiped");
            Log.d(TAG, "direction" + direction);
            switch (direction) {
                case CustomItemTouchHelper.RIGHT:
                    getToDoViewHolder(viewHolder).toggleDone();
                    mCallback.onUpdateDone(viewHolder.getAdapterPosition());
                    break;
                case CustomItemTouchHelper.LEFT:
                    mCallback.onDelete(viewHolder.getAdapterPosition());
                    break;
            }
            super.clearView(mRecyclerView, viewHolder);
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return SWIPE_THRESHOLD;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            ToDoViewHolder holder = getToDoViewHolder(viewHolder);
            if (dX > recyclerView.getWidth() * SWIPE_THRESHOLD) {
                holder.setBackgroundGreen();
            } else if (dX < -recyclerView.getWidth() * SWIPE_THRESHOLD) {
                holder.setBackgroundRed();
            } else {
                holder.setBackgroundTransparent();
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            getToDoViewHolder(viewHolder).setBackgroundTransparent();
        }
    });

    @Override
    protected ToDoAdapter.ToDoViewHolder onCreateItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(App.getInstance()).inflate(R.layout.row_todo, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(ToDoAdapter.ToDoViewHolder holder, int dataPosition) {
        holder.bind(holder.getAdapterPosition());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        helper.attachToRecyclerView(recyclerView);
    }

    class ToDoViewHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.row_todo_color_view)
        CircleView circleView;

        @BindView(R.id.row_todo_content)
        TextView contentView;

        @BindView(R.id.done_line)
        View doneView;

        @BindView(R.id.arrange_thumb)
        View mThumb;

        private boolean isGreen;
        private boolean isRed;

        ToDoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void toggleDone() {
            final ToDo todo = getData(getAdapterPosition());
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (todo.getIsdone().equals("1")) {
                        todo.setIsdone("0");
                    } else {
                        todo.setIsdone("1");
                    }
                }
            });
            if (todo.getIsdone().equals("1")) {
                doneView.setVisibility(View.VISIBLE);
            } else {
                doneView.setVisibility(View.GONE);
            }
        }

        void setBackgroundGrey() {
            ColorDrawable colorDrawable = (ColorDrawable) rootView.getBackground();
            int fromColor = colorDrawable.getColor();
            final int greyColor = ContextCompat.getColor(App.getInstance(), R.color.MyerListGray);
            animateColor(fromColor, greyColor);
        }

        void setBackgroundGreen() {
            if (isGreen) return;
            isRed = false;
            ColorDrawable colorDrawable = (ColorDrawable) rootView.getBackground();
            int fromColor = colorDrawable.getColor();
            int toColor = ContextCompat.getColor(App.getInstance(), R.color.DoneGreenColor);
            animateColor(fromColor, toColor);
            isGreen = true;
        }

        void setBackgroundRed() {
            if (isRed) return;
            isGreen = false;
            ColorDrawable colorDrawable = (ColorDrawable) rootView.getBackground();
            int fromColor = colorDrawable.getColor();
            int toColor = ContextCompat.getColor(App.getInstance(), R.color.DeleteRedColor);
            animateColor(fromColor, toColor);
            isRed = true;
        }

        void animateColor(int fromColor, int toColor) {
            ValueAnimator valueAnimator = ValueAnimator.ofArgb(fromColor, toColor);
            valueAnimator.setDuration(300);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    rootView.setBackground(new ColorDrawable((int) animation.getAnimatedValue()));
                }
            });
            valueAnimator.start();
        }

        void setBackgroundTransparent() {
            isGreen = false;
            isRed = false;
            ColorDrawable colorDrawable = (ColorDrawable) rootView.getBackground();
            int fromColor = colorDrawable.getColor();
            int toColor = Color.WHITE;
            animateColor(fromColor, toColor);
        }

        void bind(int position) {
            final ToDo todo = getData(position);
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    int cate = Integer.valueOf(todo.getCate());
                    if (cate > 0) {
                        ToDoCategory category = realm.where(ToDoCategory.class).equalTo("id",
                                cate).findFirst();
                        if (category != null) {
                            circleView.setColor(category.getIntColor());
                        }
                    } else if (cate == 0) {
                        circleView.setColor(ContextCompat.getColor(App.getInstance(), R.color.MyerListBlue));
                    }
                }
            });
            if (todo.getIsdone().equals("1")) {
                doneView.setVisibility(View.VISIBLE);
            } else {
                doneView.setVisibility(View.GONE);
            }
            contentView.setText(todo.getContent());
            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            helper.startSwipe(ToDoViewHolder.this);
                            return true;
                    }
                    return false;
                }
            });
            if (mCanDrag) {
                mThumb.setVisibility(View.VISIBLE);
                mThumb.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                helper.startDrag(ToDoViewHolder.this);
                                setBackgroundGrey();
                                return true;
                        }
                        return false;
                    }
                });
            } else {
                mThumb.setVisibility(View.GONE);
            }

        }
    }
}