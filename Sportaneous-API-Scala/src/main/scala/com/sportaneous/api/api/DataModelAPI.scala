package api

import client.v1._
import javax.ws.rs.{Path, GET, QueryParam}
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Transactional._
import javax.persistence.{NoResultException, Query}
import net.liftweb.http.rest.RestHelper._
import com.sportaneous.com.sportaneous.api.APIV1._
import com.sportaneous.Model
import net.liftweb.http.S
import org.codehaus.jackson.annotate.JsonGetter

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 8/26/11
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */

object DataModelAPI {

   serve("v1.0" prefix {
  /**
      * /v1.0/getFacilities
      *
      * Parameters
      * ==========
      * - regionID
      *
      * Returns
      * =======
      * - status
      * - facilities
      *   - facilityID
      *   - neighborhoodID
      *   - name
      *   - and others
      */
     case "getFacilities" :: Nil JsonGet _ =>  {
       val regionID = S.params("regionID")
       val region = Model.find[Region](regionID)
       val facilities = Model.findAll[Facility]("Facility.findByRegion", "region" -> region)
       API_Response(StatusCodes.SUCCESS, API_GetFacilitiesResult(facilities))
     }

     /**@GET
     @Path( " g e t N e i g h b o r h o o d s " )
      **/
     case "getNeighborhoods" :: Nil JsonGet _ => {
       val regionID = S.params("regionID")
       val region = Model.find[Region](regionID).get
       val neighborhoods = Model.findAll[Neighborhood]("Neighborhood.findByRegion", "region" -> region)
       val superneighborhoods = Model.findAll[SuperNeighborhood]("SuperNeighborhood.findByRegion", "region" -> region)
       API_Response(StatusCodes.SUCCESS, API_GetNeighborhoodsResult(neighborhoods, superneighborhoods))
     }


     /**
      * /v1.0/getRegions
      *
      * Parameters
      * ==========
      * <none>
      *
      * Returns
      * =======
      * - status
      * - regions
      *   - regionID
      *   - name
      */

     case "getRegions" :: Nil JsonGet _ => {
       val regions = Model.findAll[Region]
       API_Response(StatusCodes.SUCCESS, API_GetRegionsResult(regions))
     }

     /**
      * /v1.0/getSports
      *
      * Parameters
      * ==========
      * - regionID
      *
      * Returns
      * =======
      * - status
      * - sports
      */
     case "getSports" :: Nil JsonGet _ => {
        val regionID = S.params("regionID")
        val regionID = S.params("v")

       val region = Model.find[Region](regionID)
       val regionSport = Model.findAll("RegionSport.findByRegion", "region" -> region)


       val sports = new List[Sport]
       for (rs <- regionSport) {
         sports.add(rs.getSport)
       }
       if (v == null || v < 1.4) {
         {
           var it: Iterator[Sport] = sports.iterator
           while (it.hasNext) {
             var _sport: Sport = null
             _sport = it.next
             _sport match {
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
       API_Response(StatusCodes.SUCCESS,API_GetSportsResult(sports))
     }


     def isUserInvited(user: User, game: Game): Boolean = {
       var query: Query = em.createNamedQuery("GameParticipant.findParticipantByGameUser")
       query.setParameter("user", user)
       query.setParameter("game", game)
       var gp: GameParticipant = null
       try {
         gp = query.getSingleResult.asInstanceOf[GameParticipant]
       }
       catch {
         case e: NoResultException => {
           return false
         }
       }
       if (gp == null) {
         return false
       }
       else {
         return gp.getInvited != null && gp.getInvited
       }
     }

}