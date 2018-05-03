package golony.duckdns.org.gnumap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    // 관리할 DB이름, 버전정보를 생성자로 받음
    public DBHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB Created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public ArrayList<Building> getResult(){
        System.out.println("getResult 시작");

        ArrayList<Building> buildList = new ArrayList<Building>();

        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        System.out.println("DB 객체 생성");
        Cursor cursor = db.rawQuery("SELECT * FROM building", null);
        while(cursor.moveToNext()){
            buildList.add(new Building(cursor.getInt(1),cursor.getString(2),cursor.getDouble(3),cursor.getDouble(4),
                    cursor.getString(5), cursor.getInt(6)));

            result += cursor.getString(1) + "  " + cursor.getString(2);
            System.out.println(result);
            result = "";
        }
        cursor.close();
        return buildList;
    }

    public ArrayList<Building> searchName(String str){
        System.out.println("getName 시작");
        System.out.println("키워드: " + str);

        ArrayList<Building> buildList = new ArrayList<Building>();
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // TODO: 예외처리
        Cursor cursor = db.rawQuery("SELECT * FROM building WHERE buildName == \"" + str + "\"" , null);
        while(cursor.moveToNext()){
            buildList.add(new Building(cursor.getInt(1),cursor.getString(2),cursor.getDouble(3),cursor.getDouble(4),
                    cursor.getString(5), cursor.getInt(6)));

            result += cursor.getString(0) + "  " + cursor.getString(1);
            System.out.println(result);
            result = "";
        }
        cursor.close();
        return buildList;
    }

    public ArrayList<Building> searchNum(int num){
        System.out.println("searchNum 시작");
        System.out.println("키워드: " + num);

        ArrayList<Building> buildList = new ArrayList<Building>();
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // TODO: 예외처리
        Cursor cursor = db.rawQuery("SELECT * FROM building WHERE buildNum == " + num , null);
        while(cursor.moveToNext()){
            buildList.add(new Building(cursor.getInt(1),cursor.getString(2),cursor.getDouble(3),cursor.getDouble(4),
                    cursor.getString(5), cursor.getInt(6)));

            result += cursor.getString(0) + "  " + cursor.getString(1);
            System.out.println(result);
            result = "";
        }

        cursor.close();
        return buildList;
    }

    public ArrayList<Building> searchPos(double X, double Y, double range){
        double Xrange = range;
        double Yrange = range;
        System.out.println("searchPos 시작");
        System.out.println("키워드: X:" + X + " Y: " + Y );

        ArrayList<Building> buildList = new ArrayList<Building>();
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // TODO: 예외처리
        Cursor cursor = db.rawQuery("SELECT * FROM building WHERE X > " + (X-Xrange) + " and X < " + (X+Xrange) + " and Y > " + (Y-Yrange)
                + " and Y < " + (Y+Yrange), null);
        while(cursor.moveToNext()){
            buildList.add(new Building(cursor.getInt(1),cursor.getString(2),cursor.getDouble(3),cursor.getDouble(4),
                    cursor.getString(5), cursor.getInt(6)));

            result += cursor.getString(0) + "  " + cursor.getString(1);
//            System.out.println(result);
            result = "";
        }

        cursor.close();
        return buildList;
    }

}

