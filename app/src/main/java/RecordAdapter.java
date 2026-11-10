package com.example.myhomework;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordAdapter extends BaseAdapter {
    private final Context ctx;
    private final List<Record> data;

    public RecordAdapter(Context ctx, List<Record> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override public int getCount() { return data.size(); }
    @Override public Object getItem(int i) { return data.get(i); }
    @Override public long getItemId(int i) { return data.get(i).id; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_record, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.tvItemTitle);
            holder.sub = convertView.findViewById(R.id.tvItemSub);
            holder.amount = convertView.findViewById(R.id.tvItemAmount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Record r = data.get(position);
        holder.title.setText(r.category + (r.isFixed == 1 ? " · 固定" : ""));
        holder.sub.setText(r.date + "  " + (r.note == null ? "" : r.note));
        if (r.type == 1) {
            holder.amount.setText("+" + String.format("%.2f", r.amount));
            holder.amount.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.amount.setText("-" + String.format("%.2f", r.amount));
            holder.amount.setTextColor(Color.parseColor("#C62828"));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView title, sub, amount;
    }
}