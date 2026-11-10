package com.example.myhomework;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DBHelper(this);
        TextView tvDetail = findViewById(R.id.tvDetail);
        Button btnDelete = findViewById(R.id.btnDelete);

        recordId = getIntent().getIntExtra("id", -1);
        double amount = getIntent().getDoubleExtra("amount", 0);
        int type = getIntent().getIntExtra("type", 0);
        String category = getIntent().getStringExtra("category");
        String note = getIntent().getStringExtra("note");
        String date = getIntent().getStringExtra("date");

        String info = "类型：" + (type == 1 ? "收入" : "支出") + "\n"
                + "金额：" + String.format("%.2f", amount) + " 元\n"
                + "分类：" + category + "\n"
                + "日期：" + date + "\n"
                + "备注：" + (note == null || note.isEmpty() ? "无" : note);
        tvDetail.setText(info);

        btnDelete.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(DBHelper.TABLE, "id=?", new String[]{String.valueOf(recordId)});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            finish(); // 回 MainActivity 后 onResume 自动刷新
        });
    }
}