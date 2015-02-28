package uk.co.mentalspace.utils;

public final class StringUtils {
  
  private StringUtils() {
  }
  
  public static final boolean isEmpty(String value) {
    if (null == value || "".equals(value.trim())) {
      return true;
    }
    return false;
  }
}
