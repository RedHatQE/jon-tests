package com.redhat.qe.jon.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: rhatlapa@redhat.com
 */
public class IPAddressValidator {

    private static final Pattern ipv4Pattern;
    private static final Pattern ipv6Pattern;

    private static final String IPV4_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final String IPV6_ADDRESS_PATTERN =
            new StringBuilder()
                    .append("^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))")
                    .append("|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}")
                    .append("|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})")
                    .append("|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})")
                    .append("|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))")
                    .append("|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})")
                    .append("|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))")
                    .append("|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})")
                    .append("|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))")
                    .append("|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})")
                    .append("|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))")
                    .append("|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})")
                    .append("|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))")
                    .append("|(:(((:[0-9A-Fa-f]{1,4}){1,7})")
                    .append("|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$").toString();

    static {
        ipv4Pattern = Pattern.compile(IPV4_ADDRESS_PATTERN);
        ipv6Pattern = Pattern.compile(IPV6_ADDRESS_PATTERN);
    }

    /**
     * Validate IPv4 address with regular expression
     * @param ip IPv4 address for validation
     * @return true valid IPv4 address, false invalid IPv4 address
     */
    public static boolean isValidIPv4Address(final String ip) {
        Matcher matcher = ipv4Pattern.matcher(ip);
        return matcher.matches();
    }

    /**
     * Validate IPv6 address with regular expression
     * @param ip IPv6 address for validation
     * @return true valid IPv6 address, false invalid IPv6 address
     */
    public static boolean isValidIPv6Address(final String ip) {
        Matcher matcher = ipv6Pattern.matcher(ip);
        return matcher.matches();
    }

    public static boolean isValidIPAddress(final String ip) {
        return isValidIPv4Address(ip) || isValidIPv6Address(ip);
    }
}