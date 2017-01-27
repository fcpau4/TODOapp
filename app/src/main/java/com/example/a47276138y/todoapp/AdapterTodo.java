package com.example.a47276138y.todoapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.a47276138y.todoapp.databinding.LvTodoRowBinding;

import java.util.List;

/**
 * Created by 47276138y on 17/01/17.
 */

public class AdapterTodo extends ArrayAdapter<Todo> {

    LvTodoRowBinding binding = null;

    public AdapterTodo(Context context, int resource, List<Todo> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Todo todo = getItem(position);

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());

            binding = DataBindingUtil.inflate(inflater, R.layout.lv_todo_row, parent, false);

        }else{
            binding = DataBindingUtil.getBinding(convertView);
        }


        binding.textView.setText(todo.getMssg());


        return binding.getRoot();
    }
}
