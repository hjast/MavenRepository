package api

import client.v1.APIResult
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Transactional._
import javax.persistence.Query
import java.util.ArrayList
import com.sportaneous.model.{WaitListEntry, Facility, Neighborhood}
import net.liftweb.http.rest.RestHelper._

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 8/26/11
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */

object Legacy {
   serve("v1.0" prefix {
  /**
     * /v1.0/mergeNeighborhoods
     *
     * Description
     * ===========
     * [TO REMOVE BEFORE SHIPPING]
     *
     * Parameters
     * ==========
     * from�
     * to
     *
     * Returns
     * =======
     * none
     */
    @GET
    @Path("mergeNeighborhoods")
    @Transactional(readOnly = false) def mergeNeighborhoods(@QueryParam("from") fromNeighborhoodIDs: List[Long], @QueryParam("to") toNeighborhoodID: Long): APIResult = {
      var fromNeighborhoods: List[Neighborhood] = null
      var toNeighborhood: Neighborhood = null
      var query: Query = null
      var facilities: List[Facility] = null
      fromNeighborhoods = new ArrayList[Neighborhood]
      for (fromNeighborhoodID <- fromNeighborhoodIDs) {
        fromNeighborhoods.add(em.find(classOf[Neighborhood], fromNeighborhoodID))
      }
      toNeighborhood = em.find(classOf[Neighborhood], toNeighborhoodID)
      query = em.createQuery("SELECT f FROM Facility f")
      facilities = query.getResultList
      for (facility <- facilities) {
        var neighborhood: Neighborhood = null
        neighborhood = facility.getNeighborhood
        if (neighborhood != null && fromNeighborhoods.contains(neighborhood)) {
          facility.setNeighborhood(toNeighborhood)
        }
      }
      for (fromNeighborhood <- fromNeighborhoods) {
        em.remove(fromNeighborhood)
      }
      return APIResult.SUCCESS
    }

    /**
     * /v1.0/mapFacilitiesToNeighborhoods
     *
     * Description
     * ===========
     * [TO REMOVE BEFORE SHIPPING]
     *
     * Parameters
     * ==========
     * none
     *
     * Returns
     * =======
     * none
     */
    @GET
    @Path("mapFacilitiesToNeighborhoods")
    @Transactional(readOnly = false) def mapFacilitiesToNeighborhoods: APIResult = {
      var query: Query = null
      var facilities: List[Facility] = null
      query = em.createQuery("SELECT f FROM Facility f")
      facilities = query.getResultList
      for (facility <- facilities) {
        if (facility.getLat ne 0.0 && facility.getLng ne 0.0) {
          var n: Neighborhood = null
          n = getNeighborhoodForLocation(facility.getLat, facility.getLng)
          facility.setNeighborhood(n)
        }
      }
      return APIResult.SUCCESS
    }


    /**
     * /v1.0/addToWaitList
     *
     * Parameters
     * ==========
     * - email
     * - lat
     * - lng
     * - city
     * - name*
     * - zip*
     * - ip*
     *
     * Returns
     * =======
     * - status
     */
    @POST
    @Path("addToWaitList")
    @Transactional def addToWaitList(@FormParam("email") email: String, @FormParam("lat") lat: Double, @FormParam("lng") lng: Double, @FormParam("city") city: String, @FormParam("name") name: String, @FormParam("zip") zip: String, @FormParam("ip") ip: String): APIResult = {
      var entry: WaitListEntry = null
      entry = new WaitListEntry
      entry.setCity(city)
      entry.setEmail(email)
      entry.setIp(ip)
      entry.setLat(lat)
      entry.setLng(lng)
      entry.setName(name)
      entry.setZip(zip)
      em.persist(entry)
      return APIResult.SUCCESS
    }
   }
}