package golony.duckdns.org.gnumap;

public class Rect {
    public int X1, Y1, width, height; // 시작좌표, 가로세로를 이용한 정의
    public int X2, Y2; // 시작좌표, 끝 좌표를 이용한 정의
    public int centerX, centerY; // 중심좌표, 가로세로를 이용한 정의

    Rect(int X1, int Y1, int X2, int Y2 ){
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
    }

    public void setPosByWH(int X, int Y, int width, int height){
        X1 = X; Y1 = Y; this.width = width; this.height = height;
    }

    public void setPosByCenterPnt(int X, int Y, int width, int height){
        centerX = X; centerY = Y; this.width = width; this.height = height;
    }
}
