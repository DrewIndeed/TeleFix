package com.example.telefixmain.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.example.telefixmain.R;

public class CustomProgressDialog extends Dialog {
    View view;

    @SuppressLint("InflateParams")
    public CustomProgressDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        setCancelable(false);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        setTitle(null);
        view = LayoutInflater.from(context).inflate(R.layout.progress_dialog_custom, null);
        setContentView(view);
    }

    @SuppressLint("InflateParams")
    public CustomProgressDialog(@NonNull Context context, @StyleRes int theme, int layoutSrc) {
        super(context, theme);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        setTitle(null);
        view = LayoutInflater.from(context).inflate(layoutSrc, null);
        setContentView(view);
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
        getWindow().getAttributes().windowAnimations = R.anim.fade_out;
    }

    public void changeText(String newText) {
        TextView dialogContent = view.findViewById(R.id.text_of_dialog);
        dialogContent.setText(newText);
    }
}
