
import java.io.*; 
public class Decompress_a_File {
	public static void main(String[] args) {
		if (args[0] == "") {
			System.out.println("The input file is empty");
			System.out.println("EXIT");
			// Terminate JVM
			System.exit(0);
		}
		if (args[1] == "") {
			System.out.println("The output target file is empty");
			System.out.println("EXIT");
			// Terminate JVM
			System.exit(1);
		}

		try {
			FileInputStream inputFile = new FileInputStream(args[0]);
			String outputResult = readDataFromInputFile(inputFile);
			DataOutputStream output = new DataOutputStream(new FileOutputStream(args[1]));
			output.write(outputResult.getBytes());
			output.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String readDataFromInputFile(FileInputStream inputFile) throws IOException, ClassNotFoundException {


		ObjectInputStream objectInput = new ObjectInputStream(inputFile);
		// Read stored object write under Huffman Codes
		String[] huffmanCodes = (String[]) (objectInput.readObject());

		// Create a string builder to store to binary result
		int sizeOfData = objectInput.readInt();
		StringBuilder sb = new StringBuilder("");
		int inputInt =0;
		// Read inputFile bytes values
		while ((inputInt = inputFile.read()) != -1) {
			// Convert to binary and put it to String
			sb.append(getBinaryStringFromBytesValue(inputInt));
		}

		inputFile.close();
		sb.delete(sizeOfData, sb.length());

		// When we still have to binary input
		StringBuilder result = new StringBuilder();
		while (sb.length() != 0) {
			@SuppressWarnings("unused")
			boolean status = false;	
			for (int i = 0; i < huffmanCodes.length; i++) {
				if ((huffmanCodes[i] != null) && (sb.indexOf(huffmanCodes[i]) == 0)) {
					result.append((char)i);
					sb.delete(0, huffmanCodes[i].length());
					status = true;
					break;
				}
			}
			if (status = false) {
				System.out.println("The data in the input file is not valid");
				System.exit(2);
			}

		}

		return result.toString();
	}
	// Convert Bytes to binary data
	public static String getBinaryStringFromBytesValue(int binaryValue) {
		// Get 256 bits for the value of the binary
		binaryValue = binaryValue % 256;
		//String value of a binary String
		String binaryStringValue = "";
		
		int i = 0;
		// signed right shift of the binaryValue
		int sample = binaryValue >> i;
		
		for (int j = 0; j < 8; j++) {
			// get the binary string value
			binaryStringValue = (sample & 1) + binaryStringValue;
			i++;
			sample = binaryValue >> i;
		}
		return binaryStringValue;
	}

}
