//
// Boot.scala
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package bootstrap.liftweb

import _root_.api._
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers.TimeSpan
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.http.provider.servlet._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.com.sportaneous.api.client._
import _root_.com.sportaneous.api.client.v1._
import _root_.com.sportaneous.snippet._
import _root_.com.sportaneous.snippet.AutoLoginManager._
import com.sun.xml.internal.ws.wsdl.writer.document.Port
import javax.net.ssl.HostnameVerifier
import com.sportaneous.model.User
import org.reflections.Reflections
import javax.persistence.Entity
import org.hibernate.util.FilterHelper
import org.reflections.util._
import net.liftweb.widgets.tablesorter._
import scala.collection.JavaConversions._
import org.reflections.scanners.{SubTypesScanner, ResourcesScanner, TypeAnnotationsScanner}
import net.liftweb.widgets.autocomplete.AutoComplete
import net.liftweb.sitemap.LocPath._
import com.sportaneous.snippet.rest._
import com.sportaneous.util.DebugUtils
import com.sportaneous.model._

import net.liftweb.sitemap.LocPath._

object SportaneousLiftInfo
{
  var sportaneousPUName=""
}
/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {

  def boot = {
    println(Props.get("mongodb.host", "no-host") +
      ":" + Props.get("mongodb.name", "no-name") +
      ":" + Props.get("mongodb.user", "no-user") +
      ":" + Props.get("mongodb.pass", "no-pass"))

    MongoDBConfig.init


    LiftRules.htmlProperties.default.set((r: Req) =>new Html5Properties(r.userAgent))

    // Search for snippets.
    LiftRules.addToPackages("com.sportaneous")

    DebugUtils.printRed(LiftRules.snippets)
    DebugUtils.printRed(LiftRules.snippetNamesToSearch.apply)

    LiftRules.setSiteMap(siteMap)

    // Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    // Set up a stateful API to handle the game list/map view.
    LiftRules.dispatch.append(DataModelAPI)
    LiftRules.dispatch.append(GamesAPI)
    LiftRules.dispatch.append(GamesDiscoveryAPI)
    LiftRules.dispatch.append(Legacy)
    LiftRules.dispatch.append(MessagesAPI)
    LiftRules.dispatch.append(UserAPI)
    LiftRules.dispatch.append(NewAPIService)


    // Lift's XHTML Mime-Type breaks the Facebook JavaScript SDK.
    LiftRules.useXhtmlMimeType = false

  //  LiftRules.htmlProperties.default.set((r: Req) =>new Html5Properties(r.userAgent))

    LiftRules.noticesAutoFadeOut.default.set(
      Vendor(
        (notices: NoticeType.Value) => {
          notices match {
            case NoticeType.Notice => Full((TimeSpan(10000), TimeSpan(1000)))
            case _ => Empty
          }
        }
      )
    )
  }

  /**
   * Set up our site map.
   */
  def siteMap: SiteMap = {

    object RequireHTTP extends Loc.EarlyResponse(() => {
      for {
        r <- S.containerRequest
        req <- Box !! r if req.scheme == "https"
      } {
        var url = "http://" + req.serverName;
        if (req.serverPort == 9443) {
          url += ":9090"
        }
        url += req.contextPath + req.uri
        S.redirectTo(url)
      }
      Empty
    })

    object RequireHTTPS extends Loc.EarlyResponse(() => {
      for {
        r <- S.containerRequest
        req <- Box !! r if req.scheme == "http"
      } {
        var url = "https://" + req.serverName;
        if (req.serverPort == 9090) {
          url += ":9443"
        }
        //TODO Should we pass through parameters(??)
        //Especially password stuff
        //url += req.contextPath + req.uri
        val query = req.queryString match {
         case Full(s) => "?" + s
       case _ => ""
       }
      url += req.contextPath + req.uri + query
        S.redirectTo(url)
      }
      Empty
    })

    object IfLoggedIn extends Loc.If(
      () => Users.isUserLoggedIn,
      () => RedirectResponse("/login")
    )

    object UnlessLoggedIn extends Loc.Unless(
      () => Users.isUserLoggedIn,
      () => RedirectResponse("/home")
    )

    object IfAdmin extends Loc.If(
      ()=> Users.isAdmin,
      () => RedirectResponse("/login")
    )

    object IfStreetTeam extends Loc.If(
      ()=> Users.isStreetTeam,
      () => RedirectResponse("/login")
    )

    // An early response which will be ignored.
    object NullEarlyResponse extends Loc.EarlyResponse( () => Empty )

    // This is the global SiteMap hook for auto-login.  It will only be active
    // for the first couple of requests of a new session, during which time it
    // will either definitely succeed at login or definitely fail.  After that
    // it will effectively be a cheap no-op.  Note the case where an HTTP
    // request gets re-directed to an HTTPS connection, which may then be
    // re-directed back to an HTTP request.  This dance is necessary for auto-
    // login to access a secure cookie.
    val autoLoginEarlyResponder: PartialFunction[Box[Req], Loc.AnyLocParam] = {
      case Full(req) if (loginStatus == LoginStatus.TryAutoLogin) => {
        tryAutoLogin(req.request) match {
          case TryAutoLoginStatus.TrySecureConnection => RequireHTTPS   // try again over HTTPS
          case _ => NullEarlyResponse   // definitely logged in or definitely logged out
        }
      }
    }

    )
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }

  /**
   * Log the user out, and redirect to the home page.
   */
  private def logout(request: HTTPRequest) = {
    // We need the logout request to come over a secure connection so that we
    // can clear secure cookies.  If this request wasn't secure, then we
    // redirect back to the same URL, but specifying HTTPS.  Note that the
    // redirect after a successful logout must use "S.redirectTo()" -- returning
    // a "RedirectResponse()" prevents the cookies from being cleared.
    request.scheme match {
      case "https" => Users.logout; S.redirectTo("/splash?redirect=1")
      case _ => () => Full(RedirectResponse(secureUrl(request.url)))
    }
  }

  private def secureUrl(url: String) = {
    url.replace("http://", "https://").replace(":9090", ":9443")
  }

  private def loginWithTemporaryPassword = {
    var e = S.param("_e")
    var tp = S.param("_tp")
    if (e.isDefined && tp.isDefined) {
      var client = new SportaneousAPIClient()
      var result = client.login(null, null, null, e.get, tp.get, null)
      result.status match {
        case StatusCodes.SUCCESS =>
          Users.s_profile.set(Option(result.profile))
          Users.s_userID.set(Option(result.profile.userID.longValue))
          Users.s_tempPassword.set(Option(tp.get))
          S.notice("Welcome back, " + result.profile.firstName + ".  Please change your password.")
          S.redirectTo("/change-password")
        case _ =>
          S.error("An unexpected error occurred.")
          S.redirectTo("/")
      }
    } else {
        S.redirectTo("/")
    }
  }

}
