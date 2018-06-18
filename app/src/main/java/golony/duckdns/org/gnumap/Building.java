package golony.duckdns.org.gnumap;

public class Building {
    private String buildName;
    private int buildNum;
    public double X;
    public double Y;
    private String imgPath;
    private int wifi;
    private double xDiff;
    private double yDiff;

    Building(int buildNum, String buildName, double X, double Y, String imgPath, int wifi){
        this.buildName = buildName; this.buildNum = buildNum; this.X = X; this.Y = Y; this.imgPath = imgPath; this.wifi = wifi;
        this.xDiff = 0;
        this.yDiff = 0;
    }

    public void printAll(){
        System.out.println(buildName + " " + buildNum + " " + X + " " + Y + " " + imgPath + " " + wifi);
    }

    public String returnName(){
        return buildName;
    }
    public double returnArcTan(){
        double tan = yDiff / xDiff;
        double radAngle = Math.atan(tan);
//        if (radAngle < 0)
//            radAngle += (2*Math.PI);

        // 3사분면
        if (yDiff > 0 && xDiff > 0)
            radAngle += Math.PI;

//        // TODO: 4사분면 고려?
//        if (yDiff < 0 && xDiff > 0)
//            angle += Math.PI;

        // 2 사분면인 경우 Angle 값에서 180도 차감
        // TODO: 왜 90도 회전하면 되는걸까?
        if (yDiff > 0 && xDiff < 0)
            radAngle += Math.PI;


        if (radAngle < 0)
            radAngle += (2*Math.PI);
        else if (radAngle > 2*Math.PI)
            radAngle -= (2*Math.PI);

        return radAngle;
    }

    public void setXYDiff(double x, double y){
//        double xDiff = x - obj.X;
//        double yDiff = y - obj.Y;

        this.xDiff = x;
        this.yDiff = y;
    }

}
