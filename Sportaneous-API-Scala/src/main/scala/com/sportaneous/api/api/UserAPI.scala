package api

import client.v1._
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Transactional._
import javax.ws.rs.{FormParam, Path, POST}
import javax.persistence.{NoResultException, Query}
import org.apache.commons.lang.StringUtils
import java.util.ArrayList
import org.apache.commons.codec.digest.DigestUtils
import java.io.{IOException, BufferedReader, InputStreamReader}
import net.liftweb.http.rest.RestHelper
import org.apache.openjpa.jdbc.kernel.exps.Val
import com.sportaneous.Model
import com.sportaneous.com.sportaneous.api.APIV1.{LoginResult, API_GetProfileResult, API_Response}
import net.liftweb.http.S
import com.sportaneous.hooks.HookManager

object UserAPI extends RestHelper{

   serve("v1.0" prefix {
    /**
     * /v1.0/login
     *
     * Parameters
     * ==========
     * - fbUID
     * - fbAccessToken
     * - fbSessionExpires
     * - email
     * - passwordMD5
     * - keepUserLoggedInMaxSeconds -- may be null
     *
     * Returns
     * =======
     * - status
     * - userID
     * - passcode -- null if keepUserLoggedInMaxSeconds is null
     * - profile
     *   - firstName
     *   - lastName
     *   - regionID
     *   - neighborhoodIDs
     *   - sportIDs
     */
     case "login" :: Nil, JsonGet _  =>
     {
      val fbUID = S.params("fbUID")
      val fbAccessToken =  S.params("fbAccessToken")
      val fbSessionExpires=S.params("fbSessionExpires")
      val email=  S.params("email")
      val passwordMD5 = S.params("passwordMD5")
      val keepUserLoggedInMaxSeconds = S.params("keepUserLoggedInMaxSeconds")

      var user: Option[User] = if (!fbUID.isEmpty) {
        Model.findHeadOption("User.findByFbUID","fbUID"-> fbUID.head)
      }
      else if (!email.isEmpty && !passwordMD5.isEmpty) {
        Model.findHeadOption("User.findByEmailAndPasswordHash","email"->email,"passwordHash"-> passwordMD5)
      }

      if (user.isEmpty) {
        API_Response(StatusCodes.LOGIN_FAILED)
      }

      var passcode: String = null
      if (!keepUserLoggedInMaxSeconds.isEmpty) {
        var maxSeconds: Long = keepUserLoggedInMaxSeconds.longValue
        passcode = PasscodeUtil.createAndPersistPasscode(em, user, maxSeconds)
      }
      API_Response(StatusCodes.SUCCESS,LoginResult(profile,passcode))
    }

    /**
     * /v1.0/autoLogin
     *
     * Parameters
     * ==========
     * - passcode
     *
     * Returns
     * =======
     * - status
     * - userID
     * - passcode -- the same value as the passcode parameter
     * - profile
     *   - firstName
     *   - lastName
     *   - regionID
     *   - neighborhoodIDs
     *   - sportIDs
     */
   case "autoLogin" :: Nil, JsonGet _ = {
       val passcode S.params("passcode")

      var userPasscode = Model.findHeadOption("UserPasscode.findByPasscode","passcode"-> passcode)

      if (!userPasscode.isEmpty) {
        var userPasswordHash: String = userPasscode.getUser.getPasswordHash
        var passcodePasswordHash: String = userPasscode.getPasswordHash
        if (!(userPasswordHash == passcodePasswordHash)) {
          userPasscode = null
        }
      }
      if (userPasscode.isEmpty) {
        API_Response(StatusCodes.LOGIN_FAILED)
      }
      var result: LoginResult = new LoginResult
      API_Response(StatusCodes.SUCCESS,LoginResult(profile,passcode))
    }

    /**
     * /v1.0/register
     *
     * Parameters
     * ==========
     * - regionID
     * - fbUID
     * - fbAccessToken
     * - fbSessionExpires
     * - firstName
     * - lastName
     * - email
     * - passwordMD5
     *
     * Returns
     * =======
     * - status
     * - userID
     *
     * Notes
     * =====
     * The only interesting corner case here is when a user attempts to register
     * without either (1) a fbUID that has already been registered or (2)
     * an email that has already been registered.  In case (1), we return a
     * status of 101, with the existing user's ID.  Treatment of case (2)
     * depends on the password provided.  If the password provided matches that
     * of the existing user, we return a status of 101 with the existing user's
     * ID.  If the password does NOT match the password on record, we return
     * a status of 102.
     */
    @POST
    @Path("register")
    @Transactional def register(@FormParam("regionID") regionID: Long, @FormParam("fbUID") fbUID:
    String, @FormParam("fbAccessToken") fbAccessToken: String, @FormParam("fbSessionExpires") fbSessionExpires: RESTTimestamp
                                , @FormParam("firstName") firstName: String, @FormParam("lastName") lastName: String, @FormParam("email") email: String,
                                @FormParam("passwordMD5") passwordMD5: String): APIResult = {
     val regionID = S.params("regionID")
      val fbUID = S.params("fbUID")
      val fbAccessToken = S.params("fbAccessToken")
      val fbSessionExpires = S.params("fbSessionExpires")
      val firstName = S.params("firstName")
      val lastName = S.params("lastName")
      val email = S.params("email")
      val passwordMD5 = S.params("passwordMD5")

      var query: Query = null
      var result: RegisterResult = null
      var user: Option[User] = null
      var fExistingUser: Boolean = false
      if (StringUtils.isNotBlank(fbUID)) {
        user= Model.findHeadOption("User.findByFbUID","fbUID"->fbUID)
      }
      else if (StringUtils.isNotBlank(email)) {
        user= Model.findHeadOption("User.findByEmail","email"-> email)
        if (!user.isEmpty) {
          if (passwordMD5.isEmpty || !StringUtils.equals(passwordMD5, user.getPasswordHash)) {

            result.status = StatusCodes.EMAIL_ADDRESS_ALREADY_REGISTERED

          }
        }
      }
      fExistingUser = (!user.isEmpty)
      if (user.isEmpty) {
        var region: Region = Model.find[Region](regionID)
        user = new User
        user.setEmail(email)
        user.setFirstName(firstName)
        user.setLastName(lastName)
        user.setNeighborhoods(new ArrayList[UserNeighborhood])
        user.setPasswordHash(passwordMD5)
        user.setRegion(region)
        user.setSports(new ArrayList[UserSports])
        Model.persist(user)
      }

      if (StringUtils.isNotBlank(fbUID)) {
        user.setFbAccessToken(fbAccessToken)
        user.setFbSessionExpires(fbSessionExpires.getCal)
        user.setFbUID(fbUID)
      }
      /** TODO TODO TODO
      result = new RegisterResult
      result.status = if (fExistingUser) StatusCodes.USER_ALREADY_REGISTERED else StatusCodes.SUCCESS
      result.profile = _p(user)
      result.userID = user.getUserID
       **/
      HookManager.runPostRegisterHook(user)
      return result
    }

    /**
     * /v1.0/webRegister
     *
     *
     * Parameters
     * ==========
     * - regionID
     * - fbUID
     * - fbAccessToken
     * - fbSessionExpires
     * - firstName
     * - lastName
     * - email
     * - passwordMD5
     *
     * Returns
     * =======
     * - status
     * - userID
     *
     * Notes
     * =====
     *
     */
    @POST
    @Path("webRegister")
    @Transactional def webRegister(@FormParam("regionID") regionID: Long, @FormParam("fbUID") fbUID: String, @FormParam("fbAccessToken") fbAccessToken: String, @FormParam("fbSessionExpires") fbSessionExpires: RESTTimestamp, @FormParam("firstName") firstName: String, @FormParam("lastName") lastName: String, @FormParam("email") email: String, @FormParam("passwordMD5") passwordMD5: String): APIResult = {
      var query: Query = null
      var result: RegisterResult = null
      var user: User = null
      var region: Region = null
      if (StringUtils.isBlank(email) || StringUtils.isBlank(passwordMD5)) {
        result = new RegisterResult
        result.status = StatusCodes.MISSING_REQUIRED_PARAMS
        return result
      }
      query = em.createNamedQuery("User.findByEmail")
      query.setParameter("email", email)
      try {
        query.getSingleResult
        result = new RegisterResult
        result.status = StatusCodes.EMAIL_ADDRESS_ALREADY_REGISTERED
        return result
      }
      catch {
        case e: NoResultException => {
        }
      }
      if (StringUtils.isNotBlank(fbUID)) {
        query = em.createNamedQuery("User.findByFbUID")
        query.setParameter("fbUID", fbUID)
        try {
          user = query.getSingleResult.asInstanceOf[User]
          if (StringUtils.isNotBlank(user.getEmail)) {
            result = new RegisterResult
            result.status = StatusCodes.FACEBOOK_ACCOUNT_ALREADY_REGISTERED
            return result
          }
        }
        catch {
          case e: NoResultException => {
          }
        }
      }
      region = em.find(classOf[Region], regionID)
      if (user == null) {
        user = new User
        user.setNeighborhoods(new ArrayList[UserNeighborhood])
        user.setSports(new ArrayList[UserSports])
      }
      user.setEmail(email)
      user.setFirstName(firstName)
      user.setLastName(lastName)
      user.setPasswordHash(passwordMD5)
      user.setRegion(region)
      em.persist(user)
      if (StringUtils.isNotBlank(fbUID)) {
        user.setFbAccessToken(fbAccessToken)
        user.setFbSessionExpires(fbSessionExpires.getCal)
        user.setFbUID(fbUID)
      }
      result = new RegisterResult
      result.status = StatusCodes.SUCCESS
      result.profile = _p(user)
      result.userID = user.getUserID
      HookManager.runPostRegisterHook(user)
      return result
    }

    /**
     * /v1.0/webResetPassword
     *
     * Parameters
     * ==========
     * - email
     *
     * Returns
     * =======
     * - status
     *
     * Notes
     * =====
     *
     */
    @POST
    @Path("webResetPassword")
    @Transactional def webResetPassword(@FormParam("email") email: String): APIResult = {
      var query: Query = null
      var user: User = null
      var result: APIResult = null
      var newPassword: String = null
      var name: String = null
      var subject: String = null
      var fmt: String = null
      var url: String = null
      var html: String = null
      if (StringUtils.isBlank(email)) {
        result = new APIResult
        result.status = StatusCodes.MISSING_REQUIRED_PARAMS
        return result
      }
      query = em.createNamedQuery("User.findByEmail")
      query.setParameter("email", email)
      try {
        user = query.getSingleResult.asInstanceOf[User]
      }
      catch {
        case e: NoResultException => {
          result = new APIResult
          result.status = StatusCodes.NO_MATCHING_USER
          return result
        }
      }
      newPassword = getRandomPassword(32)
      user.setPasswordHash(DigestUtils.md5Hex(newPassword))
      try {
        var isr: InputStreamReader = null
        var br: BufferedReader = null
        var sb: StringBuilder = null
        var s: String = null
        isr = new InputStreamReader(getClass.getResourceAsStream("/reset-password.html"))
        br = new BufferedReader(isr)
        sb = new StringBuilder
        while ((({
          s = br.readLine; s
        })) != null) {
          sb.append(s + "\n")
        }
        fmt = sb.toString
      }
      catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
      name = user.getFirstName + " " + user.getLastName
      subject = "We've Reset Your Password"
      url = "http://www.sportaneous.com/_ltp?_e=" + email + "&_tp=" + newPassword
      html = String.format(fmt, Array[AnyRef](user.getFirstName, url))
      try {
        EmailService.send(name, email, subject, null, html)
      }
      catch {
        case e: IOException => {
          System.out.println("Unable to send email: " + e.getMessage)
          e.printStackTrace
        }
      }
      result = new APIResult
      result.status = StatusCodes.SUCCESS
      return result
    }

    /**
     * /v1.0/webUpdatePassword
     *
     * Parameters
     * ==========
     * - userID
     * - oldPasswordMD5
     * - newPasswordMD5
     *
     * Returns
     * =======
     * - status
     *
     * Notes
     * =====
     *
     */
    @POST
    @Path("webUpdatePassword")
    @Transactional def webUpdatePassword(@FormParam("userID") userID: Long, @FormParam("oldPasswordMD5") oldPasswordMD5: String, @FormParam("newPasswordMD5") newPasswordMD5: String): APIResult = {
      var query: Query = null
      var user: User = null
      var result: APIResult = null
      user = em.find(classOf[User], userID)
      if (user.getPasswordHash == null || !(user.getPasswordHash == oldPasswordMD5)) {
        result = new APIResult
        result.status = StatusCodes.LOGIN_FAILED
        return result
      }
      user.setPasswordHash(newPasswordMD5)
      result = new APIResult
      result.status = StatusCodes.SUCCESS
      return result
    }

    /**
     * /v1.0/updateDeviceToken
     *
     * Parameters
     * ==========
     * - userID
     * - iosDeviceToken
     *
     * Returns
     * =======
     * - status
     */
    @POST
    @Path("updateDeviceToken")
    @Transactional def updateDeviceToken(@FormParam("userID") userID: Long, @FormParam("iosDeviceToken") iosDeviceToken: String): APIResult = {
      var user: User = null
      user = em.find(classOf[User], userID)
      if (iosDeviceToken != null) {
        user.setIosDeviceToken(iosDeviceToken)
      }
      return APIResult.SUCCESS
    }

    /**
     * /v1.0/updateFitnessGoal
     *
     * Parameters
     * ==========
     * - userID
     * - count
     * - period
     *
     * Returns
     * =======
     * - status
     */

    case "updateFitnessGoal" :: Nil, JsonPost _ =>{
      val userID = S.params("userID")
     val count = S.params("count")
     val period = S.params("period")

      val user = Model.find[User](userID)
      user.setGoalCount(count)
      user.setGoalPeriod(period)
      Model.persist(user) //??
      APIResult.SUCCESS
    }

  /**
     * /v1.0/updateProfile
     *
     * Parameters
     * ==========
     * - userID
     * - regionID
     * - neighborhoodID
     * - sportID
     *
     * Returns
     * =======
     * - status
     * - profile
     */
     case "updateProfile" :: Nil, JsonPost  _ =>
     {
      val userID = S.params("userID")
       val regionID= S.params("regionID")
       val neighborhoodID =S.params("neighborhoodID")
       val sportID= S.params("sportID")
      var user: User = null

      user = Model.find[User](userID).get

      if (!regionID.isEmpty) {
        region = Model.find[Region](regionID)
        user.setRegion(region)
      }

      if (!neighborhoodIDs.isEmpty) {
        var neighborhoods: List[UserNeighborhood] = null
        for (neighborhood <- user.getNeighborhoods) {
          em.remove(neighborhood)
        }
        neighborhoods = new ArrayList[UserNeighborhood]
        for (neighborhoodID <- neighborhoodIDs) {
          var tmp: Neighborhood = em.find(classOf[Neighborhood], neighborhoodID)
          if (tmp == null) {
            System.out.println("Error:" + neighborhoodID)
          }
          else {
            var neighborhood: UserNeighborhood = null
            neighborhood = new UserNeighborhood
            neighborhood.setNeighborhood(tmp)
            neighborhood.setUser(user)
            neighborhoods.add(neighborhood)
            em.persist(neighborhood)
          }
        }
        user.setNeighborhoods(neighborhoods)
      }
      if (!sportIDs.isEmpty) {
        var userSports: List[UserSports] = null
        for (userSport <- user.getSports) {
          em.remove(userSport)
        }
        userSports = new ArrayList[UserSports]
        for (sportID <- sportIDs) {
          var sport: Sport = null
          var userSport: UserSports = null
          sport = Sport.valueOf(sportID)
          userSport = new UserSports
          userSport.setSport(sport)
          userSport.setUser(user)
          userSports.add(userSport)
          em.persist(userSport)
        }
        user.setSports(userSports)
      }
      result = new API_UpdateProfileResult
      result.status = StatusCodes.SUCCESS
      result.profile = _p(user)
      return result
    }

     /**
     * /v1.0/getProfile
     *
     * Parameters
     * ==========
     * - userID
     *
     * Returns
     * =======
     * - status
     * - profile
     *
     */
    case "getProfile" :: Nil, JsonGet _ =>{
       S.params("userID")
      val user  = Model.find[User](userID).get
      API_Response(StatusCodes.SUCCESS,API_GetProfileResult(user))
    }

}