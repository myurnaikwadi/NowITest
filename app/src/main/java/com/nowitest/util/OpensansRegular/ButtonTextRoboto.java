package com.nowitest.util.OpensansRegular;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.nowitest.R;

public class ButtonTextRoboto extends Button{
    private Context c;
    public ButtonTextRoboto(Context context) {
        super(context);
        if(!isInEditMode()){
            this.c = context;
            Typeface tfs = Typeface.createFromAsset(c.getAssets(),c.getString(R.string.font_name_opensans_regular));
            setTypeface(tfs);

        }
    }

    public ButtonTextRoboto(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()){
            this.c = context;
            Typeface tfs = Typeface.createFromAsset(c.getAssets(),c.getString(R.string.font_name_opensans_regular));
            setTypeface(tfs);

        }
    }

    public ButtonTextRoboto(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()){
            this.c = context;
            Typeface tfs = Typeface.createFromAsset(c.getAssets(),c.getString(R.string.font_name_opensans_regular));
            setTypeface(tfs);

        }
    }
}
