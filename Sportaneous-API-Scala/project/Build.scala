import java.io.File


import _root_.sbt._
import Keys._
//import _root_.sbt.FileUtilities._

import com.github.siasia.WebPlugin._

/**
 * TODO For right now just use the command line tools
 */
object BuildSettings {
  val buildOrganization = "sportaneous"
  val buildScalaVersion = "2.9.0-1"
  val buildVersion = "1.0.0"

  val buildSettings = Defaults.defaultSettings ++ Seq(organization := buildOrganization,
    scalaVersion := buildScalaVersion,
    version := buildVersion)
  // shellPrompt  := ShellPrompt.buildShellPrompt)

}

// Shell prompt which show the current project, git branch and build version
// git magic from Daniel Sobral
object ShellPrompt {

  object devnull extends ProcessLogger {
    def info(s: => String) {}

    def error(s: => String) {}

    def buffer[T](f: => T): T = f
  }

  val current = """\*\s+(\w+)""".r

  def gitBranches = ("git branch --no-color" lines_! devnull mkString)

  val buildShellPrompt = {
    (state: State) => {
      val currBranch = current findFirstMatchIn gitBranches map (_ group (1)) getOrElse "-"
      val currProject = Project.extract(state).currentProject.id
      "%s:%s:%s> ".format(currProject, currBranch, BuildSettings.buildVersion)
    }
  }

}

object Resolvers {

  val javaNet = JavaNet1Repository
  val mavenLocal = "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
  val sunrepo = "Sun Maven2 Repo" at "http://download.java.net/maven/2"
  val akkaRepo = "Akka Repo" at "http://akka.io/repository/"
  val multiverse = "Multiverse Releases" at "http://multiverse.googlecode.com/svn/maven-repository/releases/"
  val guicey = "GuiceyFruit" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
  val databinder = "DataBinder" at "http://databinder.net/repo"
  val configgy = "Configgy" at "http://www.lag.net/repo"
  val reflections = "Reflections" at "http://reflections.googlecode.com/svn/repo"
  val t_repo = "t_repo" at "http://tristanhunt.com:8081/content/groups/public/"
  val otherRepor = "some_repo" at "http://www2.ph.ed.ac.uk/maven2"

  val twitter4j = "twitter4j" at "http://twitter4j.org/maven2"
    val zentrope ="zentrope" at "http://zentrope.com/maven"
   val otherResolvers = Seq(javaNet, mavenLocal,sunrepo,akkaRepo,multiverse,guicey,databinder,configgy,t_repo,twitter4j,otherRepor)

}

object Dependencies {
  val jerseyVersion = "1.0.2"
  val jettyVersion = "6.1.22"
  val springVersion = "2.5.5"
  val akkaVersion = "1.1"
  val jacksonVersion = "1.6.0"
  val liftVersion = "2.4-M3"
  val rogueVersion = "1.0.15"


  val jetty6 = "org.mortbay.jetty" % "jetty" % jettyVersion % "jetty"
  val jetty6Test = "org.mortbay.jetty" % "jetty" % jettyVersion % "test"
  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.3.0.v20110203" % "jetty"
  val jetty7security = "org.eclipse.jetty" % "jetty-security" % "7.3.0.v20110203"

  val guava = "com.google.guava" % "guava" % "r09"
  val client2 = "com.sportaneous" % "sportaneous-api-client" % "1.0-SNAPSHOT"

  //val model = "com.sportaneous" % "sportaneous-model" % "1.0-SNAPSHOT"

  val persistence = "javax.persistence" % "persistence-api" % "1.0"
  val json = "org.json" % "json" % "20090211"
  val httpcomp = "org.apache.httpcomponents" % "httpclient" % "4.0.2"
  val commonLang = "commons-lang" % "commons-lang" % "2.5"
  val hibernate = "org.hibernate" % "hibernate-c3p0" % "3.3.0.SP1"
  val commonLogging = "commons-logging" % "commons-logging" % "1.0.4"
  val cglib = "cglib" % "cglib-nodep" % "2.1_3"
  val hibernateEntityManager = "org.hibernate" % "hibernate-entitymanager" % "3.3.2.GA"
  val commonsCodec = "commons-codec" % "commons-codec" % "1.4"

  val mysql = "mysql" % "mysql-connector-java" % "5.1.6"
  val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.6.1" % "test"
  val slf4jNonTest = "org.slf4j" % "slf4j-api" % "1.6.1"

  val jerseySpring = "com.sun.jersey.contribs" % "jersey-spring" % jerseyVersion
  val jerseyServer = "com.sun.jersey" % "jersey-server" % jerseyVersion
  val jerseyLib = Seq(jerseySpring, jerseyServer)

