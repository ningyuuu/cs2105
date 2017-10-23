import java.io.*;

public class Copier {
	public static void main(String[] args) throws IOException {
		// using a 8KB buffer
		int bufferLength = 8192;
		byte[] buffer = new byte[bufferLength];
		int numBytes;
		FileInputStream fis = new FileInputStream(args[0]);
		FileOutputStream fos = new FileOutputStream(args[1]);
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		while (true) {
			numBytes = bis.read(buffer);
			if (numBytes == -1) {
				break;
			}
			bos.write(buffer, 0, numBytes);
		}
		bos.close();
		bis.close();
		fis.close();
		fos.close();
	}
}