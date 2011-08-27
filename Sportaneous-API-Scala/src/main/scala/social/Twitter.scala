package social

import com.sportaneous.model.{User, Game}
import java.util.Random
import org.apache.openjpa.kernel.exps.Val
import twitter4j.auth.AccessToken
import conf.ConfigurationBuilder

/**
 * This contains on twitter hook code
 */
object TwitterAdapter {


  /**
   * This will post a game to twitter. Return true for success and false for not
   */
  def postGameToTwitter(g:Game):Boolean =
  {
    val u = g.getOrganizer

    println("Posting Game to Twitter")
    /*if auto social push is enabled */
    if(u==null)
    {
      println("WHY?")
    }
    if(u != null && u.getAutoTwitterPush != null && u.getAutoTwitterPush && u.getTwAccessToken != null)
    {

      println("Twitter Integration enabled")
      /* Get the twitter factory ready to send */
      val token = u.getTwAccessToken
      val tokenSecret = u.getTwSecretToken
      val accessToken = new AccessToken(token, tokenSecret);

      val factory = new TwitterFactory()

  //    val twitter = factory.getOAuthAuthorizedInstance("bB0ROccN3QaUXdxNLevA","onLklRFNgGwsZA4M0ZQ1sm5AZRZzI85Hvq3apPEc", accessToken);

      val confbuilder  = (new ConfigurationBuilder()).setOAuthAccessToken(accessToken.getToken).setOAuthAccessTokenSecret(accessToken.getTokenSecret).setOAuthConsumerKey("bB0ROccN3QaUXdxNLevA").setOAuthConsumerSecret("onLklRFNgGwsZA4M0ZQ1sm5AZRZzI85Hvq3apPEc");
      val twitter = new TwitterFactory(confbuilder.build()).getInstance();
      /* Create the tweet to send */
      val r = new Random()

      val tweetList = Seq(
       "Get your heart rate up: Join my Sportaneous game at "+g.getShortURL+". #Sportaneous",
      "Let's get Sportaneous. Join my game at " + g.getShortURL+". #Sportaneous",
      "Want to play? Join my Sportaneous game at "+g.getShortURL+".  #Sportaneous",
      "Think you can beat me? Bring it on at "+g.getShortURL+". #Sportaneous",
      "I'm ready to play. Are you in? Join me at "+g.getShortURL+". #Sportaneous",
      "Game time! Join the fun at "+g.getShortURL + ". #Sportaneous" )

      val statusUpdate = tweetList(r.nextInt(6))
      val update: StatusUpdate = new StatusUpdate(statusUpdate)
      val location: GeoLocation = new GeoLocation(g.getFacility.getLat, g.getFacility.getLng)

      update.setLocation(location)
      update.setDisplayCoordinates(true)

      val status = twitter.updateStatus(update)
      println("DOne tweeting")
  }

    true
 }



}