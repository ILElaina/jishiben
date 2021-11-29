package bistu.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements OnScrollListener,
        OnItemClickListener, OnItemLongClickListener {

    private Context mContext;
    private ListView listview;
    private SimpleAdapter simp_adapter;
    private List<Map<String, Object>> dataList;
    private Button addNote;
    private NotesDB DB;
    private SQLiteDatabase dbread;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.listview);
        dataList = new ArrayList<>();

        addNote = findViewById(R.id.btn_editnote);
        mContext = this;
        addNote.setOnClickListener(new OnClickListener() {


            public void onClick(View arg0) {
                NoteEdit.ENTER_STATE = 0;
                Intent intent = new Intent(mContext, NoteEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("info", "");
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
        DB = new NotesDB(this);
        dbread = DB.getReadableDatabase();

        RefreshNotesList();

        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        listview.setOnScrollListener(this);
    }

    public void RefreshNotesList() {

        int size = dataList.size();
        if (size > 0) {
            dataList.removeAll(dataList);
            simp_adapter.notifyDataSetChanged();
            listview.setAdapter(simp_adapter);
        }
        simp_adapter = new SimpleAdapter(this, getData(), R.layout.item,
                new String[] { "tv_content", "tv_date" }, new int[] {
                R.id.tv_content, R.id.tv_date });
        listview.setAdapter(simp_adapter);
    }

    private List<Map<String, Object>> getData() {

        Cursor cursor = dbread.query("note", null, "content!=\"\"", null, null,
                null, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("content"));
             @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
             @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("_id"));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tv_content", name);
            map.put("tv_date", date);
            map.put("tv_id", id);
            dataList.add(map);
        }
        cursor.close();
        return dataList;

    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    // 滑动listview监听事件

    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        // TODO Auto-generated method stub
        switch (arg1) {
            case SCROLL_STATE_FLING:
                Log.i("main", "");
            case SCROLL_STATE_IDLE:
                Log.i("main", "");
            case SCROLL_STATE_TOUCH_SCROLL:
                Log.i("main", "");
        }
    }

    // 点击某一个item
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        NoteEdit.ENTER_STATE = 1;
        Log.d("arg2", arg2 + "");
        Map<String,String> itemMap = (Map<String, String>) listview.getItemAtPosition(arg2);
        Log.d("content", itemMap.get("tv_content"));
        String id = itemMap.get("tv_id");
        Log.d("ID", id);
        Cursor c = dbread.query("note", null,
                "_id=" + "'" + id + "'", null, null, null, null);
        while (c.moveToNext()) {
            @SuppressLint("Range") String No = c.getString(c.getColumnIndex("_id"));
            @SuppressLint("Range") String noteContent = c.getString(c.getColumnIndex("content"));
            Log.d("TEXT", No);
            Intent myIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("info", noteContent);
            NoteEdit.id = Integer.parseInt(No);
            myIntent.putExtras(bundle);
            myIntent.setClass(MainActivity.this, NoteEdit.class);
            startActivityForResult(myIntent, 1);
        }

    }

    @Override
    // 接受上一个页面返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            RefreshNotesList();
        }
    }

    // 长按item的点击事件
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        final int n=arg2;
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除该日志吗？");
        builder.setMessage("确认删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Map<String,String> itemMap = (Map<String, String>) listview.getItemAtPosition(n);
                Log.d("content", itemMap.get("tv_content"));
                String id = itemMap.get("tv_id");
                Log.d("ID", id);
                Cursor c = dbread.query("note", null,
                        "_id=" + "'" + id + "'", null, null, null, null);
                while (c.moveToNext()) {
                    String sql_del = "update note set content='' where _id="
                            + id;
                    dbread.execSQL(sql_del);
                    RefreshNotesList();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
        return true;
    }

}