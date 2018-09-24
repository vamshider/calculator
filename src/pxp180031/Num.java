// Starter code for lp1.
// Version 1.0 (8:00 PM, Wed, Sep 5).

package pxp180031;

public class Num implements Comparable<Num> {

  static long defaultBase = 100; // Change as needed
  long base = defaultBase; // Change as needed
  long[] arr; // array to store arbitrarily large integers
  boolean isNegative; // boolean flag to represent negative numbers
  int len; // actual number of elements of array that are used; number is stored in
  // arr[0..len-1]
  int count = 0;

  // Frequently used values
  private static final Num ONE = new Num(1);
  private static final Num ZERO = new Num(0);

  public Num(String s, long base) {
    this.base = base;
    int strLen = s.length();
    int index = strLen;
    int count = 0;
    isNegative = false;
    int baseSize = baseSize(base);
    int totalChunks = (int) Math.ceil(strLen * 1.0 / baseSize);
    arr = new long[totalChunks];
    len = totalChunks;
    // System.out.println(count + " " + baseSize + " " + index + " " + totalChunks + " " + len + " " + strLen);
    System.out.println(s);
    // TODO: Need to handle negative signs in input strings
    // TODO: Handle bases that are not multiples of 10
    while (count < totalChunks) {
      int start = index / baseSize > 0 ? (index - baseSize) : 0;
      int end = index;
      arr[count++] = Long.parseLong(s.substring(start, end));
      index = index - baseSize;
    }
  }

  public Num(String s) {
    this(s, defaultBase);
  }

  public Num(long x) {
    this(String.valueOf(x), defaultBase);
  }

  public Num(long x, long base) {
    this(String.valueOf(x), base);
  }

  /**
   * Create Num object if we get array of long objects
   * Assumption - all elems in array are of give base
   * * For internal use
   * @param arr
   * @param base
   */
  private Num(long[] arr, long base) {
    this.arr = arr;
    this.len = arr.length;
    this.base = base;
  }

  private static int baseSize(long base) {
    int intBase = (int) base;
    int baseSize = (int) Math.log10(intBase);
    return baseSize;
  }

  /**
   * TODO: See if this can be removed!
   * @param arr
   * @param base
   * @return
   */
  private static String arrayToString(long[] arr, long base) {
    if (arr.length == 0)
      return null;
    StringBuilder str = new StringBuilder();
    int baseSize = baseSize(base);

    for (int i = 0, len = arr.length; i < len; i++) {
      str.insert(0, String.format("%0" + baseSize + "d", arr[i]));
    }

    return str.toString().replaceFirst("^0+(?!$)", "");
  }

  /**
   * Irrespective of the signs, will just do addition
   * Created only to remove duplicated code, to handle cases 
   * where subtraction will need addition in actual
   * @param a
   * @param b
   * @return Num
   */
  private static Num unsignedAdd(Num a, Num b) {
    long carryOver = 0;
    if (a.base != b.base) {
      System.out.println("Base of two numbers are not same");
      return null;
    }
    int aLen = a.len;
    int bLen = b.len;

    if (aLen == 0)
      return b;
    if (bLen == 0)
      return a;

    int arrLen = Math.max(a.len, b.len) + 1;
    long[] arr = new long[arrLen];
    for (int i = 0; i < arrLen; i++) {
      long sum = carryOver;
      if (i < aLen)
        sum += a.arr[i];
      if (i < bLen)
        sum += b.arr[i];

      carryOver = sum / a.base;
      arr[i] = sum % a.base;
    }

    int length = 0;
    for (int i = arrLen - 1; i >= 0; i--) {
      if (arr[i] > 0) break;
      length++;
    }

    Num result = new Num(arr, a.base);
    result.len = arrLen - length;
    return result;
  }

  public static Num add(Num a, Num b) {
    /**
     * Perform subtraction if both number have opp sign
     * if numbers are of opp signs, xor will give truthy
     * and then call subtract method
     */
    if (a.isNegative ^ b.isNegative) {
      Num sub = unsignedSubtract(a, b);
      sub.isNegative = a.unsignedCompareTo(b) > 0 && a.isNegative;
      return sub;
    } 

    Num result = unsignedAdd(a, b);
    result.isNegative = a.isNegative;
    return result;
  }
  
