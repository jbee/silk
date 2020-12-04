import com.github.sormuras.bach.project.ProjectInfo;
import com.github.sormuras.bach.project.ProjectInfo.Tweak;

@ProjectInfo(
    name = "purejin",
    version = "8-ea",
    compileModulesForJavaRelease = 8,
    tweaks = {
      @Tweak(
          tool = "javac",
          args = {"-encoding", "UTF-8", "-g", "-parameters"}),
      @Tweak(
          tool = "javadoc",
          args = {
            "-encoding", "UTF-8",
            "-use",
            "-linksource",
            "-notimestamp",
            "-quiet",
            "-Xdoclint:-missing",
            //
            "-group", "API",
            "se.jbee.inject:se.jbee.inject.api:se.jbee.inject.bind:se.jbee.inject.lang",
            //
            "-group", "Container",
            "se.jbee.inject.bootstrap:se.jbee.inject.container",
            //
            "-group", "Add-ons",
            "se.jbee.inject.action:se.jbee.inject.event:se.jbee.inject.convert"
          })
    })
module build {
  requires com.github.sormuras.bach;
}