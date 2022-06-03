package com.example.kirokhada.Board.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.kirokhada.R;

/**
 * atrr 속성 필요함
 * res>values>styles>newDialog  필요함
 *
 */
public class ProgressCircleDialog extends Dialog {
    public static final boolean DEF_CANCELABLE = true ;


    public static ProgressCircleDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, DEF_CANCELABLE, null);
    }

    public static ProgressCircleDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate,
                                            boolean cancelable, OnCancelListener cancelListener) {
        ProgressCircleDialog dialog = new ProgressCircleDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        /* The next line will add the ProgressBar to the dialog. */
        dialog.addContentView(new ProgressBar(context), new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    public ProgressCircleDialog(Context context) {
        super(context, R.style.NewDialog);
    }
}
