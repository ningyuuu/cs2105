public class IPAddress {
	public static void main(String[] args) {
		String[] addresses = new String[4];
		for (int i=0; i<4; i++) {
			addresses[i] = Integer.toString(Integer.parseInt(args[0].substring(i*8, (i+1)*8), 2));
		}

		System.out.println(String.join(".", addresses));
	}
}