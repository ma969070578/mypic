
package com.amima.app.widget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Timer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.amima.app.MyApp;
import com.my.util.Net;
import com.my.util.Tools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * ImageView extended class allowing easy downloading
 * of remote images
 * 
 * @author 
 */
public class HeadImageView extends ImageView{
	
	
	/**
	 * Maximum number of unsuccesful tries of downloading an image
	 */
	private static int MAX_FAILURES = 2;
	private boolean isLog = false; 
	private int isRoundedCorner = 0; //0：原图 1：圆形 2：圆角
	DisplayImageOptions options;

	public HeadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public HeadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public HeadImageView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	/**
	 * Sharable code between constructors
	 */
	private void init(){
		mTimeDiff = DAYS_OF_CACHE*24*60*60*1000L;
	}
	
	/**
	 * Remote image location
	 */
	private String mUrl;
	
	/**
	 * Currently successfully grabbed url
	 */
	private String mCurrentlyGrabbedUrl;
	
	/**
	 * Remote image download failure counter
	 */
	private int mFailure;

	/**
	 * Position of the image in the mListView
	 */
	private int mPosition;

	/**
	 * ListView containg this image
	 */
	private ListView mListView;
	
	/**
	 * Default image shown while loading or on url not found
	 */
	private Integer mDefaultImage;

	private ProgressDialog mProgress;
	
	private Timer mOuttime;
	
	private long mTimeDiff;
	
	//init value diffrent that possible values of mCacheSize
	private static int mPrevCacheSize= 1;
	private static int mCacheSize= 150;
	
	private static String imagever="";
	private Context mContext;
	
	private final static String HEAD= "head";
	private final static String HEADONLY= "jpg";
	private final static String BODY= "body";
	private final static String HEAD_MARKER= "img";
	private final static String MY_DIR= "Android/data/com.my.app";
	private final static int MB = 1048576;	
	//every DAYS_OF_CACHE the radio and album thumbnails jpegs are deleted
	private final static int DAYS_OF_CACHE= 45;
	//minimum free space on sd card to enable cache
	private final static int FREE_SD_SPACE_NEEDED_TO_CACHE= 10;
	
	

	public void setImageUrl(String url,ImageLoadingListener listener){
		
		MyApp.getInstance().getImageLoader().loadImage(url, listener);
		
		
	}
	/**
	 * Loads image from remote location
	 * 
	 * @param url 
	 */
	public void setImageUrl(String url,HeadImageView imageView){
		MyApp.getInstance().getImageLoader().displayImage(url, imageView, options);
	}
	
	 public static Bitmap fitSizeImg(String path) {
	  	  if(path == null || path.length()<1 ) return null;
	  	  File file = new File(path);
	  	  if(!file.exists())return null;
//	  	  Bitmap resizeBmp = null;
	  	  BitmapFactory.Options opts = new BitmapFactory.Options();
	  	  // 数字越大读出的图片占用的heap越小 不然总是溢出
	  	  if (file.length() < 20480) {       // 0-20k
	  	   opts.inSampleSize = 1;
	  	  } else if (file.length() < 51200) { // 20-50k
	  	   opts.inSampleSize = 1;
	  	  } else if (file.length() < 307200) { // 50-300k
	  	   opts.inSampleSize = 1;
	  	  } else if (file.length() < 819200) { // 300-800k
	  	   opts.inSampleSize = 2;
	  	  } else if (file.length() < 1048576) { // 800-1024k
	  	   opts.inSampleSize = 4;
	  	  }else if (file.length() < 1048576*2) { // 1024-1024k
	  	   opts.inSampleSize = 6;
	  	  } else {
	  	   opts.inSampleSize = 7;
	  	  }
	  	 WeakReference<Bitmap> wref=new WeakReference<Bitmap>(BitmapFactory.decodeFile(file.getPath(), opts));  
	  	  return wref.get();
	  	}
	/**
	 * Sets default local image shown when remote one is unavailable
	 * 
	 * @param resid
	 */
	public void setDefaultImage(Integer resid){
		mDefaultImage = resid;
		isRoundedCorner=0;
		 //0：原图 1：圆形 2：圆角
			if(isRoundedCorner==0){
				options = new DisplayImageOptions.Builder()
				.showImageOnLoading(mDefaultImage)
				.showImageForEmptyUri(mDefaultImage)
				.showImageOnFail(mDefaultImage)
				.cacheInMemory(true)
				.cacheOnDisc(true)
//				.bitmapConfig(Bitmap.Config.RGB_565)
//				.displayer(new RoundedBitmapDisplayer(360))
//				.displayer(new FadeInBitmapDisplayer(300))
				.build();
			}else if(isRoundedCorner==1){
				options = new DisplayImageOptions.Builder()
				.showImageOnLoading(mDefaultImage)
				.showImageForEmptyUri(mDefaultImage)
				.showImageOnFail(mDefaultImage)
				.cacheInMemory(true)
				.cacheOnDisc(true)
//				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(360))
//				.displayer(new FadeInBitmapDisplayer(300))
				.build();
			}else{
				options = new DisplayImageOptions.Builder()
				.showImageOnLoading(mDefaultImage)
				.showImageForEmptyUri(mDefaultImage)
				.showImageOnFail(mDefaultImage)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				
//				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(20))
//				.displayer(new FadeInBitmapDisplayer(300))
				.build();
			}
	}
	
