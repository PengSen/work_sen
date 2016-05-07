package com.ob.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class CopyDbToSdcard {
	private Context context;
	private String filePath = null;
	private String pathStr = null;

	// private SQLiteDatabase database;
	public CopyDbToSdcard(Context context) {
		this.context = context;
		// 数据库存放的文件夹 data/data/包名/databases 下面
		pathStr = context.getFilesDir().getPath();
		pathStr = pathStr.substring(0, pathStr.lastIndexOf("/")) + "/databases";
		// 数据库存储路径
		filePath = pathStr + "/sen.db";

	}

	public Boolean copyDatabase() {
		if (hasSDCard()) {//sdcard都木有...
			File jhPath = new File(filePath);
			// 查看数据库文件是否存在
			if (jhPath.exists()) {
				Log.i("CopyDbToSdcard", "存在数据库");
				// 存在则直接返回true
				return true;
			} else {
				// 不存在先创建文件夹
				File path = new File(pathStr);
				Log.i("CopyDbToSdcard", "pathStr=" + path);
				if (path.mkdir()) {
					Log.i("CopyDbToSdcard", "创建成功");
				} else {
					Log.i("CopyDbToSdcard", "创建失败");
					
				}
				try {
					// 得到资源
					AssetManager am = context.getAssets();
					// 得到数据库的输入流
					InputStream is = am.open("sen.db");
					Log.i("CopyDbToSdcard", is + "");
					// 用输出流写到SDcard上面
					FileOutputStream fos = new FileOutputStream(jhPath);
					Log.i("CopyDbToSdcard", "fos=" + fos);
					Log.i("CopyDbToSdcard", "jhPath=" + jhPath);
					// 创建byte数组 用于1KB写一次
					byte[] buffer = new byte[1024];
					int count = 0;
					while ((count = is.read(buffer)) > 0) {
						Log.i("test", "得到"+count);
						fos.write(buffer, 0, count);
					}
					// 最后关闭就可以了
					fos.flush();
					fos.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				// 如果没有这个数据库 我们已经把他写到SD卡上了，然后在执行一次这个方法 就可以true了
				return copyDatabase();
			}
		} else {
			return false;
		}
	}
	/**
	 * 判断手机是否有SD卡。
	 * 
	 * @return 有SD卡返回true，没有返回false。
	 */
	private boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}
	public Boolean isDbFile(){
		File jhPath = new File(filePath);
		// 查看数据库文件是否存在
		if (jhPath.exists()) {
			return true;
		}else{
			return false;
		}
	}
	public Boolean deleteDbFile(){
		File jhPath = new File(filePath);
		// 查看数据库文件是否存在
		if (jhPath.exists()) {
			jhPath.delete();
			return true;
		}else{
			Toast.makeText(context, "删除失败，本地数据异常请联系管理员~", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}
