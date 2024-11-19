package dashstar.util;
import org.mindrot.jbcrypt.BCrypt;
public class BCryptUtil {
    public static String hashPassword(String plainPassword) {
        return plainPassword;
//        拓展:return null;
    }
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
//        拓展:return true;
    }
}