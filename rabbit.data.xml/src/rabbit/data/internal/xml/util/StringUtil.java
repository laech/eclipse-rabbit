package rabbit.data.internal.xml.util;

/**
 * Contains utility methods for java.lang.String
 */
public class StringUtil {

  /**
   * Checks whether the two strings are equal.
   * 
   * @param str1 The first string.
   * @param str2 The second string.
   * @return True if the strings are equal, false otherwise. Also returns true
   *         if both arguments are null, or one of the argument is null and the
   *         other is an empty string.
   */
  public static boolean areEqual(String str1, String str2) {
    return getString(str1).equals(getString(str2));
  }

  /**
   * Gets the string value of the given string.
   * 
   * @param str The string.
   * @return The string itself if the string is not null, returns an empty
   *         String if the string is null.
   */
  public static String getString(String str) {
    return str == null ? "" : str;
  }
}
