package eu.goodlike.sub.box.app.cmd;

import com.google.api.client.http.HttpTransport;
import eu.goodlike.sub.box.app.ApplicationProfile;
import eu.goodlike.sub.box.app.ApplicationUi;
import eu.goodlike.sub.box.app.launch.LinkBrowser;
import eu.goodlike.sub.box.app.launch.LinkLauncher;
import eu.goodlike.sub.box.app.launch.LinkLauncherListener;
import eu.goodlike.sub.box.app.launch.LinkPaste;
import eu.goodlike.sub.box.http.OkHttpTransport;
import eu.goodlike.sub.box.http.RequestDebug;
import eu.goodlike.sub.box.http.YoutubeApiKeyProvider;
import okhttp3.OkHttpClient;

public final class MainProfile implements ApplicationProfile {

  @Override
  public ApplicationUi getUi() {
    return new CmdUi();
  }

  @Override
  public String getApplicationName() {
    return "sub_box";
  }

  @Override
  public HttpTransport getHttpTransport() {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new RequestDebug())
        .addInterceptor(new YoutubeApiKeyProvider(PUBLIC_API_KEY))
        .build();
    return new OkHttpTransport(client);
  }

  @Override
  public LinkLauncher getDefaultLauncher() {
    return new LinkBrowser();
  }

  @Override
  public LinkLauncher getBackupLauncher() {
    return new LinkPaste();
  }

  @Override
  public LinkLauncherListener getLauncherListener() {
    return new MainLauncherListener();
  }

  private static final String PUBLIC_API_KEY = "AIzaSyC7Z0dhCTFFR_0Gt4YpuFjIlmEPFuvqpg8";

}
