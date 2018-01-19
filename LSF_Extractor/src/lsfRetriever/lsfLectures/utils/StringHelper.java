package lsfRetriever.lsfLectures.utils;


public class StringHelper {

    /**
     * Removes colons and whitespaces as indicated by the {@link Character#isSpaceChar(char)} method.
     *
     * @param s the string whose whitespaces will be removed
     * @return the string without the whitespaces
     */
    public static String removeWhitespacesAndColons(String s) {
        StringBuilder toReturn = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!Character.isSpaceChar(c) && c != ':') {
                toReturn.append(c);
            }
        }
        return toReturn.toString();
    }

    /**
     * Removes whitespaces as indicated by the {@link Character#isSpaceChar(char)} method.
     *
     * @param s the string whose whitespaces will be removed
     * @return the string without the whitespaces
     */
    public static String removeWhitespaces(String s) {
        StringBuilder toReturn = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!Character.isSpaceChar(c)) {
                toReturn.append(c);
            }
        }
        return toReturn.toString();
    }

    /**
     * Replaces the unicode spaces of the given String with standard ones and trims afterwards.
     */
    public static String trimAndNormalize(String s) {
        return replaceWhiteSpacesAndApostrophesWithStandardOnes(s).trim();
    }

    /**
     * formats a given string to a valid string to be saved in a SQL database.
     */
    public static String formatSQLString(String s) {
        if (s != null) {
            return "'" + replaceWhiteSpacesAndApostrophesWithStandardOnes(s).trim() + "'";
        } else {
            return "'null'";
        }
    }

    /**
     * Replaces colons apostrophes and whitespaces as indicated by the {@link Character#isSpaceChar(char)} method with
     * regular spaces(ascii code 32)
     */
    private static String replaceWhiteSpacesAndApostrophesWithStandardOnes(String s) {
        StringBuilder toReturn = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isSpaceChar(c) || c == '\'') {
                toReturn.append(' ');
            } else {
                toReturn.append(c);
            }
        }
        return toReturn.toString();
    }
}
