pluginManagement {
//    val springBootVersion: String by settings
//    val springDependencyManagementVersion: String by settings

//    plugins {
//        id("org.springframework.boot") version springBootVersion
//        id("io.spring.dependency-management") version springDependencyManagementVersion
//    }
}


rootProject.name = "developer-diary"
include(":app:user-api")
include(":system:core-web")
include(":system:user-webclient")
include(":domain:user-domain")
include(":support:util")
