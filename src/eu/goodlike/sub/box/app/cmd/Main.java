package eu.goodlike.sub.box.app.cmd;

import eu.goodlike.sub.box.app.launch.LinkBrowser;
import eu.goodlike.sub.box.app.launch.LinkPaste;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Scanner;

public final class Main {

  public static void main(String... args) throws IOException {
    printIntroduction();

    printDefaultBrowserBehavior();

    try (CmdApplication application = new CmdApplication(new MainProfile())) {
      performApplicationLoop(application);
    }

    System.out.println("Bye!");
  }

  private static final Scanner INPUT_READER = new Scanner(System.in);

  private static void performApplicationLoop(CmdApplication application) {
    String input;
    while ((input = readInput()) != null)
      application.interpretInputAndPerformAdequateResponse(input);
  }

  private static String readInput() {
    System.out.println();
    System.out.print("Query: ");
    return StringUtils.trimToNull(INPUT_READER.nextLine());
  }

  private static void printDefaultBrowserBehavior() {
    System.out.println();

    if (LinkBrowser.isBrowserAvailable())
      System.out.println("Launching over browser supported.");
    else if (LinkPaste.isClipboardAvailable())
      System.out.println("Cannot launch over browser. Using clipboard instead.");
    else
      System.out.println("Cannot launch over browser or use clipboard. Launching not available.");
  }

  private static void printIntroduction() {
    System.out.println("Supported queries:");
    System.out.println();
    System.out.println("!max=<integer>");
    System.out.println("  Example: !max=50");
    System.out.println("  Sets max results for follow-up searches. Value will be constrained: 1 <= max <= 50");
    System.out.println();
    System.out.println("!b");
    System.out.println("  Flip browser behavior.");
    System.out.println("  If no browser is available, this option does nothing. All launches go to clipboard.");
    System.out.println("  If browser is available, switches to using clipboard instead, or back to browser.");
    System.out.println();
    System.out.println("c=<channelSearchQuery>");
    System.out.println("  Example: c=TheGoodlike13");
    System.out.println("  Search for channel, by any related query.");
    System.out.println();
    System.out.println("p=<playlistId>");
    System.out.println("  Example: p=PLh0Ul3zO7LAhXL0wblm4z-uWU4RGxtElv");
    System.out.println("  Retrieve videos in playlist, by id.");
    System.out.println();
    System.out.println("v=<videoId>");
    System.out.println("  Example: v=mNwgepMSn5E");
    System.out.println("  Retrieve video info, by id. Video url is launched.");
    System.out.println();
    System.out.println("cn=<positionNumber>");
    System.out.println("  Example: cn=1");
    System.out.println("  Print n-th channel's uploads. Channel is taken from last search.");
    System.out.println();
    System.out.println("vn=<positionNumber>");
    System.out.println("  Example: vn=1");
    System.out.println("  Launch n-th video. Video is taken from last playlist or channel.");
    System.out.println();
    System.out.println("Empty query will exit the program loop.");
  }

}
