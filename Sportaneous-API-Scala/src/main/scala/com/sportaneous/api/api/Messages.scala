package api

import client.v1.{StatusCodes, API_Message, API_GetMessagesResult, APIResult}
import net.liftweb.http.rest.RestHelper
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Transactional._
import javax.ws.rs.{FormParam, Path, POST}
import javax.persistence.Query
import com.sportaneous.model.{User, Message, Game}
import com.sportaneous.com.sportaneous.api.APIV1.{API_GetMessagesResult, API_Response}

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 8/26/11
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */

object MessagesAPI extends RestHelper {

 serve("v1.0" prefix {
  /**
      * /v1.0/getMessages
      *
      * Description
      * ===========
      * Returns the messages associated with the given game.
      *
      * Parameters
      * ==========
      * - gameID
      *
      * Returns
      * =======
      * - status
      * - messages
      */
     @POST
     @Path("getMessages")
     @Transactional(readOnly = true) def getMessages(@FormParam("gameID") gameID: Long): APIResult = {
       var game: Game = Model.find[Game](gameID)
       var msgs = Model.findAll("Message.findByGame","game"-> game)

       return API_Response(StatusCodes.SUCCESS,API_GetMessagesResult(msgs))
     }

  /**
    * /v1.0/createMessage
    *
    * Parameters
    * ==========
    * - userID
    * - gameID
    * - postTime
    * - text
    *
    * Returns
    * =======
    * - status
    */
   @POST
   @Path("createMessage")
   @Transactional def createMessage(@FormParam("userID") userID: Long, @FormParam("gameID") gameID: Long, @FormParam("postTime") postTime: RESTTimestamp, @FormParam("text") text: String): APIResult = {
     var user = Model.find[User](userID)
     var game= Model.find[Game](gameID)
     var message: Message = new Message

     message.setUser(user)
     message.setGame(game)
     message.setPostTime(postTime.getCal)
     message.setText(text)
     Model.persist(message)
     EmailUtility.sendGameWallMessage(user, game, message)
     return new APIResult
   }

 }
}