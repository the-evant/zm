plugins {
    java
    id("com.gradleup.shadow") version "9.5.0" apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

pluginManager.apply("com.gradleup.shadow")

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("fr.shuvly:core:1.0-D")
    implementation("fr.hashtek.spigot:HashGui:R0.2.0-SNAPSHOT")
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        val props = mapOf(
            "version" to project.version.toString(),
            "description" to (project.description ?: "")
        )

        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    named<Jar>("jar") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    named<Jar>("shadowJar") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveClassifier.set("")
    }

    build {
        dependsOn("shadowJar")
    }
}

val localServers = mapOf(
    "zm" to "/Users/ly/mc/srv/event/zm/plugins",
)

localServers.forEach { (serverName, path) ->
    tasks.register<Copy>("deploy_$serverName") {
        group = "deployment"
        description = "Deploys plugin to $serverName server"

        dependsOn("shadowJar")
        into(path)

        from(tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }) {
            into(".")
        }

        from("maps") {
            include("**/*.yml")
            into("zm/maps")
        }
    }
}

tasks.register("deployAll") {
    group = "deployment"
    description = "Deploys plugin to all local servers"

    dependsOn(localServers.keys.map { "deploy_$it" })
}
