package fr.elikia.backend.security;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Utility class used to sanitize user inputs
 * against XSS and malicious HTML content.
 */
public class InputSanitizer {
    /**
     * OWASP HTML Sanitizer policy.

     * FORMATTING -> allows basic text formatting tags (b, i, u, etc.)
     * BLOCKS -> allows block-level elements (p, div, ul, li, etc.)
     * STYLES -> allows safe inline styles (very limited)

     * This configuration is strict enough for backend usage
     * while preventing script injections.
     */
    private static final PolicyFactory POLICY =
            Sanitizers.FORMATTING
                    .and(Sanitizers.BLOCKS)
                    .and(Sanitizers.STYLES);

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class.
     */
    private InputSanitizer() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Sanitizes a string input to remove any potentially malicious HTML or scripts.
     *
     * @param input the raw user input
     * @return a sanitized string safe to persist or process
     */
    public static String sanitize(String input) {

        // If the input is null, return null to avoid NullPointerException
        if (input == null) {
            return null;
        }

        // Trim leading and trailing whitespace
        String trimmedInput = input.trim();

        // Sanitize the input using OWASP policy
        return POLICY.sanitize(trimmedInput);
    }

}
