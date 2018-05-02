.. _contents:

Using the Plugin
================

The *sphinx-gradle-plugin* looks for *.rst* files in the folder structure provided as part of plugin
configuration within your ``build.gradle`` file. The default location where the plugin will look for the files
is ``src/site/sphinx``.

The folder specified will contain the  `reStructured Text <http://www.sphinx-doc.org/en/master/usage/restructuredtext/basics.html>`_
source files plus any additional things like themes and configuration. The `Getting started <http://www.sphinx-doc.org/en/master/usage/quickstart.html>`_
gives a good introduction into the required tasks. Basically what you need is:

- A configuration file called `conf.py <http://www.sphinx-doc.org/en/master/config.html>`_ that defines the
  theme and other options
- The documentation files in reStructured Text format.
- Additional files such as static files (images etc.), usually in a ``_static`` subdirectory.
- Optionally, a customized theme in a subdirectory called ``_theme``

To apply this plugin, add the following to your ``build.gradle`` file:

.. parsed-literal::

  plugins {
    id "kr.motd.sphinx" version "\ |release|\ "
  }

  // Apply the 'base' plugin, which adds the 'site' task.
  // Note: You do not need to apply the 'base' plugin if you applied
  //       other plugin that extends 'base', such as 'java'.
  apply plugin: 'base'

.. hint::

  You can also use the traditional ``buildscript`` approach.
  See `plugins.gradle.org <https://plugins.gradle.org/plugin/kr.motd.sphinx>`_
  for more information.

Now you can build your project web site using the ``site`` or ``sphinx`` task::

  ./gradlew site

This will generate the documentation in the `build/site` folder.

You can also specify additional configuration properties:

.. code-block:: groovy

  sphinx {
    // Change the source directory.
    sourceDirectory = "${projectDir}/alternative-src/sphinx"
    // Change the output directory.
    outputDirectory = "${project.buildDir}/alternative-site"
    // Add environment variables.
    environments = ['ENV_FOO': 'value1', 'ENV_BAR': 'value2']
    env 'ENV_BAZ', 'value3'
    // Add tags.
    tags 'tagA', 'tagB'
  }
