package eu.goodlike.sub.box.app.launch;

import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public final class SmartLinkLauncherImpl extends LinkLauncherListenable implements SmartLinkLauncher {

  @Override
  public int nextDefaultLauncher() {
    return launcherIndex.updateAndGet(index -> (index + 1) % activeLauncherCount());
  }

  @Override
  public boolean isAvailable() {
    return launchers.stream().anyMatch(LinkLauncher::isAvailable);
  }

  @Override
  public void launch(HttpUrl url) {
    launchers.get(launcherIndex.get()).launch(url);
  }

  public SmartLinkLauncherImpl(LinkLauncher defaultLauncher, LinkLauncher... fallbackLaunchers) {
    this.launcherIndex = new AtomicInteger(0);
    this.launchers = toLauncherList(defaultLauncher, fallbackLaunchers);

    addFallbackListeners();
  }

  private final AtomicInteger launcherIndex;
  private final List<LinkLauncher> launchers;

  private List<LinkLauncher> toLauncherList(LinkLauncher defaultLauncher, LinkLauncher[] fallbackLaunchers) {
    List<LinkLauncher> launchers = new ArrayList<>();

    if (defaultLauncher != null && defaultLauncher.isAvailable())
      launchers.add(defaultLauncher);

    if (fallbackLaunchers != null)
      Stream.of(fallbackLaunchers)
          .filter(Objects::nonNull)
          .filter(LinkLauncher::isAvailable)
          .forEach(launchers::add);

    launchers.add(new LinkUnsupported());

    return launchers.stream()
        .peek(launcher -> launcher.addListener(this))
        .collect(toImmutableList());
  }

  private void addFallbackListeners() {
    LinkLauncher launcher = launchers.get(0);
    for (int i = 1; i < launchers.size(); i++)
      launcher = addFallbackLauncherAndReturnIt(launcher, launchers.get(i));
  }

  private LinkLauncher addFallbackLauncherAndReturnIt(LinkLauncher launcher, LinkLauncher fallbackLauncher) {
    launcher.addListener(new LinkLauncherFallback(fallbackLauncher));
    return fallbackLauncher;
  }

  private int activeLauncherCount() {
    return Math.max(1, launchers.size() - 1);
  }

  private static final class LinkLauncherFallback implements LinkLauncherListener {
    @Override
    public void onSuccess(LinkLauncherType type, HttpUrl url) {
      // do nothing
    }

    @Override
    public void onUnsupported(LinkLauncherType type, HttpUrl url) {
      fallbackLauncher.launch(url);
    }

    @Override
    public void onSuddenlyUnsupported(LinkLauncherType type, HttpUrl url, Exception e) {
      fallbackLauncher.launch(url);
    }

    @Override
    public void onMissingPermission(LinkLauncherType type, HttpUrl url, Exception e) {
      fallbackLauncher.launch(url);
    }

    @Override
    public void onIssueWithUrl(LinkLauncherType type, HttpUrl url, Exception e) {
      fallbackLauncher.launch(url);
    }

    @Override
    public void onOtherError(LinkLauncherType type, HttpUrl url, Exception e) {
      fallbackLauncher.launch(url);
    }

    public LinkLauncherFallback(LinkLauncher fallbackLauncher) {
      this.fallbackLauncher = fallbackLauncher;
    }

    private final LinkLauncher fallbackLauncher;
  }

}
