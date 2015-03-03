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
  
  public static final Integer getAsInteger(String str) {
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }
  
  public static final Double getAsDouble(String str) {
    try {
      return Double.parseDouble(str);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }
  
  public static final Long getAsLong(String str) {
    try {
      return Long.parseLong(str);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }
}
