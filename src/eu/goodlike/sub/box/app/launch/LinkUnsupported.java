package eu.goodlike.sub.box.app.launch;

import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;

import static eu.goodlike.sub.box.app.launch.LinkLauncherType.OTHER;
import static eu.goodlike.sub.box.util.require.Require.titled;

public final class LinkUnsupported extends LinkLauncherListenable {

  @Override
  public boolean isAvailable() {
    return false;
  }

  @Override
  public void launch(HttpUrl url) {
    Require.notNull(url, titled("url"));

    onUnsupported(OTHER, url);
  }

}
