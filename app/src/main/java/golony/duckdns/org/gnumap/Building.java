package golony.duckdns.org.gnumap;

public class Building {
    private String buildName;
    private int buildNum;
    private double X;
    private double Y;
    private String imgPath;
    private int wifi;

    Building(int buildNum, String buildName,double X, double Y, String imgPath, int wifi){
        this.buildName = buildName; this.buildNum = buildNum; this.X = X; this.Y = Y; this.imgPath = imgPath; this.wifi = wifi;
    }

    public void printAll(){
        System.out.println(buildName + " " + buildNum + " " + X + " " + Y + " " + imgPath + " " + wifi);
    }
}