  /**
   * Irrespective of the signs, will just do subtraction
   * Created only to remove duplicated code, to handle cases 
   * where addition will need subtraction in actual
   * @param a
   * @param b
   * @return Num
   */
  private static Num unsignedSubtract(Num a, Num b) {
    int borrow = 0;
    if (a.base() != b.base()) {
      System.out.println("Base of two numbers are not same");
      return null;
    }

    int aLen = a.len;
    int bLen = b.len;

    int base = (int) a.base;

    if (aLen == 0)
      return b;
    if (bLen == 0)
      return a;

    /**
     * From here on, we can safely assume that two numbers are of different signs
     */
    
     /**
     * Return if they are equal, as they will result to zero
     */
    if (a.compareTo(b) == 0) return new Num(0, a.base);

    /**
     * swap numbers if a < b
     */
    if (a.compareTo(b) < 0) {
      Num temp = a;
      a = b;
      b = temp;
      aLen = a.len;
      bLen = b.len;
    }

    int arrLen = Math.max(a.len, b.len);
    long[] arr = new long[arrLen];

    for (int i = 0; i < arrLen; i++) {
      int sub = 0;
      if (i < aLen) {
        sub += a.arr[i] - borrow;
      }
      if (i < bLen)
        sub -= b.arr[i];
      
      if (sub < 0) {
        sub += base;
        borrow = 1;
      } else borrow = 0;

      arr[i] = sub;
    }
    Num result = new Num(arr, base);

    // if last elem is zero, reduce the length by 1
    int length = 0;
    for (int i = arrLen - 1; i >= 0; i--) {
      if (arr[i] > 0) break;
      length++;
    }

    result.len = arrLen - length;
    return result;
  }

  public static Num subtract(Num a, Num b) {
    /**
     * If numbers are not of same sign, call add method
     * using xor for this
     * a(+) b(-) => a + b => add
     * a(-) b(+) => - (a + b) => add 
     */
    if (a.isNegative ^ b.isNegative) {
      // Calculate sum and then apply sign to the result
      Num addResult = add(a, b);
      // one of them is negative and take that sign
      addResult.isNegative = a.isNegative || b.isNegative;
      return addResult;
    };

    Num result = unsignedSubtract(a, b);
    // we can safely assume that both are either postive or negative
    result.isNegative = a.unsignedCompareTo(b) > 0 && a.isNegative;
    return result;
  }

  /**
   * Utility function for karatsuba multiplication method
   * Assumption - Num and long are in same base
   * @param a
   * @param b
   * @return
   */
  private static Num product(Num a, long b) {
    if (b == 0) return new Num(0, a.base);

    if (b == 1) return a;

    if (a.len == 0) return a;

    int count = 0;
    
    long carryOver = 0;
    long[] arr = new long[a.len + 1];
    while (count < a.len) {
      long sum = a.arr[count] * b + carryOver;
      arr[count] = sum % a.base;
      carryOver = sum / a.base;
      count++;
    }
    if (carryOver > 0) arr[count] = carryOver;
    Num result = new Num(arr, a.base);
    result.isNegative = a.isNegative;
    result.len = carryOver > 0 ? a.len + 1 : a.len;
    return result;
  }

  public static Num product(Num a, Num b) {
    if (a.base != b.base) {
      throw new ArithmeticException();  
    }
    Num result = karatsuba(a, b);
    result.isNegative = a.isNegative ^ b.isNegative;
    result.base = a.base;
    return result;
  }

  /**
   * Utility function for karatsuba multiplication method
   * @param a
   * @param index
   * @param length
   * @return
   */
  private static Num splitter(Num a, int index, int length) {
    if (length < 0) return new Num(0, a.base);

    long[] arr = new long[length];
    for (int i = 0; i < Math.min(a.len, length); i++) {
      arr[i] = a.arr[i + index];
    }
    return new Num(arr, a.base);
  }

  /**
   * Utility function for karatsuba multiplication method
   * Desc - Pad array by k digits to the left
   * @param a
   * @param length
   * @return
   */
  private static Num leftPad(Num a, int length) {
    long[] arr = new long[a.len + length];
    for (int i = 0; i < a.len + length; i++) {
      if (i < length) arr[i] = 0l;
      else arr[i] = a.arr[i - length];
    }
    return new Num(arr, a.base);
  }

  /**
   * https://en.wikipedia.org/wiki/Karatsuba_algorithm
   * Multiplication of two big numbers can be done in nlog2(3)
   * @param a
   * @param b
   * @return
   */
  private static Num karatsuba(Num a, Num b) {
    if (a.len == 1 || b.len == 1) {
      return a.len == 1 ? product(b, a.arr[0]) : product(a, b.arr[0]);
    } else if (a.len == 0 || b.len == 0) return new Num(0, a.base);

    int k = Math.max(a.len, b.len) / 2;

    Num aLow = splitter(a, 0, k);
    Num aHigh = splitter(a, k, a.len - k);

    Num bLow = splitter(b, 0, k);
    Num bHigh = splitter(b, k, b.len - k);

    Num z0 = karatsuba(aLow, bLow);
    Num z1 = karatsuba(unsignedAdd(aLow, aHigh), unsignedAdd(bLow, bHigh));
    Num z2 = karatsuba(aHigh, bHigh);

    // (z2 * 10 ^ (m2 * 2)) + ((z1 - z2 - z0) * 10 ^ m2) + z0
    Num result = unsignedAdd(
      unsignedAdd(
        leftPad(z2, 2 * k), 
        leftPad(unsignedSubtract(z1, unsignedAdd(z2, z0)), k)
      ),
      z0
    );

    return result;
  }

  // Use divide and conquer
  public static Num power(Num a, long n) {
    return null;
  }

