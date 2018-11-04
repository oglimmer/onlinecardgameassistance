module.exports = {

  config: {
    Name: "OnlineCardGameAssistance",
    Vagrant: {
      Box: 'ubuntu/xenial64',
      Install: 'maven openjdk-8-jdk-headless docker.io'
    },
    JavaVersions: [ "1.8" ]
  },

  software: {

    "couchdb4j": {
      Source: "mvn",
      Git: "https://github.com/mbreese/couchdb4j.git",
      Dir: "$$TMP$$/couchdb4j",
      Mvn: {
        Goal: '-DskipTests=true install'
      }
    },

    "bcg": {
      Source: "mvn",      
      Artifact: "target/bcg.war"
    },

    "couchdb": {
      Source: "couchdb",
      CouchDB: {
        Schema: "swlcg"
      }
    },

    "tomcat": {
      Source: "tomcat",
      Deploy: "bcg"
    }
  }
}
