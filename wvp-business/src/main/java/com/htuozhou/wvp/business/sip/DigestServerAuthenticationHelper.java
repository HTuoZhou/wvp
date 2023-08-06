package com.htuozhou.wvp.business.sip;

import gov.nist.core.InternalErrorHandler;

import javax.sip.address.URI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

/**
 * @author hanzai
 * @date 2023/4/12
 */
public class DigestServerAuthenticationHelper {

    public static final String DEFAULT_ALGORITHM = "MD5";
    public static final String DEFAULT_SCHEME = "Digest";
    private static final char[] toHex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private MessageDigest messageDigest = MessageDigest.getInstance("MD5");

    public DigestServerAuthenticationHelper() throws NoSuchAlgorithmException {
    }

    public static String toHexString(byte[] b) {
        int pos = 0;
        char[] c = new char[b.length * 2];

        for (int i = 0; i < b.length; ++i) {
            c[pos++] = toHex[b[i] >> 4 & 15];
            c[pos++] = toHex[b[i] & 15];
        }

        return new String(c);
    }

    private String generateNonce() {
        Date date = new Date();
        long time = date.getTime();
        Random rand = new Random();
        long pad = rand.nextLong();
        String nonceString = (new Long(time)).toString() + (new Long(pad)).toString();
        byte[] mdbytes = this.messageDigest.digest(nonceString.getBytes());
        return toHexString(mdbytes);
    }

    public void generateChallenge(HeaderFactory headerFactory, Response response, String realm) {
        try {
            WWWAuthenticateHeader wwwAuthenticateHeader = headerFactory.createWWWAuthenticateHeader("Digest");
            wwwAuthenticateHeader.setParameter("realm", realm);
            wwwAuthenticateHeader.setParameter("nonce", this.generateNonce());
            wwwAuthenticateHeader.setParameter("opaque", "");
            wwwAuthenticateHeader.setParameter("stale", "FALSE");
            wwwAuthenticateHeader.setParameter("algorithm", "MD5");
            response.setHeader(wwwAuthenticateHeader);
        } catch (Exception var5) {
            InternalErrorHandler.handleException(var5);
        }

    }

    public boolean doAuthenticateHashedPassword(Request request, String hashedPassword) {
        AuthorizationHeader authHeader = (AuthorizationHeader) request.getHeader("Authorization");
        if (authHeader == null) {
            return false;
        } else {
            String realm = authHeader.getRealm();
            String username = authHeader.getUsername();
            if (username != null && realm != null) {
                String nonce = authHeader.getNonce();
                URI uri = authHeader.getURI();
                if (uri == null) {
                    return false;
                } else {
                    String A2 = request.getMethod().toUpperCase() + ":" + uri.toString();
                    byte[] mdbytes = this.messageDigest.digest(A2.getBytes());
                    String HA2 = toHexString(mdbytes);
                    String cnonce = authHeader.getCNonce();
                    String KD = hashedPassword + ":" + nonce;
                    if (cnonce != null) {
                        KD = KD + ":" + cnonce;
                    }

                    KD = KD + ":" + HA2;
                    mdbytes = this.messageDigest.digest(KD.getBytes());
                    String mdString = toHexString(mdbytes);
                    String response = authHeader.getResponse();
                    return mdString.equals(response);
                }
            } else {
                return false;
            }
        }
    }

    public boolean doAuthenticatePlainTextPassword(Request request, String pass) {
        AuthorizationHeader authHeader = (AuthorizationHeader) request.getHeader("Authorization");
        if (authHeader == null) {
            return false;
        } else {
            String realm = authHeader.getRealm();
            String username = authHeader.getUsername();
            if (username != null && realm != null) {
                String nonce = authHeader.getNonce();
                URI uri = authHeader.getURI();
                if (uri == null) {
                    return false;
                } else {
                    String A1 = username + ":" + realm + ":" + pass;
                    String A2 = request.getMethod().toUpperCase() + ":" + uri.toString();
                    byte[] mdbytes = this.messageDigest.digest(A1.getBytes());
                    String HA1 = toHexString(mdbytes);
                    mdbytes = this.messageDigest.digest(A2.getBytes());
                    String HA2 = toHexString(mdbytes);
                    String cnonce = authHeader.getCNonce();
                    String KD = HA1 + ":" + nonce;
                    if (cnonce != null) {
                        KD = KD + ":" + cnonce;
                    }

                    KD = KD + ":" + HA2;
                    mdbytes = this.messageDigest.digest(KD.getBytes());
                    String mdString = toHexString(mdbytes);
                    String response = authHeader.getResponse();
                    return mdString.equals(response);
                }
            } else {
                return false;
            }
        }
    }

}
