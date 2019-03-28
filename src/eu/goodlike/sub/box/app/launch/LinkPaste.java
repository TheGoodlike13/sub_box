package eu.goodlike.sub.box.app.launch;

import eu.goodlike.sub.box.util.require.Require;
import okhttp3.HttpUrl;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import static eu.goodlike.sub.box.app.launch.LinkLauncherType.CLIPBOARD;
import static eu.goodlike.sub.box.util.require.Require.titled;

public final class LinkPaste extends LinkLauncherListenable {

  @Override
  public boolean isAvailable() {
    return clipboard != null;
  }

  @Override
  public void launch(HttpUrl url) {
    Require.notNull(url, titled("url"));

    if (isAvailable())
      copyToClipboard(url);
    else
      onUnsupported(CLIPBOARD, url);
  }

  public LinkPaste() {
    this(SYSTEM_CLIPBOARD);
  }

  LinkPaste(Clipboard clipboard) {
    this.clipboard = clipboard;
  }

  private final Clipboard clipboard;

  private void copyToClipboard(HttpUrl url) {
    StringSelection clipboardData = new StringSelection(url.toString());
    try {
      clipboard.setContents(clipboardData, clipboardData);
      onSuccess(CLIPBOARD, url);
    } catch (IllegalStateException e) {
      onSuddenlyUnsupported(CLIPBOARD, url, e);
    } catch (Exception e) {
      onOtherError(CLIPBOARD, url, e);
    }
  }

  private static final Clipboard SYSTEM_CLIPBOARD = tryGetClipboard();

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
