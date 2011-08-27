package hooks

import akka.actor.Actor._

import com.sportaneous.prediction.PredictActor._
import com.sportaneous.social.{FacebookAdapter, TwitterAdapter}
import com.sportaneous.model.{User, Game}
import com.sportaneous.hooks.HookManager.Messages._
import akka.actor.{ActorRef, Actor}

//TODO Make sure all hooks work
/** Singletons for all the hook actors */
object HookManager {

  /** List of hook actors */
  //When necessary we can make these Lists of actor refs
  object Actors
  {
    var postGameCreateHookMaster: ActorRef =  actorOf(new PostGameCreateHooksMaster ()).start()
    var postGameUpdateHookMaster: ActorRef = actorOf(new PostGameUpdateHooksMaster()).start()
    var postGameJoinHookMaster: ActorRef = actorOf(new PostGameJoinHooksMaster()).start()
    var postGameBailHookMaster: ActorRef = actorOf(new PostGameBailHooksMaster()).start()
    var postGameRegisterHookMaster: ActorRef = actorOf(new PostRegisterHooksMaster()).start()
  }

  object Messages
  {
    case class PostGameCreate(game:Game)
    case class PostGameJoin(game:Game, user:User)
    case class PostGameBail(game:Game, user:User)
    case class PostGameUpdate(game:Game)
    case class PostRegister(user:User)
  }

  object Hooks
  {
    var postGameCreateHooks:Seq[ActorRef] = Seq(actorOf[TwitterPostActor],actorOf[PredictPostActor],actorOf[CalculateGameGroupAssociationActor])
    //
    //PredictionDriver.addTrueInstanceToModel(game, user);
    var postGameJoinHooks:Seq[ActorRef] = Seq(actorOf[JoinEmailActor],actorOf[CalculateGameGroupUpdateAssociationActor])
    var postGameBailHooks:Seq[ActorRef] = Seq(actorOf[CalculateGameGroupBailAssociationActor])
    var postGameUpdateHooks:Seq[ActorRef]=Seq()
    var postRegisterHooks: Seq[ActorRef] = Seq(actorOf[WelcomeEmailActor])

  }

  /**
   * Java accessors
    */
  def runPostGameCreateHook(g:Game)=
  {
    Actors.postGameCreateHookMaster ! PostGameCreate(g)
  }

  //TODO Make sure you send what has been updated
  def runPostGameUpdateHook(g:Game) =
  {
    Actors.postGameUpdateHookMaster ! PostGameUpdate(g)
  }

  def runPostGameJoinHook(g:Game, u:User) =
  {
    Actors.postGameJoinHookMaster ! PostGameJoin(g, u)
  }

  def runPostRegisterHook(u:User) =
  {
    Actors.postGameRegisterHookMaster ! PostRegister(u)
  }

  def runPostGameBailHook(g:Game, u:User)=
  {
    Actors.postGameBailHookMaster ! PostGameBail(g,u)
  }

}