	public void setDefaultImage(Integer resid,int isRoundedCorner ){
		mDefaultImage = resid;
		this.isRoundedCorner = isRoundedCorner;
		 //0：原图 1：圆形 2：圆角
		if(isRoundedCorner==0){
			options = new DisplayImageOptions.Builder()
			.showImageOnLoading(mDefaultImage)
			.showImageForEmptyUri(mDefaultImage)
			.showImageOnFail(mDefaultImage)
			.cacheInMemory(true)
			.cacheOnDisc(true)
//			.bitmapConfig(Bitmap.Config.RGB_565)
//			.displayer(new RoundedBitmapDisplayer(360))
//			.displayer(new FadeInBitmapDisplayer(300))
			.build();
		}else if(isRoundedCorner==1){
			options = new DisplayImageOptions.Builder()
			.showImageOnLoading(mDefaultImage)
			.showImageForEmptyUri(mDefaultImage)
			.showImageOnFail(mDefaultImage)
			.cacheInMemory(true)
			.cacheOnDisc(true)
//			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new RoundedBitmapDisplayer(360))
//			.displayer(new FadeInBitmapDisplayer(300))
			.build();
		}else{
			options = new DisplayImageOptions.Builder()
			.showImageOnLoading(mDefaultImage)
			.showImageForEmptyUri(mDefaultImage)
			.showImageOnFail(mDefaultImage)
			.cacheInMemory(true)
			.cacheOnDisc(true)
//			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new RoundedBitmapDisplayer(20))
//			.displayer(new FadeInBitmapDisplayer(300))
			.build();
		}
		
	}
	/**
	 * Loads default image
	 */
	private void loadDefaultImage(){
		if(mDefaultImage != null)
			setImageResource(mDefaultImage);
	}
	
	/**
	 * Loads image from remote location in the ListView
	 * 
	 * @param url eg.
	 * @param position ListView position where the image is nested
	 * @param listView ListView to which this image belongs
	 */
	public void setImageUrl(String url, int position, ListView listView,String ver){
		mPosition = position;
		mListView = listView;
		imagever = ver;
		this.isRoundedCorner = 2;
		setImageUrl(url,this);
	}
	public void setImageUrl(String url, int position, ListView listView){
		mPosition = position;
		mListView = listView;
		this.isRoundedCorner = 2;
		setImageUrl(url,this);
	}
	
	
	public void setImageUrl(String url, int position, ListView listView,String ver,int isRoundedCorner){
		mPosition = position;
		mListView = listView;
		imagever = ver;
		this.isRoundedCorner = isRoundedCorner;
		setImageUrl(url,this);
	}
	
	/**
	 * 是否显示圆形角图片  
	 * @param url
	 * @param position
	 * @param listView
	 * 0：原图 1：圆形 2：圆角
	 * @param isRoundedCorner   
	 */
	
