package hooks

import akka.actor.Actor
import com.sportaneous.model.Game
import com.sportaneous.social.{FacebookAdapter, TwitterAdapter}
import com.sportaneous.hooks.HookManager.Messages._
import com.sportaneous.prediction.PredictionDriver
import com.sportaneous.hooks.HookManager

/**
 * The master of the hooks, which send to all hooks
 */
class PostGameUpdateHooksMaster extends Actor
{

  var postGameHooks  = HookManager.Hooks.postGameUpdateHooks.map(_.start())

  def receive =
  {
    case PostGameUpdate(game) =>
    println("Starting Post Game Create Hooks")
    postGameHooks.foreach( hook =>  hook ! PostGameUpdate(game))
  }

}

/**
 * An abstract post game create actor
 */
abstract class PostGameUpdateHookActor extends Actor
{
  def hook(g:Game)

  def receive =
  {
    case PostGameUpdate(game) => hook(game)
  }

}
