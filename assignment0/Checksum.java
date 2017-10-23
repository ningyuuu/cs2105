import java.util.zip.CRC32;
import java.nio.file.*;

public class Checksum {
	public static void main(String[] args) {
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
			CRC32 crc = new CRC32();
			crc.update(bytes);
			System.out.println(crc.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
