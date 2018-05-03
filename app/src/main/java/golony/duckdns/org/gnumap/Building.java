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
    public double returnTan(){
        return yDiff / xDiff;
    }

    public void setXYDiff(double x, double y){
        this.xDiff = x;
        this.yDiff = y;
    }

}
