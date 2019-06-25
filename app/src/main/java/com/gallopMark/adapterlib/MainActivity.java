package com.gallopMark.adapterlib;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.gallopmark.recycler.adapterhelper.CommonAdapter;
import com.gallopmark.recycler.decorationhepler.GridItemDecoration;
import com.gallopmark.recycler.swipehelper.OnSwipeItemTouchListener;
import com.gallopmark.recycler.swipehelper.SwipeItemLayout;
import com.gallopmark.recycler.widgetwrapper.WrapperRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WrapperRecyclerView recyclerView = findViewById(R.id.mRecyclerView);
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            list.add("Row\t" + (i + 1));
        }
//        RecyclerTouchListener touchListener = new RecyclerTouchListener(this, recyclerView);
//        touchListener.setSwipeOptionViews(R.id.tvDelete)
//                .setSwipeable(R.id.ll_rowFG, R.id.tvDelete, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
//                    @Override
//                    public void onSwipeOptionClicked(int viewID, int position) {
//                        list.remove(position);
//                        if (recyclerView.getAdapter() != null) {
//                            recyclerView.getAdapter().notifyItemRemoved(position);
//                        }
//                        Toast.makeText(getApplicationContext(), "删除了第" + position + "条", Toast.LENGTH_LONG).show();
//                    }
//                });
//        recyclerView.addOnItemTouchListener(touchListener);
        int divider = getResources().getDimensionPixelSize(R.dimen.dp_1);
        recyclerView.addItemDecoration(new GridItemDecoration(this, divider, R.color.colorPrimary));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addOnItemTouchListener(new OnSwipeItemTouchListener(this));
//        View headerView = getLayoutInflater().inflate(R.layout.header_view, recyclerView, false);
//        recyclerView.addHeaderView(headerView);
//        View footerView = getLayoutInflater().inflate(R.layout.footer_view, recyclerView, false);
//        recyclerView.addFooterView(footerView);
        MySwipeAdapter adapter = new MySwipeAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    private class MySwipeAdapter extends CommonAdapter<String> {

        MySwipeAdapter(Context context, List<String> mDatas) {
            super(context, mDatas);
        }

        @Override
        public void onBindHolder(RecyclerHolder holder, String s, final int position) {
            final SwipeItemLayout swipeItemLayout = holder.obtainView(R.id.swipeItemLayout);
            holder.setText(R.id.tvTitle, s);
            holder.setOnClickListener(R.id.tvDelete, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeItemLayout.close();
                    mDatas.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "删除了第" + position + "条", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        protected int bindView(int viewType) {
            return R.layout.item_swipe;
        }
    }
}
