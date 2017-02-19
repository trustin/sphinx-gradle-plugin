.. _`Gradle`: https://gradle.org/
.. _`Sphinx`: http://sphinx.pocoo.org/
.. _`reStructured Text`: http://docutils.sf.net/rst.html
.. _`Markdown`: http://daringfireball.net/projects/markdown/
.. _`PlantUML`: http://plantuml.sourceforge.net/
.. _`Thomas Dudziak`: https://github.com/tomdz/sphinx-maven
.. _`Bala Sridhar`: https://github.com/balasridhar/sphinx-maven
.. _`sphinxcontrib-plantuml`: https://pypi.python.org/pypi/sphinxcontrib-plantuml
.. _`sphinxcontrib-inlinesyntaxhighlight`: http://sphinxcontrib-inlinesyntaxhighlight.readthedocs.org/en/latest/
.. _`recommonmark`: https://recommonmark.readthedocs.org/en/latest/
.. _`javasphinx`: http://bronto.github.io/javasphinx/

sphinx-gradle-plugin
====================
The *sphinx-gradle-plugin* is a `Gradle`_ plugin that uses `Sphinx`_ to generate the project web site.
Sphinx itself was originally created by the Python community for the new Python documentation. It uses a
plain text format called `reStructured Text`_ which it then compiles into a variety of documentation formats
such as HTML, LaTeX (for PDF), epub. reStructured Text is similar to `Markdown`_ but - at least via Sphinx -
has better support for multi-page documentation.

The *sphinx-gradle-plugin* is licensed under Apache License, Version 2.0. The plugin was created for Java-based
applications. The idea was to introduce the benefits of reStructured Text format and Sphinx documentation
generator for generating documentation for custom applications.

.. Explain what comes along with this plugin. ..

This plugin contains the python packages and its dependencies that are needed to generate the documentation
using `Sphinx`_. The plugin only supports the default themes that come along with `Sphinx`_ python package.
It also incorporates other open source plugins that are helpful while explaining complex concepts within
documentation. These plugins are:

Extensions
----------
Besides the extensions distributed with `Sphinx`_, this plugin includes the following extensions:

- `sphinxcontrib-plantuml`_ - enables embedding `PlantUML`_ diagrams
- `sphinxcontrib-inlinesyntaxhighlight`_ - enables syntax-highlighting inline literals
- `recommonmark`_ - adds Markdown support
- `javasphinx`_ - adds the Java domain

Credits and changes
-------------------
This plugin reuses the ``SphinxRunner`` implementation of ``sphinx-maven-plugin`` originally written by
`Thomas Dudziak`_. `Bala Sridhar`_ since then upgraded Sphinx to 1.3.1 and added PlantUML and JavaSphinx
support in his fork. I'd like to appreciate their effort that did all the heavy lifting.

1.0.3.Final (19-Feb-2017)
^^^^^^^^^^^^^^^^^^^^^^^^^
- Updated sphinx-maven-plugin to 1.5.3, which includes:

  - Sphinx 1.5.2
  - sphinx_rtd_theme master (eef98b3)
  - PyYAML 3.12

1.0.2.Final (28-Sep-2016)
^^^^^^^^^^^^^^^^^^^^^^^^^
- Fixed a bug where line ending conversion is not performed on CSS files

1.0.1.Final (28-Sep-2016)
^^^^^^^^^^^^^^^^^^^^^^^^^
- Fixed line ending issues with the generated site
- Change the default ``sourceDirectory`` from ``src/sphinx`` to ``src/site/sphinx`` to be consistent with
  ``sphinx-maven-plugin``

1.0.0.Final (27-Sep-2016)
^^^^^^^^^^^^^^^^^^^^^^^^^
- Initial release

Read more
---------
.. toctree::
   :maxdepth: 2

   basic-usage
   configuration
