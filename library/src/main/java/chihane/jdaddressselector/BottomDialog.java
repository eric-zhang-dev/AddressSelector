package chihane.jdaddressselector;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import mlxy.utils.Dev;

public class BottomDialog extends Dialog {
    private AddressSelector selector;
    private String q1,q2,q3,q4,q5,q6;
    public BottomDialog(Context context,String p1,String p2,String p3,String p4,String p5,String p6) {
        super(context, R.style.bottom_dialog);
        this.q1 = p1;
        this.q2 = p2;
        this.q3 = p3;
        this.q4 = p4;
        this.q5 = p5;
        this.q6 = p6;
        init(context);
    }

    public BottomDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public BottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        selector = new AddressSelector(context,q1,q2,q3,q4,q5,q6);
        setContentView(selector.getView());
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = Dev.dp2px(context, 265);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
        this.selector.setOnAddressSelectedListener(listener);
    }

    public static BottomDialog show(Context context) {
        return show(context, null);
    }

    public static BottomDialog show(Context context, OnAddressSelectedListener listener) {
        BottomDialog dialog = new BottomDialog(context, R.style.bottom_dialog);
        dialog.selector.setOnAddressSelectedListener(listener);
        dialog.show();
        return dialog;
    }
}
