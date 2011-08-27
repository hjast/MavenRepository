package hooks

import akka.actor.Actor
import com.sportaneous.social.{FacebookAdapter, TwitterAdapter}
import com.sportaneous.hooks.HookManager.Messages._
import com.sportaneous.hooks.HookManager
import org.jpaextension.manager.{EntityManagerWrapper, QueryHelper, UsesEntityManager, LocalEntityManagerFactory}
import scala.collection.JavaConversions._
import com.sportaneous.prediction.{SimplePredictor, PredictionDriver}
import com.sportaneous.model.{GroupGameAssociationRank, Group, Game}
import java.lang.Double

/**
 * The master of the hooks, which send to all hooks
 */
class PostGameCreateHooksMaster extends Actor
{

  var postGameHooks  = HookManager.Hooks.postGameCreateHooks.map(_.start())

  def receive =
  {
    case PostGameCreate(game) =>
    println("Starting Post Game Create Hooks")
    postGameHooks.foreach( hook =>  hook ! PostGameCreate(game))
  }

}

/**
 * An abstract post game create actor
 */
abstract class PostGameCreateHookActor extends Actor
{
  def hook(g:Game)

  def receive =
  {
    case PostGameCreate(game) => hook(game)
  }

}

/**
 * A post to twitter actor
 */
class TwitterPostActor extends PostGameCreateHookActor{
  override def hook(g:Game) = TwitterAdapter.postGameToTwitter(g)
}


/**
 * A post to facebook actor
 */
class FacebookPostActor extends PostGameCreateHookActor{
  override def hook(g:Game)= FacebookAdapter.postGameToFacebook(g)
}

/**
 * A call to the predictor
 */
class PredictPostActor extends PostGameCreateHookActor{
  override def hook(g:Game) = PredictionDriver.runPredictor(g)
}


object GroupGameAssociateAlgos
{
  def calculateGroupGameRank(g:Game,grp:Group):Double=
  {
    var partOfGroup = 0
    val userListID = grp.getMembers.map(_.getUser.getUserID)
    //the count of how many participants are part of the group
    val parts = g.getParticipants
    val partCount = parts.count(part => userListID.contains(part.getUser.getUserID))
    println(partCount)

    val rank : Double  = partCount * ( partCount / parts.size())

    println("rank "+rank)
    rank

  }
}

class CalculateGameGroupAssociationActor extends PostGameCreateHookActor
{
  object Model extends LocalEntityManagerFactory(SimplePredictor.persistenceUnit) with UsesEntityManager with QueryHelper with EntityManagerWrapper
  {
     val cachedEm = openEM()
    /* This functions returns all of some entity */
    def findAll[T](implicit m: scala.reflect.Manifest[T]): scala.collection.mutable.Buffer[T] = createQuery[T]("Select a FROM " + m.erasure.getSimpleName + " a").findAll

     def em =  cachedEm
     val factory = this
  }

  override def hook(g:Game) = {
    Model.withTrxAndCommit{
    Model.findAll[Group].foreach
      {
        group =>
          var rank = new GroupGameAssociationRank
          rank.setGame(g)
          rank.setGroup(group)
          rank.setRank(GroupGameAssociateAlgos.calculateGroupGameRank(g,group))

      }
    }
  }
}




