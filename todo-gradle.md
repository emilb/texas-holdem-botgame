Gradle conversion
=====================

**ToDo**

x Better organization for common sub project settings
x Enable filtering
x Hook in rebelGenerate, initLogDir etc in one of the build steps
- Logging for Jetty is misplaced
- Fix (x)intellij and eclipse settings
- Common versioning
- Result artifact names (server.jar is not good)
- Add help and info task
- FindBugs, Emma, CheckStyle, PMD, Cobertura and Sonar plugins
- Reporting
- Site ála Maven reporting
- Release management with version increment, github tagging etc
x Organize common dependencies in lists, for example all Logging libs
- Jenkins integration with tasks to copy resources for deployment, downloads etc
- How to package non-java projects?
- Install common artefacts in maven-repo (how to create pom.xml?)
- Figure out how to use profiles for different types of builds
- Build of client implementations (zip package)
- How to increment version in java-client's pom?
- Add jslint etc to nodejs-client
- Build of projects in labs


Structure changes
====================

x Move client examples to separate folder
- Create new folder for: {integration-tests, performance-tests}
- Create new project for static web
- Add JSLint, CSS merge, JS minify etc to static web project
- Let the server only expose RESTful interface
- Let the server document RESTful services via https://github.com/wordnik/swagger-ui
- Let the server notify some things via websockets (tournament updates, current live game updates, server status etc)
- Breakout java serverbots to own projects under folder: serverbots
