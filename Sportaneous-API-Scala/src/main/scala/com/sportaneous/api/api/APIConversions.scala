package api

import client.v1._
import java.text.{SimpleDateFormat, DateFormat}
import java.util.{Calendar, Comparator, Collections, ArrayList}


object ImplicitDefs {



/* Convert anything message like to a message */
implicit def convertMessage2APIMessage(m: {def getUser(): User; def getText(): String; def getPostTime(): Calendar}): API_Message = {
  var df: SimpleDateFormat = new SimpleDateFormat(DATE_FMT)
  var tf: SimpleDateFormat = new SimpleDateFormat(TIME_FMT)
  var mAPI: API_Message = new API_Message
  mAPI.user = m.getUser
  mAPI.text = m.getText
  mAPI.date = df.format(m.getPostTime.getTime)
  mAPI.time = tf.format(m.getPostTime.getTime)
  return mAPI
}

/**
 * Serializer for the Neighborhood entity.
 */
implicit def convertNeighborhood2API(n: Neighborhood): API_Neighborhood = {
  var nAPI: API_Neighborhood = null
  nAPI = new API_Neighborhood
  nAPI.name = n.getName
  nAPI.neighborhoodID = n.getNeighborhoodID
  if (n.getSuperNeighborhood != null) {
    nAPI.superNeighborhoodID = n.getSuperNeighborhood.getSuperNeighborhoodID
  }
  return nAPI
}

/**
 * Serializer for the GameParticipant entity.
 */
implicit def convertGameParticpant2Player(g: GameParticipant): API_Player = {
  var u: User = g.getUser
  var api: API_Player = null
  api = new API_Player
  api.fbUID = u.getFbUID
  api.firstName = u.getFirstName
  api.userID = u.getUserID
  api.isOrganizer = g.getIsOrganizer
  return api
}

/**
 * Serializer for the Region entity.
 */
implicit def convertRegion2APIRegion(r: Region): API_Region = {
  var a: API_Region = null
  a = new API_Region
  a.name = r.getName
  a.regionID = r.getRegionID
  return a
}


implicit def convertSuperNeighborhood2API(sn: SuperNeighborhood): API_SuperNeighborhood = {
  var a: API_SuperNeighborhood = null
  a = new API_SuperNeighborhood
  a.name = sn.getName
  a.superNeighborhoodID = sn.getSuperNeighborhoodID
  return a
}

implicit def convertUser2APIUser(u: User): API_User = {
  var user: API_User = null
  user = new API_User
  user.fbUID = u.getFbUID
  user.firstName = u.getFirstName
  user.userID = u.getUserID
  return user
}

/**
 * Serializer for the Facility entity.
 */
implicit def convertFacility2APIFacility(f: Facility): API_Facility = {
  var fac: API_Facility = new API_Facility
  fac.addressLine1 = f.getAddressLine1
  fac.addressLine2 = f.getAddressLine2
  fac.city = f.getCity
  fac.facilityID = f.getFacilityID
  fac.lat = f.getLat
  fac.lng = f.getLng
  fac.name = f.getName
  if (f.getNeighborhood != null) {
    fac.neighborhoodID = f.getNeighborhood.getNeighborhoodID
  }
  fac.notes = f.getNotes
  fac.shortDescription = f.getShortDescription
  fac.sports = new ArrayList[Sport]
  for (fs <- f.getSports) {
    fac.sports.add(fs.getSport)
  }
  if (fac.sports.contains(Sport.SOCCER)) {
    if (!fac.sports.contains(Sport.BOOT_CAMP)) fac.sports.add(Sport.BOOT_CAMP)
    if (!fac.sports.contains(Sport.GROUP_FITNESS)) fac.sports.add(Sport.GROUP_FITNESS)
    if (!fac.sports.contains(Sport.RUGBY)) fac.sports.add(Sport.RUGBY)
    if (!fac.sports.contains(Sport.ULTIMATE)) fac.sports.add(Sport.ULTIMATE)
    if (!fac.sports.contains(Sport.YOGA)) fac.sports.add(Sport.YOGA)
  }
  fac.state = f.getState
  if (f.getRegion != null) {
    fac.timeZone = f.getRegion.getTimeZoneName
  }
  fac.zip = f.getZip
  return fac
}

    /**
     * Serializer for the Game entity.
     */
    private def _ (g: Game, didIOrganize: Boolean, didIJoin: Boolean): API_Game =
    {
      var df: DateFormat = null
      var tf: DateFormat = null
      var _: API_Game = null
      df = new SimpleDateFormat(DATE_FMT)
      df.setTimeZone(g.getRegion.getTimeZone)
      tf = new SimpleDateFormat(TIME_FMT)
      tf.setTimeZone(g.getRegion.getTimeZone)
      Collections.sort(g.getMessages, new Comparator[Message] {
        def compare(t: Message, t1: Message): Int = {
          return t.getPostTime.compareTo(t1.getPostTime)
        }
      })
      _ = new API_Game
      _.date = df.format(g.getStartTime.getTime)
      _.didIJoin = didIJoin
      _.didIOrganize = didIOrganize
      _.facility = _(g.getFacility)
      _.gameID = g.getGameID
      _.maxPlayerCount = g.getMaxPlayerCount
      _.messages = new ArrayList[API_Message]
      for (message <- g.getMessages) {
        _.messages.add(_(message))
      }
      _.minPlayerCount = g.getMinPlayerCount
      _.notes = g.getNotes
      _.playLevel = g.getPlayLevel
      _.playerCount = g.getParticipants.size
      _.players = new ArrayList[API_Player]
      for (participant <- g.getParticipants) {
        if (participant.getInvited == null || !participant.getInvited) {
          _.players.add(_(participant))
        }
      }
      _.shortURL = g.getShortURL
      _.sport = g.getSport
      _.status = g.getStatus
      _.time = tf.format(g.getStartTime.getTime)
      _.privacy = g.getPrivacy
      return _
    }




    /**
     * Serializer for the user entity --> profile.
     */
    private def _p(u: User): API_Profile =
    {
      var _: API_Profile = null
      _ = new API_Profile
      _.firstName = u.getFirstName
      _.lastName = u.getLastName
      _.neighborhoods = new ArrayList[API_Neighborhood]
      for (neighborhood <- u.getNeighborhoods) {
        _.neighborhoods.add(_(neighborhood.getNeighborhood))
      }
      _.regionID = u.getRegion.getRegionID
      _.sportIDs = new ArrayList[Sport]
      for (sport <- u.getSports) {
        _.sportIDs.add(sport.getSport)
      }
      _.userID = u.getUserID
      if (u.getGoalCount != null && u.getGoalPeriod != null) {
        _.goal = _(u.getGoalCount, u.getGoalPeriod)
      }
      _.fbUID = u.getFbUID
      return _
    }

    /**
     * Serializer for fitness goals.
     */
    private def _ (count: Integer, period: FitnessGoalPeriod): API_FitnessGoal =
    {
      var _: API_FitnessGoal = null
      _ = new API_FitnessGoal
      _.count = count
      _.period = period
      return _
    }



}

