import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class huffman_project {
	/**
	 * @param args
	 * @throws IOException
	 */
	/**
	 * @param args
	 * @throws IOException
	 */
	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
//		@SuppressWarnings("resource")
//		Scanner input = new Scanner(System.in);
//		System.out.print("Enter a text: ");
//		String text = input.nextLine();

		File sourceFile = new File("sourceFile.txt");
		if (!sourceFile.exists()) {
			System.out.println("File 'sourceFile.txt' does not exist");
			System.exit(2);
		}
		System.out.printf("%-15s%-15s%-15s%-15s\n", "ASCII Code", "Character", "Frequency", "Code");

		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
		int size = input.available();
		byte[] b = new byte[size];
		input.read(b);
		input.close();
		String text = new String(b);

		int[] counts = getCharacterFrequency(text);
		Tree tree = getHuffmanTree(counts);
		String[] codes = getCode(tree.root);
	   

		for (int z = 0; z < codes.length; z++) {
			if (counts[z] != 0) {
				System.out.printf("%-15d%-15s%-15d%-15s\n", z, (char) z + "", counts[z], codes[z]);
			} // (char)i is not in text if counts[i] is 0
				
		}

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			result.append(codes[text.charAt(i)]);
		}
	
		
		ObjectOutputStream codesOutput = new ObjectOutputStream(new FileOutputStream("objOut2.txt"));
		codesOutput.writeObject(codes);
		codesOutput.writeInt(result.length());
		codesOutput.close();
		

		
		BitOutputStream output = new BitOutputStream(new File("objOut2.txt"));
		output.writeBit(result.toString());
		output.close();
//		System.out.print(result);
//		reading compressed file for decompression 
//		System.out.println(result);
		System.out.println();
		System.out.println();

		
		
		
		//input is encoded file
		ObjectInputStream codesinput = new ObjectInputStream(new FileInputStream("objOut2.txt"));
	      System.out.println ("READ"+ codesinput.readObject());

		codesinput.close();
		FileInputStream input2 = new FileInputStream("objOut2.txt");
		int size2 = input2.available();
		byte[] b2 = new byte[size2];
		input2.read(b2);

		input2.close();
		String text2 = new String(b2);

		BitInputStream BinIn = new BitInputStream("objOut2.txt");
		System.out.println(BinIn.readBit());
		BinIn.close();
	}


	public static class BitInputStream {
	    private FileInputStream input;
	    private int digits;     // next set of digits (buffer)
	    private int numDigits;  // how many digits from buffer have been used

	    private static final int BYTE_SIZE = 8;  // digits per byte

	    // pre : given file name is legal
	    // post: creates a BitInputStream reading input from the file
	    public BitInputStream(String file) {
	        try {
	            input = new FileInputStream(file);
	        } catch (IOException e) {
	            throw new RuntimeException(e.toString());
	        }
	        nextByte();
	    }

	    // post: reads next bit from input (-1 if at end of file)
	    public int readBit() {
	        // if at eof, return -1
	        if (digits == -1)
	            return -1;
	        int result = digits % 2;
	        digits /= 2;
	        numDigits++;
	        if (numDigits == BYTE_SIZE)
	            nextByte();
	        return result;
	    }

	    // post: refreshes the internal buffer with the next BYTE_SIZE bits
	    private void nextByte() {
	        try {
	            digits = input.read();
	        } catch (IOException e) {
	            throw new RuntimeException(e.toString());
	        }
	        numDigits = 0;
	    }

	    // post: input is closed
	    public void close() {
	        try {
	            input.close();
	        } catch (IOException e) {
	            throw new RuntimeException(e.toString());
	        }
	    }

	    // included to ensure that the stream is closed
	    protected void finalize() {
	        close();
	    }
	}

	static class BitOutputStream {
		private DataOutputStream output;
		// programs statements
		// converts bytes to bit
		int bits; // buffer waits until it is full to write to file
		int resetIndex; // resets when index == 8

		// Constructor
		public BitOutputStream(File file) throws FileNotFoundException {
			output = new DataOutputStream(new FileOutputStream(file, true));

		}

		public void writeBit(String string) throws IOException {
			for (int i = 0; i < string.length(); i++)
				writeBit(string.charAt(i));
		}

		public void writeBit(char bit) throws IOException {
			// Program statements for this method
			bits = bits << 1;

			if (bit == '1')
				bits = bits | 1;

			if (++resetIndex == 8) {
				output.write(bits);
				resetIndex = 0;
			}

		}

		/**
		 * Write the last byte and close the stream. If the last byte is not full,
		 * right-shfit with zeros
		 */
		public void close() throws IOException {
			// Program statements for this method
			if (resetIndex > 0) {
				bits = bits << 8 - resetIndex; // add 0's to end of byte
				output.write(bits);
			}
			output.close(); // This makes use of the close() method for a FileOutputStream object
		}
	}

	/**
	 * Get Huffman codes for the characters This method is called once after a
	 * Huffman tree is built
	 */
	public static String[] getCode(Tree.Node root) {
		if (root == null)
			return null;
		String[] codes = new String[2 * 128];
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
		Heap<Tree> heap = new Heap<Tree>(); // Defined in Listing 24.10
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

	/** Get the frequency of the characters */
	public static int[] getCharacterFrequency(String text) {
		int[] counts = new int[256]; // 256 ASCII characters

		for (int i = 0; i < text.length(); i++)
			counts[(int) text.charAt(i)]++; // Count the character in text

		return counts;
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

		private Node overallRoot;
		public static final int CHAR_MAX = 256;

		public class Node {
			Character ch;
			char element; // Stores the character for a leaf node
			int weight; // weight of the subtree rooted at this node
			Node left; // Reference to the left subtree
			Node right; // Reference to the right subtree
			String code = ""; // The code of this node from the root
			Integer freq;
			public int letter;

			/** Create an empty node */
			public Node() {
			}

			Node(Character ch, Integer freq, int letter) {
				this.ch = ch;
				this.freq = freq;
				this.letter = letter;

			}

			/** Create a node with the specified weight and character */
			public Node(int weight, char element) {
				this.weight = weight;
				this.element = element;

			}
		}

	}

	public static class Heap<E extends Comparable<E>> {
		private java.util.ArrayList<E> list = new java.util.ArrayList<>();

		/** Create a default heap */
		public Heap() {
		}

		/** Create a heap from an array of objects */
		public Heap(E[] objects) {
			for (int i = 0; i < objects.length; i++)
				add(objects[i]);
		}

		/** Add a new object into the heap */
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
				} else
					break; // the tree is a heap now

				currentIndex = parentIndex;
			}
		}

		/** Remove the root from the heap */
		public E remove() {
			if (list.size() == 0)
				return null;

			E removedObject = list.get(0);
			list.set(0, list.get(list.size() - 1));
			list.remove(list.size() - 1);

			int currentIndex = 0;
			while (currentIndex < list.size()) {
				int leftChildIndex = 2 * currentIndex + 1;
				int rightChildIndex = 2 * currentIndex + 2;

				// Find the maximum between two children
				if (leftChildIndex >= list.size())
					break; // The tree is a heap
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
				} else
					break; // The tree is a heap
			}

			return removedObject;
		}

		/** Get the number of nodes in the tree */
		public int getSize() {
			return list.size();
		}

		/** Return true if heap is empty */
		public boolean isEmpty() {
			return list.size() == 0;
		}
	}
}
