package eu.goodlike.sub.box.app.launch;

import okhttp3.HttpUrl;

public interface LinkLauncher {

  boolean isAvailable();

  boolean launch(HttpUrl url);

  void addListener(LinkLauncherListener listener);

}