  val spring = "org.springframework" % "spring" % springVersion
  val springTest = "org.springframework" % "spring-test" % springVersion
  val springLib = Seq(spring, springTest)

  val jackson = "org.codehaus.jackson" % "jackson-jaxrs" % jacksonVersion
  val jacksonxc = "org.codehaus.jackson" % "jackson-xc" % jacksonVersion
  val jacksonLib = Seq(jackson, jacksonxc)

  val akkaActors = "se.scalablesolutions.akka" % "akka-actor" % akkaVersion
  val typedActors = "se.scalablesolutions.akka" % "akka-typed-actor" % akkaVersion
  val akkaRemote = "se.scalablesolutions.akka" % "akka-remote" % akkaVersion
  val akkaLib = Seq(akkaActors, typedActors, akkaRemote)

  val rogue = "com.foursquare" %% "rogue" % rogueVersion

  val webkit = "net.liftweb" %% "lift-webkit" % liftVersion
  val widgets = "net.liftweb" %% "lift-widgets" % liftVersion
  val record = "net.liftweb" %% "lift-record" % liftVersion
  val wizard = "net.liftweb" %% "lift-wizard" % liftVersion
  val lift_json = "net.liftweb" %% "lift-json" % liftVersion

  val lift_imaging = "net.liftweb" %% "lift-imaging" % liftVersion
  val lift_mongo = "net.liftweb" %% "lift-mongodb-record" % liftVersion
  val liftDeps = Seq(webkit, widgets, record, wizard, lift_json, lift_imaging, rogue, lift_mongo)

  val jpaExtension = "org.scala-libs" % "jpaextension" % "0.0.2-SNAPSHOT"


  val logback = "ch.qos.logback" % "logback-classic" % "0.9.26"

  val servlet = "javax.servlet" % "servlet-api" % "2.5"
  //val jetty6    = "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default"
  val junit = "junit" % "junit" % "4.5"

  // val specs     = "org.scala-tools.testing" %% "specs" % "1.6.6" % "test->default"
  //val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.RC0" % "test"

  //val clientLib = "sportaneous" % "sportaneous-api-client_2.7.7" % "1.0"
  val seleniumServerCoreless = "org.openqa.selenium.server" % "selenium-server-coreless" % "1.0-20081010.060147"
  val seleniumCore = "org.openqa.selenium.core" % "selenium-core" % "1.0-20080914.225453"

  val selenium = "org.seleniumhq.selenium" % "selenium" % "2.0rc2" % "test->default"
  val seleniumServer = "org.seleniumhq.selenium" % "selenium-server" % "2.0rc2" % "test->default"
  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1"
  val testingLibs = Seq(selenium, seleniumServer, scalatest)

  val reflectionDep = "org.reflections" % "reflections" % "0.9.5-RC2"


  val knockoff = "com.tristanhunt" % "knockoff_2.8.1" % "0.7.3-15"

  val tweet4j = "org.twitter4j" % "twitter4j-core" % "2.2.3"

  val scribe ="org.scribe" % "scribe" % "1.1.2"
  val coffeescript =  "com.zentrope" %% "xsbt-coffeescript-plugin" % "1.0"
}

