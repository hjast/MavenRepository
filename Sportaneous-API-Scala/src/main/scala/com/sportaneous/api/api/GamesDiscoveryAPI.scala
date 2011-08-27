package api

import client.v1._
import net.liftweb.http.rest.RestHelper
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Transactional._
import javax.ws.rs.{FormParam, Path, POST}
import javax.persistence.{TemporalType, Query}
import java.util.{Collections, ArrayList, Calendar}
import com.sportaneous.model.{GameStartTimeComparator, Game, User, Neighborhood}

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 8/26/11
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */


object GamesDiscoveryAPI extends RestHelper
{
   serve("v1.0" prefix {
    /**
     * /v1.0/getUpcomingGames
     *
     * Description
     * ===========
     * General purpose search mechanism for a user.  We'll use the following
     * guidelines for determining which games to return:
     *   (1) We'll return all games organized/joined by the user that have not
     *       yet started or whose start time is less 30 minutes ago.
     *   (2) We'll include all games that are within the user's home region.
     *
     * We'll group games matching criteria (1) together, and games matching (2)
     * together.  Inside these groups, we'll order the games by start time.
     *
     * Parameters
     * ==========
     * - userID
     * - sport+
     * - lat
     * - lng
     * - neighborhoodID+
     * - v*
     *
     * + denotes multi-valued parameter
     * * if v is non-null and >= 1.2, include rugby, volleyball, OTL, boot camp,
     *   group fitness, and yoga.
     *
     * Returns
     * =======
     * - status
     * - games
     */
    @POST
    @Path("getUpcomingGames")
    @Transactional(readOnly = true) def getUpcomingGames(@FormParam("userID") userID: Long, @FormParam("sport") sports: List[Sport], @FormParam("lat") lat: Double, @FormParam("lng") lng: Double, @FormParam("neighborhoodIDs") neighborhoodIDs: List[Long], @FormParam("v") v: Double): APIResult = {
      var neighborhood: Neighborhood = null
      var user: User = null
      var thirtyMinutesAgo: Calendar = null
      var jpql: String = null
      var query: Query = null
      var myGames: List[Game] = null
      var tmpGames: List[Game] = null
      var otherGames: List[Game] = null
      var _games: List[API_Game] = null
      var result: API_GetUpcomingGamesResult = null
      if (lat ne 0.0 && lng ne 0.0) {
        neighborhood = getNeighborhoodForLocation(lat, lng)
      }
      thirtyMinutesAgo = Calendar.getInstance
      thirtyMinutesAgo.add(Calendar.MINUTE, -30)
      System.out.println("userid:" + userID)
      user = em.find(classOf[User], userID)
      System.out.println("user name:" + user.getFirstName)
      jpql = "SELECT g from Game g " + "JOIN g.participants p " + "WHERE g.startTime >= :thirtyMinutesAgo " + "AND p.user = :user"
      query = em.createQuery(jpql)
      query.setParameter("thirtyMinutesAgo", thirtyMinutesAgo, TemporalType.TIMESTAMP)
      query.setParameter("user", user)
      myGames = query.getResultList
      System.out.println("user:" + user.getEmail)
      jpql = "SELECT g from Game g " + "WHERE g.startTime >= :thirtyMinutesAgo " + "AND g.region = :region" + "AND g.privacy = false"
      query = em.createQuery(jpql)
      query.setParameter("thirtyMinutesAgo", thirtyMinutesAgo, TemporalType.TIMESTAMP)
      query.setParameter("region", user.getRegion)
      System.out.println("region:" + user.getRegion.getName)
      tmpGames = query.getResultList
      otherGames = new ArrayList[Game]
      for (game <- tmpGames) {
        if (!isUserParticipant(user, game)) {
          otherGames.add(game)
        }
      }
      _games = new ArrayList[API_Game]
      Collections.sort(myGames, new GameStartTimeComparator)
      for (game <- myGames) {
        var isOrganizer: Boolean = false
        var isParticipant: Boolean = false
        isOrganizer = isUserOrganizer(user, game)
        isParticipant = isUserParticipant(user, game)
        _games.add(_(game, isOrganizer, isParticipant))
      }
      Collections.sort(otherGames, new GameStartTimeComparator)
      for (game <- otherGames) {
        var isOrganizer: Boolean = false
        var isParticipant: Boolean = false
        isOrganizer = isUserOrganizer(user, game)
        isParticipant = isUserParticipant(user, game)
        _games.add(_(game, isOrganizer, isParticipant))
      }
      if (v == null || v < 1.2) {
        {
          var it: Iterator[API_Game] = _games.iterator
          while (it.hasNext) {
            var _game: API_Game = null
            _game = it.next
            _game.sport match {
              case BOOT_CAMP =>
              case GROUP_FITNESS =>
              case OVER_THE_LINE =>
              case RUGBY =>
              case VOLLEYBALL =>
              case YOGA =>
                it.remove
                break //todo: break is not supported
            }
          }
        }
      }
      if (v == null || v < 1.3) {
        {
          var it: Iterator[API_Game] = _games.iterator
          while (it.hasNext) {
            var _game: API_Game = null
            _game = it.next
            _game.sport match {
              case CYCLING =>
              case OTHER =>
              case RUNNING =>
              case SOFTBALL =>
              case TENNIS =>
                it.remove
                break //todo: break is not supported
            }
          }
        }
      }
      result = new API_GetUpcomingGamesResult
      result.status = StatusCodes.SUCCESS
      result.games = _games
      return result
    }

    /**
     * /v1.0/getUpcomingGamesAlt
     *
     * Description
     * ===========
     * Alternative search method optimized for map-based clients.
     *
     * Parameters
     * ==========
     * - userID
     * - sport+
     * - minLat
     * - maxLat
     * - minLng
     * - maxLng
     * - maxResults
     *
     * + denotes multi-valued parameter
     *
     * Returns
     * =======
     * - status
     * - games
     */
    @POST
    @Path("getUpcomingGamesAlt")
    @Transactional(readOnly = true) def getUpcomingGamesAlt(@FormParam("userID") userID: Long, @FormParam("sport") sports: List[Sport], @FormParam("minLat") minLat: Double, @FormParam("maxLat") maxLat: Double, @FormParam("minLng") minLng: Double, @FormParam("maxLng") maxLng: Double, @FormParam("maxResults") maxResults: Integer): APIResult = {
      System.out.println("Here")
      var user: User = null
      var thirtyMinutesAgo: Calendar = null
      var jpql: String = null
      var query: Query = null
      var games: List[Game] = null
      var tmpGames: List[Game] = null
      var _games: List[API_Game] = null
      var result: API_GetUpcomingGamesResult = null
      thirtyMinutesAgo = Calendar.getInstance
      thirtyMinutesAgo.add(Calendar.MINUTE, -30)
      user = em.find(classOf[User], userID)
      System.out.println("here")
      jpql = "SELECT g from Game g " + "JOIN g.participants p " + "JOIN g.facility f " + "WHERE g.startTime >= :thirtyMinutesAgo " + "AND p.user = :user " + "AND f.lat >= :minLat " + "AND f.lat <= :maxLat " + "AND f.lng >= :minLng " + "AND f.lng <= :maxLng " + "ORDER BY g.startTime"
      query = em.createQuery(jpql)
      query.setParameter("thirtyMinutesAgo", thirtyMinutesAgo, TemporalType.TIMESTAMP)
      query.setParameter("user", user)
      query.setParameter("minLat", minLat)
      query.setParameter("maxLat", maxLat)
      query.setParameter("minLng", minLng)
      query.setParameter("maxLng", maxLng)
      query.setMaxResults(maxResults)
      games = query.getResultList
      System.out.println("Games organized by you " + games.size)
      maxResults -= games.size
      jpql = "SELECT g from Game g " + "JOIN g.facility f " + "WHERE g.startTime >= :thirtyMinutesAgo " + "AND f.lat >= :minLat " + "AND f.lat <= :maxLat " + "AND f.lng >= :minLng " + "AND f.lng <= :maxLng " + "ORDER BY g.startTime"
      query = em.createQuery(jpql)
      query.setParameter("thirtyMinutesAgo", thirtyMinutesAgo, TemporalType.TIMESTAMP)
      query.setParameter("minLat", minLat)
      query.setParameter("maxLat", maxLat)
      query.setParameter("minLng", minLng)
      query.setParameter("maxLng", maxLng)
      query.setMaxResults(maxResults)
      tmpGames = query.getResultList
      System.out.println("Games organized by you " + tmpGames.size)
      for (game <- tmpGames) {
        if (!isUserParticipant(user, game)) {
          games.add(game)
        }
      }
      _games = new ArrayList[API_Game]
      for (game <- games) {
        var isOrganizer: Boolean = false
        var isParticipant: Boolean = false
        isOrganizer = isUserOrganizer(user, game)
        isParticipant = isUserParticipant(user, game)
        _games.add(_(game, isOrganizer, isParticipant))
      }
      System.out.println("Games to serialize " + _games.size)
      result = new API_GetUpcomingGamesResult
      result.status = StatusCodes.SUCCESS
      result.games = _games
      return result
    }

/**
     * /v1.0/getUpcomingGamesAltPage
     * <p/>
     * Description
     * ===========
     * Alternative search method optimized for map-based clients.
     * <p/>
     * Parameters
     * ==========
     * - userID
     * - sport+
     * - minLat
     * - maxLat
     * - minLng
     * - maxLng
     * - maxResults
     * <p/>
     * + denotes multi-valued parameter
     * <p/>
     * Returns
     * =======
     * - status
     * - games
     */
    @POST
    @Path("getUpcomingGamesAltPage")
    @Transactional(readOnly = true) def getUpcomingGamesAltPage(@FormParam("userID") userID: Long, @FormParam("sport") sports: List[Sport], @FormParam("minLat") minLat: Double, @FormParam("maxLat") maxLat: Double, @FormParam("minLng") minLng: Double, @FormParam("maxLng") maxLng: Double, @FormParam("offGamePos") offGamePos: Integer, @FormParam("inGamePos") inGamePos: Integer, @FormParam("history") history: Integer, @FormParam("maxResults") maxResults: Integer): APIResult = {
      var user: User = null
      var thirtyMinutesAgo: Calendar = null
      var jpql: String = null
      var query: Query = null
      var games: List[Game] = new ArrayList[Game]
      var tmpGames: List[Game] = null
      var _games: List[API_Game] = null
      var result: API_GetUpcomingGamesPageResult = new API_GetUpcomingGamesPageResult
      var conditiontTime: String = "AND g.startTime < :thirtyMinutesAgo "
      var conditionSport: String = ""
      var conditionGeo: String = ""
      var order: String = "DESC"
      var filtergeo: Boolean = (minLat + minLng + maxLng + maxLat != 0)
      var filtersport: Boolean = (sports != null && sports.size > 0)
      thirtyMinutesAgo = Calendar.getInstance
      history match {
        case 1 =>
          conditiontTime = "AND g.startTime >= :thirtyMinutesAgo "
          thirtyMinutesAgo.add(Calendar.MINUTE, -30)
          order = "ASC"
          break //todo: break is not supported
        case 3 =>
          conditiontTime = ""
          break //todo: break is not supported
        case _ =>
          break //todo: break is not supported
      }
      if (filtersport) {
        conditionSport = "AND g.sport IN (:sports) "
      }
      if (filtergeo) {
        conditionGeo = "AND f.lat >= :minLat " + "AND f.lat <= :maxLat " + "AND f.lng >= :minLng " + "AND f.lng <= :maxLng "
      }
      user = em.find(classOf[User], userID)
      if (offGamePos eq 0) {
        jpql = "SELECT DISTINCT g from Game g " + "JOIN g.participants p " + "JOIN g.facility f WHERE " + "p.user = :user " + conditiontTime + conditionGeo + conditionSport + "ORDER BY g.startTime " + order
        query = em.createQuery(jpql)
        if (history ne 3) {
          query.setParameter("thirtyMinutesAgo", thirtyMinutesAgo, TemporalType.TIMESTAMP)
        }
        query.setParameter("user", user)
        if (filtersport) query.setParameter("sports", sports)
        if (filtergeo) {
          query.setParameter("minLat", minLat)
          query.setParameter("maxLat", maxLat)
          query.setParameter("minLng", minLng)
          query.setParameter("maxLng", maxLng)
        }
        query.setFirstResult(inGamePos)
        query.setMaxResults(maxResults)
        games = query.getResultList
        inGamePos += games.size
        maxResults -= games.size
      }
      if (maxResults > 0 && filtergeo) {
        jpql = "SELECT  g from Game g " + "JOIN g.facility f WHERE " + "g not in (select distinct gp.game from GameParticipant gp where gp.user = :user) " + conditiontTime + conditionGeo + conditionSport + "ORDER BY g.startTime " + order
        query = em.createQuery(jpql)
        if (history ne 3) {
          query.setParameter("thirtyMinutesAgo", thirtyMinutesAgo, TemporalType.TIMESTAMP)
        }
        query.setParameter("user", user)
        query.setParameter("minLat", minLat)
        query.setParameter("maxLat", maxLat)
        query.setParameter("minLng", minLng)
        query.setParameter("maxLng", maxLng)
        if (filtersport) query.setParameter("sports", sports)
        query.setFirstResult(offGamePos)
        query.setMaxResults(maxResults)
        tmpGames = query.getResultList
        offGamePos += tmpGames.size
        games.addAll(tmpGames)
      }
      _games = new ArrayList[API_Game]
      for (game <- games) {
        var isOrganizer: Boolean = false
        var isParticipant: Boolean = false
        isOrganizer = isUserOrganizer(user, game)
        isParticipant = isUserParticipant(user, game)
        _games.add(_(game, isOrganizer, isParticipant))
      }
      result = new API_GetUpcomingGamesPageResult
      result.status = StatusCodes.SUCCESS
      result.inGamePos = inGamePos
      result.offGamePos = offGamePos
      result.games = _games
      return result
    }

   }
}
