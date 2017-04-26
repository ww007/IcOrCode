package ww.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import ww.greendao.dao.StudentItem;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table STUDENT_ITEM.
*/
public class StudentItemDao extends AbstractDao<StudentItem, Long> {

    public static final String TABLENAME = "STUDENT_ITEM";

    /**
     * Properties of entity StudentItem.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property StudentItemID = new Property(0, Long.class, "StudentItemID", true, "STUDENT_ITEM_ID");
        public final static Property StudentCode = new Property(1, String.class, "studentCode", false, "STUDENT_CODE");
        public final static Property ItemCode = new Property(2, String.class, "itemCode", false, "ITEM_CODE");
        public final static Property LastResult = new Property(3, Integer.class, "lastResult", false, "LAST_RESULT");
        public final static Property ResultState = new Property(4, Integer.class, "resultState", false, "RESULT_STATE");
        public final static Property LastTestTime = new Property(5, String.class, "lastTestTime", false, "LAST_TEST_TIME");
        public final static Property TestState = new Property(6, Integer.class, "TestState", false, "TEST_STATE");
        public final static Property Remark1 = new Property(7, String.class, "Remark1", false, "REMARK1");
        public final static Property Remark2 = new Property(8, String.class, "Remark2", false, "REMARK2");
        public final static Property Remark3 = new Property(9, String.class, "Remark3", false, "REMARK3");
    };

    private DaoSession daoSession;


    public StudentItemDao(DaoConfig config) {
        super(config);
    }
    
    public StudentItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'STUDENT_ITEM' (" + //
                "'STUDENT_ITEM_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: StudentItemID
                "'STUDENT_CODE' TEXT," + // 1: studentCode
                "'ITEM_CODE' TEXT," + // 2: itemCode
                "'LAST_RESULT' INTEGER," + // 3: lastResult
                "'RESULT_STATE' INTEGER," + // 4: resultState
                "'LAST_TEST_TIME' TEXT," + // 5: lastTestTime
                "'TEST_STATE' INTEGER," + // 6: TestState
                "'REMARK1' TEXT," + // 7: Remark1
                "'REMARK2' TEXT," + // 8: Remark2
                "'REMARK3' TEXT);"); // 9: Remark3
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'STUDENT_ITEM'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, StudentItem entity) {
        stmt.clearBindings();
 
        Long StudentItemID = entity.getStudentItemID();
        if (StudentItemID != null) {
            stmt.bindLong(1, StudentItemID);
        }
 
        String studentCode = entity.getStudentCode();
        if (studentCode != null) {
            stmt.bindString(2, studentCode);
        }
 
        String itemCode = entity.getItemCode();
        if (itemCode != null) {
            stmt.bindString(3, itemCode);
        }
 
        Integer lastResult = entity.getLastResult();
        if (lastResult != null) {
            stmt.bindLong(4, lastResult);
        }
 
        Integer resultState = entity.getResultState();
        if (resultState != null) {
            stmt.bindLong(5, resultState);
        }
 
        String lastTestTime = entity.getLastTestTime();
        if (lastTestTime != null) {
            stmt.bindString(6, lastTestTime);
        }
 
        Integer TestState = entity.getTestState();
        if (TestState != null) {
            stmt.bindLong(7, TestState);
        }
 
        String Remark1 = entity.getRemark1();
        if (Remark1 != null) {
            stmt.bindString(8, Remark1);
        }
 
        String Remark2 = entity.getRemark2();
        if (Remark2 != null) {
            stmt.bindString(9, Remark2);
        }
 
        String Remark3 = entity.getRemark3();
        if (Remark3 != null) {
            stmt.bindString(10, Remark3);
        }
    }

    @Override
    protected void attachEntity(StudentItem entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public StudentItem readEntity(Cursor cursor, int offset) {
        StudentItem entity = new StudentItem( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // StudentItemID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // studentCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // itemCode
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // lastResult
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // resultState
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // lastTestTime
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // TestState
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // Remark1
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // Remark2
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // Remark3
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, StudentItem entity, int offset) {
        entity.setStudentItemID(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStudentCode(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setItemCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLastResult(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setResultState(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setLastTestTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setTestState(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setRemark1(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setRemark2(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setRemark3(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(StudentItem entity, long rowId) {
        entity.setStudentItemID(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(StudentItem entity) {
        if(entity != null) {
            return entity.getStudentItemID();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}