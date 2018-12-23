package eu.goodlike.test.mocks.http;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MockHttpResponseParser {

  public List<MockHttpResponse> parseLines() {
    while (lineIterator.hasNext())
      parseLine(getNextLine());

    if (resource != null)
      addNextResponse();

    return ImmutableList.copyOf(responses);
  }

  MockHttpResponseParser(List<String> lines) {
    this.responses = new ArrayList<>();

    this.lineIterator = lines.iterator();
    this.lineNumber = 0;

    this.pathMatcher = null;
    resetResourceParts();
  }

  private final List<MockHttpResponse> responses;

  private final Iterator<String> lineIterator;
  private int lineNumber;

  private boolean lastLineWasMatcher;
  private String pathMatcher;
  private String resource;
  private int statusCode;
  private String reasonPhrase;
  private Map<String, String> queryMatchers;

  private void resetResourceParts() {
    this.lastLineWasMatcher = false;
    this.resource = null;
    this.statusCode = -1;
    this.reasonPhrase = null;
    this.queryMatchers = new HashMap<>();
  }

  private String getNextLine() {
    lineNumber++;
    return lineIterator.next();
  }

  private void parseLine(String line) {
    if (StringUtils.isBlank(line))
      addNextResponse();
    else if (line.startsWith("  "))
      handleNextResourceLine(line.trim());
    else
      addNextPathMatcher(line.trim());
  }

  private void addNextPathMatcher(String pathMatcher) {
    if (lastLineWasMatcher)
      throwError("multiple path matchers in a row without any definitions");

    this.lastLineWasMatcher = true;
    this.pathMatcher = pathMatcher;
  }

  private void handleNextResourceLine(String resourceLine) {
    this.lastLineWasMatcher = false;

    if (pathMatcher == null)
      throwError("no path matcher defined before resource.");

    if (resourceLine.contains("="))
      addNextQuery(StringUtils.substringBefore(resourceLine, "="), StringUtils.substringAfter(resourceLine, "="));
    else if (resource == null)
      this.resource = resourceLine;
    else
      addStatus(resourceLine);
  }

  private void addNextQuery(String queryName, String queryValue) {
    if (queryMatchers.put(queryName, queryValue) != null) {
      throwError("duplicate query name for single definition");
    }
  }

  private void addStatus(String statusCodeAndPhrase) {
    Matcher matcher = STATUS_LINE.matcher(statusCodeAndPhrase);
    if (!matcher.matches())
      throwError("duplicate resource for single definition or invalid status definition");
    if (statusCode > -1)
      throwError("duplicate status for single definition");

    this.statusCode = Integer.parseInt(matcher.group(1));
    this.reasonPhrase = matcher.group(2);
  }

  private void addNextResponse() {
    if (resource == null)
      throwError("path matcher without any resources");

    MockHttpResponse response = new MockHttpResponse(
        pathMatcher,
        queryMatchers,
        resource,
        statusCode < 0 ? 200 : statusCode,
        statusCode < 0 ? "OK" : reasonPhrase
    );
    responses.add(response);

    resetResourceParts();
  }

  private void throwError(String error) {
    throw new AssertionError(invalidLine() + error);
  }

  private String invalidLine() {
    return "Invalid line " + lineNumber + ": ";
  }

  private static final Pattern STATUS_LINE = Pattern.compile("(\\d{3})\\s([\\w\\s]+)");

}
