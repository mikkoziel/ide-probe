import org.jetbrains.sbtidea.SbtIdeaPlugin
import org.jetbrains.sbtidea.packaging.PackagingKeys.{packageArtifactZip, packageArtifactZipFile}
import org.jetbrains.sbtidea.packaging.PackagingPlugin
import org.jetbrains.sbtidea.tasks.structure.render.ProjectStructureVisualizerPlugin
import sbt.{Compile, Project, file}
import sbt.Keys.resourceGenerators
import sbt.internal.DslEntry

object IdeaPluginAdapter {
  private val removedPlugins =
    List(SbtIdeaPlugin, PackagingPlugin, ProjectStructureVisualizerPlugin)

  implicit class ProjectExtension(project: Project) {
    def usesIdeaPlugin(plugin: Project): Project = {
      project
        .dependsOn(Project.classpathDependency(plugin))
        .settings(Compile / resourceGenerators += (plugin / packageArtifactZip).map(List(_)).taskValue)
    }

    def usesIdeaPlugins(basePlugin: Project, alternativeVersion: Project): Project = {
      project
        .usesIdeaPlugin(basePlugin)
        .settings(Compile / resourceGenerators += (alternativeVersion / packageArtifactZip).map(List(_)).taskValue)
    }

    def usesIdeaPlugins(basePlugin: Project): Project = {
      project
        .usesIdeaPlugin(basePlugin)
    }

    def enableIdeaPluginDevelopment: Project = {
      disableDefaultIdeaPlugins.enablePlugins(IdeaPluginDevelopment)
    }

    def disableIdeaPluginDevelopment: Project = {
      disableDefaultIdeaPlugins
    }

    private def disableDefaultIdeaPlugins: Project = {
      project.disablePlugins(removedPlugins: _*)
    }
  }

  def disableIdeaPluginDevelopment(): DslEntry = {
    DslEntry.DslDisablePlugins(removedPlugins)
  }
}
