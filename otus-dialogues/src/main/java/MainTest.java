public class MainTest {
    public static void main(String args[])  {

        final Object obj1 = new Object();
        final Object obj2 = new Object();

        System.out.println(obj1.equals(obj2)); // false
        System.out.println(obj1 ==obj2); // false

        final String str1 = new String("string");
        final String str2 = new String("string");

        System.out.println(str1.equals(str2)); // true
        System.out.println(str1 ==str2); // false

        final String str3 = "string";
        final String str4 = "string";
        final String str5 = "str" + "ing";

        System.out.println(str3.equals(str4)); // true
        System.out.println(str3 ==str4); // true
        System.out.println(str3 ==str5); // true

        final Integer i1 = new Integer(1);
        final Integer i2 = new Integer(1);

        System.out.println(i1.equals(i2)); // true
        System.out.println(i1 ==i2); // false

        final Integer i3 = 128;
        final Integer i4 = 128;

        System.out.println(i3.equals(i4)); // true
        System.out.println(i3 ==i4); // false
    }
}
