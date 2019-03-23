package eu.goodlike.sub.box;

import java.util.stream.Stream;

public interface Subscribable {

  Stream<SubscriptionItem> getCurrentItems();

}
