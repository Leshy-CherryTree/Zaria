/* The following code was generated by JFlex 1.4.1 on 09/05/13 21:27 */

/*
 * Generated on 5/9/13 9:27 PM
 */
package eu.cherrytree.zaria.editor.modes;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import java.io.*;
import java.util.logging.Level;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


@SuppressWarnings({"fallthrough"})
public class ZoneTokenMaker extends AbstractJFlexCTokenMaker {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int EOL_COMMENT = 2;
  public static final int YYINITIAL = 0;
  public static final int MLC = 1;

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\21\1\10\1\0\1\21\1\17\22\0\1\21\1\31\1\15"+
    "\1\20\1\1\1\31\1\31\1\7\2\33\1\23\1\27\1\31\1\27"+
    "\1\25\1\22\1\4\3\16\4\6\2\3\1\43\1\31\1\17\1\31"+
    "\1\17\1\31\1\32\6\5\24\1\1\33\1\11\1\33\1\17\1\2"+
    "\1\0\1\46\1\14\1\45\1\5\1\26\1\40\1\1\1\34\1\41"+
    "\2\1\1\42\1\1\1\47\1\1\1\36\1\1\1\13\1\37\1\35"+
    "\1\12\1\1\1\44\1\24\2\1\1\30\1\17\1\30\1\31\uff81\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\2\1\2\2\1\3\1\4\1\5\1\6\2\1"+
    "\1\7\3\1\1\10\1\1\1\11\1\12\5\11\1\13"+
    "\3\11\1\0\1\14\1\0\2\14\1\3\1\15\1\0"+
    "\1\3\2\5\1\16\1\17\1\20\1\21\1\0\3\1"+
    "\1\22\11\0\1\21\1\0\1\23\1\3\1\24\2\3"+
    "\1\15\1\3\1\5\1\25\1\5\1\0\3\1\11\0"+
    "\1\3\1\5\1\0\1\26\2\0\1\27\2\0\1\30"+
    "\1\0\1\3\1\5\6\0\1\3\1\5\1\31";

