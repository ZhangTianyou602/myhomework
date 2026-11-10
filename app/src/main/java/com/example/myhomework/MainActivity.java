package com.example.myhomework;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private ListView lvRecords;
    private TextView tvIncome, tvExpense, tvBalance;
    private final List<Record> recordList = new ArrayList<>();
    private RecordAdapter adapter;

    private final ActivityResultLauncher<Intent> addLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(this, "已回传：" + result.getData().getStringExtra("note"),
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        lvRecords = findViewById(R.id.lvRecords);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvBalance = findViewById(R.id.tvBalance);
        ImageButton btnAdd = findViewById(R.id.btnAdd);

        adapter = new RecordAdapter(this, recordList);
        lvRecords.setAdapter(adapter);

        // 跳转记账页：去程 Intent 传值（当前结余）
        btnAdd.setOnClickListener(v -> {
            Intent it = new Intent(MainActivity.this, AddRecordActivity.class);
            it.putExtra("currentBalance", tvBalance.getText().toString());
            addLauncher.launch(it);
        });

        // 点击列表项进详情：传值
        lvRecords.setOnItemClickListener((parent, view, position, id) -> {
            Record r = recordList.get(position);
            Intent it = new Intent(MainActivity.this, DetailActivity.class);
            it.putExtra("id", r.id);
            it.putExtra("amount", r.amount);
            it.putExtra("type", r.type);
            it.putExtra("category", r.category);
            it.putExtra("note", r.note);
            it.putExtra("date", r.date);
            startActivity(it);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // 每次回来刷新，保证增删后数据同步
    }

    private void loadData() {
        recordList.clear();
        double income = 0, expense = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE, null, null, null, null, null, "id DESC");
        while (c.moveToNext()) {
            Record r = new Record();
            r.id = c.getInt(c.getColumnIndexOrThrow("id"));
            r.amount = c.getDouble(c.getColumnIndexOrThrow("amount"));
            r.type = c.getInt(c.getColumnIndexOrThrow("type"));
            r.category = c.getString(c.getColumnIndexOrThrow("category"));
            r.note = c.getString(c.getColumnIndexOrThrow("note"));
            r.date = c.getString(c.getColumnIndexOrThrow("date"));
            r.isFixed = c.getInt(c.getColumnIndexOrThrow("isFixed"));
            recordList.add(r);
            if (r.type == 1) income += r.amount; else expense += r.amount;
        }
        c.close();
        adapter.notifyDataSetChanged();
        tvIncome.setText("收入\n" + String.format("%.2f", income));
        tvExpense.setText("支出\n" + String.format("%.2f", expense));
        tvBalance.setText("结余\n" + String.format("%.2f", income - expense));
    }
}