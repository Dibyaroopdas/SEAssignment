package com.example.seassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbmanager extends SQLiteOpenHelper {

    private static final String dbname="GeneInfo";

    public dbmanager(Context context) {


        super(context, dbname, null, 1);
    }

    //sl. no., gene info, nucleotide
    //sequence, gene length, G+C% of the gene, AA sequence and remark.

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String qry="create table tbl_gene ( sno integer primary key autoincrement, geneInfo text, nucleotide text, AAsequence text, len integer, gcpercent float )";
        sqLiteDatabase.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String qry="DROP TABLE IF EXISTS tbl_gene";
        sqLiteDatabase.execSQL(qry);
        onCreate(sqLiteDatabase);
    }

    public  String addrecord(String geneInfo, String nucleotide, String AAsequence, int len, float gcpercent)
    {
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("geneInfo",geneInfo);
        cv.put("nucleotide",nucleotide);
        cv.put("AAsequence",AAsequence);
        cv.put("len",len);
        cv.put("gcpercent",gcpercent);
        float res=db.insert("tbl_gene",null,cv);

        if(res==-1)
            return "Failed";
        else
            return  "Successfully inserted";

    }

    public Cursor getData(String name){
        SQLiteDatabase db = this.getWritableDatabase();

        String qry = "select * from tbl_gene where geneInfo='"+ name + "'";



        return db.rawQuery(qry,null);
    }

    public Cursor readalldata()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String qry="select * from tbl_gene order by id desc";
        Cursor cursor=db.rawQuery(qry,null);
        return  cursor;
    }

}
