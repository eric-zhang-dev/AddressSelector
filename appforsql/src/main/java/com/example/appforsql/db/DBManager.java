package com.example.appforsql.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.appforsql.App;
import com.example.appforsql.R;
import com.example.appforsql.utils.FileUtil;

public class DBManager<T> {

	public static final String DB_NAME = "my.db"; // 数据库名
	// 表名
	public static final String DB_TABLE_AREA="Distinct";
	public static final String DB_PATH = FileUtil.getDataPath();
	private SQLiteDatabase database;
	private Class<?> cls;
	private HashMap<String, Method> setMethodMap; // 存放泛型类的字段名及此字段的set方法
	private HashMap<String, Class<?>> getMap; // 存放泛型类的字段名及字段类型
	private HashMap<String, Method> getMethodMap; // 存放泛型类的字段名及字段get方法
	public DBManager() {
		super();
	}
	public DBManager(Class<?> cls) {
		super();
		this.cls = cls;
		init();
	}

	/**
	 * 初始化字段Map
	 */
	private void init() {
		try {
			getMap = new HashMap<String, Class<?>>();
			setMethodMap = new HashMap<String, Method>();
			getMethodMap = new HashMap<String, Method>();

			Method[] allMethods = cls.getMethods();
			String methodName;
			String fieldName;
			for (Method m : allMethods) {
				// 排除父类
				if (m.getDeclaringClass() != cls)
					continue;
				methodName = m.getName();
				if (methodName.substring(0, 3).equals("get")) {
					fieldName = methodName.substring(3, methodName.length());
					getMap.put(fieldName.toLowerCase(), m.getReturnType());
					setMethodMap
							.put(fieldName.toLowerCase(),
									cls.getMethod("set" + fieldName,
											m.getReturnType()));
					getMethodMap.put(fieldName.toLowerCase(),
							cls.getMethod("get" + fieldName));
				}
			}
		} catch (Exception e) {
			Log.e("DBManager 初始化字段Map异常", ""+e);
		}
	}

