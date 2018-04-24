package golony.duckdns.org.gnumap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public void getResult(){
        System.out.println("getResult 시작");

        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        System.out.println("DB 객체 생성");
        // TODO: 위도, 경도 값을 이용한 검색, 키워드를 이용한 검색 기능 추가
        Cursor cursor = db.rawQuery("SELECT buildNum, buildName FROM building", null);
        while(cursor.moveToNext()){
            result += cursor.getString(0) + cursor.getString(1);
            System.out.println(result);
            result = "";
        }
    }

}

