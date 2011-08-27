package social

import com.sportaneous.model.Game
import org.fb.GraphAPI
import org.json.JSONObject

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 7/18/11
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */

object FacebookAdapter {
  def postGameToFacebook(g:Game): Boolean=
  {
    println("\n\nAttempting to post to fb")
    val u = g.getOrganizer

    if(u== null)
      return false;

    if(u.getFbAccessToken != null)
    {
      println("fb connect enalbed Post")
      val graph = new GraphAPI(u.getFbAccessToken);

      graph.putWallPost("me", "I am writing on my wall!", null);
      return true;
    }

    return true;
  }

}