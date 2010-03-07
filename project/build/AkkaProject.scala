/*-------------------------------------------------------------------------------
   Copyright (C) 2009-2010 Scalable Solutions AB <http://scalablesolutions.se>

   ----------------------------------------------------
   -------- sbt buildfile for the Akka project --------
   ----------------------------------------------------

   Akka implements a unique hybrid of:
    * Actors , which gives you:
    * Simple and high-level abstractions for concurrency and parallelism.
    * Asynchronous, non-blocking and highly performant event-driven programming 
      model.
    * Very lightweight event-driven processes (create ~6.5 million actors on 
      4 G RAM).
    * Supervision hierarchies with let-it-crash semantics. For writing highly 
      fault-tolerant systems that never stop, systems that self-heal.
    * Software Transactional Memory (STM). (Distributed transactions coming soon).
    * Transactors: combine actors and STM into transactional actors. Allows you to 
      compose atomic message flows with automatic rollback and retry.
    * Remoting: highly performant distributed actors with remote supervision and 
      error management.
    * Cluster membership management.

  Akka also has a set of add-on modules:
    * Persistence: A set of pluggable back-end storage modules that works in sync 
      with the STM.
    * Cassandra distributed and highly scalable database.
    * MongoDB document database.
    * Redis data structures database (upcoming)
    * REST (JAX-RS): Expose actors as REST services.
    * Comet: Expose actors as Comet services.
    * Security: Digest and Kerberos based security.
    * Microkernel: Run Akka as a stand-alone kernel.
    
-------------------------------------------------------------------------------*/

import sbt._

class AkkaParent(info: ProjectInfo) extends ParentProject(info) {
  // repos
  val sunjdmk = "sunjdmk" at "http://wp5.e-taxonomy.eu/cdmlib/mavenrepo"
  val databinder = "DataBinder" at "http://databinder.net/repo"
  val configgy = "Configgy" at "http://www.lag.net/repo"
  val codehaus = "Codehaus" at "http://repository.codehaus.org"
  val codehaus_snapshots = "Codehaus Snapshots" at "http://snapshots.repository.codehaus.org"
  val jboss = "jBoss" at "http://repository.jboss.org/maven2"
  val guiceyfruit = "GuiceyFruit" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
  val embeddedrepo = "embedded repo" at "http://guice-maven.googlecode.com/svn/trunk"
  val google = "google" at "http://google-maven-repository.googlecode.com/svn/repository"
  val m2 = "m2" at "http://download.java.net/maven/2"

  // project versions
  val JERSEY_VERSION = "1.1.5"
  val ATMO_VERSION = "0.6-SNAPSHOT"
  val CASSANDRA_VERSION = "0.5.0"

  // project defintions
  lazy val akka_java_util = project("akka-util-java", "akka-java-util", new AkkaJavaUtilProject(_))
  lazy val akka_util = project("akka-util", "akka-util", new AkkaUtilProject(_))
  lazy val akka_core = project("akka-core", "akka-core", new AkkaCoreProject(_), akka_util, akka_java_util)
  lazy val akka_amqp = project("akka-amqp", "akka-amqp", new AkkaAMQPProject(_), akka_core)
  lazy val akka_rest = project("akka-rest", "akka-rest", new AkkaRestProject(_), akka_core)
  lazy val akka_comet = project("akka-comet", "akka-comet", new AkkaCometProject(_), akka_rest)
  lazy val akka_patterns = project("akka-patterns", "akka-patterns", new AkkaPatternsProject(_), akka_core)
  lazy val akka_security = project("akka-security", "akka-security", new AkkaSecurityProject(_), akka_core)
  lazy val akka_persistence = project("akka-persistence", "akka-persistence", new AkkaPersistenceParentProject(_))
  lazy val akka_cluster = project("akka-cluster", "akka-cluster", new AkkaClusterParentProject(_))
  lazy val akka_kernel = project("akka-kernel", "akka-kernel", new AkkaKernelProject(_), akka_core, akka_rest, akka_persistence, akka_cluster, akka_amqp, akka_security, akka_comet)

  // examples
  lazy val akka_fun_test = project("akka-fun-test-java", "akka-fun-test-java", new AkkaFunTestProject(_), akka_kernel)
  lazy val akka_samples = project("akka-samples", "akka-samples", new AkkaSamplesParentProject(_))

  def akkaHome = {
    val home = System.getenv("AKKA_HOME")
    if (home == null) throw new Error("You need to set the $AKKA_HOME environment variable to the root of the Akka distribution")
    home
  }

