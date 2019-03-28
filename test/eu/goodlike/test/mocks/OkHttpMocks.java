package eu.goodlike.test.mocks;

import okhttp3.*;

public final class OkHttpMocks {

  public static Response.Builder basicResponse() {
    return new Response.Builder()
        .request(basicRequest().build())
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK");
  }

  public static Request.Builder basicRequest() {
    return new Request.Builder().url(basicUrlString());
  }

  public static ResponseBody toBody(String text) {
    return ResponseBody.create(MediaType.get("text/plain"), text);
  }

  public static String basicUrlString() {
    return "https://www.google.com/";
  }

  public static HttpUrl basicUrl() {
    return HttpUrl.get(basicUrlString());
  }

  private OkHttpMocks() {
    throw new AssertionError("Do not instantiate! Use static fields/methods!");
  }

}
