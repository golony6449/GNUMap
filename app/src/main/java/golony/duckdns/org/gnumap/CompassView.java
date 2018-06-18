package golony.duckdns.org.gnumap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

public class CompassView extends View {
    private Drawable mCompass;
    private float mAzimuth = 0;
    private int PADDING = 2;

    public CompassView(Context ctx) {
        super(ctx);


//        this.mCompass = ctx.getResources().getDrawable(R.drawable.arrow_n);
    }
    private int param = 0;
    protected void onDraw(Canvas canvas) {
        canvas.save();

        Paint Pnt = new Paint();   // 페인트 생성
        Pnt.setColor(0xfff612ab);  // 핑크색
        Pnt.setAlpha(70); // 반투명
        RectF r;
        if (param == 0){
            System.out.println("Param: 0");
            r = new RectF(getWidth()*0.5f, getHeight()*0.1f, getWidth()*0.9f, getHeight()*0.35f); // 시작 X,Y 좌표, 끝 X,Y 좌표
            param = 1;
        }
        else {
            System.out.println("Params: 1");
            r = new RectF(getWidth()*0.5f/2, getHeight()*0.1f/2, getWidth()*0.9f/2, getHeight()*0.35f/2); // 시작 X,Y 좌표, 끝 X,Y 좌표
            param = 0;
        }


        canvas.drawRoundRect(r,5,5, Pnt); // 사각형 모서리 깎아 그리기
//        canvas.rotate(360 - mAzimuth, PADDING + mCompass.getMinimumWidth()
//                / 2, PADDING + mCompass.getMinimumHeight() / 2);
//        mCompass.setBounds(PADDING, PADDING, PADDING
//                + mCompass.getMinimumWidth(), PADDING
//                + mCompass.getMinimumHeight());
//
//        mCompass.draw(canvas);
//        canvas.restore();

        super.onDraw(canvas);
    }

//    public void

    public void setAzimuth(float aAzimuth) {
        mAzimuth = aAzimuth;

        this.invalidate();
    }

}
