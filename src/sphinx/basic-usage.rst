.. _`Sphinx`: http://sphinx.pocoo.org/
.. _`Sphinx first steps tutorial`: http://sphinx.pocoo.org/tutorial.html
.. _`conf.py`: http://sphinx.pocoo.org/config.html
.. _`Sphinx' examples page`: http://sphinx.pocoo.org/examples.html
.. _`reStructured Text`: http://docutils.sf.net/rst.html
.. _`Werkzeug`: http://werkzeug.pocoo.org/docs/
.. _`Werkzeug's github page`: https://github.com/mitsuhiko/werkzeug/tree/master/docs
.. _`Celery`: http://docs.celeryproject.org/en/latest/index.html
.. _`Celery's github page`: http://docs.celeryproject.org/en/latest/index.html

.. _contents:

Using the Plugin
================

The *sphinx-gradle-plugin* looks for *.rst* files in the folder structure provided as part of plugin
configuration within your ``build.gradle`` file. The default location where the plugin will look for the files
is ``src/sphinx``.

The folder specified will contain the `reStructured Text`_ source files plus any additional things like themes
and configuration. The `Sphinx first steps tutorial`_ gives a good introduction into the required tasks.
Basically what you need is:

- A configuration file called `conf.py`_ that defines the theme and other options (such as which output formats
  etc.)
- The documentation files in reStructured Text format.
- Additional files such as static files (images etc.), usually in a ``_static`` sub directory.
- Optionally, a customized theme in a sub directory called ``_theme``

For good examples of documentation, see `Sphinx' examples page`_.

To apply this plugin, add the following to your ``build.gradle`` file:

.. parsed-literal::

  buildscript {
    repositories {
      mavenLocal()
      mavenCentral()
    }
    dependencies {
      classpath group: 'kr.motd.gradle', name: 'sphinx-gradle-plugin', version: '\ |release|\ '
    }
  }

  apply plugin: 'kr.motd.sphinx'

Now you can build your project web site using the ``site`` or ``sphinx`` task::

  ./gradlew site

This will generate the documentation in the `build/site` folder.
