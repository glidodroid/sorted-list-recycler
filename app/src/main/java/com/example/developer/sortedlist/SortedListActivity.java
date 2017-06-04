package com.example.developer.sortedlist;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class SortedListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SortedListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sorted_list_activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new SortedListAdapter(getLayoutInflater(),
                new Item("buy milk"), new Item("wash the car"),
                new Item("wash the dishes"));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        final EditText newItemTextView = (EditText) findViewById(R.id.new_item_text_view);
        newItemTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE &&
                        (keyEvent == null || keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                    final String text = textView.getText().toString().trim();
                    if (text.length() > 0) {
                        mAdapter.addItem(new Item(text));
                    }
                    textView.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private static class SortedListAdapter extends RecyclerView.Adapter<TodoViewHolder> {
        SortedList<Item> mData;
        final LayoutInflater mLayoutInflater;
        public SortedListAdapter(LayoutInflater layoutInflater, Item... items) {
            mLayoutInflater = layoutInflater;
            mData = new SortedList<Item>(Item.class, new SortedListAdapterCallback<Item>(this) {
                @Override
                public int compare(Item t0, Item t1) {
                    Log.d("left t0",t0.toString());
                    Log.e("left t1",t1.toString());
                    if (t0.mIsDone != t1.mIsDone) {
                        return t0.mIsDone ? 1 : -1;
                    }
                    int txtComp = t0.mText.compareTo(t1.mText);
                    if (txtComp != 0) {
                        return txtComp;
                    }
                    if (t0.id < t1.id) {
                        return -1;
                    } else if (t0.id > t1.id) {
                        return 1;
                    }
                    return 0;
                }

                @Override
                public boolean areContentsTheSame(Item oldItem,
                                                  Item newItem) {
                    return oldItem.mText.equals(newItem.mText);
                }

                @Override
                public boolean areItemsTheSame(Item item1, Item item2) {
                    return item1.id == item2.id;
                }
            });
            for (Item item : items) {
                mData.add(item);
            }
        }

        public void addItem(Item item) {
            mData.add(item);
        }

        @Override
        public TodoViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            return new TodoViewHolder (mLayoutInflater.inflate(R.layout.sorted_list_item_view, parent, false)) {
                @Override
                void onDoneChanged(boolean isDone) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition == RecyclerView.NO_POSITION) {
                        return;
                    }
                    mBoundItem.mIsDone = isDone;
                    mData.recalculatePositionOfItemAt(adapterPosition);
                }
            };
        }

        @Override
        public void onBindViewHolder(TodoViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    abstract private static class TodoViewHolder extends RecyclerView.ViewHolder {
        final CheckBox mCheckBox;
        Item mBoundItem;
        public TodoViewHolder(View itemView) {
            super(itemView);
            mCheckBox = (CheckBox) itemView;
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mBoundItem != null && isChecked != mBoundItem.mIsDone) {
                        onDoneChanged(isChecked);
                    }
                }
            });
        }

        public void bindTo(Item item) {
            mBoundItem = item;
            mCheckBox.setText(item.mText);
            mCheckBox.setChecked(item.mIsDone);
        }

        abstract void onDoneChanged(boolean isChecked);
    }

    private static class Item {
        String mText;
        boolean mIsDone = false;
        final public int id;
        private static int idCounter = 0;

        public Item(String text) {
            id = idCounter ++;
            this.mText = text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (mIsDone != item.mIsDone) return false;
            if (id != item.id) return false;
            return mText != null ? mText.equals(item.mText) : item.mText == null;

        }

        @Override
        public int hashCode() {
            int result = mText != null ? mText.hashCode() : 0;
            result = 31 * result + (mIsDone ? 1 : 0);
            result = 31 * result + id;
            return result;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "mText='" + mText + '\'' +
                    ", mIsDone=" + mIsDone +
                    ", id=" + id +
                    '}';
        }
    }
}