  // subprojects
  class AkkaCoreProject(info: ProjectInfo) extends DefaultProject(info) {
    val sjson = "sjson.json" % "sjson" % "0.4" % "compile"
    val werkz = "org.codehaus.aspectwerkz" % "aspectwerkz-nodeps-jdk5" % "2.1" % "compile"
    val werkz_core = "org.codehaus.aspectwerkz" % "aspectwerkz-jdk5" % "2.1" % "compile"
    val commons_io = "commons-io" % "commons-io" % "1.4" % "compile"
    val dispatch_json = "net.databinder" % "dispatch-json_2.7.7" % "0.6.4" % "compile"
    val dispatch_http = "net.databinder" % "dispatch-http_2.7.7" % "0.6.4" % "compile"
    val sbinary = "sbinary" % "sbinary" % "0.3" % "compile"
    val jackson = "org.codehaus.jackson" % "jackson-mapper-asl" % "1.2.1" % "compile"
    val jackson_core = "org.codehaus.jackson" % "jackson-core-asl" % "1.2.1" % "compile"
    val voldemort = "voldemort.store.compress" % "h2-lzf" % "1.0" % "compile"
    val javautils = "org.scala-tools" % "javautils" % "2.7.4-0.1" % "compile"
    val netty = "org.jboss.netty" % "netty" % "3.2.0.BETA1" % "compile"
    // testing
    val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
    val junit = "junit" % "junit" % "4.5" % "test"
  }

  class AkkaUtilProject(info: ProjectInfo) extends DefaultProject(info) {
    val werkz = "org.codehaus.aspectwerkz" % "aspectwerkz-nodeps-jdk5" % "2.1" % "compile"
    val werkz_core = "org.codehaus.aspectwerkz" % "aspectwerkz-jdk5" % "2.1" % "compile"
    val configgy = "net.lag" % "configgy" % "1.4.7" % "compile"
  }

  class AkkaJavaUtilProject(info: ProjectInfo) extends DefaultProject(info) {
    val guicey = "org.guiceyfruit" % "guice-core" % "2.0-beta-4" % "compile"
    val protobuf = "com.google.protobuf" % "protobuf-java" % "2.2.0" % "compile"
    val multiverse = "org.multiverse" % "multiverse-alpha" % "0.4-SNAPSHOT" % "compile"
  }

  class AkkaAMQPProject(info: ProjectInfo) extends DefaultProject(info) {
    val rabbit = "com.rabbitmq" % "amqp-client" % "1.7.2"
  }

  class AkkaRestProject(info: ProjectInfo) extends DefaultProject(info) {
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
    val jersey = "com.sun.jersey" % "jersey-core" % JERSEY_VERSION % "compile"
    val jersey_server = "com.sun.jersey" % "jersey-server" % JERSEY_VERSION % "compile"
    val jersey_json = "com.sun.jersey" % "jersey-json" % JERSEY_VERSION % "compile"
    val jersey_contrib = "com.sun.jersey.contribs" % "jersey-scala" % JERSEY_VERSION % "compile"
    val jsr311 = "javax.ws.rs" % "jsr311-api" % "1.1" % "compile"
  }

  class AkkaCometProject(info: ProjectInfo) extends DefaultProject(info) {
    val grizzly = "com.sun.grizzly" % "grizzly-comet-webserver" % "1.9.18-i" % "compile"
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
    val atmo = "org.atmosphere" % "atmosphere-annotations" % ATMO_VERSION % "compile"
    val atmo_jersey = "org.atmosphere" % "atmosphere-jersey" % ATMO_VERSION % "compile"
    val atmo_runtime = "org.atmosphere" % "atmosphere-runtime" % ATMO_VERSION % "compile"
  }

  class AkkaPatternsProject(info: ProjectInfo) extends DefaultProject(info) {
    // testing
    val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
    val junit = "junit" % "junit" % "4.5" % "test"
  }

  class AkkaSecurityProject(info: ProjectInfo) extends DefaultProject(info) {
    val annotation = "javax.annotation" % "jsr250-api" % "1.0"
    val jersey_server = "com.sun.jersey" % "jersey-server" % JERSEY_VERSION % "compile"
    val jsr311 = "javax.ws.rs" % "jsr311-api" % "1.1" % "compile"
    val lift_util = "net.liftweb" % "lift-util" % "1.1-M6" % "compile"
    // testing
    val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
    val junit = "junit" % "junit" % "4.5" % "test"
    val mockito = "org.mockito" % "mockito-all" % "1.8.1" % "test"
  }

