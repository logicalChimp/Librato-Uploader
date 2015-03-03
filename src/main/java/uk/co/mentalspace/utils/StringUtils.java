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
    return Integer.parseInt(str);
  }
  
  public static final Double getAsDouble(String str) {
    return Double.parseDouble(str);
  }
  
  public static final Long getAsLong(String str) {
    return Long.parseLong(str);
  }
}