object EBSCommands
{

  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }


  /**
    * This creates a script so you can update new QA version script that you should run right afterwards.
    */
   val createQAUploadScriptCommand: sbt.Command = sbt.Command.single("create-new-qa-version-script") {
     (state: State, version: String) => println(version);
      //copyModel(state.baseDir)
      // result.get.toEither.right.get.foreach(p=>println(p._2))
      val warResult: Option[Result[File]] = Project.evaluateTask(packageWar,state)

      println("War name " + warResult.get.toEither.right.get)

     printToFile(new File("scripts/deployToQA.sh"))(p => {
       p.println("#!/bin/sh")
       p.println("rvm use 1.9.2")
        p.println("cp -rf "+ warResult.get.toEither.right.get+" /tmp/"+version+".war")
       p.println("s3cmd put /tmp/"+version+".war s3://elasticbeanstalk-us-east-1-773777220056")
       p.println("elastic-beanstalk-create-application-version -a \"sportaneousqa\" -l "+version+" -s elasticbeanstalk-us-east-1-773777220056/"+version+".war")
       p.println("elastic-beanstalk-update-environment -e \"sportaneousqa\" -l "+version)
     })
       println("Please check out script to push to QA server and use it before you prepare-webapp again.")
     //deleteModel(state.baseDir)
     state
   }

     /**
    * This creates a script so you can update new QA version script that you should run right afterwards.
    */
   val createProdEBSUploadScriptCommand: sbt.Command = sbt.Command.single("create-new-ebsprod-version-script") {
     (state: State, version: String) => println(version);
      //copyModel(state.baseDir)
      // result.get.toEither.right.get.foreach(p=>println(p._2))
      val warResult: Option[Result[File]] = Project.evaluateTask(packageWar,state)

      println("War name " + warResult.get.toEither.right.get)

     printToFile(new File("scripts/deployToProd.sh"))(p => {
       p.println("#!/bin/sh")
       p.println("rvm use 1.9.2")
        p.println("cp -rf "+ warResult.get.toEither.right.get+" /tmp/prod"+version+".war")
       p.println("s3cmd put /tmp/prod"+version+".war s3://elasticbeanstalk-us-east-1-773777220056")
       p.println("elastic-beanstalk-create-application-version -a \"sportaneousLiftWeb\" -l "+version+" -s elasticbeanstalk-us-east-1-773777220056/prod"+version+".war")
       p.println("elastic-beanstalk-update-environment -e \"SportaneousProduction\" -l "+version)
     })
       println("Please check out script to push to QA server and use it before you prepare-webapp again.")
     //deleteModel(state.baseDir)
     state
   }

   /**
    * This will change the version on the QA Servr
    * ADD Autocomplete
    * */
   val changeQaVersion:sbt.Command=sbt.Command.single("change-qa-version")
   {
      (state: State, version: String) =>
     //check version variable, make sure
      /*if(version!="qa"|| version != "prod")
      {
         println("Version is either qa or prod.")
      }
      */
      //git push to something
      //Use elastic-beanstalk-change-environment
      //check to see if version exists

       (("elastic-beanstalk-update-environment -e sportaneousqa -l "+version)!)
     state
   }

  /**
    * This will change the version on the QA Servr
    * */
   val changeProdVersion:sbt.Command=sbt.Command.single("change-prod-version")
   {
      (state: State, version: String) =>
     //check version variable, make sure
      /*if(version!="qa"|| version != "prod")
      {
         println("Version is either qa or prod.")
      }
      */
      //git push to something
      //Use elastic-beanstalk-change-environment
      //check to see if version exists

       (("elastic-beanstalk-update-environment -e SportaneousProduction -l "+version)!)
     state
   }

   /**
    * This will work like git branch -a.
    * For now we will
    */
   val listVersions:sbt.Command= sbt.Command.single("list-versions")
   {
     (state: State, version: String) =>
     val envs :  String= ("elastic-beanstalk-describe-applications" !! )

     //application names
     for(line <- envs.linesIterator)
     {
       println("Line")
       var split =line.split('|')
       println(if(split.size < 5){"nothing"}else{split(1)})
     }

     //create a map of <application name>-><versions> then show

     //application versions
     for(line <- envs.linesIterator)
     {
       println("Line")
       var split =line.split('|')
       println(if(split.size < 5){"nothing"}else{split(5)})
     }




     state
   }

   val pushQAToProd:sbt.Command= sbt.Command.single("push-qa-to-prod")
   {
     (state: State, version: String) =>
     val envs :String = ("elastic-beanstalk-describe-environments" !!)
     for( line <- envs.linesIterator)
     {
      var delem = line.split('|')
       if(delem.size > 5)
       {
        println(delem(6))
       }
     }

     state
   }

  /* val revertLastVersion:sbt.Command =sbt.Command.single("revert-last-version")
   {
   }
   */


}

object MyBuild extends Build {



  // All internal projects must be listed in `projects`.
  override lazy val projects = Seq(root)

  import Resolvers._
  import Dependencies._
  import BuildSettings._
  import EBSCommands._
  import com.github.siasia.WebPlugin._

    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
  def copyModel(base:File) =
  {
      //Copy the model
      var copyFrom = file(System.getenv("MODEL_DIRECTORY"))/".."/"java"/"src"/"main"/"com"/"sportaneous"/"model"
      var copyTo = base/"src"/"java"/"main"/"com"/"sportaneous"/"model"
      println("Copying from "+copyFrom +" to "+copyTo)
      IO.copyDirectory(copyFrom, copyTo)

  }

