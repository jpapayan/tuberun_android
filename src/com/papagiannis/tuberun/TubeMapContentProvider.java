package com.papagiannis.tuberun;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class TubeMapContentProvider extends ContentProvider {

	String FILENAME="tuberun.map";
    public static final String AUTHORITY = "com.papagiannis.tuberun.mapprovider";
    public static final String IMAGE = "map";
    
	@Override
	public boolean onCreate() {
		return true;
	}
    
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		try {
			byte[] ba=values.getAsByteArray("map");
			Context context=getContext();
			context.deleteFile(FILENAME);
			FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(ba, 0, ba.length);
			fos.close();
			return Uri.parse(AUTHORITY+"/"+IMAGE);
		}
		catch (Exception e) {
			Log.w("MapController",e);
		}
		return Uri.parse("");
		
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
	    File root = new File(getContext().getFilesDir(),FILENAME);
	    return ParcelFileDescriptor.open(root, ParcelFileDescriptor.MODE_READ_ONLY);
	}
	
	@Override
	public String getType(Uri uri) {
		return "image/gif";
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		try {
			Context context=getContext();
			FileInputStream fis=context.openFileInput(FILENAME);
			byte[] buffer = new byte[1024];
			byte[] fullFile = new byte[fis.available()];
			int bufferLength = 0; // used to store a temporary size of the
//									// buffer
			int i=0;
			while ((bufferLength = fis.read(buffer)) > 0) {
				int k=0;
				for (int j=i;j<i+bufferLength;j++) {
					fullFile[j]=buffer[k++];
				}
				i+=bufferLength;
			}
			MatrixCursor res=new MatrixCursor(new String[]{"map"});
			res.addRow(new Object[]{fullFile});
			return res;
		
		}
		catch (Exception e) {
			Log.w("MapProvider",e);
		}
		return null;
		
	}
	
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

}

