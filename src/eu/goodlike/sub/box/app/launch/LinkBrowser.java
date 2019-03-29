package eu.goodlike.sub.box.app.launch;

import com.google.common.base.Suppliers;
import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.function.Supplier;

import static eu.goodlike.sub.box.app.launch.LinkLauncherType.BROWSER;
import static eu.goodlike.sub.box.util.require.Require.titled;

public final class LinkBrowser extends LinkLauncherListenable {

  public static boolean isBrowserAvailable() {
    return DESKTOP_WITH_BROWSER_SUPPORT.get() != null;
  }

  @Override
  public boolean isAvailable() {
    return desktop != null;
  }

  @Override
  public boolean launch(HttpUrl url) {
    Require.notNull(url, titled("url"));

    return isAvailable()
        ? tryBrowse(url)
        : reportIsUnsupported(url);
  }

  public LinkBrowser() {
    this(DESKTOP_WITH_BROWSER_SUPPORT.get());
  }

  LinkBrowser(Desktop desktop) {
    this.desktop = desktop;
  }

  private final Desktop desktop;

  private boolean tryBrowse(HttpUrl url) {
    URI uri = url.uri();
    try {
      desktop.browse(uri);
      onSuccess(BROWSER, url);
      return true;
    } catch (UnsupportedOperationException | IOException e) {
      onSuddenlyUnsupported(BROWSER, url, e);
    } catch (SecurityException e) {
      onMissingPermission(BROWSER, url, e);
    } catch (IllegalArgumentException e) {
      onIssueWithUrl(BROWSER, url, e);
    } catch (Exception e) {
      onOtherError(BROWSER, url, e);
    }
    return false;
  }

  private boolean reportIsUnsupported(HttpUrl url) {
    onUnsupported(BROWSER, url);
    return false;
  }

  private static final Supplier<Desktop> DESKTOP_WITH_BROWSER_SUPPORT = Suppliers.memoize(LinkBrowser::tryGetDesktopWithBrowserSupport);

  private static Desktop tryGetDesktopWithBrowserSupport() {
    if (GraphicsEnvironment.isHeadless() || !Desktop.isDesktopSupported())
      return null;

    Desktop desktop;
    try {
      desktop = Desktop.getDesktop();
    } catch (Throwable ignored) {
      return null;
    }

    return desktop.isSupported(Desktop.Action.BROWSE)
        ? desktop
        : null;
  }

}