  private static int [] zzUnpackAction() {
    int [] result = new int[106];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\50\0\120\0\170\0\240\0\310\0\360\0\u0118"+
    "\0\170\0\u0140\0\u0168\0\u0190\0\u01b8\0\170\0\u01e0\0\u0208"+
    "\0\u0230\0\170\0\u0258\0\u0280\0\170\0\u02a8\0\u02d0\0\u02f8"+
    "\0\u0320\0\u0348\0\170\0\u0370\0\u0398\0\u03c0\0\u03e8\0\u0410"+
    "\0\u01b8\0\u0438\0\u0460\0\u0488\0\170\0\u04b0\0\u04d8\0\u0500"+
    "\0\u0528\0\170\0\170\0\170\0\u0550\0\u0578\0\u05a0\0\u05c8"+
    "\0\u05f0\0\170\0\u0618\0\u0640\0\u0668\0\u0690\0\u06b8\0\u06e0"+
    "\0\u0708\0\u0730\0\u0758\0\u0780\0\u07a8\0\u0460\0\u07d0\0\170"+
    "\0\u07f8\0\u0820\0\u04b0\0\u0848\0\u0870\0\170\0\u0898\0\u08c0"+
    "\0\u08e8\0\u0910\0\u0938\0\u0960\0\u0988\0\u09b0\0\u09d8\0\u0a00"+
    "\0\u0a28\0\u0a50\0\u0a78\0\u0aa0\0\u0ac8\0\u0af0\0\u0b18\0\240"+
    "\0\u0b40\0\u0b68\0\u0b90\0\u0bb8\0\u0be0\0\u0c08\0\u0c30\0\u0c58"+
    "\0\u0c80\0\u0ca8\0\u0cd0\0\u0b90\0\u0cf8\0\u0c08\0\u0d20\0\u0d48"+
    "\0\u0d70\0\170";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[106];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\2\5\1\6\1\7\1\5\1\6\1\10\1\11"+
    "\1\4\3\5\1\12\1\6\2\4\1\13\1\14\1\4"+
    "\1\5\1\15\1\5\1\4\1\16\1\4\1\17\1\16"+
    "\1\5\1\20\2\5\1\21\2\5\1\22\3\5\1\23"+
    "\10\24\1\25\12\24\1\26\10\24\1\27\3\24\1\30"+
    "\3\24\1\31\3\24\10\32\1\33\23\32\1\34\3\32"+
    "\1\35\3\32\1\36\3\32\51\0\6\5\2\0\1\37"+
    "\3\5\1\0\1\5\5\0\1\5\1\0\1\5\5\0"+
    "\7\5\1\0\4\5\3\40\2\6\1\40\1\6\2\0"+
    "\4\40\1\0\1\6\1\0\1\40\3\0\1\40\1\41"+
    "\1\42\3\0\1\40\1\0\7\40\1\0\7\40\2\6"+
    "\1\40\1\6\2\0\4\40\1\0\1\6\1\0\1\40"+
    "\3\0\1\43\1\41\1\42\3\0\1\40\1\0\7\40"+
    "\1\0\4\40\7\44\1\45\1\46\1\47\36\44\10\12"+
    "\1\50\1\51\3\12\1\52\32\12\21\0\1\13\50\0"+
    "\1\53\1\54\27\0\2\55\1\0\1\55\7\0\1\55"+
    "\76\0\1\56\3\0\6\5\2\0\1\37\1\5\1\57"+
    "\1\5\1\0\1\5\5\0\1\5\1\0\1\5\5\0"+
    "\7\5\1\0\4\5\1\0\6\5\2\0\1\37\3\5"+
    "\1\0\1\5\5\0\1\5\1\0\1\5\5\0\7\5"+
    "\1\0\2\5\1\60\1\5\1\0\6\5\2\0\1\37"+
    "\1\61\2\5\1\0\1\5\5\0\1\5\1\0\1\5"+
    "\5\0\7\5\1\0\4\5\10\24\1\0\12\24\1\0"+
    "\10\24\1\0\3\24\1\0\3\24\1\0\3\24\22\0"+
    "\1\62\62\0\1\63\47\0\1\64\3\0\1\65\52\0"+
    "\1\66\3\0\10\32\1\0\23\32\1\0\3\32\1\0"+
    "\3\32\1\0\3\32\35\0\1\67\47\0\1\70\3\0"+
    "\1\71\52\0\1\72\15\0\1\73\35\0\7\40\2\0"+
    "\4\40\1\0\1\40\1\0\1\40\3\0\1\40\1\0"+
    "\1\40\3\0\1\40\1\0\7\40\1\0\7\40\2\74"+
    "\1\40\1\74\2\0\4\40\1\0\1\74\1\0\1\40"+
    "\3\0\1\40\1\0\1\40\1\75\2\0\1\40\1\0"+
    "\7\40\1\0\7\40\4\76\2\0\3\40\1\76\1\0"+
    "\1\76\1\0\1\40\3\0\1\40\1\0\1\76\3\0"+
    "\1\40\1\0\4\40\1\76\2\40\1\0\1\40\2\76"+
    "\1\40\7\77\1\100\1\0\37\77\7\0\1\100\40\0"+
    "\4\77\1\101\1\77\1\102\1\103\1\0\1\44\1\104"+
    "\3\44\1\101\16\77\1\44\2\77\1\44\6\77\1\44"+
    "\11\50\1\105\3\50\1\106\36\50\1\12\1\50\2\12"+
    "\1\0\1\12\1\107\4\12\16\50\1\12\2\50\1\12"+
    "\6\50\1\12\3\40\2\55\1\40\1\55\2\0\4\40"+
    "\1\0\1\55\1\0\1\40\3\0\1\40\1\0\1\42"+
    "\3\0\1\40\1\0\7\40\1\0\4\40\42\0\1\110"+
    "\6\0\6\5\2\0\1\37\1\111\2\5\1\0\1\5"+
    "\5\0\1\5\1\0\1\5\5\0\7\5\1\0\4\5"+
    "\1\0\6\5\2\0\1\37\3\5\1\0\1\5\5\0"+
    "\1\5\1\0\1\5\5\0\6\5\1\112\1\0\4\5"+
    "\1\0\6\5\2\0\1\37\3\5\1\0\1\5\5\0"+
    "\1\5\1\0\1\5\5\0\6\5\1\113\1\0\4\5"+
    "\35\0\1\114\50\0\1\115\53\0\1\116\51\0\1\117"+
    "\40\0\1\120\50\0\1\121\53\0\1\122\51\0\1\123"+
    "\6\0\4\124\5\0\1\124\1\0\1\124\7\0\1\124"+
    "\11\0\1\124\4\0\2\124\1\0\3\40\2\74\1\40"+
    "\1\74\2\0\4\40\1\0\1\74\1\0\1\40\3\0"+
    "\1\40\1\0\1\40\3\0\1\40\1\0\7\40\1\0"+
    "\4\40\3\0\2\74\1\0\1\74\7\0\1\74\31\0"+
    "\7\77\1\45\1\0\43\77\1\102\1\77\1\102\1\100"+
    "\1\0\5\77\1\102\35\77\1\44\1\77\1\44\1\100"+
    "\1\0\5\77\1\44\34\77\4\125\1\45\1\0\3\77"+
    "\1\125\1\77\1\125\7\77\1\125\11\77\1\125\4\77"+
    "\2\125\1\77\10\50\1\0\42\50\4\126\2\50\1\105"+
    "\2\50\1\126\1\106\1\126\7\50\1\126\11\50\1\126"+
    "\4\50\2\126\1\50\46\0\1\127\2\0\6\5\2\0"+
    "\1\37\3\5\1\0\1\5\5\0\1\5\1\0\1\130"+
    "\5\0\7\5\1\0\4\5\1\0\6\5\2\0\1\37"+
    "\3\5\1\0\1\5\5\0\1\5\1\0\1\5\5\0"+
    "\3\5\1\111\3\5\1\0\4\5\1\0\6\5\2\0"+
    "\1\37\3\5\1\0\1\5\5\0\1\5\1\0\1\5"+
    "\5\0\6\5\1\130\1\0\4\5\36\0\1\131\54\0"+
    "\1\132\32\0\1\115\46\0\1\133\60\0\1\134\54\0"+
    "\1\135\32\0\1\121\46\0\1\136\25\0\4\137\5\0"+
    "\1\137\1\0\1\137\7\0\1\137\11\0\1\137\4\0"+
    "\2\137\1\0\3\77\4\140\1\45\1\0\3\77\1\140"+
    "\1\77\1\140\7\77\1\140\11\77\1\140\4\77\2\140"+
    "\1\77\3\50\4\141\2\50\1\105\2\50\1\141\1\106"+
    "\1\141\7\50\1\141\11\50\1\141\4\50\2\141\1\50"+
    "\37\0\1\142\47\0\1\115\3\0\1\132\26\0\1\143"+
    "\26\0\1\133\1\144\4\133\1\144\2\0\3\133\1\0"+
    "\1\133\1\0\1\144\1\0\1\133\1\144\1\133\1\144"+
    "\1\133\1\144\1\0\3\144\7\133\1\144\4\133\37\0"+
    "\1\121\3\0\1\135\26\0\1\145\26\0\1\136\1\146"+
    "\4\136\1\146\2\0\3\136\1\0\1\136\1\0\1\146"+
    "\1\0\1\136\1\146\1\136\1\146\1\136\1\146\1\0"+
    "\3\146\7\136\1\146\4\136\3\0\4\147\5\0\1\147"+
    "\1\0\1\147\7\0\1\147\11\0\1\147\4\0\2\147"+
    "\1\0\3\77\4\150\1\45\1\0\3\77\1\150\1\77"+
    "\1\150\7\77\1\150\11\77\1\150\4\77\2\150\1\77"+
    "\3\50\4\151\2\50\1\105\2\50\1\151\1\106\1\151"+
    "\7\50\1\151\11\50\1\151\4\50\2\151\1\50\37\0"+
    "\1\152\32\0\1\133\47\0\1\136\30\0\4\5\5\0"+
    "\1\5\1\0\1\5\7\0\1\5\11\0\1\5\4\0"+
    "\2\5\1\0\3\77\4\44\1\45\1\0\3\77\1\44"+
    "\1\77\1\44\7\77\1\44\11\77\1\44\4\77\2\44"+
    "\1\77\3\50\4\12\2\50\1\105\2\50\1\12\1\106"+
    "\1\12\7\50\1\12\11\50\1\12\4\50\2\12\1\50";

  private static int [] zzUnpackTrans() {
    int [] result = new int[3480];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\11\4\1\1\11\4\1\1\11\3\1\1\11"+
    "\2\1\1\11\5\1\1\11\3\1\1\0\1\1\1\0"+
    "\3\1\1\11\1\0\3\1\3\11\1\1\1\0\3\1"+
    "\1\11\11\0\1\1\1\0\2\1\1\11\5\1\1\11"+
    "\1\1\1\0\3\1\11\0\2\1\1\0\1\1\2\0"+
    "\1\1\2\0\1\1\1\0\2\1\6\0\2\1\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[106];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /* user code: */


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public ZoneTokenMaker() {
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addToken(int, int, int)
	 */
	private void addHyperlinkToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, true);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int tokenType) {
		addToken(zzStartRead, zzMarkedPos-1, tokenType);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addHyperlinkToken(int, int, int)
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, false);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *        occurs.
	 * @param hyperlink Whether this token is a hyperlink.
	 */
	public void addToken(char[] array, int start, int end, int tokenType,
						int startOffset, boolean hyperlink) {
		super.addToken(array, start,end, tokenType, startOffset, hyperlink);
		zzStartRead = zzMarkedPos;
	}


	/**
	 * Returns the text to place at the beginning and end of a
	 * line to "comment" it in a this programming language.
	 *
	 * @return The start and end strings to add to a line to "comment"
	 *         it out.
	 */
	public String[] getLineCommentStartAndEnd() {
		return new String[] { "//", null };
	}


	/**
	 * Returns the first token in the linked list of tokens generated
	 * from <code>text</code>.  This method must be implemented by
	 * subclasses so they can correctly implement syntax highlighting.
	 *
	 * @param text The text from which to get tokens.
	 * @param initialTokenType The token type we should start with.
	 * @param startOffset The offset into the document at which
	 *        <code>text</code> starts.
	 * @return The first <code>Token</code> in a linked list representing
	 *         the syntax highlighted text.
	 */
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

		resetTokenList();
		this.offsetShift = -text.offset + startOffset;

		// Start off in the proper state.
		int state = Token.NULL;
		switch (initialTokenType) {
						case Token.COMMENT_MULTILINE:
				state = MLC;
				start = text.offset;
				break;

			/* No documentation comments */
			default:
				state = Token.NULL;
		}

		s = text;
		try {
			yyreset(zzReader);
			yybegin(state);
			return yylex();
		} catch (IOException ex) {
			DebugConsole.logger.log(Level.SEVERE, null, ex);
			return null;
		}

	}