  class AkkaPersistenceCommonProject(info: ProjectInfo) extends DefaultProject(info) {
    val thrift = "com.facebook" % "thrift" % "1.0" % "compile"
    val commons_pool = "commons-pool" % "commons-pool" % "1.5.1" % "compile"
  }

  class AkkaRedisProject(info: ProjectInfo) extends DefaultProject(info) {
    val redis = "com.redis" % "redisclient" % "1.1" % "compile"
    override def testOptions = TestFilter((name: String) => name.endsWith("Test")) :: Nil
  }

  class AkkaMongoProject(info: ProjectInfo) extends DefaultProject(info) {
    val mongo = "org.mongodb" % "mongo-java-driver" % "1.1" % "compile"
    override def testOptions = TestFilter((name: String) => name.endsWith("Test")) :: Nil
  }

  class AkkaCassandraProject(info: ProjectInfo) extends DefaultProject(info) {
    val cassandra = "org.apache.cassandra" % "cassandra" % CASSANDRA_VERSION % "compile"
    val high_scale = "org.apache.cassandra" % "high-scale-lib" % CASSANDRA_VERSION % "test"
    val cassandra_clhm = "org.apache.cassandra" % "clhm-production" % CASSANDRA_VERSION % "test"
    val commons_coll = "commons-collections" % "commons-collections" % "3.2.1" % "test"
    val google_coll = "com.google.collections" % "google-collections" % "1.0" % "test"
    val slf4j = "org.slf4j" % "slf4j-api" % "1.5.8" % "test"
    val slf4j_log4j = "org.slf4j" % "slf4j-log4j12" % "1.5.8" % "test"
    val log4j = "log4j" % "log4j" % "1.2.15" % "test"    
    override def testOptions = TestFilter((name: String) => name.endsWith("Test")) :: Nil
  }

  class AkkaPersistenceParentProject(info: ProjectInfo) extends ParentProject(info) {
    lazy val akka_persistence_common = project("akka-persistence-common", "akka-persistence-common", new AkkaPersistenceCommonProject(_), akka_core)
    lazy val akka_persistence_redis = project("akka-persistence-redis", "akka-persistence-redis", new AkkaRedisProject(_), akka_persistence_common)
    lazy val akka_persistence_mongo = project("akka-persistence-mongo", "akka-persistence-mongo", new AkkaMongoProject(_), akka_persistence_common)
    lazy val akka_persistence_cassandra = project("akka-persistence-cassandra", "akka-persistence-cassandra", new AkkaCassandraProject(_), akka_persistence_common)
  }

  class AkkaJgroupsProject(info: ProjectInfo) extends DefaultProject(info) {
    val jgroups = "jgroups" % "jgroups" % "2.8.0.CR7" % "compile"
  }

  class AkkaShoalProject(info: ProjectInfo) extends DefaultProject(info) {
    val shoal = "shoal-jxta" % "shoal" % "1.1-20090818" % "compile"
    val shoal_extra = "shoal-jxta" % "jxta" % "1.1-20090818" % "compile"
  }

  class AkkaClusterParentProject(info: ProjectInfo) extends ParentProject(info) {
    lazy val akka_cluster_jgroups = project("akka-cluster-jgroups", "akka-cluster-jgroups", new AkkaJgroupsProject(_), akka_core)
    lazy val akka_cluster_shoal = project("akka-cluster-shoal", "akka-cluster-shoal", new AkkaShoalProject(_), akka_core)
  }

  class AkkaKernelProject(info: ProjectInfo) extends DefaultProject(info) with AssemblyProject {
    val jersey_server = "com.sun.jersey" % "jersey-server" % JERSEY_VERSION % "compile"
    val atmo = "org.atmosphere" % "atmosphere-annotations" % ATMO_VERSION % "compile"
    val atmo_jersey = "org.atmosphere" % "atmosphere-jersey" % ATMO_VERSION % "compile"
    val atmo_runtime = "org.atmosphere" % "atmosphere-runtime" % ATMO_VERSION % "compile"

    val assemblyDistPath = Path.fromFile(new java.io.File(akkaHome + "/dist/akka-" + version.toString + ".jar"))
    def mainMethod = Some("se.scalablesolutions.akka.Main")
    override def packageOptions: Seq[PackageOption] = MainClass("se.scalablesolutions.akka.Main") :: Nil
  }