	public void setImageUrl(String url, int position, ListView listView,int isRoundedCorner){
		mPosition = position;
		mListView = listView;
		this.isRoundedCorner = isRoundedCorner;
		setImageUrl(url,this);
	}
	
	public void setDialogOrTimeOut(ProgressDialog progress,Timer outtime){
		mProgress = progress;
		mOuttime = outtime;
	}
	/**
	 * Asynchronous image download task
	 * 
	 *
	 */
	class DownloadTask extends AsyncTask<String, Void, String>{
		
		private String mTaskUrl;
		private Bitmap mBmp = null;

		@Override
		public void onPreExecute() {
			loadDefaultImage();
			super.onPreExecute();
		}

		@Override
		public String doInBackground(String... params) {

			mTaskUrl = params[0];
			Tools.downImageMap.put(mTaskUrl, this);
			InputStream stream = null;
//			URL imageUrl=null;
			Bitmap bmp = null;

			try {
//				imageUrl = new URL(mTaskUrl);
				try {
//					stream = imageUrl.openStream();
//					bmp = BitmapFactory.decodeStream(stream);
					
					Net net = new Net();
					byte[] data = net.downloadResource(mContext, mTaskUrl);
					
					
					
					bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
					try {
						if(bmp != null){
								mBmp = bmp;
								MyApp.getInstance().getImageCache().put(mTaskUrl, bmp);
								if(isLog)
								Log.d(MyApp.TAG, "Image cached "+mTaskUrl);
							
						} else {
							if(isLog)
							Log.w(MyApp.TAG, "Failed to cache "+mTaskUrl);
						}
					} catch (NullPointerException e) {
						if(isLog)
						Log.w(MyApp.TAG, "Failed to cache "+mTaskUrl);
					}
				} catch (Exception e) {
//					e.printStackTrace();
					if(isLog)
					Log.w(MyApp.TAG, "Couldn't load bitmap from url: " + mTaskUrl);
				} finally {
					try {
						if(stream != null){
							stream.close();
						}
					} catch (IOException e) { }
				}

			} catch (Exception e) {
//				e.printStackTrace();
			}
			return mTaskUrl;
		}

		@Override
		public void onPostExecute(String url) {
			super.onPostExecute(url);
			
			// target url may change while loading
			if(!mTaskUrl.equals(mUrl)){
				
				if(url.contains(HEAD)||url.contains(BODY)||url.contains(HEADONLY)){
					saveBmpToSd(mBmp, url);					
				}
				return;
			}
			
			Bitmap bmp = MyApp.getInstance().getImageCache().get(url);
			if(bmp == null){
				if(isLog)
				Log.w(MyApp.TAG, "Trying again to download " + url);
//				HeadImageView.this.setImageUrl(url);
			} else {
				
				// if image belongs to a list update it only if it's visible
				if(mListView != null){
					if(mPosition < mListView.getFirstVisiblePosition() || mPosition > mListView.getLastVisiblePosition())
						return;
				}
				cancelPrompt();
				if(isRoundedCorner!=0){
				    HeadImageView.this.setImageBitmap(Tools.getRoundedCornerBitmap(bmp,isRoundedCorner));
				}else{
					HeadImageView.this.setImageBitmap(bmp);
				}
				mCurrentlyGrabbedUrl = url;				
				if(url.contains(HEAD)||url.contains(BODY)||url.contains(HEADONLY) ){				
					saveBmpToSd(mBmp, url);
				}	
				Tools.downImageMap.remove(url);

			}
		}

	};
	