	/**
	 * Refills the input buffer.
	 *
	 * @return      <code>true</code> if EOF was reached, otherwise
	 *              <code>false</code>.
	 */
	private boolean zzRefill() {
		return zzCurrentPos>=s.offset+s.count;
	}


	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>YY_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
	public final void yyreset(Reader reader) {
		// 's' has been updated.
		zzBuffer = s.array;
		/*
		 * We replaced the line below with the two below it because zzRefill
		 * no longer "refills" the buffer (since the way we do it, it's always
		 * "full" the first time through, since it points to the segment's
		 * array).  So, we assign zzEndRead here.
		 */
		//zzStartRead = zzEndRead = s.offset;
		zzStartRead = s.offset;
		zzEndRead = zzStartRead + s.count - 1;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
		zzLexicalState = YYINITIAL;
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
	}




  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public ZoneTokenMaker(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public ZoneTokenMaker(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 140) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public org.fife.ui.rsyntaxtextarea.Token yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = zzLexicalState;


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 4: 
          { addNullToken(); return firstToken;
          }
        case 26: break;
        case 20: 
          { addToken(Token.LITERAL_CHAR);
          }
        case 27: break;
        case 16: 
          { start = zzMarkedPos-2; yybegin(MLC);
          }
        case 28: break;
        case 6: 
          { addToken(Token.WHITESPACE);
          }
        case 29: break;
        case 19: 
          { addToken(Token.LITERAL_NUMBER_HEXADECIMAL);
          }
        case 30: break;
        case 21: 
          { addToken(Token.ERROR_STRING_DOUBLE);
          }
        case 31: break;
        case 17: 
          { addToken(Token.LITERAL_NUMBER_FLOAT);
          }
        case 32: break;
        case 25: 
          { addToken(Token.RESERVED_WORD);
          }
        case 33: break;
        case 7: 
          { addToken(Token.SEPARATOR);
          }
        case 34: break;
        case 1: 
          { addToken(Token.IDENTIFIER);
          }
        case 35: break;
        case 11: 
          { addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken;
          }
        case 36: break;
        case 15: 
          { start = zzMarkedPos-2; yybegin(EOL_COMMENT);
          }
        case 37: break;
        case 3: 
          { addToken(Token.ERROR_CHAR); addNullToken(); return firstToken;
          }
        case 38: break;
        case 5: 
          { addToken(Token.ERROR_STRING_DOUBLE); addNullToken(); return firstToken;
          }
        case 39: break;
        case 18: 
          { yybegin(YYINITIAL); addToken(start,zzStartRead+2-1, Token.COMMENT_MULTILINE);
          }
        case 40: break;
        case 13: 
          { addToken(Token.ERROR_CHAR);
          }
        case 41: break;
        case 14: 
          { addToken(Token.LITERAL_STRING_DOUBLE_QUOTE);
          }
        case 42: break;
        case 24: 
          { int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_EOL); start = zzMarkedPos;
          }
        case 43: break;
        case 23: 
          { int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_MULTILINE); start = zzMarkedPos;
          }
        case 44: break;
        case 22: 
          { addToken(Token.RESERVED_WORD_2);
          }
        case 45: break;
        case 12: 
          { addToken(Token.ERROR_NUMBER_FORMAT);
          }
        case 46: break;
        case 2: 
          { addToken(Token.LITERAL_NUMBER_DECIMAL_INT);
          }
        case 47: break;
        case 8: 
          { addToken(Token.OPERATOR);
          }
        case 48: break;
        case 9: 
          { 
          }
        case 49: break;
        case 10: 
          { addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken;
          }
        case 50: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            switch (zzLexicalState) {
            case EOL_COMMENT: {
              addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken;
            }
            case 107: break;
            case YYINITIAL: {
              addNullToken(); return firstToken;
            }
            case 108: break;
            case MLC: {
              addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken;
            }
            case 109: break;
            default:
            return null;
            }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
