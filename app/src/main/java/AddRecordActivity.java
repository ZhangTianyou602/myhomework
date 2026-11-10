package com.example.myhomework;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddRecordActivity extends AppCompatActivity {
    private EditText etAmount, etNote;
    private RadioGroup rgType;
    private Spinner spCategory;
    private CheckBox cbFixed;
    private DBHelper dbHelper;

    private final String[] expenseCats = {"餐饮", "交通", "购物", "房租", "学习"};
    private final String[] incomeCats = {"工资", "兼职", "理财收益", "奖学金"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        dbHelper = new DBHelper(this);
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        rgType = findViewById(R.id.rgType);
        spCategory = findViewById(R.id.spCategory);
        cbFixed = findViewById(R.id.cbFixed);
        Button btnSave = findViewById(R.id.btnSave);

        // 接收去程传值并展示（演示 Intent 传值）
        String balance = getIntent().getStringExtra("currentBalance");
        if (balance != null) {
            Toast.makeText(this, "当前" + balance.replace("\n", " "), Toast.LENGTH_SHORT).show();
        }

        // RadioGroup 切换 → 联动 Spinner 分类
        rgType.setOnCheckedChangeListener((group, checkedId) ->
                setCategoryAdapter(checkedId == R.id.rbIncome));
        setCategoryAdapter(false); // 默认支出

        btnSave.setOnClickListener(v -> save());
    }

    private void setCategoryAdapter(boolean isIncome) {
        ArrayAdapter<String> ad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                isIncome ? incomeCats : expenseCats);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(ad);
    }

    private void save() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "金额格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount <= 0) {
            Toast.makeText(this, "金额必须大于0", Toast.LENGTH_SHORT).show();
            return;
        }

        int type = rgType.getCheckedRadioButtonId() == R.id.rbIncome ? 1 : 0;
        String category = spCategory.getSelectedItem().toString();
        String note = etNote.getText().toString().trim();
        int isFixed = cbFixed.isChecked() ? 1 : 0;
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);
        cv.put("type", type);
        cv.put("category", category);
        cv.put("note", note);
        cv.put("date", date);
        cv.put("isFixed", isFixed);
        db.insert(DBHelper.TABLE, null, cv);

        // 回传值给 MainActivity（演示 Intent 回传）
        Intent result = new Intent();
        result.putExtra("note", (type == 1 ? "收入 " : "支出 ") + category + " " + amount + " 元");
        setResult(RESULT_OK, result);
        finish();
    }
}