	/**
	 * 复制数据库到手机指定文件夹下
	 *
	 * @throws IOException
	 */
	public static void copyDataBase() {
		try {
			File file = new File(DB_PATH);
			if (!file.exists()){
				file.mkdirs();
			}
			File dbFile = new File(file, DB_NAME);
			if (dbFile.exists())
				return;
			FileOutputStream os = new FileOutputStream(dbFile);
			InputStream is = App.getInstance().getResources().openRawResource(
					R.raw.my);
			byte[] buffer = new byte[1024];
			int count = -1;
			while ((count = is.read(buffer)) != -1) {
				os.write(buffer, 0, count);
				os.flush();
			}
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * 复制数据库到SDCard
//	 */
//	public static void copyDataBaseToSDCard() {
//		try {
//			File dbFile = new File(new File(DB_PATH), DB_NAME);
//			FileInputStream fileInputStream = new FileInputStream(dbFile);
//			FileUtil.writeFile("/storage/emulated/360/" + DB_NAME, fileInputStream);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 更新表结构
	 *
	 * @param context
	 */
//	public static void UpdateTable(Context context) {
//		DBManager<Menu> dbManager = new DBManager<Menu>(Menu.class);
//		dbManager.openDatabase();
//		try {
//			InputStream inputStream = context.getAssets().open("sql");
//			BufferedReader bufferedReader = new BufferedReader(
//					new InputStreamReader(inputStream));
//			String str = null;
//			while ((str = bufferedReader.readLine()) != null) {
//				dbManager.execuSQL(str);
//			}
//		} catch (Exception e) {
//			Log.e("执行SQL异常", ""+e);
//		}
//		dbManager.closeDatabase();
//	}

	/**
	 * 打开数据库
	 */
	public void openDatabase() {
		if (!isOpen())
			this.database = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME,
					null, SQLiteDatabase.OPEN_READWRITE
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}

	/**
	 * 查询数据分页 (start、total 为null查询所有数据)
	 *
	 * @param tablename
	 *            表名
	 * @param start
	 *            开始的数据位置
	 * @param total
	 *            要获取的数据个数
	 * @return 返回List对象集合
	 */
	public List<T> getAll(String tablename, Integer start, Integer total) {
		String sql = "select * from " + tablename;
		if (start != null && total != null) {
			sql = sql + " limit " + start + "," + total;
		}
		Cursor cursor = database.rawQuery(sql, null);
		List<T> listResult = getListResult(cursor);
		cursor.close();
		return listResult;
	}

	/**
	 * 条件查询
	 *
	 * @param tablename
	 * @param parms
	 * @param start
	 * @param total
	 * @return
	 */
	public List<T> getByCondition(String tablename, Map<String, String> parms,
								  Integer start, Integer total) {
		StringBuffer sql = new StringBuffer("select * from " + tablename);
		int i = 0;
		String selectionArgs[] = null;
		if (parms != null) {
			int len = parms.size();
			selectionArgs = new String[len];
			sql.append(" where ");
			for (Map.Entry<String, String> entry : parms.entrySet()) {
				sql.append(entry.getKey());
				if (i != len - 1) {
					sql.append(" or ");
				}
				selectionArgs[i] = entry.getValue();
				i++;
			}
		}
		if (start != null && total != null)
			sql.append(" limit " + start + "," + total);
		Cursor cursor = database.rawQuery(sql.toString(), selectionArgs);
		List<T> listResult = getListResult(cursor);
		cursor.close();
		return listResult;
	}

	/**
	 * 多表条件查询
	 *
	 * @param parms
	 * @param start
	 * @param total
	 * @return
	 */
	public List<T> getByCondition(String[] tableNames,
								  Map<String, String> parms, Integer start, Integer total) {
		StringBuffer sql = new StringBuffer("select * from ");
		int tableLen = tableNames.length;
		for (int k = 0; k < tableLen; k++) {
			sql.append(tableNames[k] + " ");
			if (k != tableLen - 1)
				sql.append(", ");
		}
		int i = 0;
		String selectionArgs[] = null;
		if (parms != null && parms.size() != 0) {
			sql.append(" where ");
			int len = parms.size();
			selectionArgs = new String[len];
			for (Map.Entry<String, String> entry : parms.entrySet()) {
				sql.append(entry.getKey());
				selectionArgs[i] = entry.getValue();
				i++;
			}
		}
		if (start != null && total != null)
			sql.append(" limit " + start + "," + total);
		Cursor cursor = database.rawQuery(sql.toString(), selectionArgs);
		List<T> listResult = getListResult(cursor);
		cursor.close();
		return listResult;
	}

	/**
	 * 执行Sql语句 返回的List不为null
	 *
	 * @param sql
	 * @param start
	 * @param total
	 * @return
	 */
	public LinkedList<T> getBySql(String sql, Integer start, Integer total) {
		try {
			if (start != null && total != null)
				sql = sql + " limit " + start + "," + total;
			Cursor cursor = database.rawQuery(sql.toString(), null);
			LinkedList<T> listResult = getListResult(cursor);
			cursor.close();
			return listResult;
		} catch (Exception e) {
			Log.e("查询异常", ""+e);
			return new LinkedList<T>();
		}
	}

	/**
	 * 将Cursor结果集 封装成List对象集合
	 *
	 * @param cursor
	 * @return
	 */
	private LinkedList<T> getListResult(Cursor cursor) {
		LinkedList<T> listResult = new LinkedList<T>();
		try {
			String columns[] = cursor.getColumnNames();
			Object o;
			while (cursor.moveToNext()) {
				o = cls.newInstance();
				for (String column : columns) {
					Object value;
					if ("id".equalsIgnoreCase(column)) {
						value = cursor.getInt(cursor.getColumnIndex(column));
					} else {
						value = cursor.getString(cursor.getColumnIndex(column));
					}
					reflectionValue(o, column, value);
				}
				listResult.add((T) o);
			}
			return listResult;
		} catch (Exception e) {
			Log.e("将Cursor结果集 封装异常",""+ e);
			return listResult;
		}
	}

	/**
	 * 反射设置值
	 *
	 * @param o
	 * @param fieldName
	 * @param value
	 */
	private void reflectionValue(Object o, String fieldName, Object value) {
		Class<?> typeClass = getMap.get(fieldName.toLowerCase());
		Method method = setMethodMap.get(fieldName.toLowerCase());
		try {
			if (typeClass != null && method != null) {
				if (typeClass == String.class) {
					method.invoke(o, value == null ? null : value.toString());
				} else if (typeClass == Integer.class) {
					method.invoke(
							o,
							value == null ? null : Integer.parseInt(value
									.toString()));
				} else if (typeClass == Long.class) {
					method.invoke(
							o,
							value == null ? null : Long.parseLong(value
									.toString()));
				} else if (typeClass == Boolean.class) {
					method.invoke(
							o,
							value == null ? null : Boolean.parseBoolean(value
									.toString()));
				}
			}
		} catch (Exception e) {
			Log.e("===",""+e);
		}
	}

	/**
	 * 反射封装ContentValues
	 *
	 * @param object
	 * @param ignoreNames
	 *            忽略字段
	 * @return
	 */
	private ContentValues reflectionContentValue(Object object,
												 String[] ignoreNames) {
		this.cls = object.getClass();
		init();
		ContentValues values = new ContentValues();
		try {
			for (String columnName : getMap.keySet()) {
				boolean isIgnore = false;
				if (ignoreNames != null) {
					for (String ignoreName : ignoreNames) {
						if (columnName.equalsIgnoreCase(ignoreName)) {
							isIgnore = true;
							break;
						}
					}
				}
				if (isIgnore)
					continue;
				Class<?> typeClass = getMap.get(columnName.toLowerCase());
				Method method = getMethodMap.get(columnName.toLowerCase());
				if (typeClass != null && method != null) {
					Object obj = method.invoke(object);
					if (typeClass == String.class) {
						values.put(columnName,
								obj == null ? "" : obj.toString());
					} else if (typeClass == Integer.class) {
						values.put(columnName, obj == null ? "" : ""
								+ (Integer) obj);
					}
				}
			}
		} catch (Exception e) {
			Log.e("反射封装ContentValues失败", ""+e);
			return values;
		}
		return values;
	}

	/**
	 * 插入数据
	 *
	 * @return 失败返回-1
	 */
	public long insert(String tableName, Object object, String[] ignoreNames) {
		if (object == null)
			return -1;
		ContentValues values = reflectionContentValue(object, ignoreNames);
		if (values.size() == 0)
			return -1;
		long value = 0;
		try {
			value = database.insert(tableName, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("插入数据失败", ""+e);
		}
		return value;
	}

	/**
	 * 修改数据
	 *
	 * @param tableName
	 * @param primaryKeyId
	 *            主键
	 * @param primaryKeyValue
	 *            主键值
	 * @param object
	 * @return
	 */
	public int modify(String tableName, String primaryKeyId,
					  String primaryKeyValue, String[] ignoreNames, Object object) {
		if (object == null || primaryKeyValue == null)
			return -1;
		ContentValues values = reflectionContentValue(object, ignoreNames);
		if (values.size() == 0)
			return -1;
		return database.update(tableName, values, primaryKeyId + "=?",
				new String[] { primaryKeyValue });
	}

	/**
	 * 删除数据
	 *
	 * @param tableName
	 * @param primaryKeyId
	 * @param primaryKeyValue
	 * @return
	 */
	public int delete(String tableName, String primaryKeyId,
					  String primaryKeyValue) {
		return database.delete(tableName, primaryKeyId + "=?",
				new String[] { primaryKeyValue });
	}

	/**
	 * 执行非查询语句,多条数据时不支持使用易抛出异常
	 *
	 * @param sql
	 */
	public void execuSQL(String sql) {
		database.execSQL(sql);
	}

	/**
	 * 查询表数据总数
	 *
	 * @param tablename
	 *            表名
	 * @return
	 */
	public int getTotalCount(String tablename) {
		Cursor cursor = database.query(tablename, new String[] { "count(*)" },
				null, null, null, null, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	/**
	 * 根据SQL查询表数据总数
	 *
	 * @param
	 *
	 * @return
	 */
	public int getTotalCountBySql(String sql) {
		Cursor cursor = database.rawQuery(sql, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	/**
	 * 根据SQL查询唯一字段
	 *
	 * @param
	 *
	 * @return
	 */
	public List<String> getDesdintBySql(String sql) {
		List<String> nameList = new LinkedList<String>();
		Cursor cursor = database.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor
					.getColumnIndex("PATIENT_NAME"));
			nameList.add(name);
		}
		cursor.close();
		return nameList;
	}

	/**
	 * 开始事务
	 */
	public void beginTransaction() {
		this.database.beginTransaction();
	}

	/**
	 * 关闭事务
	 */
	public void endTransaction() {
		this.database.setTransactionSuccessful();
		this.database.endTransaction();
	}

	/**
	 * 清空表数据
	 *
	 * @param tableName
	 */
	public void clearTable(String tableName) {
		this.database.execSQL("delete from " + tableName);
		// this.database.execSQL("update sqlite_sequence set seq=0 where name = '"+tableName+"'");
		// //将自增长字段重置为0
	}

	/**
	 * 判断数据库状态
	 *
	 * @return
	 */
	public boolean isOpen() {
		if (database != null)
			return database.isOpen();
		return false;
	}

	/**
	 * 关闭数据库
	 *
	 * @return
	 */
	public void closeDatabase() {
		if (database != null && database.isOpen()) {
			database.close();
		}
	}
}