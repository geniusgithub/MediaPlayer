package com.geniusgithub.mediaplayer.player.music.lrc;

import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ClientConnectionManager;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;


public class HttpManager {
	private static final DefaultHttpClient sClient;
	static {
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 15 * 1000);
		HttpConnectionParams.setSoTimeout(params,15 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 50 * 1024);
		HttpClientParams.setRedirecting(params, false);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schemeRegistry);
		sClient = new DefaultHttpClient(manager, params);
	}

	private HttpManager() {
	}

	public static HttpResponse execute(HttpHead head) throws IOException {
		return sClient.execute(head);
	}

	public static HttpResponse execute(HttpHost host, HttpGet get)
			throws IOException {
		return sClient.execute(host, get);
	}

	public static HttpResponse execute(HttpGet get) throws IOException {
		return sClient.execute(get);
	}

	public static String doGetReturnString(String url) throws ErrorThrowable {

		String data = null;

		// initialize HTTP GET request objects
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse;

		try {
			// execute request
			try {
				httpResponse = sClient.execute(httpGet);
			} catch (UnknownHostException e) {
				ErrorThrowable wsError = new ErrorThrowable();
				wsError.setMessage(e.getLocalizedMessage());
				throw wsError;
			} catch (SocketException e) {
				ErrorThrowable wsError = new ErrorThrowable();
				wsError.setMessage(e.getLocalizedMessage());
				throw wsError;
			}

			// request data
			HttpEntity httpEntity = httpResponse.getEntity();

			if (httpEntity != null) {
				InputStream inputStream = httpEntity.getContent();
				data = convertStreamToString(inputStream);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}

	public static InputStream doGetReturnInputStream(String url, Long begin)
			throws ErrorThrowable {
		if (url == null || TextUtils.isEmpty(url)) {
			return null;
		}		
		InputStream data = null;
		HttpGet httpGet = new HttpGet(url);
		if (begin != null) {
			httpGet.setHeader("Range", "bytes=" + begin.intValue() + "-");
		}
		HttpResponse httpResponse;
		try {
			httpResponse = sClient.execute(httpGet);
		} catch (UnknownHostException e) {
			ErrorThrowable wsError = new ErrorThrowable();
			wsError.setMessage(e.getLocalizedMessage());
			throw wsError;
		} catch (Exception e) {
			ErrorThrowable wsError = new ErrorThrowable();
			wsError.setMessage(e.getLocalizedMessage());
			throw wsError;
		}
		if (httpResponse != null) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				try {
					data = httpEntity.getContent();
				} catch (Exception e) {
					ErrorThrowable wsError = new ErrorThrowable();
					wsError.setMessage(e.getLocalizedMessage());
					throw wsError;
				}
			}
		}
		return data;
	}

	public static InputStream executeGet(String url, Long begin,
			StringBuffer fileSize) throws Exception {
		HttpUriRequest hr = null;
		hr = new HttpGet(url);
		hr.setHeader("X-Target-Encoding", "UTF-8");
		if (begin != null) {
			hr.setHeader("Range", "bytes=" + begin.intValue() + "-");
		}
		HttpClient mHttpClient = new DefaultHttpClient();
		try {
			HttpParams hcp = mHttpClient.getParams();
			if (null != hcp) {
				final int TIMEOUT_MS = 10 * 1000;
				ConnManagerParams.setTimeout(hcp, TIMEOUT_MS);
				HttpConnectionParams.setSoTimeout(hcp, TIMEOUT_MS);
				HttpConnectionParams.setConnectionTimeout(hcp, TIMEOUT_MS);
				ConnRouteParams.setDefaultProxy(hcp, null);
			}
			HttpEntity resEntity = null;
			InputStream is = null;
			for (int i = 0; i < 3; i++) {
				try {
					HttpResponse rsp = mHttpClient.execute(hr);
					resEntity = rsp.getEntity();

					is = resEntity.getContent();
					if (fileSize != null) {
						fileSize = fileSize
								.append(resEntity.getContentLength());

					}
					if (is != null) {
						return is;
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (i + 1 == 3) {
						return null;
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
