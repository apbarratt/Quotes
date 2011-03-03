import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator Class filled with common checks for validating data.
 * @author Andy Barratt - www.andybarratt.co.uk
 * @version 0.1
 */
public class Validator {

	private static Pattern pattern;
	private static Matcher matcher;
	//----------------------------------------------------------------------------------------------------------
	
	private static final String EMAIL_PATTERN =
		"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private static final String ALPHANUMERIC_PATTERN =
		"[a-zA-Z0-9]+";
	
	private static final String ALPHANUMERIC_PATTERN_SPACE_ALLOWED =
		"[a-zA-Z][a-zA-Z\\s]+";
	
	private static final String ALPHABET_PATTERN =
		"[a-zA-Z]+";
	
	private static final String ALPHABET_PATTERN_SPACE_ALLOWED =
		"[a-zA-Z][a-zA-Z\\s]+";
	
	
	
	
	
	//----------------------------------------------------------------------------------------------------------
	
	public Validator()
	{
		
	}
	
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * Checks provided string is a validly formatted email address.
	 * DOES NOT CHECK TO SEE IF EMAIL ADDRESS ACTUALLY EXISTS.
	 * 
	 * @param String toCheck
	 * @return boolean - True if valid
	 */
	public static boolean validateEmail(final String toCheck)
	{
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(toCheck);
		return matcher.matches();
	}
	
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * Checks provided string for only English Alphabet characters or numbers.
	 * 
	 * @param String toCheck
	 * @return boolean - True if valid
	 */
	public static boolean validateAlphanumeric(final String toCheck, boolean allowSpaces)
	{
		if(allowSpaces)
			pattern = Pattern.compile(ALPHANUMERIC_PATTERN_SPACE_ALLOWED);
		else
			pattern = Pattern.compile(ALPHANUMERIC_PATTERN);
		
		matcher = pattern.matcher(toCheck);
		return matcher.matches();
	}
	
	//----------------------------------------------------------------------------------------------------------
	
	/**
	 * Checks provided string for only English Alphabet characters.
	 * 
	 * @param String toCheck
	 * @return boolean - True if valid
	 */
	public static boolean validateAlphabet(final String toCheck, boolean allowSpaces)
	{
		if(allowSpaces)
			pattern = Pattern.compile(ALPHABET_PATTERN_SPACE_ALLOWED);
		else
			pattern = Pattern.compile(ALPHABET_PATTERN);
		
		matcher = pattern.matcher(toCheck);
		return matcher.matches();
	}
	
}
