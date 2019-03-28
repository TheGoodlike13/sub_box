package eu.goodlike.sub.box.app.launch;

import okhttp3.HttpUrl;

public interface LinkLauncherListener {

  void onSuccess(LinkLauncherType type, HttpUrl url);

  void onUnsupported(LinkLauncherType type, HttpUrl url);

  void onSuddenlyUnsupported(LinkLauncherType type, HttpUrl url, Exception e);

  void onMissingPermission(LinkLauncherType type, HttpUrl url, Exception e);

  void onIssueWithUrl(LinkLauncherType type, HttpUrl url, Exception e);

  void onOtherError(LinkLauncherType type, HttpUrl url, Exception e);

}
