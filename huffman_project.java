import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


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
		
		for (int z = 0; z < codes.length; z++) {
			if (counts[z] != 0) {
				System.out.println( z+" "+counts[z]);
			} // (char)i is not in text if counts[i] is 0
				
		}

		System.out.println();
		System.out.println();
		


		StringBuilder result = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			result.append(codes[text.charAt(i)]);
		}
	
		
		encode e = new encode();
		binOut h = new binOut();

		e.codes= codes;
		e.counts=counts;
		h.result=result;
		
		ObjectOutputStream codesOutput = new ObjectOutputStream(new FileOutputStream("objOut2.txt"));
		codesOutput.writeObject(e);
		codesOutput.writeInt(result.length());		
		codesOutput.close();
		BitOutputStream output = new BitOutputStream(new File("objOut2.txt"));
		output.writeBit(h);
		output.close();

//		reading compressed file for decompression 
		System.out.println();
		System.out.println();
		System.out.println("Result: \n"+ result);
		System.out.println();
		System.out.println();
		System.out.println("Result Length:"+ result.length());
		System.out.println();
		System.out.println();
		

		
		//input is encoded file
		ObjectInputStream codesinput = new ObjectInputStream(new FileInputStream("objOut2.txt"));
//	    System.out.println ("Result Length"+ resultLength);
//		System.out.println("Read Object:"+ );
        e = (encode) codesinput.readObject();

		System.out.println("Counts decoded from file:");
		for (int z = 0; z < e.codes.length; z++) {
			if (counts[z] != 0) {
				System.out.print( z+" "+counts[z]+" ");
			} // (char)i is not in text if counts[i] is 0
				
		}
		System.out.println();
		System.out.println();	
		System.out.println("Results decoded from file:"+ h.result);
		System.out.println();
		System.out.println();
		
		
		codesinput.close();


	
//		while(true) {
//	         try {
//	     		int resultLength = codesinput.readInt();
//	     	    System.out.println ("Result Length"+ resultLength);
//	         } catch (EOFException e1) {
//	            System.out.println("");
//	            System.out.println("End of file reached");
//	            break;
//	         }
//	      }
		


		//reads text to binary 
		//don't need this part??
