package hooks

import akka.actor.Actor
import com.sportaneous.hooks.HookManager
import com.sportaneous.hooks.HookManager.Messages.PostGameUpdate._
import com.sportaneous.model.{Game,User}
import com.sportaneous.hooks.HookManager.Messages.{PostRegister, PostGameUpdate}
import com.sportaneous.api.APIService
import com.sportaneous.email.EmailUtility

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 8/14/11
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * The master of the hooks, which send to all hooks
 */
class PostRegisterHooksMaster extends Actor
{

  var postRegisterHooks  = HookManager.Hooks.postRegisterHooks.map(_.start())

  def receive =
  {
    case PostRegister(user) =>
    println("Starting Post Register Create Hooks")
    postRegisterHooks.foreach( hook =>  hook ! PostRegister(user))
  }

}

/**
 * An abstract post game create actor
 */
abstract class PostRegisterHookActor extends Actor
{
  def hook(u:User)

  def receive =
  {
    case PostRegister(user) => hook(user)
  }

}

class WelcomeEmailActor extends PostRegisterHookActor
{

  override def hook(u:User) =
  {
    EmailUtility.sendWelcomeEmail(u);
  }
}