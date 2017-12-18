package com.example.zzy.aiad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

import com.example.zzy.aiad.DragGridView.OnItemChangerListener;

public class Sudoku extends Activity {

    DragGridView dragGridView;
    SimpleAdapter adapter;
    ArrayList<HashMap<String, Object>> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        initView();
        initAdapter();
        initListener();
    }

    private void initView() {
        dragGridView = (DragGridView) findViewById(R.id.myDragGridView);
    }

    private void initListener() {
        // 交互数据
        dragGridView.setOnItemChangeListener(new OnItemChangerListener() {

            @Override
            public void onChange(int from, int to) {
                HashMap<String, Object> temp = data.get(from);
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(data, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(data, i, i - 1);
                    }
                }

                data.set(to, temp);
                adapter.notifyDataSetChanged();
            }

        });

        dragGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(Sudoku.this, "您点击了" + position + "位置",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initAdapter() {
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.ic_launcher_background);
        Bitmap bitmap = drawable.getBitmap();

        for (int i = 0; i < 9; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("item_img1", bitmap);
            map.put("item_tv1", "第" + i + "项");
            data.add(map);
        }

        adapter = new SimpleAdapter(Sudoku.this, data,
                R.layout.gridview_item, new String[]{"item_img1", "item_tv1"},
                new int[]{R.id.item_img1, R.id.item_tv1});

        adapter.setViewBinder(new ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub

                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageBitmap((Bitmap) data);
                    return true;
                } else {
                    return false;
                }
            }
        });

        dragGridView.setAdapter(adapter);

    }
}