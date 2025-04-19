APP_NAME := legacy-spring-app
ORG_NAME := example

.PHONY: container
container: package
	docker build -t $(ORG_NAME)/$(APP_NAME) . 
	docker run -d --name $(APP_NAME) -p 8080:8080 $(ORG_NAME)/$(APP_NAME)

.PHONY: package
package: clean
	mvn package
	
.PHONY: clean
clean:
	mvn clean
	docker stop $(APP_NAME) || true
	docker rm $(APP_NAME) || true
	docker image rm $(ORG_NAME)/$(APP_NAME) || true

.PHONY: rewrite
rewrite:
	mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
	-Drewrite.exportDatatables=true \
	-Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-spring:RELEASE \
	-Drewrite.activeRecipes=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_2,org.openrewrite.java.spring.NoRequestMappingAnnotation,com.example.ReplaceSystemOutWithLogger