	private void saveBmpToSd(Bitmap bm, String url) {
		
		if (bm == null) {			
			return;
		}
		
		if (mCacheSize == 0){			
			return;
		}

		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			if(isLog)
			Log.w(MyApp.TAG, "Low free space on sd, do not cache");
			return;
		}
		String filename = convertUrlToFileName(url);
		String dir = getDirectory(filename);
		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
//	    	BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
			if(isLog)
			Log.i(MyApp.TAG, "Image saved to sd");

		} catch (FileNotFoundException e) {
			if(isLog)
			Log.w(MyApp.TAG, "FileNotFoundException");

		} catch (IOException e) {
			e.printStackTrace();
			if(isLog)
			Log.w(MyApp.TAG, "IOException");
		}

	}
	
	private String convertUrlToFileName(String url) {
		String filename = url;
		if(filename.contains("_")){
			filename = filename.replace("_", ".");
		}

		filename = filename.replaceAll("http://[^/]*/","");
		filename = filename.replace("/", ".");
		filename = filename.replace(":", ".");
		filename = filename.concat(HEAD_MARKER);
		
		filename = filename.replace("jpg", "dat");
		if(Tools.isNull(imagever)){
	        	imagever="";
	        }
		filename = filename.concat(imagever);

        
		//http://media.95081.com/data/driver/01/08/00/_head.jpg
		// do filename complicated, hard to read by user while using sd

		
		return filename.trim();
	}

	private String getDirectory(String filename) {

		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		String dirPath = extStorageDirectory + "/" + MY_DIR;
		File dirFile = new File(dirPath);
		dirFile.mkdirs();

		dirPath = dirPath + "/head";
		dirFile = new File(dirPath);
		dirFile.mkdirs();

		return dirPath;
	}

	private void updateFileTime(String dir, String fileName) {
		// update time of album large covers		
		if (!fileName.contains(HEAD_MARKER)) {
			return;
		}
		File file = new File(dir, fileName);		
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
		
	}

	private void removeHeadCache(String dirPath, String filename) {

		if (!filename.contains(HEAD_MARKER)) {
			return;

		}

		File dir = new File(dirPath);
		if(dir==null){
			return;
		}
		File[] files = dir.listFiles();

		if (files == null||(files!=null&&files.length== 0)) {
			// possible sd card is not present/cant write
			return;
		}

		int dirSize = 0;

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(HEAD_MARKER)) {
				dirSize += files[i].length();
			}
		}

		
		
		if (dirSize > mCacheSize * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			if(isLog)
			Log.i(MyApp.TAG, "Clear some album covers cache files ");
			for (int i = 0; i < removeFactor; i++) {
				if (files[i].getName().contains(HEAD_MARKER)) {
					files[i].delete();				
				}
			}
		}

	}

	private void removeCoversCache(String dirPath, String filename) {

		if (filename.contains(HEAD_MARKER)) {
			return;
		}

		File file = new File(dirPath, filename);
		if (file.lastModified() != 0
				&& System.currentTimeMillis() - file.lastModified() > mTimeDiff) {
			
			if(isLog)			
			Log.i(MyApp.TAG, "Clear some album or radio thumbnail cache files ");
			file.delete();
		}

	}

	private void clearCache() {
		
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		String dirPath = extStorageDirectory + "/" + MY_DIR + "/head";
		File dir = new File(dirPath);
		File[] files = dir.listFiles();

		if (files == null) {
			// possible that sd card is not present/can't write
			return;
		}

		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		
	}

	private int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;

		return (int) sdFreeMB;
	}
	
	public void updateCacheSize() {
		
		mPrevCacheSize = mCacheSize;		
		mCacheSize = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(mContext).getString(
						"cache_option", "100"));
		
		if(mPrevCacheSize!= 0 && mCacheSize == 0 ){
		//do it only once after changing mCacheSize value to 0
			clearCache();
		}

	}

	private void cancelPrompt(){
		
		if(mProgress!=null){
			mProgress.cancel();
		}
		
		if (mOuttime != null) {
			try {
				mOuttime.cancel();
			} catch (Exception e) {

			}
			mOuttime = null;
		}
	}
	
	private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
       } catch (IOException e) {
    	   e.printStackTrace();
           Log.e(MyApp.TAG, "Error getting bitmap", e);
       }
       return bm;
    } 
}


class FileLastModifSort implements Comparator<File>{

	public int compare(File arg0, File arg1) {
		if (arg0.lastModified() > arg1.lastModified()) {
			return 1;
		} else if (arg0.lastModified() == arg1.lastModified()) {
			return 0;
		} else {
			return -1;
		}

	}

	
	

}
