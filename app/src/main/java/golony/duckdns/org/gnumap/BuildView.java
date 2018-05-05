package golony.duckdns.org.gnumap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import java.lang.Math;

import java.util.ArrayList;

public class BuildView extends View {
    private Drawable mCompass;
    private int PADDING = 2;
    private ArrayList<RectF> markerList;
    private ArrayList<Building> buildingList = new ArrayList<Building>();
    private double calcedAngle;
    private int boxList[] = {1, 0, 0, 0, 0, 0, 0, 0, 1};
    private double angle;

    public BuildView(Context ctx) {
        super(ctx);

//        this.mCompass = ctx.getResources().getDrawable(R.drawable.arrow_n);
    }

    protected void onDraw(Canvas canvas) {
        canvas.save();

        // TODO: 수정필요
//        double angleFromXaxis = (this.angle ) * (Math.PI / 180) - Math.PI / 2;
//        double angleFromXaxis = -(this.angle * (Math.PI / 180)) - (Math.PI / 2);
        double angleFromXaxis = (90 - this.angle) * (Math.PI / 180);

        Paint Pnt = new Paint();   // 페인트 생성
        Pnt.setColor(0xfff612ab);  // 핑크색
        Pnt.setAlpha(70); // 반투명

        Paint pntText = new Paint();
        pntText.setStyle(Paint.Style.FILL);
        pntText.setTextSize(30);
        pntText.setColor(Color.WHITE);

//        RectF r = new RectF(getWidth()*0.5f, getHeight()*0.1f, getWidth()*0.9f, getHeight()*0.35f); // 시작 X,Y 좌표, 끝 X,Y 좌표

        if (buildingList.isEmpty()){
            super.onDraw(canvas);
            return;
        }

        for (int i =0; i < buildingList.size(); i++){
            Building buildObj = buildingList.get(i);
            double diff = angleFromXaxis - buildObj.returnArcTan(); // 방위각과 건물 위치간의 차이

            if (Math.abs(diff) < (Math.PI / 3)) {
                double xStep = getWidth() / 60;
                float position = (getWidth() / 2) - ((float) (diff *(180/Math.PI)) * (float) xStep);    // TODO: 수정필요

                RectF obj = new RectF( position - 100.f , getHeight()*0.4f, position + 100.f , getHeight()*0.6f);
                System.out.println("angle From X: " + (angleFromXaxis * (180/Math.PI)) + "   " + buildObj.returnName()+"의 X축 기준 건물위치: " + (buildObj.returnArcTan()* (180/Math.PI)));
//                System.out.println("diff: " + (diff *(180/Math.PI)) + "   position: " + position);
                canvas.drawText(buildObj.returnName(),position, getHeight() / 2f, pntText);
                canvas.drawRoundRect(obj, 5, 5, Pnt);
            }
            else{
//                System.out.println("범위 초과: " + buildObj.returnName() + "   diff: " + (diff*(180/ Math.PI)));
            }
        }

        // 주석
//        if (boxList[0] == 1){
//            RectF LU = new RectF(0, 0, getWidth()*0.5f, getHeight()*0.5f); // 시작 X,Y 좌표, 끝 X,Y 좌표
//            canvas.drawRoundRect(LU,5,5, Pnt); // 사각형 모서리 깎아 그리기
//        }
//
//        if (boxList[2] == 1){
//            RectF RU = new RectF(getWidth()*0.5f, 0, getWidth(), getHeight()*0.5f); // 시작 X,Y 좌표, 끝 X,Y 좌표
//            canvas.drawRoundRect(RU,5,5, Pnt); // 사각형 모서리 깎아 그리기
//        }
//
//        if (boxList[6] == 1){
//            RectF LB = new RectF(0, getHeight()*0.5f, getWidth()*0.5f, getHeight()); // 시작 X,Y 좌표, 끝 X,Y 좌표
//            canvas.drawRoundRect(LB,5,5, Pnt); // 사각형 모서리 깎아 그리기
//        }
//
//
//        if (boxList[8] == 1){
//            RectF RB = new RectF(getWidth()*0.5f, getHeight()*0.5f, getWidth(), getHeight()); // 시작 X,Y 좌표, 끝 X,Y 좌표
//            canvas.drawRoundRect(RB,5,5, Pnt); // 사각형 모서리 깎아 그리기
//        }

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

    public void setMarkerList(ArrayList<Building> buildList, double x, double y){
        // 초기화
        for (int i = 0; i < 9; i++)
            boxList[i] = 0;
        buildingList.clear();
        buildingList.addAll(buildList);
        System.out.println("setMarkerList Called");
        for (int i = 0; i < buildList.size(); i++){
            buildList.get(i).printAll();
        }

        for (int i = 0; i < buildList.size(); i++){
            Building obj = buildList.get(i);
            double xDiff = x - obj.X;
            double yDiff = y - obj.Y;
            obj.setXYDiff(xDiff, yDiff);
            buildList.set(i, obj);

////            정규화
//            xDiff = xDiff / (Math.max(Math.abs(xDiff), Math.abs(yDiff)));
//            yDiff = yDiff / (Math.max(Math.abs(xDiff), Math.abs(yDiff)));


            calcedAngle = Math.atan(yDiff / xDiff);
//            calcedAngle = Math.acos(yDiff / Math.sqrt(xDiff * xDiff + yDiff * yDiff));
//            System.out.println("X: " + x + "  Y: " + y);
//            System.out.println("objX: " + obj.X + "  objY: " + obj.Y);
//            System.out.println("xDiff: " + xDiff + "  yDiff: " + yDiff);
            System.out.println("계산된 방위각: " + (obj.returnArcTan() * (180/Math.PI)));

//            if (Math.abs(calcedAngle - this.angle) < 60){
//                if ((calcedAngle - this.angle) < 30 & (calcedAngle - this.angle) > 0)
//                    boxList[0] = 1;
//                if ((calcedAngle - this.angle) > -30 & (calcedAngle - this.angle) < 0)
//                    boxList[2] = 1;
//            }
        }
//        invalidate();
    }

    public void setAzimut(double angle){
        this.angle = angle;

//        System.out.println("방위각 차이: " + (calcedAngle - this.angle));

//            if (Math.abs(calcedAngle - this.angle) < 60){
//                if ((calcedAngle - this.angle) < 30 & (calcedAngle - this.angle) > 0)
//                    boxList[0] = 1;
//                if ((calcedAngle - this.angle) > -30 & (calcedAngle - this.angle) < 0)
//                    boxList[2] = 1;
//            }
            if (Math.abs(calcedAngle - this.angle) < 30 & calcedAngle < angle)
                boxList[2] = 1;
            if (Math.abs(calcedAngle - this.angle) < 30 & calcedAngle > angle)
                boxList[0] = 1;

            invalidate();
        }
}


