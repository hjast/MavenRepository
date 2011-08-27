package api

import client.v1._
import net.liftweb.http.rest.RestHelper
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Transactional._
import javax.ws.rs.{FormParam, Path, POST}
import java.text.{SimpleDateFormat, DateFormat}
import javax.persistence.{TemporalType, Query}
import java.util.{ArrayList, Calendar}
import com.sportaneous.prediction.PredictionDriver
import net.liftweb.http.S
import com.sportaneous.Model

object GamesAPI extends RestHelper {

    serve("v1.0" prefix {
  /**
     * /v1.0/getGame
     *
     * Description
     * ===========
     * Returns the given game.
     *
     * Parameters
     * ==========
     * - userID
     * - gameID
     *
     * Returns
     * =======
     * - status
     * - game
     */
      case "getGame" :: Nil JsonPost _ =>
      {
        val userID = S.params("userID")
        val gameID = S.params("gameID")
      var user: User = null
      var game: Game = null
      var isOrganizer: Boolean = false
      var isParticipant: Boolean = false
      var result: API_GetGameResult = null
      val user = if (userID.isEmpty) Model.find[User](userID) else None
      val game = Model.find[Game](gameID)
      val status =  StatusCodes.SUCCESS

      if (!game.isEmpty) {
        var isPublic: Boolean = game.get.getPrivacy == null || game.get.getPrivacy eq GamePrivacy.PUBLIC
        var isPrivateAndAllowed: Boolean = false
        if (game.get.getPrivacy != null) {
          if (user.isEmpty) {
            System.out.println("User null")
          }
          var isAllowed: Boolean = (isUserInvited(user, game) || isUserParticipant(user, game) || isUserOrganizer(user, game))
          isPrivateAndAllowed = game.getPrivacy eq GamePrivacy.PRIVATE && isAllowed
        }
        if (isPublic || isPrivateAndAllowed) {
          isOrganizer = (user != null) && isUserOrganizer(user, game)
          isParticipant = (user != null) && isUserParticipant(user, game)
          result.game = _(game, isOrganizer, isParticipant)

        }
        else {
          result.status = StatusCodes.NOT_ALLOWED
        }
      }
      else {
        result.status = StatusCodes.GAME_DOES_NOT_EXIST
      }
      return result
    }

/**
     * /v1.0/
     *
     * Parameters
     * ==========
     * - userID
     * - facilityID
     * - sport
     * - date
     * - time
     * - playLevel
     * - minPlayerCount
     * - maxPlayerCount
     * - notes
     *
     * Returns
     * =======
     * - status
     * - game
     */
    @POST
    @Path("createGame")
    @Transactional def createGame(@FormParam("userID") userID: Long, @FormParam("facilityID") facilityID: Long, @FormParam("sport") sport: Sport, @FormParam("date") date: RESTDate, @FormParam("time") time: RESTTime, @FormParam("playLevel") playLevel: PlayLevel, @FormParam("minPlayerCount") minPlayerCount: Integer, @FormParam("maxPlayerCount") maxPlayerCount: Integer, @FormParam("notes") notes: String): APIResult = {
      return _createGame(userID, true, facilityID, sport, date, time, playLevel, minPlayerCount, maxPlayerCount, notes)
    }
    @POST
    @Path("createGamePrivacy")
    @Transactional def createGamePrivacy(@FormParam("userID") userID: Long, @FormParam("privacy") privacy: Boolean, @FormParam("facilityID") facilityID: Long, @FormParam("sport") sport: Sport, @FormParam("date") date: RESTDate, @FormParam("time") time: RESTTime, @FormParam("playLevel") playLevel: PlayLevel, @FormParam("minPlayerCount") minPlayerCount: Integer, @FormParam("maxPlayerCount") maxPlayerCount: Integer, @FormParam("notes") notes: String): APIResult = {
      return _createGame(userID, privacy, facilityID, sport, date, time, playLevel, minPlayerCount, maxPlayerCount, notes)
    }
    def _createGame(userID: Long, privacy: Boolean, facilityID: Long, sport: Sport, date: RESTDate, time: RESTTime, playLevel: PlayLevel, minPlayerCount: Integer, maxPlayerCount: Integer, notes: String): APIResult = {
      var user: User = null
      var facility: Facility = null
      var region: Region = null
      var ts: Calendar = null
      var earliestStartTime: Long = null
      var longURL: String = null
      var shortURL: String = null
      var game: Game = null
      var organizer: GameParticipant = null
      var result: CreateGameResult = null
      user = em.find(classOf[User], userID)
      facility = em.find(classOf[Facility], facilityID)
      region = facility.getRegion
      var gp: GamePrivacy = if ((privacy || privacy == null)) GamePrivacy.PUBLIC else GamePrivacy.PRIVATE
      ts = Calendar.getInstance
      ts.setTimeZone(region.getTimeZone)
      ts.set(Calendar.YEAR, date.getCal.get(Calendar.YEAR))
      ts.set(Calendar.DAY_OF_YEAR, date.getCal.get(Calendar.DAY_OF_YEAR))
      ts.set(Calendar.HOUR_OF_DAY, time.getCal.get(Calendar.HOUR_OF_DAY))
      ts.set(Calendar.MINUTE, time.getCal.get(Calendar.MINUTE))
      ts.set(Calendar.SECOND, 0)
      ts.set(Calendar.MILLISECOND, 0)
      earliestStartTime = System.currentTimeMillis - 10 * 60 * 1000
      if (ts.getTimeInMillis < earliestStartTime) {
        var err: APIResult = null
        err = new APIResult
        err.status = StatusCodes.GAME_IN_PAST
        return err
      }
      game = new Game
      game.setFacility(facility)
      game.setMaxPlayerCount(maxPlayerCount)
      game.setMessages(new ArrayList[Message])
      game.setMinPlayerCount(minPlayerCount)
      game.setNotes(notes)
      game.setParticipants(new ArrayList[GameParticipant])
      game.setPlayLevel(playLevel)
      game.setRegion(facility.getRegion)
      game.setSport(sport)
      game.setStartTime(ts)
      game.setStatus(if (minPlayerCount > 1) GameStatus.PROPOSED else GameStatus.ON)
      game.setPrivacy(gp)
      em.persist(game)
      organizer = new GameParticipant
      organizer.setGame(game)
      organizer.setIsOrganizer(Boolean.TRUE)
      organizer.setUser(user)
      em.persist(organizer)
      game.getParticipants.add(organizer)
      longURL = "http://www.sportaneous.com/game-details/" + game.getGameID
      shortURL = BitLy.shorten(longURL)
      game.setShortURL(shortURL)
      result = new CreateGameResult
      result.status = StatusCodes.SUCCESS
      result.game = _(game, true, true)
      HookManager.runPostGameCreateHook(game)
      return result
    }

    /**
     * /v1.0/updateGame
     *
     * Parameters
     * ==========
     * - gameID
     * - userID
     * - facilityID
     * - sport
     * - date
     * - time
     * - playLevel
     * - minPlayerCount
     * - maxPlayerCount
     * - notes
     *
     * Returns
     * =======
     * - status
     */
    @POST
    @Path("updateGamePrivacy")
    @Transactional def updateGamePrivacy(@FormParam("gameID") gameID: Long, @FormParam("privacy") newPrivacy: Boolean, @FormParam("userID") userID: Long, @FormParam("facilityID") facilityID: Long, @FormParam("sport") sport: Sport, @FormParam("date") date: RESTDate, @FormParam("time") time: RESTTime, @FormParam("playLevel") playLevel: PlayLevel, @FormParam("minPlayerCount") minPlayerCount: Integer, @FormParam("maxPlayerCount") maxPlayerCount: Integer, @FormParam("notes") notes: String): APIResult = {
      System.out.println("game id null " + (gameID == null))
      System.out.println(gameID)
      return _updateGame(gameID, newPrivacy, userID, facilityID, sport, date, time, playLevel, minPlayerCount, maxPlayerCount, notes)
    }
    @POST
    @Path("updateGame")
    @Transactional def updateGame(@FormParam("gameID") gameID: Long, @FormParam("userID") userID: Long, @FormParam("facilityID") facilityID: Long, @FormParam("sport") sport: Sport, @FormParam("date") date: RESTDate, @FormParam("time") time: RESTTime, @FormParam("playLevel") playLevel: PlayLevel, @FormParam("minPlayerCount") minPlayerCount: Integer, @FormParam("maxPlayerCount") maxPlayerCount: Integer, @FormParam("notes") notes: String): APIResult = {
      System.out.println("game id null " + (gameID == null))
      System.out.println(gameID)
      val gameID= S.params("gameID")
      val userID = S.params("userID")
      val facilityID = S.params("facilityID")
      val sport = S.params("sport")
      val date = S.params("date")
      val time = S.params("time")
      val
      return _updateGame(gameID, null, userID, facilityID, sport, date, time, playLevel, minPlayerCount, maxPlayerCount, notes)
    }
    private def _updateGame(gameID: Long, newPrivacy: Boolean, userID: Long, facilityID: Long, sport: Sport, date: RESTDate, time: RESTTime, playLevel: PlayLevel, minPlayerCount: Integer, maxPlayerCount: Integer, notes: String): APIResult =
    {
      var user: User = null
      var facility: Facility = null
      var region: Region = null
      var ts: Calendar = null
      var earliestStartTime: Long = null
      var longURL: String = null
      var shortURL: String = null
      var game: Game = null
      var organizer: GameParticipant = null
      var result: APIResult = null
      var privacy: Boolean = null
      var importantChange: Boolean = false
      game = em.find(classOf[Game], gameID)
      user = em.find(classOf[User], userID)
      facility = em.find(classOf[Facility], facilityID)
      region = facility.getRegion
      privacy = if (game.getPrivacy != null && game.getPrivacy eq GamePrivacy.PRIVATE) false else true
      System.out.println("Updating game privacy " + privacy + " new privacy " + newPrivacy)
      var gameStartTime: Calendar = game.getStartTime
      gameStartTime.setTimeZone(game.getFacility.getRegion.getTimeZone)
      ts = Calendar.getInstance
      ts.setTimeZone(region.getTimeZone)
      ts.set(Calendar.YEAR, date.getCal.get(Calendar.YEAR))
      ts.set(Calendar.DAY_OF_YEAR, date.getCal.get(Calendar.DAY_OF_YEAR))
      ts.set(Calendar.HOUR_OF_DAY, time.getCal.get(Calendar.HOUR_OF_DAY))
      ts.set(Calendar.MINUTE, time.getCal.get(Calendar.MINUTE))
      ts.set(Calendar.SECOND, 0)
      ts.set(Calendar.MILLISECOND, 0)
      earliestStartTime = System.currentTimeMillis - 10 * 60 * 1000
      if (ts.getTimeInMillis < earliestStartTime) {
        var err: APIResult = null
        err = new APIResult
        err.status = StatusCodes.GAME_IN_PAST
        return err
      }
      var changeList: List[APIService.ChangeDescription] = new ArrayList[APIService.ChangeDescription]
      if (!(gameStartTime == ts)) {
        var dateFormatter: DateFormat = new SimpleDateFormat("EEEE',' MMMM d 'at' h:mm a")
        dateFormatter.setTimeZone(ts.getTimeZone)
        var newStartTime: String = dateFormatter.format(ts.getTime)
        dateFormatter.setTimeZone(gameStartTime.getTimeZone)
        var oldStartTime: String = dateFormatter.format(gameStartTime.getTime)
        changeList.add(new APIService.ChangeDescription("start time", newStartTime, oldStartTime))
        game.setStartTime(ts)
      }
      if (newPrivacy != null && privacy ne newPrivacy) {
        var gp: GamePrivacy = if (newPrivacy) GamePrivacy.PUBLIC else GamePrivacy.PRIVATE
        game.setPrivacy(gp)
      }
      if (game.getFacility.getFacilityID ne facility.getFacilityID) {
        changeList.add(new APIService.ChangeDescription("court/field", facility.getName, game.getFacility.getName))
        game.setFacility(facility)
        importantChange = true
      }
      if (game.getRegion.getRegionID ne region.getRegionID) {
        changeList.add(new APIService.ChangeDescription("region", region.getName, game.getRegion.getName))
        game.setRegion(region)
        importantChange = true
      }
      if (game.getSport ne sport) {
        changeList.add(new APIService.ChangeDescription("sport", sport, game.getSport))
        game.setSport(sport)
        importantChange = true
      }
      if (game.getPlayLevel ne playLevel) {
        changeList.add(new APIService.ChangeDescription("play level", playLevel, game.getPlayLevel))
        game.setPlayLevel(playLevel)
        importantChange = true
      }
      var numPlayers: Int = game.getParticipants.size
      var status: GameStatus = if ((numPlayers < minPlayerCount)) GameStatus.PROPOSED else GameStatus.ON
      if (game.getStatus ne status) {
        changeList.add(new APIService.ChangeDescription("status", status, game.getStatus))
        game.setStatus(status)
      }
      if (!(game.getMaxPlayerCount == maxPlayerCount)) {
        changeList.add(new APIService.ChangeDescription("max players", maxPlayerCount, game.getMaxPlayerCount))
        game.setMaxPlayerCount(maxPlayerCount)
      }
      if (!(game.getMinPlayerCount == minPlayerCount)) {
        changeList.add(new APIService.ChangeDescription("min players", minPlayerCount, game.getMinPlayerCount))
        game.setMinPlayerCount(minPlayerCount)
      }
      if (!(game.getNotes == notes)) {
        changeList.add(new APIService.ChangeDescription("notes field", notes, game.getNotes))
        game.setNotes(notes)
      }
      sendUpdateNotificationEmail(game, changeList)
      result = new APIResult
      result.status = StatusCodes.SUCCESS
      if (importantChange) PredictionDriver.runPredictor(game)
      HookManager.runPostGameUpdateHook(game)
      return result
    }

    }
}