  // Use binary search to calculate a/b
  public static Num divide(Num a, Num b) {
    if (a.base != b.base) {
      throw new ArithmeticException();
    }

    // handle divide by zero exception 
    if (b.len == 1 && b.arr[0] == 0) {
      throw new IllegalArgumentException("Dividing by 0");
    }

    // if dividend is less, it will result to zero
    if (a.unsignedCompareTo(b) < 0) return new Num(0, a.base);
    
    // if both are same, it results to one and choose sign accordingly
    if (a.unsignedCompareTo(b) == 0) {
      Num result = new Num(1);
      result.isNegative = a.isNegative ^ b.isNegative;
      return result;
    }

    Num low = new Num(1, a.base);
    Num high = a;

    Num result = binarySearch(low, high, a, b);
    result.isNegative = a.isNegative ^ b.isNegative;
    return result;
  }

	private static Num binarySearch(Num low, Num high, Num x, Num y) {
    Num mid = unsignedAdd(low, high).by2();
		Num prod = product(mid, y);
		Num right = unsignedAdd(prod, y);
		int leftSide = prod.unsignedCompareTo(x);
    int rightSide = x.unsignedCompareTo(right);

    Num ONE = new Num(1, x.base);
		if (leftSide > 0) {
			return binarySearch(low, unsignedSubtract(mid, ONE), x, y);
		} else if (leftSide <= 0 && rightSide < 0) {
			return mid;
		} else {
			return binarySearch(unsignedAdd(mid, ONE), high, x, y);
		}
	}

  // return a%b
  public static Num mod(Num a, Num b) {
    return null;
  }

  // Use binary search
  public static Num squareRoot(Num a) {
    return null;
  }

  // Utility functions
  // compare "this" to "other": return +1 if this is greater, 0 if equal, -1
  // otherwise
  public int compareTo(Num other) {
    if (this.base() != other.base()) {
      throw new ArithmeticException();
    }

    if (this.isNegative && !other.isNegative) return -1;

    if (!this.isNegative && other.isNegative) return 1;
    
    return this.unsignedCompareTo(other);
  }

  /**
   * compares just two numbers and ignores their signs
   * @param other
   * @return
   */
  private int unsignedCompareTo(Num other) {
    if (this.base() != other.base()) {
      throw new ArithmeticException();
    }

    if (this.len > other.len) return 1;

    if (this.len < other.len) return -1;
    
    // compare from last index
    int pos = this.len - 1;
    
    while (pos >= 0 && this.arr[pos] == other.arr[pos]) {
      pos--;
    }
    
    // if both are same till end return 0
    if (pos == -1) return 0;

    return this.arr[pos] > other.arr[pos] ? 1 : -1;
  }

  // Output using the format "base: elements of list ..."
  // For example, if base=100, and the number stored corresponds to 10965,
  // then the output is "100: 65 9 1"
  public void printList() {
    System.out.print(base + ": ");
    for (int i = 0, len = this.len; i < len; i++) {
      System.out.print(arr[i] + " ");
    }
    System.out.println();
  }

  // Return number to a string in base 10
  public String toString() {
    return arrayToString(this.arr, this.base);
  }

  public long base() {
    return base;
  }

  // Return number equal to "this" number, in base=newBase
  public Num convertBase(int newBase) {
    return null;
  }

  /**
   * Utility function for division by 2
   * Desc - Shifts numbers by k digits to right
   * @param a
   * @param length
   * @return
   */
  private static long[] shift(long[] a, int count) {
    long[] shiftedArray = new long[a.length - count];
    for (int i = 0; i < a.length - count; i++) {
      shiftedArray[i] = a[count + i];
    }
    return shiftedArray;
  }

  // Evaluate an expression in postfix and return resulting number
  // Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
  // a number: [1-9][0-9]*. There is no unary minus operator.
  public static Num evaluatePostfix(String[] expr) {
    return null;
  }

  // Evaluate an expression in infix and return resulting number
  // Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
  // a number: [1-9][0-9]*. There is no unary minus operator.
  public static Num evaluateInfix(String[] expr) {
    return null;
  }

  // Divide by 2, for using in binary search
  public Num by2() {
    Num prod = product(this, new Num(this.base / 2, this.base));
    Num result = new Num(shift(prod.arr, 1), this.base);
    return result;
  }

  public static void main(String[] args) {
    Num x = new Num(40772);
    Num y = new Num("896");
    x.printList();
    y.printList();
    Num z = Num.subtract(x, y);
    if (z != null) {
      System.out.print("Subtraction result: ");
      z.printList();
      System.out.println(z.toString());
    }

    x = new Num(30);
    y = new Num("120");
    z = Num.subtract(x, y);
    if (z != null) {
      System.out.print("Subtraction result: ");
      z.printList();
      System.out.println(z.toString());
    }

    z = Num.add(x, y);
    if (z != null) {
      System.out.print("Addition result: ");
      z.printList();
      System.out.println(z.toString());
    }

    z = Num.product(x, 30);
    if (z != null) {
      System.out.print("Product(Num, long) result: ");
      z.printList();
      System.out.println(z.toString());
    }
    System.out.println("compareTo x,y " + x.compareTo(y));
  }
}
