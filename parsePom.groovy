def pomFile = new File("pom.xml")
def pom = new XmlSlurper().parse(pomFile)


pom.dependencyManagement.dependencies.children().each { dep ->
	def scope = dep.scope == null || dep.scope.size() == 0 ? "" : "//$dep.scope"
	key = dep.artifactId.toString().replaceAll('-','_')
	println "$key:\t\t\t'$dep.groupId:$dep.artifactId:$dep.version', $scope"
}
