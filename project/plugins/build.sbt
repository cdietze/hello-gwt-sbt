
resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

//libraryDependencies <+= sbtVersion("com.github.siasia" %% "xsbt-web-plugin" % ("0.1.0-"+ _ ))
libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % ("0.1.0-"+v))
