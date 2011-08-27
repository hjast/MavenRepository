package hooks

import akka.actor.Actor
import com.sportaneous.social.{FacebookAdapter, TwitterAdapter}
import com.sportaneous.hooks.HookManager.Messages._
import com.sportaneous.hooks.HookManager
import com.sportaneous.email.EmailUtility
import com.sportaneous.prediction.{SimplePredictor, PredictionDriver}
import org.jpaextension.manager.{EntityManagerWrapper, QueryHelper, UsesEntityManager, LocalEntityManagerFactory}
import scala.collection.JavaConversions._
import com.sportaneous.model.{GroupGameAssociationRank, Group, User, Game}

/**
 * The master of the hooks, which send to all hooks
 */
class PostGameJoinHooksMaster extends Actor
{

  var postGameHooks  = HookManager.Hooks.postGameJoinHooks.map(_.start())

  def receive =
  {
    case PostGameJoin(game,user) =>
    println("Starting Post Game Join Hooks")
    postGameHooks.foreach( hook =>  hook ! PostGameJoin(game,user))
  }

}

/**
 * An abstract post game create actor
 */
abstract class PostGameJoinHookActor extends Actor
{
  def hook(g:Game, u:User)

  def receive =
  {
    case PostGameJoin(game, u) => hook(game,u)
  }

}

class JoinEmailActor extends PostGameJoinHookActor
{
  override def hook(g:Game, u:User) = EmailUtility.sendPostJoinMessage(g,u);

}

//redirect this to the create game actor
class CalculateGameGroupUpdateAssociationActor extends PostGameJoinHookActor
{
  object Model extends LocalEntityManagerFactory(SimplePredictor.persistenceUnit) with UsesEntityManager with QueryHelper with EntityManagerWrapper
  {
     val cachedEm = openEM()
    /* This functions returns all of some entity */
    def findAll[T](implicit m: scala.reflect.Manifest[T]): scala.collection.mutable.Buffer[T] = createQuery[T]("Select a FROM " + m.erasure.getSimpleName + " a").findAll

     def em =  cachedEm
     val factory = this
  }

  override def hook(g:Game,u:User) = {
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