  // examples
  class AkkaFunTestProject(info: ProjectInfo) extends DefaultProject(info) {
    val protobuf = "com.google.protobuf" % "protobuf-java" % "2.2.0"
    val grizzly = "com.sun.grizzly" % "grizzly-comet-webserver" % "1.9.18-i" % "compile"
    val jersey_server = "com.sun.jersey" % "jersey-server" % JERSEY_VERSION % "compile"
    val jersey_json = "com.sun.jersey" % "jersey-json" % JERSEY_VERSION % "compile"
    val jersey_atom = "com.sun.jersey" % "jersey-atom" % JERSEY_VERSION % "compile"
    // testing
    val junit = "junit" % "junit" % "4.5" % "test"
    val jmock = "org.jmock" % "jmock" % "2.4.0" % "test"
  }


  class AkkaSampleChatProject(info: ProjectInfo) extends DefaultProject(info)

  class AkkaSampleLiftProject(info: ProjectInfo) extends DefaultProject(info) {
    val lift = "net.liftweb" % "lift-webkit" % "1.1-M6" % "compile"
    val lift_util = "net.liftweb" % "lift-util" % "1.1-M6" % "compile"
    val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
    // testing
    val jetty = "org.mortbay.jetty" % "jetty" % "6.1.6" % "test"
    val junit = "junit" % "junit" % "4.5" % "test"
  }

  class AkkaSampleRestJavaProject(info: ProjectInfo) extends DefaultProject(info)

  class AkkaSampleRestScalaProject(info: ProjectInfo) extends DefaultProject(info) {
    val jsr311 = "javax.ws.rs" % "jsr311-api" % "1.1" % "compile"
  }

  class AkkaSampleSecurityProject(info: ProjectInfo) extends DefaultProject(info) {
    val jsr311 = "javax.ws.rs" % "jsr311-api" % "1.1" % "compile"
    val jsr250 = "javax.annotation" % "jsr250-api" % "1.0"
  }

  class AkkaSamplesParentProject(info: ProjectInfo) extends ParentProject(info) {
    lazy val akka_sample_chat = project("akka-sample-chat", "akka-sample-chat", new AkkaSampleChatProject(_), akka_kernel)
    lazy val akka_sample_lift = project("akka-sample-lift", "akka-sample-lift", new AkkaSampleLiftProject(_), akka_kernel)
    lazy val akka_sample_rest_java = project("akka-sample-rest-java", "akka-sample-rest-java", new AkkaSampleRestJavaProject(_), akka_kernel)
    lazy val akka_sample_rest_scala = project("akka-sample-rest-scala", "akka-sample-rest-scala", new AkkaSampleRestScalaProject(_), akka_kernel)
    lazy val akka_sample_security = project("akka-sample-security", "akka-sample-security", new AkkaSampleSecurityProject(_), akka_kernel)
    
    //FileUtilities.copyFile(assemblyOutputPath, assemblyDistPath, log)
  }
}

trait AssemblyProject extends BasicScalaProject {
  val assemblyDistPath: Path

  def assemblyExclude(base: PathFinder) = base / "META-INF" ** "*"
  def assemblyOutputPath = outputPath / assemblyJarName
  def assemblyJarName = artifactID + "-assembly-" + version + ".jar"
  def assemblyTemporaryPath = outputPath / "assembly-libs"
  def assemblyClasspath = runClasspath
  def assemblyExtraJars = mainDependencies.scalaJars

  def assemblyPaths(tempDir: Path, classpath: PathFinder, extraJars: PathFinder, exclude: PathFinder => PathFinder) = {
    val (libs, directories) = classpath.get.toList.partition(ClasspathUtilities.isArchive)
    for(jar <- extraJars.get ++ libs) FileUtilities.unzip(jar, tempDir, log)
    val base = (Path.lazyPathFinder(tempDir :: directories) ##)
    (descendents(base, "*") --- exclude(base)).get
  }

  lazy val assembly = assemblyTask(assemblyTemporaryPath, assemblyClasspath, assemblyExtraJars, assemblyExclude) dependsOn(compile) describedAs("Packaging assembly")
 
  lazy val dist = task({ log.info("Creating distribution " + assemblyDistPath); FileUtilities.copyFile(assemblyOutputPath, assemblyDistPath, log) }) dependsOn(assembly) describedAs("Creating distribution")
  
  def assemblyTask(tempDir: Path, classpath: PathFinder, extraJars: PathFinder, exclude: PathFinder => PathFinder) = {
    packageTask(Path.lazyPathFinder(assemblyPaths(tempDir, classpath, extraJars, exclude)), assemblyOutputPath, packageOptions)
  }
}