package eu.goodlike.sub.box.app.launch;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.function.Supplier;

import static eu.goodlike.sub.box.app.launch.LinkLauncherType.CLIPBOARD;
import static eu.goodlike.sub.box.util.require.Require.titled;

public final class LinkPaste extends LinkLauncherListenable {

  public static boolean isClipboardAvailable() {
    return SYSTEM_CLIPBOARD.get() != null;
  }

  @Override
  public boolean isAvailable() {
    return clipboard != null;
  }

  @Override
  public boolean launch(HttpUrl url) {
    Require.notNull(url, titled("url"));

    return isAvailable()
        ? tryCopyToClipboard(url)
        : reportIsUnsupported(url);
  }

  public LinkPaste() {
    this(SYSTEM_CLIPBOARD.get());
  }

  @VisibleForTesting
  LinkPaste(Clipboard clipboard) {
    this.clipboard = clipboard;
  }

  private final Clipboard clipboard;

  private boolean tryCopyToClipboard(HttpUrl url) {
    StringSelection clipboardData = new StringSelection(url.toString());
    try {
      clipboard.setContents(clipboardData, clipboardData);
      onSuccess(CLIPBOARD, url);
      return true;
    } catch (IllegalStateException e) {
      onSuddenlyUnsupported(CLIPBOARD, url, e);
    } catch (Exception e) {
      onOtherError(CLIPBOARD, url, e);
    }
    return false;
  }

  private boolean reportIsUnsupported(HttpUrl url) {
    onUnsupported(CLIPBOARD, url);
    return false;
  }

  private static final Supplier<Clipboard> SYSTEM_CLIPBOARD = Suppliers.memoize(LinkPaste::tryGetClipboard);

  private static Clipboard tryGetClipboard() {
    if (!GraphicsEnvironment.isHeadless()) {
      try {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
      } catch (Throwable ignored) {
      }
    }

    return null;
  }

}
