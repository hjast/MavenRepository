package api

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 8/26/11
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
//
// APIService.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//
import java.io._
import java.util._
import com.sportaneous.email.EmailUtility
import com.sportaneous.prediction.PredictionDriver
import com.sportaneous.hooks.HookManager
import org.apache.commons.codec.digest.DigestUtils
import model._
import prediction.NotificationManager
import api.client.v1._
import net.liftweb.http.rest.RestHelper
import java.lang.Double
import hooks.CalculateGameGroupAssociationActor.Model

/**
 * The API Service should only do data updating in the method everything else should be hooks. THis means that is it done async. and we can return to
 * the user as fast as possible.
 *
 * Organization:
 *
 * All methods dealing with get which do not change state should be first.
 *
 * After that we have methods that change state
 */
object NewAPIService extends RestHelper {

  serve("v1.0" prefix {


    @POST
    @Path("Notifiers")
    @Transactional(readOnly = true) def notifiers: APIResult = {
      System.out.println("calling Notifiers ")
      NotificationManager.runNotifierHook
      var result: API_GetUpcomingGamesResult = new API_GetUpcomingGamesResult
      result.status = StatusCodes.SUCCESS
      return result
    }




    /**
     * /v1.0/isLocationSupported
     *
     * Parameters
     * ==========
     * - lat
     * - lng
     *
     * Returns
     * =======
     * - status
     * - city
     * - regionID
     */
    @GET
    @Path("isLocationSupported")
    @Transactional(readOnly = true) def isLocationSupported(@QueryParam("lat") lat: Double, @QueryParam("lng") lng: Double): IsLocationSupportedResult = {
      var query: Query = null
      var regions: List[Region] = null
      var regionID: Long = null
      var result: IsLocationSupportedResult = null
      query = em.createNamedQuery("Region.findAll")
      regions = query.getResultList
      for (region <- regions) {
        var d: Double = null
        d = GeometryUtils.getDistanceBetweenLocations(lat, lng, region.getLat, region.getLng)
        if (d <= region.getRadius) {
          regionID = region.getRegionID
          break //todo: break is not supported
        }
      }
      result = new IsLocationSupportedResult
      if (regionID != null) {
        result.status = StatusCodes.SUCCESS
        result.regionID = regionID
      }
      else {
        result.status = StatusCodes.UNSUPPORTED_LOCATION
        result.city = GMapsReverseGeocoder.getCity(lat, lng)
      }
      return result
    }





    /**
     * /v1.0/respond
     *
     * Parameters
     * ==========
     * - userID
     * - gameID
     * - decision
     *
     * Returns
     * =======
     * - status
     */
    @POST
    @Path("respond")
    @Transactional def respond(@FormParam("userID") userID: Long, @FormParam("gameID") gameID: Long, @FormParam("decision") decision: GameTimeDecisionType): APIResult = {
      var user: User = null
      var game: Game = null
      var gtd: GameTimeDecision = null
      user = em.find(classOf[User], userID)
      game = em.find(classOf[Game], gameID)
      if (game == null) {
        if (decision == GameTimeDecisionType.JOIN) {
          var result: APIResult = null
          result = new APIResult
          result.status = StatusCodes.GAME_DOES_NOT_EXIST
          return result
        }
        else {
          return APIResult.SUCCESS
        }
      }
      try {
        var query: Query = null
        query = em.createNamedQuery("GameTimeDecision.findByUserAndGame")
        query.setParameter("user", user)
        query.setParameter("game", game)
        gtd = query.getSingleResult.asInstanceOf[GameTimeDecision]
        if (gtd.getType == decision) {
          return APIResult.SUCCESS
        }
      }
      catch {
        case e: NoResultException => {
        }
      }
      if (gtd == null) {
        gtd = new GameTimeDecision
        gtd.setGame(game)
        gtd.setUser(user)
        gtd.setType(decision)
        em.persist(gtd)
      }
      else {
        gtd.setType(decision)
      }
      if ((decision == GameTimeDecisionType.JOIN) && !isUserParticipant(user, game)) {
        var invited: Boolean = false
        var participant: GameParticipant = null
        if (isUserInvited(user, game)) {
          var query: Query = em.createNamedQuery("GameParticipant.findParticipantByGameUser")
          query.setParameter("user", user)
          query.setParameter("game", game)
          try {
            participant = query.getSingleResult.asInstanceOf[GameParticipant]
            invited = true
          }
          catch {
            case e: NoResultException => {
            }
          }
        }
        var oldStatus: GameStatus = null
        var newStatus: GameStatus = null
        oldStatus = game.getStatus
        if (participant == null) participant = new GameParticipant
        participant.setGame(game)
        participant.setIsOrganizer(Boolean.FALSE)
        participant.setUser(user)
        participant.setInvited(false)
        em.persist(participant)
        game.getParticipants.add(participant)
        updateGameStatus(game)
        newStatus = game.getStatus
        if ((oldStatus == GameStatus.PROPOSED) && (newStatus == GameStatus.ON)) {
          sendGameOnNotification(game)
        }
        else {
          sendJoinNotification(game)
        }
        HookManager.runPostGameJoinHook(game, user)
      }
      else if (isUserParticipant(user, game) && !(decision == GameTimeDecisionType.JOIN)) {
        for (participant <- game.getParticipants) {
          if (participant.getUser == user) {
            game.getParticipants.remove(participant)
            em.remove(participant)
            break //todo: break is not supported
          }
        }
        if (game.getParticipants.isEmpty) {
          em.remove(game)
        }
      }
      return APIResult.SUCCESS
    }


    /**
     * /v1.0/sendInvitations
     *
     * Parameters
     * ==========
     * - gameID
     * - userName
     * - sportName
     * - facilityName
     * - timeAndDate
     * - emailAddresses
     *
     * Returns
     * =======
     * - status
     */
    @POST
    @Path("sendInvitations")
    @Transactional def sendInvitations(@FormParam("gameID") gameID: Long, @FormParam("userName") userName: String, @FormParam("sportName") sportName: String, @FormParam("facilityName") facilityName: String, @FormParam("timeAndDate") timeAndDate: String, @FormParam("emailAddresses") emailAddresses: List[String]): APIResult = {
      var subject: String = userName + " has invited you to a game"
      var emailHtml: String = readTextResourceFile("/game-invitation.html")
      emailHtml = expandMacro(emailHtml, "GAME_ID", gameID.toString)
      emailHtml = expandMacro(emailHtml, "USER_NAME", userName)
      emailHtml = expandMacro(emailHtml, "SPORT_NAME", sportName)
      emailHtml = expandMacro(emailHtml, "FACILITY_NAME", facilityName)
      emailHtml = expandMacro(emailHtml, "TIME_AND_DATE", timeAndDate)
      for (emailAddress <- emailAddresses) {
        try {
          EmailService.send(null, emailAddress, subject, null, emailHtml)
        }
        catch {
          case e: IOException => {
            System.out.println("Unable to send email: " + e.getMessage)
            e.printStackTrace(System.err)
          }
        }
      }
      return new APIResult
    }



    /**
     * Send the 'Game On' notification to all participants.
     */
    private def sendGameOnNotification(game: Game): Unit =
    {
      for (participant <- game.getParticipants) {
        var user: User = null
        user = participant.getUser
        if (user.getIosDeviceToken != null) {
          UrbanAirship.push(user.getIosDeviceToken, "Your game is on", game.getGameID)
        }
      }
    }

    /**
     * Send the 'Somebody joined your game.' notification to the organizer.
     */
    private def sendJoinNotification(game: Game): Unit =
    {
      for (participant <- game.getParticipants) {
        if (participant.getIsOrganizer) {
          var user: User = null
          user = participant.getUser
          if (user.getIosDeviceToken != null) {
            UrbanAirship.push(user.getIosDeviceToken, "Someone joined your game", game.getGameID)
          }
        }
      }
    }
    private def getNeighborhoodForLocation(lat: Double, lng: Double): Neighborhood =
    {
      var pt: LatLng = null
      var query: Query = null
      var neighborhoods: List[Neighborhood] = null
      var theNeighborhood: Neighborhood = null
      pt = new LatLng(lat, lng)
      query = em.createNamedQuery("Neighborhood.findByLatLng")
      query.setParameter("lat", lat)
      query.setParameter("lng", lng)
      neighborhoods = query.getResultList
      for (neighborhood <- neighborhoods) {
        var poly: Polygon = null
        var pts: ArrayList[LatLng] = null
        Collections.sort(neighborhood.getBoundaries, new Comparator[NeighborhoodBoundary] {
          def compare(o1: NeighborhoodBoundary, o2: NeighborhoodBoundary): Int = {
            return o1.getIdx.compareTo(o2.getIdx)
          }
        })
        pts = new ArrayList[LatLng]
        for (boundary <- neighborhood.getBoundaries) {
          pts.add(new LatLng(boundary.getLat, boundary.getLng))
        }
        poly = new Polygon(pts)
        if (poly.contains(pt)) {
          theNeighborhood = neighborhood
          break //todo: break is not supported
        }
      }
      return theNeighborhood
    }
    private def updateGameStatus(game: Game): Unit =
    {
      if (game.getStatus == GameStatus.PROPOSED) {
        var playerCount: Int = 0
        var minCount: Int = 0
        playerCount = game.getParticipants.size
        minCount = game.getMinPlayerCount
        if (playerCount >= minCount) {
          game.setStatus(GameStatus.ON)
        }
      }
    }

    /**
     *
     */
    private def isUserParticipant(user: User, game: Game): Boolean =
    {
      for (participant <- game.getParticipants) {
        if (participant.getUser == user) {
          if (participant.getInvited == null || !participant.getInvited) return true
        }
      }
      return false
    }

    /**
     *
     */
    private def isUserOrganizer(user: User, game: Game): Boolean =
    {
      for (participant <- game.getParticipants) {
        if (participant.getIsOrganizer && (participant.getUser == user)) {
          return true
        }
      }
      return false
    }
    private def sendUpdateNotificationEmail(game: Game, changeList: List[APIService.ChangeDescription]): Unit =
    {
      var emailHtmlTemplate: String = readTextResourceFile("/game-updated.html")
      var changeHtmlTemplate: String = readTextResourceFile("/game-updated-change.html")
      var changeListHtml: String = createChangeListHtml(changeHtmlTemplate, changeList)
      for (p <- game.getParticipants) {
        var user: User = p.getUser
        var emailHtml: String = createEmailHtml(user, game, emailHtmlTemplate, changeListHtml)
        sendGameUpdateEmail(user, emailHtml)
      }
    }
    private def createChangeListHtml(changeTemplateHtml: String, changeList: List[APIService.ChangeDescription]): String =
    {
      var sb: StringBuilder = new StringBuilder
      for (changeDescription <- changeList) {
        sb.append(changeDescription.expandHtmlTemplate(changeTemplateHtml))
      }
      return sb.toString
    }
    private def createEmailHtml(user: User, game: Game, htmlTemplate: String, changeListHtml: String): String =
    {
      var html: String = htmlTemplate
      html = expandMacro(html, "FIRST_NAME", user.getFirstName)
      html = expandMacro(html, "EMAIL", user.getEmail)
      html = expandMacro(html, "GAME_URL", game.getShortURL)
      html = expandMacro(html, "CHANGE_LIST", changeListHtml)
      return html
    }
    private def sendGameUpdateEmail(user: User, emailHtml: String): Unit =
    {
      var name: String = user.getFirstName + " " + user.getLastName
      var subject: String = "Game Changes"
      try {
        EmailService.send(name, user.getEmail, subject, null, emailHtml)
      }
      catch {
        case e: IOException => {
          System.out.println("Unable to send email: " + e.getMessage)
          e.printStackTrace(System.err)
        }
      }
    }
    def readTextResourceFile(path: String): String = {
      try {
        var isr: InputStreamReader = null
        var br: BufferedReader = null
        var sb: StringBuilder = null
        var s: String = null
        isr = new InputStreamReader(getClass.getResourceAsStream(path))
        br = new BufferedReader(isr)
        sb = new StringBuilder
        while ((({
          s = br.readLine; s
        })) != null) {
          sb.append(s)
          sb.append('\n')
        }
        return sb.toString
      }
      catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
    }
    private def getRandomPassword(length: Int): String =
    {
      var r: Random = null
      var sb: StringBuilder = null
      r = new Random
      sb = new StringBuilder

      {
        var i: Int = 0
        while (i < length) {
          {
            var idx: Int = 0
            idx = r.nextInt(62)
            if (idx < 10) {
              sb.append(idx)
            }
            else if (idx < 36) {
              var c: Char = ('a' + (idx - 10)).asInstanceOf[Char]
              sb.append(c)
            }
            else {
              var c: Char = ('a' + (idx - 36)).asInstanceOf[Char]
              sb.append(c)
            }
          }
          ({
            i += 1; i
          })
        }
      }
      return sb.toString
    }

    @PersistenceContext(n a m e = "ServerPU") private var em: EntityManager = null
  }



//used for json serialize/ deserialize




