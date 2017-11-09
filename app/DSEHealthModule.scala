import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import service.DSECheckPlugin
import service.log.LogPlugin


class DSEHealthModule extends AbstractModule {
  def configure() = {
    val pluginBinder = Multibinder.newSetBinder(binder(), classOf[DSECheckPlugin])
    pluginBinder.addBinding().to(classOf[LogPlugin])
//    bind(classOf[DSECheckPlugin]).to(classOf[LogAnalyzer])
  }
}

