import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Compression classes: 
 ***writeBinaryString
 ***readDataInpuStreamConvertToBytes
 ***convertTextIntoArrayOfInteger
 ***BitOutputStream
 ***getCode
 ***assignCode
 ***getHuffmanTree
 ***Tree
 ***Heap
 *
 *
 *Decompression classes:
 ***readDataFromInputFile
 ***getBinaryStringFromBytesValue

 */

	public class huffman_project{
		@SuppressWarnings("unused")
		public static void main(String[] args) {
		
			
			//compression  
			
			String dataInBytes = readDataInpuStreamConvertToBytes(args);
			
			int[] inputASCIIArray = convertTextIntoArrayOfInteger(dataInBytes);

			Tree tree = getHuffmanTree(inputASCIIArray);
			String[] huffmanCodes = getCode(tree.root);

			String binaryResult = writeBinaryString(huffmanCodes, dataInBytes);

			try {
				// Using ObjectOutputStream to write Huffman codes into targetFile
				ObjectOutputStream huffmanOutput = new ObjectOutputStream(new FileOutputStream((args[1])));
				huffmanOutput.writeObject(huffmanCodes);
				huffmanOutput.writeInt(binaryResult.length());
				huffmanOutput.close();
				
				// Using BitOutputStream to write binary codes into targetFile
				BitOutputStream outputBinary = new BitOutputStream(new File((args[1])));
				outputBinary.writeBit(binaryResult.toString());
				outputBinary.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  //end of compression in main
			
			
			//decompression
			if ("output2.txt" == "") {
				System.out.println("The output target file is empty");
				System.out.println("EXIT");
				// Terminate JVM
				System.exit(1);
			}
			
			try {
				FileInputStream inputFile2 = new FileInputStream(args[1]);
				String outputResult = readDataFromInputFile(inputFile2);
				DataOutputStream output = new DataOutputStream(new FileOutputStream("output2.txt"));
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
			}//end of decompression in main


		}
		
		
		//compression methods
		private static String writeBinaryString(String[] codes, String dataInBytes) {
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < dataInBytes.length(); i++) {
				char sample = dataInBytes.charAt(i);
				result.append(codes[sample]);
			}
			return result.toString();
			
		}
		
		@SuppressWarnings("unused")
		private static String readDataInpuStreamConvertToBytes(String [] args) {
	
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
				System.exit(0);
			}

			File inputFile = new File(args[0]);

			if (!inputFile.exists()) {
				System.out.println("The input file does not exist");
				System.exit(2);
			}

			byte[] inputBytes = null;
			try {
				// Get dataInputStream
				FileInputStream fileInputStream = new FileInputStream(inputFile);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

				int sizeOfData = dataInputStream.available();
				inputBytes = new byte[sizeOfData];
				dataInputStream.read(inputBytes);
				dataInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return new String(inputBytes);
		}
		
		

		/*
		 * Assign unique ASCII code number for each input characters Count the Frequency
		 */
		public static int[] convertTextIntoArrayOfInteger(String inputText) {

			int[] integerArray = new int[256];

			for (int i = 0; i < inputText.length(); i++) {
				// get the input char
				char temp = inputText.charAt(i);
				// convert the input char into ASCII code
				// plus 1 with the ASCII code position based on the input character
				// create and assign the frequency of input char
				integerArray[(int) temp]++;
			}
			return integerArray;
		}
		
		static class BitOutputStream {
			private ArrayList<Integer> bits = new ArrayList<>();
			private DataOutputStream output;

			public BitOutputStream(File file) throws FileNotFoundException {
				output = new DataOutputStream(new FileOutputStream(file, true));

			}

			public void writeBit(char bit) throws IOException {
				if (bit == '0') {
					bits.add(0);
				} else {
					bits.add(1);
				}

				if (bits.size() == 8) {
					output.writeByte(getByte());
					bits.clear();
				}
			}

			public void writeBit(String bit) throws IOException {
				for (int i = 0; i < bit.length(); i++) {
					writeBit(bit.charAt(i));
				}
			}

			public void close() throws IOException {
				while (bits.size() != 0) {
					writeBit('0');
				}
				output.close();
			}

			private byte getByte() {
				int sum = 0;
				for (int i = 7, number = 1; i >= 0; i--, number *= 2) {
					sum += bits.get(i) * number;
				}
				return (byte) sum;
			}
		}
		
		

		/*
		 * From the textbook: Introduction to Java Programming, Ninth Edition
		 *
		 * http://www.cs.armstrong.edu/liang/intro9e/index.html
		 *
		 * @author Y. Daniel Liang
		 */

		public static String[] getCode(Tree.Node root) {
			if (root == null)
				return null;
			String[] codes = new String[128];
			assignCode(root, codes);
			return codes;
		}

		/* Recursively get codes to the leaf node */
		private static void assignCode(Tree.Node root, String[] codes) {
			if (root.left != null) {
				root.left.code = root.code + "0";
				assignCode(root.left, codes);

				root.right.code = root.code + "1";
				assignCode(root.right, codes);
			} else {
				codes[(int) root.element] = root.code;
			}
		}

		/** Get a Huffman tree from the codes */
		public static Tree getHuffmanTree(int[] counts) {
			// Create a heap to hold trees
			Heap<Tree> heap = new Heap<>(); // Defined in Listing 24.10
			for (int i = 0; i < counts.length; i++) {
				if (counts[i] > 0)
					heap.add(new Tree(counts[i], (char) i)); // A leaf node tree
			}

			while (heap.getSize() > 1) {
				Tree t1 = heap.remove(); // Remove the smallest weight tree
				Tree t2 = heap.remove(); // Remove the next smallest weight
				heap.add(new Tree(t1, t2)); // Combine two trees
			}

			return heap.remove(); // The final tree
		}

		/** Define a Huffman coding tree */
		public static class Tree implements Comparable<Tree> {
			Node root; // The root of the tree

			/** Create a tree with two subtrees */
			public Tree(Tree t1, Tree t2) {
				root = new Node();
				root.left = t1.root;
				root.right = t2.root;
				root.weight = t1.root.weight + t2.root.weight;
			}

			/** Create a tree containing a leaf node */
			public Tree(int weight, char element) {
				root = new Node(weight, element);
			}

			@Override /** Compare trees based on their weights */
			public int compareTo(Tree t) {
				if (root.weight < t.root.weight) // Purposely reverse the order
					return 1;
				else if (root.weight == t.root.weight)
					return 0;
				else
					return -1;
			}

			public class Node {
				char element; // Stores the character for a leaf node
				int weight; // weight of the subtree rooted at this node
				Node left; // Reference to the left subtree
				Node right; // Reference to the right subtree
				String code = ""; // The code of this node from the root

				/** Create an empty node */
				public Node() {
				}

				/** Create a node with the specified weight and character */
				public Node(int weight, char element) {
					this.weight = weight;
					this.element = element;
				}
			}
		}

		/*
		 * From the textbook: Introduction to Java Programming, Ninth Edition
		 *
		 * http://www.cs.armstrong.edu/liang/intro9e/index.html
		 *
		 * @author Y. Daniel Liang
		 */

		public static class Heap<E extends Comparable<E>> {

			private java.util.ArrayList<E> list = new java.util.ArrayList<E>();

			/**
			 * Create a default heap
			 */
			public Heap() {
			}

			/**
			 * Create a heap from an array of objects
			 */
			public Heap(E[] objects) {
				for (int i = 0; i < objects.length; i++) {
					add(objects[i]);
				}
			}

			/**
			 * Add a new object into the heap
			 */
			public void add(E newObject) {
				list.add(newObject); // Append to the heap
				int currentIndex = list.size() - 1; // The index of the last node

				while (currentIndex > 0) {
					int parentIndex = (currentIndex - 1) / 2;
					// Swap if the current object is greater than its parent
					if (list.get(currentIndex).compareTo(list.get(parentIndex)) > 0) {
						E temp = list.get(currentIndex);
						list.set(currentIndex, list.get(parentIndex));
						list.set(parentIndex, temp);
					} else {
						break; // the tree is a heap now
					}
					currentIndex = parentIndex;
				}
			}

			/**
			 * Remove the root from the heap
			 */
			public E remove() {
				if (list.size() == 0) {
					return null;
				}

				E removedObject = list.get(0);
				list.set(0, list.get(list.size() - 1));
				list.remove(list.size() - 1);

				int currentIndex = 0;
				while (currentIndex < list.size()) {
					int leftChildIndex = 2 * currentIndex + 1;
					int rightChildIndex = 2 * currentIndex + 2;

					// Find the maximum between two children
					if (leftChildIndex >= list.size()) {
						break; // The tree is a heap
					}
					int maxIndex = leftChildIndex;
					if (rightChildIndex < list.size()) {
						if (list.get(maxIndex).compareTo(list.get(rightChildIndex)) < 0) {
							maxIndex = rightChildIndex;
						}
					}

					// Swap if the current node is less than the maximum
					if (list.get(currentIndex).compareTo(list.get(maxIndex)) < 0) {
						E temp = list.get(maxIndex);
						list.set(maxIndex, list.get(currentIndex));
						list.set(currentIndex, temp);
						currentIndex = maxIndex;
					} else {
						break; // The tree is a heap
					}
				}

				return removedObject;
			}

			/**
			 * Get the number of nodes in the tree
			 */
			public int getSize() {
				return list.size();
			}
		}
		
		
		
		
		
		
		//DECOMPRESSION CLASSES
		
		
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
	}
