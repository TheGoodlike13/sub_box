package eu.goodlike.sub.box.app.cmd;

import eu.goodlike.sub.box.app.launch.LinkLauncherListener;
import eu.goodlike.sub.box.app.launch.LinkLauncherType;
import okhttp3.HttpUrl;

public final class MainLauncherListener implements LinkLauncherListener {

  @Override
  public void onSuccess(LinkLauncherType type, HttpUrl url) {
    System.out.println(getSuccessMessage(type, url));
  }

  @Override
  public void onUnsupported(LinkLauncherType type, HttpUrl url) {
    System.out.println(getUnsupportedMessage(type, url));
  }

  @Override
  public void onSuddenlyUnsupported(LinkLauncherType type, HttpUrl url, Exception e) {
    System.out.println(getErrorMessage(type, url, e));
  }

  @Override
  public void onMissingPermission(LinkLauncherType type, HttpUrl url, Exception e) {
    System.out.println(getErrorMessage(type, url, e));
  }

  @Override
  public void onIssueWithUrl(LinkLauncherType type, HttpUrl url, Exception e) {
    System.out.println(getErrorMessage(type, url, e));
  }

  @Override
  public void onOtherError(LinkLauncherType type, HttpUrl url, Exception e) {
    System.out.println(getErrorMessage(type, url, e));
  }

  private String getSuccessMessage(LinkLauncherType type, HttpUrl url) {
    switch (type) {
      case BROWSER: return "Video URL launched in browser.";
      case CLIPBOARD: return "Video URL copied to clipboard.";
      case OTHER: return "Video URL launched via some means.";
    }
    throw new IllegalStateException("Unsupported link type: " + type);
  }

  private String getUnsupportedMessage(LinkLauncherType type, HttpUrl url) {
    switch (type) {
      case BROWSER: return "Launching URL via browser is not supported.";
      case CLIPBOARD: return "Copying URL to clipboard is not supported.";
      case OTHER: return "Could not launch video URL via any means.";
    }
    throw new IllegalStateException("Unsupported link type: " + type);
  }

  private String getErrorMessage(LinkLauncherType type, HttpUrl url, Exception e) {
    switch (type) {
      case BROWSER: return "Could not launch video URL in browser: " + e;
      case CLIPBOARD: return "Could not copy video URL to clipboard: " + e;
      case OTHER: return "Could not launch video URL via some means: " + e;
    }
    throw new IllegalStateException("Unsupported link type: " + type);
  }
  
}
