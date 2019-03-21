package eu.goodlike.sub.box.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public final class RequestDebug implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    System.out.println("HTTP request: " + request.url());
    return chain.proceed(request);
  }

}
