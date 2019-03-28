package eu.goodlike.sub.box.app.launch;

import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static eu.goodlike.sub.box.util.require.Require.titled;

abstract class LinkLauncherListenable implements LinkLauncher, LinkLauncherListener {

  @Override
  public final void addListener(LinkLauncherListener listener) {
    Require.notNull(listener, titled("listener"));

    listeners.add(listener);
  }

  @Override
  public final void onSuccess(LinkLauncherType type, HttpUrl url) {
    listeners.forEach(listener -> listener.onSuccess(type, url));
  }

  @Override
  public final void onUnsupported(LinkLauncherType type, HttpUrl url) {
    listeners.forEach(listener -> listener.onUnsupported(type, url));
  }

  @Override
  public final void onSuddenlyUnsupported(LinkLauncherType type, HttpUrl url, Exception e) {
    listeners.forEach(listener -> listener.onSuddenlyUnsupported(type, url, e));
  }

  @Override
  public final void onMissingPermission(LinkLauncherType type, HttpUrl url, Exception e) {
    listeners.forEach(listener -> listener.onMissingPermission(type, url, e));
  }

  @Override
  public final void onIssueWithUrl(LinkLauncherType type, HttpUrl url, Exception e) {
    listeners.forEach(listener -> listener.onIssueWithUrl(type, url, e));
  }

  @Override
  public final void onOtherError(LinkLauncherType type, HttpUrl url, Exception e) {
    listeners.forEach(listener -> listener.onOtherError(type, url, e));
  }

  private final List<LinkLauncherListener> listeners = new CopyOnWriteArrayList<>();

  private void onEvent(Consumer<LinkLauncherListener> event) {
    listeners.forEach(event);
  }

}
