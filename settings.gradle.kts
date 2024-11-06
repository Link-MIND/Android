enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
  includeBuild("build-logic")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven(url = "https://devrepo.kakao.com/nexus/content/groups/public/")
  }
}

rootProject.name = "Link-Mind"
include(":app")
include(":core:network")
include(":core:common")
include(":core:model")
include(":core:ui")
include(":core:auth")
include(":core:authimpl")
include(":core:datastore")
include(":core:designsystem")
include(":domain:oauthdomain")
include(":domain:timer")
include(":domain:user")
include(":domain:category")
include(":domain:link")
include(":domain:home")
include(":data:oauthdata")
include(":data:timer")
include(":data:user")
include(":data:category")
include(":data:home")
include(":data:link")
include(":data-remote:timer")
include(":data-remote:user")
include(":data-remote:category")
include(":data-remote:link")
include(":data-remote:home")
include(":feature:home")
include(":feature:maincontainer")
include(":feature:login")
include(":feature:clip")
include(":feature:mypage")
include(":feature:timer")
include(":feature:savelink")
include(":feature:share")
include(":feature:search")
