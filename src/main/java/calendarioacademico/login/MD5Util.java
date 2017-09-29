package calendarioacademico.login;

import java.math.BigInteger;
import java.security.MessageDigest;
/**
 *
 * @author cnmoro
 */
public class MD5Util {

    public static String md5Hash(String p) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(p.getBytes(), 0, p.length());
            return (new BigInteger(1, m.digest()).toString(16));
        } catch (Exception e) {
            return null;
        }
    }
}
