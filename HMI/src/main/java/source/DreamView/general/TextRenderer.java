package org.rajawali3d.examples.DreamView.general;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class TextRenderer {
    public static Bitmap renderTextToBitmap(String text, int textColor , int textSize) {
       Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
       Canvas canvas = new Canvas(bitmap);
       canvas.drawColor(Color.TRANSPARENT);
       Paint paint = new Paint();
       paint.setColor(textColor);
       paint.setTextSize(textSize);
       paint.setTypeface(Typeface.DEFAULT_BOLD);
       float x = 50, y = 250;
       canvas.drawText(text, x, y, paint);
       return bitmap;
    }
}
