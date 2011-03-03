
/**
 * Holds all data in a tweet.
 * @author apbarratt
 *
 */
public class Tweet {

	private String tweetID;
	private String quote;
	private String by;
	private String displayName;
	private String howLong;
	
	/**
	 * Constructure for a tweet.
	 * @param tweetID
	 * @param quote
	 * @param by
	 * @param displayName
	 */
	public Tweet(String tweetID, String quote, String by, String displayName, String howLong)
	{
		this.tweetID = tweetID;
		this.quote = quote;
		this.by = by;
		this.displayName = displayName;
		this.howLong = howLong;
	}
	
	/**
	 * returns the tweetID
	 * @return tweetID
	 */
	public String getTweetID()
	{
		return tweetID;
	}
	
	/**
	 * returns the Quote
	 * @return quote
	 */
	public String getQuote()
	{
		return quote;
	}
	
	/**
	 * returns who the quote is by.
	 * @return by
	 */
	public String getBy()
	{
		return by;
	}
	
	/**
	 * returns the displayName of the poster
	 * @return displayName
	 */
	public String getDisplayName()
	{
		return displayName;
	}
	
	/**
	 * returns the ammount of time the post has existed.
	 * @return displayName
	 */
	public String getHowLong()
	{
		return howLong;
	}
	
}
