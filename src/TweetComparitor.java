import java.util.Comparator;

public class TweetComparitor implements Comparator<Tweet>
{
	
	@Override
	public int compare(Tweet arg0, Tweet arg1)
	{
		String IDone = arg0.getTweetID();
		String IDtwo = arg1.getTweetID();
		return IDone.compareTo(IDtwo);
	}

}