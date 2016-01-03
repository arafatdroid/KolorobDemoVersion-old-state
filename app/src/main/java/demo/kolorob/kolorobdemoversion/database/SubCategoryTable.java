package demo.kolorob.kolorobdemoversion.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import demo.kolorob.kolorobdemoversion.model.CategoryItem;
import demo.kolorob.kolorobdemoversion.model.SubCategoryItem;
import demo.kolorob.kolorobdemoversion.utils.Lg;

/**
 * Created by Yeakub Hassan Rafi on 26-Dec-15.
 */
public class SubCategoryTable {
    private static final String TAG = SubCategoryTable.class.getSimpleName();

    private static final String TABLE_NAME = DatabaseHelper.SUB_CATEGORY;

    private static final String KEY_CAT_ID = "_cat_id"; // 0 -integer
    private static final String KEY_SUB_CAT_ID = "_sub_cat_id"; // 1 -integer
    private static final String KEY_NAME = "_sub_cat_name"; // 2 - text
    private static final String KEY_SUB_CAT_HEADER= "_sub_cat_header";
    // : But boolean value,
    // for simplicity of the local table
    // structure it's kept as string.

    private Context tContext;

    public SubCategoryTable(Context context) {
        tContext = context;
        createTable();
    }

    private void createTable() {
        SQLiteDatabase db = openDB();
        String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + "( "
                + KEY_CAT_ID + " INTEGER, "
                + KEY_SUB_CAT_ID + " INTEGER, "
                + KEY_NAME + " TEXT, "
                + KEY_SUB_CAT_HEADER + " TEXT, "
                + " PRIMARY KEY("+KEY_CAT_ID+","+KEY_SUB_CAT_ID+"))";
        db.execSQL(CREATE_TABLE_SQL);
        closeDB();
    }

    private SQLiteDatabase openDB() {
        return DatabaseManager.getInstance(tContext).openDatabase();
    }

    private void closeDB() {
        DatabaseManager.getInstance(tContext).closeDatabase();
    }

    public long insertItem(SubCategoryItem subCategoryItem){
        return insertItem(
                subCategoryItem.getCatId(),
                subCategoryItem.getId(),
                subCategoryItem.getSubCatName(),
                subCategoryItem.getSubcatHeader()
        );
    }

    public long insertItem(int cat_id, int id, String name,String header) {
        if (isFieldExist(id,cat_id)) {
            return updateItem(cat_id,id,name,header);
        }
        ContentValues rowValue = new ContentValues();
        rowValue.put(KEY_CAT_ID, cat_id);
        rowValue.put(KEY_SUB_CAT_ID, id);
        rowValue.put(KEY_NAME, name);
        rowValue.put(KEY_SUB_CAT_HEADER,header);

        SQLiteDatabase db = openDB();
        long ret = db.insert(TABLE_NAME, null, rowValue);
        closeDB();
        return ret;
    }

    public boolean isFieldExist(int id,int cat_id) {
       // Lg.d(TAG, "isFieldExist : inside, id=" + id);
        SQLiteDatabase db = openDB();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                if (Integer.parseInt(cursor.getString(1)) == id && Integer.parseInt(cursor.getString(0)) == cat_id ) {
                    cursor.close();
                    closeDB();
                    return true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDB();
        return false;
    }

    private long updateItem(int cat_id,int id, String name,String header) {
        ContentValues rowValue = new ContentValues();
        rowValue.put(KEY_CAT_ID, cat_id);
        rowValue.put(KEY_SUB_CAT_ID, id);
        rowValue.put(KEY_NAME, name);
        rowValue.put(KEY_SUB_CAT_HEADER,header);

        SQLiteDatabase db = openDB();
        long ret = db.update(TABLE_NAME, rowValue, KEY_CAT_ID + " = ? AND "+KEY_SUB_CAT_ID+" = ?",
                new String[]{cat_id + "",id+""});
        closeDB();
        return ret;
    }

    public ArrayList<SubCategoryItem> getAllSubCategories(int id) {
        ArrayList<SubCategoryItem> siList = new ArrayList<>();

        SQLiteDatabase db = openDB();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME+" WHERE "+KEY_CAT_ID+" = "+id, null);

        if (cursor.moveToFirst()) {
            do {
                siList.add(cursorToSubCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDB();
        return siList;
    }

    public ArrayList<SubCategoryItem> getAllSubCategoriesHeader(int id,String head) {
        ArrayList<SubCategoryItem> siList = new ArrayList<>();

        SQLiteDatabase db = openDB();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME+" WHERE "+KEY_CAT_ID+" = "+ id +" AND "+KEY_SUB_CAT_HEADER+" = '"+head+"'", null);

        if (cursor.moveToFirst()) {
            do {
                siList.add(cursorToSubCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDB();
        return siList;
    }

    private SubCategoryItem cursorToSubCategory(Cursor cursor) {
        int cat_id = cursor.getInt(0);
        int id = cursor.getInt(1);
        String name = cursor.getString(2);
        String head = cursor.getString(3);
        return new SubCategoryItem(cat_id,id, name,head);
    }

    public void dropTable() {
        SQLiteDatabase db = openDB();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        createTable();
        Lg.d(TAG, "Table dropped and recreated.");
        closeDB();
    }
}