  def deleteModel(base:File) =
  {
    var copyTo = base/"src"/"java"/"main"/"com"/"sportaneous"/"model"
    IO.delete(copyTo)
  }
  /**
   * This changes the jetty-web to be correct. It then makes a script which you have to run.
   * I can't run it from Sbt since you can't see the status
   * @param version : This is the version you want to call it on the EC2 instance.
   */
  val createProdUploadScriptProduction: sbt.Command = sbt.Command.single("create-new-production-version-script") {
    (state:State, version:String) => {


      println("Creating new script for uploading to production.");

      //TODO TODO TODO Move model files to Jar from Model project and package
      //TODO TODO TODO CHange persistence so it does not have jar-file stuff

      //Copy the production jetty-web
      IO.copyFile(state.baseDir/"src"/"main"/"webapp"/"WEB-INF"/"jetty-web-prod.xml",state.baseDir/"src"/"main"/"webapp"/"WEB-INF"/"jetty-web.xml")
     // copyModel(state.baseDir)

      val result: Option[Result[Seq[(File, String)]]] = Project.evaluateTask( prepareWebapp, state)
      // result.get.toEither.right.get.foreach(p=>println(p._2))c
      result.get.toEither.right.get.foreach(p=>println(p._2))

      val warResult: Option[Result[File]] = Project.evaluateTask(packageWar,state)

      println("War name " + warResult.get.toEither.right.get)

      printToFile(new File("scripts/deployToProduction.sh"))(p => {
        p.println("#!/bin/sh")
        p.println("#Running this script to upload and set version")
         //so we can countine dev
        p.println("cp -rf "+ warResult.get.toEither.right.get+" /tmp/sportaneousUploadTmp.war")
        p.println("sudo scp -i ~/.ssh/sportaneousdev.pem /tmp/sportaneousUploadTmp.war root@api.sportaneous.com:~/web"+version+".war;")
        p.println("ssh -i ~/.ssh/sportaneousdev.pem root@api.sportaneous.com 'cp -f ~/web"+version+".war ~/new-web.war';")
      })



      println("Please check out script and use it before you prepare-webapp again.")
      //copy the jetty-web back
      IO.copyFile(state.baseDir/"src"/"main"/"webapp"/"WEB-INF"/"jetty-web-dev.xml",state.baseDir/"src"/"main"/"webapp"/"WEB-INF"/"jetty-web.xml")
      //delete model dir
      deleteModel(state.baseDir)

      //TODO TODO TODO DELETE MODEL FILES AND CHANGE PERSISTENCE BACK TO USE MODEL JAR
      state
    }

  }



  //TODO Make command sticky and also keep track of API used in SettingKey
  /**
   * This changes the API to another API
   */
  val changeAPIDB: sbt.Command = sbt.Command.single("change-api") {
    (state:State, spot:String) => println("Changing to "+ spot);

    //Add here for more types of stuff
    val configName :Option[String] = spot match
    {
      case "local" => Some("development.props")
      case "prod" => Some("production.default.props")
      case "qa" => Some("staging.default.props")
      case _ => None
    }
    configName match
    {
      case Some(_) => IO.copyFile(state.baseDir /"project"/"devenvs"/configName.get, state.baseDir /"src"/"main"/"resources"/"props"/"default.props")
      case _ => println("Please choose either local, prod or qa");
    }
    state

  }


  //
  val modelJar = TaskKey[Unit]("model-jar", "set up the symlink to the model.jar'")


  val modelJarTask = modelJar := {
    "scripts/setUpJarPath.sh" !
  }




  val ServerDeps: Seq[sbt.ModuleID] = jerseyLib ++ akkaLib ++ springLib ++ jacksonLib ++
    Seq(guava, persistence, json, httpcomp, commonLang, hibernate, commonLogging,
      cglib, hibernateEntityManager, mysql, client2, slf4j, slf4jNonTest, jetty6/*, model*/)

  val ClientDeps: Seq[sbt.ModuleID] = jacksonLib ++ Seq(json, httpcomp, commonsCodec)

  val WebDeps: Seq[sbt.ModuleID] = Seq(logback, servlet, junit, commonLang, client2, /* model,*/
    hibernate, hibernateEntityManager, persistence, jpaExtension, reflectionDep, jetty6,
    jetty6Test, mysql, knockoff, tweet4j, scribe) ++ liftDeps ++ testingLibs

  //Root Project
  lazy val root = Project("web", file("."), settings = buildSettings ++ com.github.siasia.WebPlugin.webSettings ++ Seq(modelJarTask)) settings
    (libraryDependencies := WebDeps, jettyPort := 80, resolvers := otherResolvers, commands ++= Seq(createQAUploadScriptCommand,changeAPIDB,
      createProdUploadScriptProduction,listVersions, pushQAToProd,changeQaVersion, changeProdVersion))

}

