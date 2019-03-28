package eu.goodlike.sub.box.app.launch;

import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static eu.goodlike.sub.box.app.launch.LinkLauncherType.OTHER;

public final class SmartLinkLauncherImpl extends LinkLauncherListenable implements SmartLinkLauncher {

  @Override
  public int nextDefaultLauncher() {
    return launcherIndex.updateAndGet(index -> (index + 1) % launchers.size());
  }

  @Override
  public boolean isAvailable() {
    return launchers.stream().anyMatch(LinkLauncher::isAvailable);
  }

  @Override
  public boolean launch(HttpUrl url) {
    return tryLaunchOnRemainingLaunchers(url) || reportIsUnsupported(url);
  }

  public SmartLinkLauncherImpl(LinkLauncher defaultLauncher, LinkLauncher... fallbackLaunchers) {
    this.launcherIndex = new AtomicInteger(0);
    this.launchers = toLauncherList(defaultLauncher, fallbackLaunchers);
  }

  private final AtomicInteger launcherIndex;
  private final List<LinkLauncher> launchers;

  private List<LinkLauncher> toLauncherList(LinkLauncher defaultLauncher, LinkLauncher[] fallbackLaunchers) {
    List<LinkLauncher> launchers = new ArrayList<>();

    if (defaultLauncher != null)
      launchers.add(defaultLauncher);

    if (fallbackLaunchers != null)
      Stream.of(fallbackLaunchers)
          .filter(Objects::nonNull)
          .forEach(launchers::add);

    return launchers.stream()
        .peek(launcher -> launcher.addListener(this))
        .collect(toImmutableList());
  }

  private boolean tryLaunchOnRemainingLaunchers(HttpUrl url) {
    return launchers.stream()
        .filter(LinkLauncher::isAvailable)
        .skip(launcherIndex.get())
        .anyMatch(launcher -> launcher.launch(url));
  }

  private boolean reportIsUnsupported(HttpUrl url) {
    onUnsupported(OTHER, url);
    return false;
  }

}
