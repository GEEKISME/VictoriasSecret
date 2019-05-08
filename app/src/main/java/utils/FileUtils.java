package utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;


public class FileUtils {



	public static Uri getUriForFile(Context context, File file) {
		if (context == null || file == null) {
			throw new NullPointerException();
		}
		Uri uri;
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.biotag.cockowner.fileprovider", file);
		} else {
			uri = Uri.fromFile(file);
		}
		return uri;
	}


}