//		BitInputStream BinIn = new BitInputStream("objOut2.txt");
//		System.out.print( "Decoded:");
//		for (int i = 0; i < result.length(); i++) {
//			System.out.print( BinIn.readBits(1));
//		}
//		BinIn.close();

	}

	public static class binOut implements Serializable {  
			StringBuilder result;

			public int length() {
				// TODO Auto-generated method stub
				return 0;
			}

			public huffman_project.binOut charAt(int i) {
				// TODO Auto-generated method stub
				return null;
			} 
	}  
	
	public static class encode implements Serializable {  
		public String result;
		String[] codes ;
		int [] counts; 
	}  
	

	
	//may not need this 
	public static class BitInputStream extends InputStream
	{
	    private InputStream     myInput;
	    private int             myBitCount;
	    private int             myBuffer;
	    private File            myFile;
	    
	    private static final int bmask[] = {
	        0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff,
	        0x1ff,0x3ff,0x7ff,0xfff,0x1fff,0x3fff,0x7fff,0xffff,
	        0x1ffff,0x3ffff,0x7ffff,0xfffff,0x1fffff,0x3fffff,
	        0x7fffff,0xffffff,0x1ffffff,0x3ffffff,0x7ffffff,
	        0xfffffff,0x1fffffff,0x3fffffff,0x7fffffff,0xffffffff
	    };

	    private static final int BITS_PER_BYTE = 8;
	    /**
	     * Construct a bit-at-a-time input stream from a file whose
	     * name is supplied. 
	     * @param filename is the name of the file that will be read.
	     * @throws RuntimeException if filename cannot be opened.
	     */
	    public BitInputStream(String filename)
	    {
	        this(new File(filename));
	    }
	    
	    /**
	     * Construct a bit-at-a-time input stream from <code>file</code>.
	     * @param file is the File that is the source of the input
	     * @throws RuntimeExceptoin if file cannot be opened.
	     */
	    public BitInputStream(File file)
	    {
	        myFile = file;  
	        try {
	            reset();
	        } catch (IOException e) {
	            throw new RuntimeException("could not open file for reading bits "+e);
	        }
	        
	    }
	    
	    /**
	     * Open a bit-at-a-time stream that reads from supplied InputStream. If this
	     * constructor is used the BitInputStream is not reset-able.
	     * @param in is the stream from which bits are read.
	     */
	    public BitInputStream(InputStream in){
	        myInput = in;
	        myFile = null;
	    }
	    
	    /**
	     * Return true if the stream has been initialized from a File and
	     * is thus reset-able. If constructed from an InputStream it is not reset-able.
	     * @return true if stream can be reset (it has been constructed appropriately from a File).
	     */
	    public boolean markSupported(){
	        return myFile != null;
	    }

	    /**
	     * Reset stream to beginning. The implementation creates a new
	     * stream.
	     * @throws IOException if not reset-able (e.g., constructed from InputStream).
	     */
	    
	    public void reset() throws IOException
	    {
	        if (! markSupported()){
	            throw new IOException("not resettable");
	        }
	        try{
	            close();
	            myInput = new BufferedInputStream(new FileInputStream(myFile));
	        }
	        catch (FileNotFoundException fnf){
	            System.err.println("error opening " + myFile.getName() + " " + fnf);
	        }
	        myBuffer = myBitCount = 0;
	    } 

	    /**
	     * Closes the input stream.
	     * @throws RuntimeException if the close fails
	     */
	    
	    public void close()
	    {
	        try{
	            if (myInput != null) {
	                myInput.close();
	            }
	        }
	        catch (java.io.IOException ioe){
	           throw new RuntimeException("error closing bit stream " + ioe);
	        }
	    }

	    /**
	     * Returns the number of bits requested as rightmost bits in
	     * returned value, returns -1 if not enough bits available to
	     * satisfy the request.
	     *
	     * @param howManyBits is the number of bits to read and return
	     * @return the value read, only rightmost <code>howManyBits</code>
	     * are valid, returns -1 if not enough bits left
	     */

	    public int readBits(int howManyBits) throws IOException
	    {
	        int retval = 0;
	        if (myInput == null){
	            return -1;
	        }
	        
	        while (howManyBits > myBitCount){
	            retval |= ( myBuffer << (howManyBits - myBitCount) );
	            howManyBits -= myBitCount;
	            try{
	                if ( (myBuffer = myInput.read()) == -1) {
	                    return -1;
	                }
	            }
	            catch (IOException ioe) {
	                throw new IOException("bitreading trouble "+ioe);
	            }
	            myBitCount = BITS_PER_BYTE;
	        }

	        if (howManyBits > 0){
	            retval |= myBuffer >> (myBitCount - howManyBits);
	            myBuffer &= bmask[myBitCount - howManyBits];
	            myBitCount -= howManyBits;
	        }
	        return retval;
	    }

	    /**
	     * Required by classes extending InputStream, returns
	     * the next byte from this stream as an int value.
	     * @return the next byte from this stream
	     */
	    public int read() throws IOException {
	        return readBits(8);
	    }
	}


	static class BitOutputStream extends ObjectOutputStream  {
		private ObjectOutputStream output;
		// programs statements
		// converts bytes to bit
		int bits; // buffer waits until it is full to write to file
		int resetIndex; // resets when index == 8

		// Constructor
		public BitOutputStream(File file) throws IOException {
			output = new ObjectOutputStream(new FileOutputStream(file, true));
		}

		public void writeBit(huffman_project.binOut h) throws IOException {
			for (int i = 0; i < h.length(); i++)
				writeBit(h.charAt(i));
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