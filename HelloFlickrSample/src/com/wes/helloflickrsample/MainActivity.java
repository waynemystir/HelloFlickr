package com.wes.helloflickrsample;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.squareup.picasso.Picasso;
import com.wes.helloflickrsample.R;

public class MainActivity extends Activity {

	private GridView gridView;
	private GridAdapter gridAdapter;
	private final String BASE_URL = "http://api.flickr.com/services/rest/";
	private final String METHOD = "flickr.interestingness.getList";
	private final String API_KEY = "1eec2861941ba4c2a13c516116ce30b5";
	private final String FLICKR_URL = String.format("%s?method=%s&api_key=%s&format=%s&nojsoncallback=%s", BASE_URL,
			METHOD, API_KEY, "json", "1");

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
					// you can use an async task or thread
					new GetImageUrlsWithTask().execute("");
					// getImageUrlsWithThread();
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
		new Thread() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (gridAdapter != null)
							gridAdapter.clearFlickrImages();
					}
				});

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
			String farm = result.getString("farm");
			String server = result.getString("server");
			String id = result.getString("id");
			String secret = result.getString("secret");

			String durl = String.format("http://farm%s.staticflickr.com/%s/%s_%s.jpg", farm, server, id, secret);
			if (gridAdapter != null)
				gridAdapter.addFlickerImage(durl);
		}
	}

	private class GridAdapter extends BaseAdapter {

		private List<FlickrImage> flickrImages = new ArrayList<MainActivity.FlickrImage>();

		@Override
		public int getCount() {
			return flickrImages.size();
		}

		@Override
		public FlickrImage getItem(int position) {
			return flickrImages.get(position);
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

			FlickrImage flickrImage = getItem(position);

			// you can use either Picasso or UrlImageViewHelper

			if (imageView != null && flickrImage != null) {
				Picasso.with(MainActivity.this) //
						.load(flickrImage.getUrl()) //
						.placeholder(R.drawable.ic_launcher) //
						.error(R.drawable.error) //
						// .fit() //
						.into(imageView);
			}

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

		public void addFlickerImage(String url) {
			flickrImages.add(new FlickrImage(url));
			notifyDataSetChanged();
		}

		public void clearFlickrImages() {
			flickrImages.clear();
			notifyDataSetChanged();
		}

	}

	private class FlickrImage {

		private String url;

		public FlickrImage(String url) {
			this.setUrl(url);
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
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
