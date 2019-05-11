
public class Test {
	public static void main(String[] da) {
		test();
	}
	
	private static void test() {
		try {
			System.out.println("TRY");
			throw new RuntimeException();
		}catch (Exception e) {
			System.out.println("CATCH");
			return;
		}finally {
			System.out.println("FINALLY");
		}
	}
}
