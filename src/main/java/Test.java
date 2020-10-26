import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        String str = null;
        Map<String,String> map = new HashMap<String, String>();
        map.put("1","1");
        boolean result = CheckObjectUtil.isEmpty(map,str);
        System.out.println(result);
    }
}
