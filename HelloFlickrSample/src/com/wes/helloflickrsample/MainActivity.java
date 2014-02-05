package com.wes.helloflickrsample;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Query;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {

	private GridView gridView;
	private GridAdapter gridAdapter;
	private static final String BASE_URL = "http://api.flickr.com/services/";
	private static final String METHOD = "flickr.interestingness.getList";
	private static final String API_KEY = "1eec2861941ba4c2a13c516116ce30b5";
	private static final String FORMAT = "json";
	private static final int NON_JSON_CALLBACK = 1;
	private final String FLICKR_URL = String.format("%s/rest?method=%s&api_key=%s&format=%s&nojsoncallback=%s",
			BASE_URL, METHOD, API_KEY, FORMAT, NON_JSON_CALLBACK);

	private static final Gson GSON = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onResume() {
		super.onResume();
		gridView = (GridView) findViewById(R.id.results);
		gridAdapter = new GridAdapter();
		gridView.setAdapter(gridAdapter);

		Button getImages = (Button) findViewById(R.id.getImages);
		if (getImages != null)
			getImages.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// you can use an async task or thread or the thread with
					// okhttp and gson

					// new GetImageUrlsWithTask().execute("");
					// getImageUrlsWithThread();
					//getImageUrlsWithOKHttpAndGson();
					getImageUrlsWithRetrofit();
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class GetImageUrlsWithTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (gridAdapter != null)
				gridAdapter.clearFlickrImages();
		}

		@Override
		protected String doInBackground(String... params) {
			String responseString = null;
			try {
				responseString = getHttpResponse();
			} catch (final Exception ex) {
				Log.d("MainActivity with Task", ex.toString());
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			try {
				parseAndStoreResponse(response);
			} catch (final Exception ex) {
				Log.d("MainActivity with Task", ex.toString());
			}
		}
	}

	private void getImageUrlsWithThread() {
		if (gridAdapter != null)
			gridAdapter.clearFlickrImages();

		new Thread() {
			@Override
			public void run() {
				String tmpResponse = null;
				try {
					tmpResponse = getHttpResponse();
				} catch (final Exception ex) {
					Log.d("MainActivity with Thread", ex.toString());
				}

				final String response = tmpResponse;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							parseAndStoreResponse(response);
						} catch (final Exception ex) {
							Log.d("MainActivity with Thread", ex.toString());
						}
					}
				});

				super.run();
			}
		}.start();
	}

	private void getImageUrlsWithOKHttpAndGson() {
		if (gridAdapter != null)
			gridAdapter.clearFlickrImages();

		new Thread() {
			@Override
			public void run() {
				OkHttpClient client = new OkHttpClient();

				// Create request for remote resource.
				HttpURLConnection connection = null;
				InputStream is = null;
				InputStreamReader isr = null;

				try {
					connection = client.open(new URL(FLICKR_URL));
					is = connection.getInputStream();
				} catch (final Exception ex) {
					Log.d("MainActivity with OkHttp", ex.toString());
				}

				if (is == null)
					return;

				isr = new InputStreamReader(is);

				// Deserialize HTTP response to concrete type.
				final FlickrJsonPhotos flickrs = GSON.fromJson(isr, FlickrJsonPhotos.class);

				if (flickrs == null)
					Log.d("MainActivity okhttp", "flickrs is null");
				else if (flickrs.getPhotos() == null)
					Log.d("MainActivity okhttp", "flickrs.getPhotoSSS()==null");
				else if (flickrs.getPhotos().getPhoto() == null)
					Log.d("MainActivity okhttp", "flickrs.getPhotos().getPhoto()==null");
				else {
					Log.d("MainActivity okhttp",
							String.format("flickrs.getPhotos().getPhoto().size()=%s", flickrs.getPhotos().getPhoto()
									.size()));

					for (final FlickrPhoto flickr : flickrs.getPhotos().getPhoto()) {
						if (gridAdapter != null)
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									gridAdapter.addFlickerImage(flickr);
								}
							});
					}
				}
			}
		}.start();
	}

	private void getImageUrlsWithRetrofit() {
		RestAdapter ra = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
		FlickrService s = ra.create(FlickrService.class);
		s.getPhotos(METHOD, API_KEY, FORMAT, NON_JSON_CALLBACK, new Callback<FlickrJsonPhotos>() {

			// @Override
			// public void success(Response arg0, Response arg1) {
			// InputStream is0 = null;
			// InputStream is1 = null;
			// String b0 = null;
			// String b1 = null;
			// try {
			// is0 = arg0.getBody().in();
			// is1 = arg1.getBody().in();
			// b0 = readToEnd(is0);
			// b1 = readToEnd(is1);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// Log.d("WWWW RETROFIT", String.format("SUCCESS 1[%s] 2[%s]", b0,
			// b1));
			// }

			@Override
			public void failure(RetrofitError arg0) {
				Log.d("WWWW RETROFIT", "FAILURE");
			}

			@Override
			public void success(FlickrJsonPhotos flickrs, Response arg1) {

				if (flickrs == null)
					Log.d("MainActivity RETROFIT", "flickrs is null");
				else if (flickrs.getPhotos() == null)
					Log.d("MainActivity RETROFIT", "flickrs.getPhotoSSS()==null");
				else if (flickrs.getPhotos().getPhoto() == null)
					Log.d("MainActivity RETROFIT", "flickrs.getPhotos().getPhoto()==null");
				else {
					Log.d("MainActivity RETROFIT",
							String.format("flickrs.getPhotos().getPhoto().size()=%s", flickrs.getPhotos().getPhoto()
									.size()));

					for (final FlickrPhoto flickr : flickrs.getPhotos().getPhoto()) {
						if (gridAdapter != null)
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									gridAdapter.addFlickerImage(flickr);
								}
							});
					}
				}
			}
		});
	}

	private interface FlickrService {
		@POST("/rest")
		void getPhotos(@Query("method") String method, @Query("api_key") String apiKey, @Query("format") String format,
				@Query("nojsoncallback") int nonJson, Callback<FlickrJsonPhotos> cb);
	}

	private String getHttpResponse() throws IOException {
		String responseString = null;
		DefaultHttpClient client = new DefaultHttpClient();
		Log.d("MainActivity flickr url", FLICKR_URL);
		HttpGet get = new HttpGet(FLICKR_URL);
		HttpResponse resp = client.execute(get);
		HttpEntity entity = resp.getEntity();
		InputStream is = entity.getContent();
		responseString = readToEnd(is);
		is.close();
		Log.d("MainActivity responseString", responseString);
		return responseString;
	}

	private void parseAndStoreResponse(String response) throws JSONException {
		final JSONObject json = new JSONObject(response);
		final JSONArray results = json.getJSONObject("photos").getJSONArray("photo");
		for (int i = 0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			// get the photo here
			String id = result.getString("id");
			String owner = result.getString("owner");
			String secret = result.getString("secret");
			String server = result.getString("server");
			String farm = result.getString("farm");
			String title = result.getString("title");
			int ispublic = result.getInt("ispublic");
			int isfriend = result.getInt("isfriend");
			int isfamily = result.getInt("isfamily");

			if (gridAdapter != null)
				gridAdapter.addFlickerImage(new FlickrPhoto(id, owner, secret, server, farm, title, ispublic, isfriend,
						isfamily));
		}
	}

	private class GridAdapter extends BaseAdapter {

		private List<FlickrPhoto> flickrPhotos = new ArrayList<FlickrPhoto>();

		@Override
		public int getCount() {
			return flickrPhotos.size();
		}

		@Override
		public FlickrPhoto getItem(int position) {
			return flickrPhotos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {

			ImageView imageView = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.flickr_image, parentView, false);
				imageView = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(new ViewHolder(imageView));
			} else {
				ViewHolder vh = (ViewHolder) convertView.getTag();
				imageView = vh.imageView;
			}

			FlickrPhoto flickrPhoto = getItem(position);

			// you can use either Picasso or UrlImageViewHelper

			if (imageView != null && flickrPhoto != null)
				Picasso.with(MainActivity.this) //
						.load(flickrPhoto.getUrl()) //
						.placeholder(R.drawable.ic_launcher) //
						.error(R.drawable.error) //
						// .fit() //
						.into(imageView);

			// if (imageView != null && flickrImage != null)
			// UrlImageViewHelper.setUrlDrawable(imageView,
			// flickrImage.getUrl(), R.drawable.ic_launcher,
			// new UrlImageViewCallback() {
			//
			// @Override
			// public void onLoaded(ImageView arg0, Bitmap arg1, String arg2,
			// boolean arg3) {
			// Log.d("MainActivity UrlImageViewCallback",
			// "Url Image View Callback");
			// }
			// });

			return convertView;
		}

		private class ViewHolder {
			public final ImageView imageView;

			public ViewHolder(ImageView imageView) {
				this.imageView = imageView;
			}
		}

		public void addFlickerImage(FlickrPhoto flickrPhoto) {
			flickrPhotos.add(flickrPhoto);
			notifyDataSetChanged();
		}

		public void clearFlickrImages() {
			flickrPhotos.clear();
			notifyDataSetChanged();
		}

	}

	private class FlickrJsonPhotos {
		private FlickrPhotos photos;

		public FlickrPhotos getPhotos() {
			return photos;
		}

		public void setPhotos(FlickrPhotos photos) {
			this.photos = photos;
		}
	}

	private class FlickrPhotos {
		private int page;
		private int pages;
		private int perpage;
		private int total;

		private List<FlickrPhoto> photo;

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public int getPages() {
			return pages;
		}

		public void setPages(int pages) {
			this.pages = pages;
		}

		public int getPerpage() {
			return perpage;
		}

		public void setPerpage(int perpage) {
			this.perpage = perpage;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public List<FlickrPhoto> getPhoto() {
			return photo;
		}

		public void setPhoto(List<FlickrPhoto> photo) {
			this.photo = photo;
		}
	}

	private class FlickrPhoto {

		public FlickrPhoto() {

		}

		public FlickrPhoto(String id, String owner, String secret, String server, String farm, String title,
				int ispublic, int isfriend, int isfamily) {
			this.id = id;
			this.owner = owner;
			this.secret = secret;
			this.server = server;
			this.farm = farm;
			this.title = title;
			this.ispublic = ispublic;
			this.isfriend = isfriend;
			this.isfamily = isfamily;
		}

		public String getUrl() {
			return String.format("http://farm%s.staticflickr.com/%s/%s_%s.jpg", farm, server, id, secret);
		}

		private String id;
		private String owner;
		private String secret;
		private String server;
		private String farm;
		private String title;
		private int ispublic;
		private int isfriend;
		private int isfamily;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public String getServer() {
			return server;
		}

		public void setServer(String server) {
			this.server = server;
		}

		public String getFarm() {
			return farm;
		}

		public void setFarm(String farm) {
			this.farm = farm;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int isIspublic() {
			return ispublic;
		}

		public void setIspublic(int ispublic) {
			this.ispublic = ispublic;
		}

		public int isIsfriend() {
			return isfriend;
		}

		public void setIsfriend(int isfriend) {
			this.isfriend = isfriend;
		}

		public int isIsfamily() {
			return isfamily;
		}

		public void setIsfamily(int isfamily) {
			this.isfamily = isfamily;
		}
	}

	private static String readToEnd(InputStream input) throws IOException {
		DataInputStream dis = new DataInputStream(input);
		byte[] stuff = new byte[1024];
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		int read = 0;
		while ((read = dis.read(stuff)) != -1) {
			buff.write(stuff, 0, read);
		}

		return new String(buff.toByteArray());
	}

}
